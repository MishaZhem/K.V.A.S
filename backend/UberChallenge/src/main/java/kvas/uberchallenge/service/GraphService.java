package kvas.uberchallenge.service;

import kvas.uberchallenge.entity.Driver;
import kvas.uberchallenge.model.GraphResponseDTO;
import kvas.uberchallenge.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GraphService {
    private final DriverRepository driverRepository;
    private final ResourceLoader resourceLoader;

    private static final String PYTHON_CONTAINER_NAME = "python-service";
    private static final String PYTHON_SCRIPT_PATH = "/scripts/kvas_graphmaker.py";

    public GraphResponseDTO getGraphData(String username, Double currentLat, Double currentLon) {
        Driver driver = driverRepository.getDriverByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Driver not found for username: " + username));

        try {
            String driverCsv = buildDriverInputCsv(driver, currentLat, currentLon);
            String dataGraphCsv = readDataGraphCsv();

            // Combine the two CSV contents as expected by the Python script
            String scriptInput = "---DATA_GRAPH_CSV_START---\n" +
                    dataGraphCsv +
                    "\n---DATA_GRAPH_CSV_END---\n" +
                    driverCsv;

            String outputCsv = executePythonScriptInContainer(scriptInput);
            List<Boolean> graphData = parseOutputCsv(outputCsv);

            return GraphResponseDTO.builder()
                    .graphData(graphData)
                    .build();

        } catch (IOException | InterruptedException e) {
            log.error("Failed to get graph data for driver {}", username, e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException("Failed to process graph analysis", e);
        }
    }

    private String readDataGraphCsv() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:python-models/data_graph.csv");
        try (InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private String buildDriverInputCsv(Driver driver, Double currentLat, Double currentLon) {
        // Header
        String header = "driverLat,driverLon,rating,earnerType,experienceMonths,fuelType,isEv,vehicleType,productType,weatherType";

        // Single row with driver data
        String driverData = String.join(",",
                String.valueOf(currentLat),
                String.valueOf(currentLon),
                String.valueOf(driver.getRating()),
                "0",
                String.valueOf(driver.getExperienceMonths()),
                String.valueOf(driver.getFuelType().ordinal()),
                String.valueOf(driver.getIsEv()),
                String.valueOf(driver.getVehicleType().ordinal()),
                "0",
                "0"
        );

        return header + "\n" + driverData + "\n";
    }

    /**
     * Executes Python script in the python-service Docker container
     * Uses docker exec to run the script and pipes CSV data via stdin
     */
    private String executePythonScriptInContainer(String scriptInput) throws IOException, InterruptedException {
        log.debug("Executing Python graph script in container: {}", PYTHON_CONTAINER_NAME);

        // Build the docker exec command
        ProcessBuilder processBuilder = new ProcessBuilder(
                "python", PYTHON_SCRIPT_PATH
        );
        processBuilder.redirectErrorStream(false);

        Process process = processBuilder.start();

        // Write the combined CSV input to the Python script's stdin
        try (OutputStreamWriter writer = new OutputStreamWriter(process.getOutputStream())) {
            writer.write(scriptInput);
            writer.flush();
        } catch (IOException e) {
            log.error("Failed to write input to Python process", e);
            process.destroyForcibly();
            throw e;
        }

        // Read the output
        String output;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            output = reader.lines().collect(Collectors.joining("\n"));
        }

        // Read any errors from stderr
        String errors;
        try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            errors = errorReader.lines().collect(Collectors.joining("\n"));
        }

        if (!errors.isEmpty()) {
            log.warn("Python script stderr output: {}", errors);
        }

        // Wait for the process to complete
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            log.error("Python script failed with exit code {}. Error output: {}", exitCode, errors);
            throw new RuntimeException("Python script execution failed with exit code " + exitCode +
                    ". Errors: " + errors);
        }

        log.debug("Python graph script executed successfully");
        return output.trim();
    }

    private List<Boolean> parseOutputCsv(String csvOutput) {
        String[] values = csvOutput.split(",");
        List<Boolean> result = new ArrayList<>(24);

        for (int i = 0; i < Math.min(values.length, 24); i++) {
            result.add("1".equals(values[i].trim()));
        }

        return result;
    }
}
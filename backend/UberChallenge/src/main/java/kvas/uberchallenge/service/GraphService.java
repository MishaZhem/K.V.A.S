package kvas.uberchallenge.service;

import kvas.uberchallenge.entity.Driver;
import kvas.uberchallenge.model.GraphResponseDTO;
import kvas.uberchallenge.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class GraphService {
    private final DriverRepository driverRepository;

    private static final String PYTHON_SCRIPT_PATH = "src/main/resources/python/graph_prediction/kvas_graphmaker.py";

    public GraphResponseDTO getGraphData(String username, Double currentLat, Double currentLon) {
        Driver driver = driverRepository.getDriverByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Driver not found for username: " + username));

        try {
            String inputCsv = buildInputCsv(driver, currentLat, currentLon);
            String outputCsv = executePythonScript(inputCsv);
            List<Boolean> graphData = parseOutputCsv(outputCsv);

            return GraphResponseDTO.builder()
                    .graphData(graphData)
                    .build();

        } catch (IOException | InterruptedException e) {
            log.error("Failed to get graph data for driver {}", username, e);
            throw new RuntimeException("Failed to process graph analysis", e);
        }
    }

    private String buildInputCsv(Driver driver, Double currentLat, Double currentLon) {
        StringBuilder csv = new StringBuilder();

        // Header
        String[] header = {
                "driverLat", "driverLon", "rating", "earnerType", "experienceMonths",
                "fuelType", "isEv", "vehicleType", "weatherType"
        };
        csv.append(String.join(",", header)).append("\n");

        // Single row with driver data
        String[] driverData = {
                String.valueOf(currentLat),
                String.valueOf(currentLon),
                String.valueOf(driver.getRating()),
                String.valueOf(driver.getEarnerType().ordinal()),
                String.valueOf(driver.getExperienceMonths()),
                String.valueOf(driver.getFuelType().ordinal()),
                String.valueOf(driver.getIsEv()),
                String.valueOf(driver.getVehicleType().ordinal()),
                "0"
        };

        String line = String.join(",", driverData);
        csv.append(line).append("\n");

        return csv.toString();
    }

    private String executePythonScript(String inputCsv) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("python", PYTHON_SCRIPT_PATH);
        processBuilder.redirectErrorStream(false);
        Process process = processBuilder.start();

        // Write CSV to stdin
        Thread writerThread = new Thread(() -> {
            try (OutputStreamWriter writer = new OutputStreamWriter(process.getOutputStream())) {
                writer.write(inputCsv);
                writer.flush();
            } catch (IOException e) {
                log.error("Error writing to Python process stdin", e);
            }
        });
        writerThread.start();

        // Read stdout
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        // Read stderr for errors
        StringBuilder errors = new StringBuilder();
        try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = errorReader.readLine()) != null) {
                errors.append(line).append("\n");
                log.warn("Python stderr: {}", line);
            }
        }

        writerThread.join();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Python script failed with exit code " + exitCode +
                    ". Errors: " + errors.toString());
        }

        return output.toString().trim();
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

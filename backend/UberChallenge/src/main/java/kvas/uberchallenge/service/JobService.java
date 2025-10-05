package kvas.uberchallenge.service;

import kvas.uberchallenge.entity.Driver;
import kvas.uberchallenge.entity.Order;
import kvas.uberchallenge.model.job.JobItemResponseDTO;
import kvas.uberchallenge.model.job.JobListResponseDTO;
import kvas.uberchallenge.repository.DriverRepository;
import kvas.uberchallenge.repository.OrderRepository;
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
public class JobService {
    private final DriverRepository driverRepository;
    private final OrderRepository orderRepository;

    private static final double MAX_RADIUS_KM = 10.0;
    private static final String PYTHON_CONTAINER_NAME = "python-service";
    private static final String PYTHON_SCRIPT_PATH = "/scripts/kvas_evaluateorders.py";

    public JobListResponseDTO getJobs(String username, Double currentLat, Double currentLon) {
        Driver driver = driverRepository.getDriverByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Driver not found for username: " + username));

        try {
            List<Order> orders = orderRepository.findNearbyOrders(
                    currentLat, currentLon, MAX_RADIUS_KM, driver.getEarnerType().getValue());

            if (orders.isEmpty()) {
                return new JobListResponseDTO(new ArrayList<>());
            }

            String inputCsv = buildInputCsv(currentLat, currentLon, driver, orders);
            String outputCsv = executePythonScriptInContainer(inputCsv);
            List<JobItemResponseDTO> jobs = parseOutputCsv(outputCsv);

            return new JobListResponseDTO(jobs);

        } catch (IOException | InterruptedException e) {
            log.error("Failed to get jobs for driver {}", username, e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException("Failed to process job analysis", e);
        }
    }

    private String buildInputCsv(Double currentLat, Double currentLon, Driver driver, List<Order> orders) {
        StringBuilder csv = new StringBuilder();

        // Header
        String[] header = {
                "driverLat", "driverLon", "rating", "earnerType", "experienceMonths", "fuelType", "isEv", "vehicleType",
                "startLat", "startLon", "endLat", "endLon", "startingTime", "productType",
                "paymentType", "weatherType"
        };
        csv.append(String.join(",", header)).append("\n");

        // Data rows
        for (Order order : orders) {
            String[] driverData = {
                    String.valueOf(currentLat), String.valueOf(currentLon),
                    String.valueOf(driver.getRating()), String.valueOf(driver.getEarnerType().ordinal()),
                    String.valueOf(driver.getExperienceMonths()), String.valueOf(driver.getFuelType().ordinal()),
                    String.valueOf(driver.getIsEv()), String.valueOf(driver.getVehicleType().ordinal())
            };

            String[] orderData = {
                    String.valueOf(order.getStartLat()), String.valueOf(order.getStartLon()),
                    String.valueOf(order.getEndLat()), String.valueOf(order.getEndLon()),
                    String.valueOf(order.getStartingTime()), String.valueOf(order.getProductType().ordinal()),
                    String.valueOf(order.getPaymentType().ordinal()), String.valueOf(order.getWeatherType().ordinal())
            };

            String line = Stream.of(driverData, orderData)
                    .flatMap(Stream::of)
                    .collect(Collectors.joining(","));
            csv.append(line).append("\n");
        }

        return csv.toString();
    }

    /**
     * Executes Python script in the python-service Docker container
     * Uses docker exec to run the script and pipes CSV data via stdin
     */
    private String executePythonScriptInContainer(String inputCsv) throws IOException, InterruptedException {
        log.debug("Executing Python script in container: {}", PYTHON_CONTAINER_NAME);

        // Build the docker exec command
        ProcessBuilder processBuilder = new ProcessBuilder(
               "python", PYTHON_SCRIPT_PATH
        );
        processBuilder.redirectErrorStream(false);

        Process process = processBuilder.start();

        // Write CSV input to the Python script's stdin
        try (OutputStreamWriter writer = new OutputStreamWriter(process.getOutputStream())) {
            writer.write(inputCsv);
            writer.flush();
        } catch (IOException e) {
            log.error("Failed to write input to Python process", e);
            process.destroyForcibly();
            throw e;
        }

        // Read the output (CSV results)
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

        log.debug("Python script executed successfully");
        return output;
    }

    private List<JobItemResponseDTO> parseOutputCsv(String csvOutput) throws IOException {
        List<JobItemResponseDTO> jobs = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new StringReader(csvOutput))) {
            String header = reader.readLine(); // Skip header
            if (header == null) {
                log.warn("Empty CSV output from Python script");
                return jobs;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] data = line.split(",");
                if (data.length < 6) {
                    log.warn("Skipping invalid output line (expected 6 columns, got {}): {}",
                            data.length, line);
                    continue;
                }

                try {
                    JobItemResponseDTO job = new JobItemResponseDTO(
                            Double.parseDouble(data[0]), // dist_Order
                            Double.parseDouble(data[1]), // time_Order
                            Double.parseDouble(data[2]), // earn_Order
                            Double.parseDouble(data[3]), // dist_Pick
                            Double.parseDouble(data[4]), // time_Pick
                            Double.parseDouble(data[5])  // moneyPerHour
                    );
                    jobs.add(job);
                } catch (NumberFormatException e) {
                    log.warn("Failed to parse numeric values in line: {}", line, e);
                }
            }
        }

        log.debug("Parsed {} jobs from Python output", jobs.size());
        return jobs;
    }
}
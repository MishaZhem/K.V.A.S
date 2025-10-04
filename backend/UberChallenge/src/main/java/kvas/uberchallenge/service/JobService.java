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
    private static final String PYTHON_SCRIPT_PATH = "src/main/resources/python/job_analysis/kvas_evaluateorders.py";

    public JobListResponseDTO getJobs(String username, Double currentLat, Double currentLon) {
        Driver driver = driverRepository.getDriverByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Driver not found for username: " + username));

        try {
            List<Order> orders = orderRepository.findNearbyOrders(
                    currentLat, currentLon, MAX_RADIUS_KM, driver.getEarnerType().getValue());

            // Execute Python script
            ProcessBuilder processBuilder = new ProcessBuilder(
                    ".venv/bin/python", pythonScript, inputCsv, outputCsv
            );
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // Read output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            String inputCsv = buildInputCsv(currentLat, currentLon, driver, orders);
            String outputCsv = executePythonScript(inputCsv);
            List<JobItemResponseDTO> jobs = parseOutputCsv(outputCsv);

            return new JobListResponseDTO(jobs);

        } catch (IOException | InterruptedException e) {
            log.error("Failed to get jobs for driver {}", username, e);
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

    private String executePythonScript(String inputCsv) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("python", PYTHON_SCRIPT_PATH);
        processBuilder.redirectErrorStream(false); // Keep stderr separate
        Process process = processBuilder.start();

        // Write CSV to stdin in a separate thread to avoid deadlock
        Thread writerThread = new Thread(() -> {
            try (OutputStreamWriter writer = new OutputStreamWriter(process.getOutputStream())) {
                writer.write(inputCsv);
                writer.flush();
            } catch (IOException e) {
                log.error("Error writing to Python process stdin", e);
            }
        });
        writerThread.start();

        // Read stdout (the CSV output)
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        // Read stderr for errors/logging
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

        return output.toString();
    }

    private List<JobItemResponseDTO> parseOutputCsv(String csvOutput) throws IOException {
        List<JobItemResponseDTO> jobs = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new StringReader(csvOutput))) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] data = line.split(",");
                if (data.length < 6) {
                    log.warn("Skipping invalid output line: {}", line);
                    continue;
                }

                JobItemResponseDTO job = new JobItemResponseDTO(
                        Double.parseDouble(data[0]), // dist_Order
                        Double.parseDouble(data[1]), // time_Order
                        Double.parseDouble(data[2]), // earn_Order
                        Double.parseDouble(data[3]), // dist_Pick
                        Double.parseDouble(data[4]), // time_Pick
                        Double.parseDouble(data[5])  // moneyPerHour
                );
                jobs.add(job);
            }
        }

        return jobs;
    }
}
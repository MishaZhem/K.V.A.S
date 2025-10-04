package kvas.uberchallenge.service;

import kvas.uberchallenge.entity.Driver;
import kvas.uberchallenge.entity.Order;
import kvas.uberchallenge.model.LocationDTO;
import kvas.uberchallenge.model.RouteAssessmentResponseDTO;
import kvas.uberchallenge.model.job.JobListResponseDTO;
import kvas.uberchallenge.repository.DriverRepository;
import kvas.uberchallenge.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobService {
    private final DriverRepository driverRepository;
    private final OrderRepository orderRepository;
    private final RestTemplateBuilder restTemplateBuilder;
    private final DriverInfoService driverInfoService;

    @Value("${ml.service.url:http://localhost:5000}")
    private String mlServiceUrl;

    private static final double MAX_RADIUS_KM = 10.0;

    public JobListResponseDTO getJobs(String username, Double currentLat, Double currentLon) {
        try {
            String pythonScript = "src/main/resources/python/kvas_usemodel.py";
            String inputCsv = "src/main/resources/python/test_dataframe.csv";
            String outputCsv = "src/main/resources/python/predictions.csv";

            // Execute Python script
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "python", pythonScript, inputCsv, outputCsv
            );
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // Read output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Python script failed with exit code: " + exitCode);
            }

            // Read predictions from output CSV
            List<Double> predictions = new ArrayList<>();
            try (BufferedReader csvReader = new BufferedReader(new FileReader(outputCsv))) {
                csvReader.readLine(); // Skip header
                while ((line = csvReader.readLine()) != null) {
                    predictions.add(Double.parseDouble(line.trim()));
                }
            }

            // Return response with predictions
            return new JobListResponseDTO(null);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to execute Python script", e);
        }
    }

    public RouteAssessmentResponseDTO assessRoute(String username, LocationDTO currentDriverLoc, LocationDTO pickupLoc, LocationDTO dropoffLoc) {return null;}


    private RouteAssessmentResponseDTO getMockRouteAssessment() {
        return new RouteAssessmentResponseDTO(25, 1500, 3600);
    }
}

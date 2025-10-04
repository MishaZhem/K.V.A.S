package kvas.uberchallenge.service;

import kvas.uberchallenge.entity.Driver;
import kvas.uberchallenge.entity.enums.EarnerType;
import kvas.uberchallenge.model.HeatmapPointDTO;
import kvas.uberchallenge.model.HeatmapResponseDTO;
import kvas.uberchallenge.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import com.opencsv.CSVWriter;

@Service
@RequiredArgsConstructor
public class HeatmapService {
    private final DriverRepository driverRepository;
    private final RestTemplateBuilder restTemplateBuilder;


    @Value("${ml.service.url:http://localhost:5000}")
    private String mlServiceUrl;

    public HeatmapResponseDTO getHeatmap(String username, String time) {
        try {
            String pythonScript = "src/main/resources/python/kvas_usemodel.py";
            String outputCsv = "src/main/resources/python/predictions.csv";

            Optional<Driver> driver = driverRepository.getDriverByUser_Username(username);
            if (driver.isEmpty()) {
                return new HeatmapResponseDTO();
            }
            Driver d = driver.get();

            String inputCsv = makeCsv(d.getId(), d.getEarnerType());

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

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Python script failed with exit code: " + exitCode);
            }

            return new HeatmapResponseDTO(null);


        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to execute Python script", e);
        }
    }

        public String makeCsv(UUID id, EarnerType type) {
            String csvFile = "output" + id.toString() + ".csv";
            File file = new File(csvFile);

            try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
                String[] header = {"earner_type"};
                writer.writeNext(header);
                String[] record = {String.valueOf(type.getValue())};
                writer.writeNext(record);
                System.out.println("CSV file created");
                return file.toURI().toString();
            } catch (IOException e) {
                System.err.println("Error occurs while creating CSV: " + e.getMessage());
                return "";
            }
        }

    private HeatmapResponseDTO getMockHeatmap() {
        List<List<HeatmapPointDTO>> points = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            points.add(new ArrayList<>());
            for (int j = 0; j < 10; j++) {
                points.get(i).add(new HeatmapPointDTO(
                        40.7 + Math.random() * 0.1,
                        -74.0 + Math.random() * 0.1,
                        Math.random() * 100
                ));
            }
        }
        return new HeatmapResponseDTO(points);
    }
}


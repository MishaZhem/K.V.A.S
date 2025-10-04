package kvas.uberchallenge.service;

import kvas.uberchallenge.entity.Driver;
import kvas.uberchallenge.model.HeatmapPointDTO;
import kvas.uberchallenge.model.HeatmapResponseDTO;
import kvas.uberchallenge.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HeatmapService {
    private final DriverRepository driverRepository;
    private final RestTemplateBuilder restTemplateBuilder;


    @Value("${ml.service.url:http://localhost:5000}")
    private String mlServiceUrl;

    public HeatmapResponseDTO getHeatmap(String username, String time) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        Optional<Driver> driver = driverRepository.getDriverByUser_Username(username);
        if (driver.isEmpty()) {
            return new HeatmapResponseDTO();
        }
        Driver d = driver.get();
        Map<String, Object> mlRequest = new HashMap<>();
        mlRequest.put("driverId", d.getId().toString());
        mlRequest.put("rating", d.getRating());
        mlRequest.put("earnerType", d.getEarnerType());
        mlRequest.put("fuelType", d.getFuelType());
        mlRequest.put("vehicleType", d.getVehicleType());
        mlRequest.put("time", time);

        try {
            @SuppressWarnings("unchecked")
            List<List<Double>> heatmapData = restTemplate.postForObject(
                    mlServiceUrl + "/predict/heatmap",
                    mlRequest,
                    List.class
            );

            List<HeatmapPointDTO> points = heatmapData.stream()
                    .map(point -> new HeatmapPointDTO(point.get(0), point.get(1), point.get(2)))
                    .collect(Collectors.toList());

            return new HeatmapResponseDTO(points);
        } catch (Exception e) {
            return getMockHeatmap();
        }
    }

    private HeatmapResponseDTO getMockHeatmap() {
        List<HeatmapPointDTO> points = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            points.add(new HeatmapPointDTO(
                    40.7 + Math.random() * 0.1,
                    -74.0 + Math.random() * 0.1,
                    Math.random() * 100
            )); 
        }
        return new HeatmapResponseDTO(points);
    }
}


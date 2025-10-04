package kvas.uberchallenge.service;

import kvas.uberchallenge.entity.Driver;
import kvas.uberchallenge.model.EarningsGraphResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RouteService {
    private final DriverInfoService driverInfoService;
    private final RestTemplateBuilder restTemplateBuilder;

    @Value("${ml.service.url:http://localhost:5000}")
    private String mlServiceUrl;

    public EarningsGraphResponseDTO getEarningsGraph(UUID driverId, Double currentLat, Double currentLon) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        Driver driver = driverInfoService.getDriverById(driverId);

        Map<String, Object> mlRequest = new HashMap<>();
        mlRequest.put("driverId", driverId.toString());
        mlRequest.put("rating", driver.getRating());
        mlRequest.put("earnerType", driver.getEarnerType());
        mlRequest.put("fuelType", driver.getFuelType());
        mlRequest.put("vehicleType", driver.getVehicleType());
        mlRequest.put("currentLat", currentLat);
        mlRequest.put("currentLon", currentLon);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Double> earnings = restTemplate.postForObject(
                    mlServiceUrl + "/predict/graph",
                    mlRequest,
                    Map.class
            );
            return new EarningsGraphResponseDTO(earnings);
        } catch (Exception e) {
            return getMockEarningsGraph();
        }
    }

    private EarningsGraphResponseDTO getMockEarningsGraph() {
        Map<String, Double> earnings = new LinkedHashMap<>();
        for (int i = 0; i < 24; i++) {
            earnings.put(String.format("%02d:00", i), 15.0 + Math.random() * 20);
        }
        return new EarningsGraphResponseDTO(earnings);
    }
}


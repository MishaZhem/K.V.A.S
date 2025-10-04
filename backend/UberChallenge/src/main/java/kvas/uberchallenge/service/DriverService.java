package kvas.uberchallenge.service;

import kvas.uberchallenge.entity.Driver;
import kvas.uberchallenge.entity.Order;
import kvas.uberchallenge.model.*;
import kvas.uberchallenge.repository.DriverRepository;
import kvas.uberchallenge.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverService {
    private final DriverRepository driverRepository;
    private final OrderRepository orderRepository;
    private final RestTemplateBuilder restTemplateBuilder;

    @Value("${ml.service.url:http://localhost:5000}")
    private String mlServiceUrl;

    private static final double MAX_RADIUS_KM = 10.0;

    public Driver getDriverById(UUID driverId) {
        return driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
    }

    public EarningsGraphResponseDTO getEarningsGraph(UUID driverId, Double currentLat, Double currentLon) {
        RestTemplate restTemplate = restTemplateBuilder.build();

        Driver driver = getDriverById(driverId);

        Map<String, Object> mlRequest = new HashMap<>();
        mlRequest.put("driverId", driverId.toString());
        mlRequest.put("rating", driver.getRating());
        mlRequest.put("earnerType", driver.getEarnerType());
        mlRequest.put("fuelType", driver.getFuelType());
        mlRequest.put("homeCity", driver.getHomeCity());
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
            // Return mock data if ML service is unavailable
            return getMockEarningsGraph();
        }
    }

    public HeatmapResponseDTO getHeatmap(UUID driverId, String time) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        Driver driver = getDriverById(driverId);

        Map<String, Object> mlRequest = new HashMap<>();
        mlRequest.put("driverId", driverId.toString());
        mlRequest.put("rating", driver.getRating());
        mlRequest.put("earnerType", driver.getEarnerType());
        mlRequest.put("fuelType", driver.getFuelType());
        mlRequest.put("homeCity", driver.getHomeCity());
        mlRequest.put("vehicleType", driver.getVehicleType());
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
            // Return mock data if ML service is unavailable
            return getMockHeatmap();
        }
    }

    public JobListResponseDTO getJobs(UUID driverId, Double currentLat, Double currentLon) {
        Driver driver = getDriverById(driverId);
        List<Order> orders = orderRepository.findByCityId(driver.getHomeCity());

        List<JobItemDTO> jobs = orders.stream()
                .filter(order -> calculateDistance(currentLat, currentLon, order.getStartLat(), order.getStartLon()) <= MAX_RADIUS_KM)
                .map(order -> {
                    RouteAssessmentResponseDTO assessment = assessRoute(
                            driverId,
                            new LocationDTO(currentLat, currentLon),
                            new LocationDTO(order.getStartLat(), order.getStartLon()),
                            new LocationDTO(order.getEndLat(), order.getEndLon())
                    );

                    return new JobItemDTO(
                            order.getId(),
                            new LocationDTO(order.getStartLat(), order.getStartLon()),
                            new LocationDTO(order.getEndLat(), order.getEndLon()),
                            assessment.getExpectedEarnNetCents(),
                            assessment.getMoneyPerHourCents(),
                            order.getStartingTime().toEpochSecond(ZoneOffset.UTC) * 1000
                    );
                })
                .collect(Collectors.toList());

        return new JobListResponseDTO(jobs);
    }

    public RouteAssessmentResponseDTO assessRoute(UUID driverId, LocationDTO currentDriverLoc, LocationDTO pickupLoc, LocationDTO dropoffLoc) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        Driver driver = getDriverById(driverId);

        Map<String, Object> mlRequest = new HashMap<>();
        mlRequest.put("driverId", driverId.toString());
        mlRequest.put("rating", driver.getRating());
        mlRequest.put("earnerType", driver.getEarnerType());
        mlRequest.put("fuelType", driver.getFuelType());
        mlRequest.put("homeCity", driver.getHomeCity());
        mlRequest.put("vehicleType", driver.getVehicleType());
        mlRequest.put("currentDriverLoc", Map.of("lat", currentDriverLoc.getLat(), "lon", currentDriverLoc.getLon()));
        mlRequest.put("pickupLoc", Map.of("lat", pickupLoc.getLat(), "lon", pickupLoc.getLon()));
        mlRequest.put("dropoffLoc", Map.of("lat", dropoffLoc.getLat(), "lon", dropoffLoc.getLon()));

        try {
            return restTemplate.postForObject(
                    mlServiceUrl + "/predict/route",
                    mlRequest,
                    RouteAssessmentResponseDTO.class
            );
        } catch (Exception e) {
            // Return mock data if ML service is unavailable
            return getMockRouteAssessment();
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private EarningsGraphResponseDTO getMockEarningsGraph() {
        Map<String, Double> earnings = new LinkedHashMap<>();
        for (int i = 0; i < 24; i++) {
            earnings.put(String.format("%02d:00", i), 15.0 + Math.random() * 20);
        }
        return new EarningsGraphResponseDTO(earnings);
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

    private RouteAssessmentResponseDTO getMockRouteAssessment() {
        return new RouteAssessmentResponseDTO(25, 1500, 3600);
    }
}


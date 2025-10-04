package kvas.uberchallenge.service;

import kvas.uberchallenge.entity.Driver;
import kvas.uberchallenge.entity.Order;
import kvas.uberchallenge.model.*;
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
public class JobService {
    private final DriverInfoService driverInfoService;
    private final OrderRepository orderRepository;
    private final RestTemplateBuilder restTemplateBuilder;

    @Value("${ml.service.url:http://localhost:5000}")
    private String mlServiceUrl;

    private static final double MAX_RADIUS_KM = 10.0;

    public JobListResponseDTO getJobs(UUID driverId, Double currentLat, Double currentLon) {
        Driver driver = driverInfoService.getDriverById(driverId);
        List<Order> orders = orderRepository.findAll();

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
        Driver driver = driverInfoService.getDriverById(driverId);

        Map<String, Object> mlRequest = new HashMap<>();
        mlRequest.put("driverId", driverId.toString());
        mlRequest.put("rating", driver.getRating());
        mlRequest.put("earnerType", driver.getEarnerType());
        mlRequest.put("fuelType", driver.getFuelType());
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

    private RouteAssessmentResponseDTO getMockRouteAssessment() {
        return new RouteAssessmentResponseDTO(25, 1500, 3600);
    }
}


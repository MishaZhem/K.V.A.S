package kvas.uberchallenge.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kvas.uberchallenge.entity.Driver;
import kvas.uberchallenge.entity.Order;
import kvas.uberchallenge.exception.MLModelException;
import kvas.uberchallenge.model.job.JobItemResponseDTO;
import kvas.uberchallenge.model.job.JobListResponseDTO;
import kvas.uberchallenge.model.job.JobMLRequestDTO;
import kvas.uberchallenge.repository.DriverRepository;
import kvas.uberchallenge.repository.OrderRepository;
import kvas.uberchallenge.service.client.MLServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {
    private final DriverRepository driverRepository;
    private final OrderRepository orderRepository;
    private final MLServiceClient mlServiceClient;

    private final double MAX_RADIUS_KM = 10.0;

    public JobListResponseDTO getJobs(String username, Double currentLat, Double currentLon) {
        Driver driver = driverRepository.getDriverByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Driver not found for username: " + username));

        try {
            List<Order> orders = orderRepository.findNearbyOrders(
                    currentLat, currentLon, MAX_RADIUS_KM, driver.getEarnerType().getValue());

            if (orders.isEmpty()) {
                return new JobListResponseDTO(new ArrayList<>());
            }

            List<JobMLRequestDTO> requestData = buildRequestData(currentLat, currentLon, driver, orders);
            return mlServiceClient.analyzeJobs(requestData).getBody();
        } catch (Exception e) {
            log.error("Failed to get jobs for driver {}", username, e);
            throw new MLModelException("Failed to process job analysis");
        }
    }

    private List<JobMLRequestDTO> buildRequestData(Double currentLat, Double currentLon, Driver driver, List<Order> orders) {
        List<JobMLRequestDTO> requestList = new ArrayList<>(orders.size());

        for (Order order : orders) {
            JobMLRequestDTO dto = JobMLRequestDTO.builder()
                    // Driver (inherited fields)
                    .driverLat(currentLat)
                    .driverLon(currentLon)
                    .rating(driver.getRating())
                    .earnerType(driver.getEarnerType().getValue())
                    .experienceMonths(driver.getExperienceMonths())
                    .fuelType(driver.getFuelType().getValue())
                    .isEv(driver.getIsEv())
                    .vehicleType(driver.getVehicleType().getValue())
                    // Order (job) fields
                    .startLat(order.getStartLat())
                    .startLon(order.getStartLon())
                    .endLat(order.getEndLat())
                    .endLon(order.getEndLon())
                    .startingTime(order.getStartingTime())
                    .productType(order.getProductType().getValue())
                    .paymentType(order.getPaymentType().getValue())
                    .weatherType(order.getWeatherType().getValue())
                    .build();

            requestList.add(dto);
        }

        return requestList;
    }
}
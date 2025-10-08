package kvas.uberchallenge.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kvas.uberchallenge.entity.Driver;
import kvas.uberchallenge.entity.Order;
import kvas.uberchallenge.exception.MLModelException;
import kvas.uberchallenge.model.DriverRequestDTO;
import kvas.uberchallenge.model.GraphResponseDTO;
import kvas.uberchallenge.model.job.JobMLRequestDTO;
import kvas.uberchallenge.repository.DriverRepository;
import kvas.uberchallenge.service.client.MLServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GraphService {
    private final DriverRepository driverRepository;
    private final ObjectMapper objectMapper;
    private final MLServiceClient mlServiceClient;

    @Value("${ml.service.url:http://127.0.0.1:5000}")
    private String mlServiceUrl;

    public GraphResponseDTO getGraphData(String username, Double currentLat, Double currentLon) {
        Driver driver = driverRepository.getDriverByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Driver not found for username: " + username));

        try {
            DriverRequestDTO requestData = buildRequestData(currentLat, currentLon, driver);
            return mlServiceClient.predictGraph(requestData).getBody();
        } catch (MLModelException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to get graph data for driver {}", username, e);
            throw new MLModelException("Failed to process graph analysis: " + e.getMessage(), e);
        }
    }

    private DriverRequestDTO buildRequestData(Double currentLat, Double currentLon, Driver driver) {
        return DriverRequestDTO.builder()
                .driverLat(currentLat)
                .driverLon(currentLon)
                .rating(driver.getRating())
                .earnerType(driver.getEarnerType().getValue())
                .experienceMonths(driver.getExperienceMonths())
                .isEv(driver.getIsEv())
                .fuelType(driver.getFuelType().getValue())
                .vehicleType(driver.getVehicleType().getValue())
                .productType(0)
                .weatherType(0)
                .build();
    }
}
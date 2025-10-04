package kvas.uberchallenge.controller;

import jakarta.validation.Valid;
import kvas.uberchallenge.model.*;
import kvas.uberchallenge.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/driver")
@RequiredArgsConstructor
public class DriverController {
    private final DriverService driverService;

    @GetMapping("/route")
    public ResponseEntity<EarningsGraphResponseDTO> getRoute(
            @RequestParam Double currentLat,
            @RequestParam Double currentLon,
            Authentication authentication) {
        UUID driverId = extractDriverId(authentication);
        EarningsGraphResponseDTO response = driverService.getEarningsGraph(driverId, currentLat, currentLon);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/heatmap")
    public ResponseEntity<HeatmapResponseDTO> getHeatmap(
            @RequestParam String time,
            Authentication authentication) {
        UUID driverId = extractDriverId(authentication);
        HeatmapResponseDTO response = driverService.getHeatmap(driverId, time);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/jobs")
    public ResponseEntity<JobListResponseDTO> getJobs(
            @RequestParam Double currentLat,
            @RequestParam Double currentLon,
            Authentication authentication) {
        UUID driverId = extractDriverId(authentication);
        JobListResponseDTO response = driverService.getJobs(driverId, currentLat, currentLon);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/route-assessment")
    public ResponseEntity<RouteAssessmentResponseDTO> assessRoute(
            @Valid @RequestBody RouteAssessmentRequestDTO request,
            Authentication authentication) {
        UUID driverId = extractDriverId(authentication);
        RouteAssessmentResponseDTO response = driverService.assessRoute(
                driverId,
                request.getCurrentDriverLoc(),
                request.getPickupLoc(),
                request.getDropoffLoc()
        );
        return ResponseEntity.ok(response);
    }

    private UUID extractDriverId(Authentication authentication) {
        // Assuming JWT filter sets driverId in authentication
        if (authentication != null && authentication.getDetails() instanceof UUID) {
            return (UUID) authentication.getDetails();
        }
        throw new RuntimeException("Driver ID not found in authentication");
    }
}


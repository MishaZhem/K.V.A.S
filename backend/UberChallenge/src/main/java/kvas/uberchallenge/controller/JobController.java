package kvas.uberchallenge.controller;

import jakarta.validation.Valid;
import kvas.uberchallenge.model.JobListResponseDTO;
import kvas.uberchallenge.model.RouteAssessmentRequestDTO;
import kvas.uberchallenge.model.RouteAssessmentResponseDTO;
import kvas.uberchallenge.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/driver")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;

    @GetMapping("/jobs")
    public ResponseEntity<JobListResponseDTO> getJobs(
            @RequestParam Double currentLat,
            @RequestParam Double currentLon,
            Authentication authentication) {
        UUID driverId = extractDriverId(authentication);
        JobListResponseDTO response = jobService.getJobs(driverId, currentLat, currentLon);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/route-assessment")
    public ResponseEntity<RouteAssessmentResponseDTO> assessRoute(
            @Valid @RequestBody RouteAssessmentRequestDTO request,
            Authentication authentication) {
        UUID driverId = extractDriverId(authentication);
        RouteAssessmentResponseDTO response = jobService.assessRoute(
                driverId,
                request.getCurrentDriverLoc(),
                request.getPickupLoc(),
                request.getDropoffLoc()
        );
        return ResponseEntity.ok(response);
    }

    private UUID extractDriverId(Authentication authentication) {
        if (authentication != null && authentication.getDetails() instanceof UUID) {
            return (UUID) authentication.getDetails();
        }
        throw new RuntimeException("Driver ID not found in authentication");
    }
}


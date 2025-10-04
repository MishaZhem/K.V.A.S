package kvas.uberchallenge.controller;

import jakarta.validation.Valid;
import kvas.uberchallenge.model.job.JobListResponseDTO;
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

        String username = authentication.getName();

        JobListResponseDTO response = jobService.getJobs(username, currentLat, currentLon);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/route-assessment")
    public ResponseEntity<RouteAssessmentResponseDTO> assessRoute(
            @Valid @RequestBody RouteAssessmentRequestDTO request,
            Authentication authentication) {
        String username = authentication.getName();
        RouteAssessmentResponseDTO response = jobService.assessRoute(
                username,
                request.getCurrentDriverLoc(),
                request.getPickupLoc(),
                request.getDropoffLoc()
        );
        return ResponseEntity.ok(response);
    }
}


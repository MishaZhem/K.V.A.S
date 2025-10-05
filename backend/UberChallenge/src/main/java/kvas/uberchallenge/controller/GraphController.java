package kvas.uberchallenge.controller;

import kvas.uberchallenge.model.GraphResponseDTO;
import kvas.uberchallenge.model.job.JobListResponseDTO;
import kvas.uberchallenge.service.GraphService;
import kvas.uberchallenge.service.JobService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/driver")
@RequiredArgsConstructor
public class GraphController {
    private final GraphService graphService;

    @GetMapping("/graph")
    public ResponseEntity<GraphResponseDTO> getJobs(
            @RequestParam Double currentLat,
            @RequestParam Double currentLon,
            Authentication authentication) {

        String username = authentication.getName();

        GraphResponseDTO response = graphService.getGraphData(username, currentLat, currentLon);
        return ResponseEntity.ok(response);
    }
}

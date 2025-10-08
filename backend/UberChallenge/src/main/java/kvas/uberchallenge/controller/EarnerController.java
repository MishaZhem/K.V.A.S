package kvas.uberchallenge.controller;

import kvas.uberchallenge.model.GraphResponseDTO;
import kvas.uberchallenge.model.heatmap.HeatmapPointMapBoxDTO;
import kvas.uberchallenge.model.job.JobListResponseDTO;
import kvas.uberchallenge.service.GraphService;
import kvas.uberchallenge.service.HeatmapService;
import kvas.uberchallenge.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/earner")
@RequiredArgsConstructor
public class EarnerController {
    private final GraphService graphService;
    private final HeatmapService heatmapService;
    private final JobService jobService;

    @GetMapping("/jobs")
    @Cacheable(value = "jobCache")
    public ResponseEntity<JobListResponseDTO> getJobs(
            @RequestParam Double currentLat,
            @RequestParam Double currentLon,
            Authentication authentication) {

        String username = authentication.getName();

        JobListResponseDTO response = jobService.getJobs(username, currentLat, currentLon);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/heatmap")
    public ResponseEntity<HeatmapPointMapBoxDTO> getHeatmap(
            Authentication authentication) {

        String username = authentication.getName();
        return ResponseEntity.ok(heatmapService.getHeatmap(username));
    }

    @GetMapping("/heatmap/url")
    public ResponseEntity<HeatmapPointMapBoxDTO> getHeatmapAsURLToData(
            @RequestParam String username
    ) {
        HeatmapPointMapBoxDTO mapBoxDTO = heatmapService.getHeatmap(username);
        return ResponseEntity.ok(mapBoxDTO);
    }

    @GetMapping("/graph")
    @Cacheable(value = "graphCache")
    public ResponseEntity<GraphResponseDTO> getGraph(
            @RequestParam Double currentLat,
            @RequestParam Double currentLon,
            Authentication authentication) {

        String username = authentication.getName();

        GraphResponseDTO response = graphService.getGraphData(username, currentLat, currentLon);
        return ResponseEntity.ok(response);
    }
}

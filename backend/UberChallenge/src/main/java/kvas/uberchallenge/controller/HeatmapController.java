package kvas.uberchallenge.controller;

import kvas.uberchallenge.model.HeatmapResponseDTO;
import kvas.uberchallenge.service.HeatmapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/driver")
@RequiredArgsConstructor
public class HeatmapController {
    private final HeatmapService heatmapService;

    @GetMapping("/heatmap")
    public ResponseEntity<HeatmapResponseDTO> getHeatmap(
            @RequestParam Integer hour,
            Authentication authentication) {
        String username = authentication.getName();
        HeatmapResponseDTO response = heatmapService.getHeatmap(username, hour);

        return ResponseEntity.ok(response);
    }

    private UUID extractDriverId(Authentication authentication) {
        if (authentication != null && authentication.getDetails() instanceof UUID) {
            return (UUID) authentication.getDetails();
        }
        throw new RuntimeException("Driver ID not found in authentication");
    }
}


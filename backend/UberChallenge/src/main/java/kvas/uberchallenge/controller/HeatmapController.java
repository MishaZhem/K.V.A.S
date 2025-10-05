package kvas.uberchallenge.controller;

import kvas.uberchallenge.model.HeatmapPointMapBoxDTO;
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
    @GetMapping("/heatmap/url")
    public ResponseEntity<HeatmapPointMapBoxDTO> getHeatmapAsURLToData(
            @RequestParam Integer hour,
            @RequestParam String username
        ) {
            HeatmapResponseDTO response = heatmapService.getHeatmap(username, hour);
            HeatmapPointMapBoxDTO mapBoxDTO = new HeatmapPointMapBoxDTO(response);
        return ResponseEntity.ok(mapBoxDTO);
    }
}


package kvas.uberchallenge.controller;

import kvas.uberchallenge.model.EarningsGraphResponseDTO;
import kvas.uberchallenge.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/driver")
@RequiredArgsConstructor
public class RouteController {
    private final RouteService routeService;

    @GetMapping("/route")
    public ResponseEntity<EarningsGraphResponseDTO> getRoute(
            @RequestParam Double currentLat,
            @RequestParam Double currentLon,
            Authentication authentication) {
        UUID driverId = extractDriverId(authentication);
        EarningsGraphResponseDTO response = routeService.getEarningsGraph(driverId, currentLat, currentLon);
        return ResponseEntity.ok(response);
    }

    private UUID extractDriverId(Authentication authentication) {
        if (authentication != null && authentication.getDetails() instanceof UUID) {
            return (UUID) authentication.getDetails();
        }
        throw new RuntimeException("Driver ID not found in authentication");
    }
}


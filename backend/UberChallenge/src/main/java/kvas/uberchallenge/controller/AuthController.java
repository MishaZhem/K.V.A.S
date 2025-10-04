package kvas.uberchallenge.controller;

import jakarta.validation.Valid;
import kvas.uberchallenge.model.JwtResponseDTO;
import kvas.uberchallenge.model.LogInRequestDTO;
import kvas.uberchallenge.model.RegisterRequestDTO;
import kvas.uberchallenge.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@Valid @RequestBody LogInRequestDTO request) {
        JwtResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<JwtResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        JwtResponseDTO response = authService.register(request);
        return ResponseEntity.ok(response);
    }
}


package kvas.uberchallenge.controller;

import jakarta.validation.Valid;
import kvas.uberchallenge.constant.JWTConstants;
import kvas.uberchallenge.model.authentification.LogInRequestDTO;
import kvas.uberchallenge.model.authentification.LogInResponseDTO;
import kvas.uberchallenge.model.authentification.RegisterRequestDTO;
import kvas.uberchallenge.model.authentification.RegisterResponseDTO;
import kvas.uberchallenge.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LogInResponseDTO> login(@Valid @RequestBody LogInRequestDTO request) {
        Pair<String, LogInResponseDTO> loginResult = authService.login(request);

        return ResponseEntity.status(HttpStatus.OK)
                .header(JWTConstants.JWT_HEADER, loginResult.getFirst())
                .body(loginResult.getSecond());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO request) {
        RegisterResponseDTO response = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}


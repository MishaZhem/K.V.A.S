package kvas.uberchallenge.controller;

import jakarta.validation.Valid;
import kvas.uberchallenge.constant.ApplicationConstants;
import kvas.uberchallenge.model.authentification.LogInRequestDTO;
import kvas.uberchallenge.model.authentification.RegisterRequestDTO;
import kvas.uberchallenge.model.authentification.RegisterResponseDTO;
import kvas.uberchallenge.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LogInRequestDTO request) {
        Authentication authenticationResponse = authService.authenticateUser(request);

        if(authenticationResponse == null || !authenticationResponse.isAuthenticated()) {
            throw new BadCredentialsException("Invalid password.");
        }

        String jwt = authService.generateJWTToken(authenticationResponse);

        return ResponseEntity.status(HttpStatus.OK)
                .header(ApplicationConstants.JWT_HEADER, jwt)
                .build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO request) {
        RegisterResponseDTO response = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}


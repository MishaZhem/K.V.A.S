package kvas.uberchallenge.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import kvas.uberchallenge.entity.Driver;
import kvas.uberchallenge.entity.User;
import kvas.uberchallenge.model.JwtResponseDTO;
import kvas.uberchallenge.model.LogInRequestDTO;
import kvas.uberchallenge.model.RegisterRequestDTO;
import kvas.uberchallenge.repository.DriverRepository;
import kvas.uberchallenge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret:mySecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLong12345678}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private Long jwtExpiration;

    @Transactional
    public JwtResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();
        user = userRepository.save(user);

        Driver driver = Driver.builder()
                .user(user)
                .rating(request.getRating())
                .earnerType(request.getEarnerType())
                .fuelType(request.getFuelType())
                .homeCity(request.getHomeCity())
                .vehicleType(request.getVehicleType())
                .build();
        driver = driverRepository.save(driver);

        String token = generateToken(user.getId(), driver.getId());
        return new JwtResponseDTO(token, driver.getId());
    }

    public JwtResponseDTO login(LogInRequestDTO request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        Driver driver = driverRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Driver profile not found"));

        String token = generateToken(user.getId(), driver.getId());
        return new JwtResponseDTO(token, driver.getId());
    }

    private String generateToken(java.util.UUID userId, java.util.UUID driverId) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("driverId", driverId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}


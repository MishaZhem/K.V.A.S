package kvas.uberchallenge.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import kvas.uberchallenge.constant.ApplicationConstants;
import kvas.uberchallenge.entity.enums.Role;
import kvas.uberchallenge.entity.Driver;
import kvas.uberchallenge.entity.User;
import kvas.uberchallenge.exception.UserAlreadyExistsException;
import kvas.uberchallenge.helper.TimeTranslator;
import kvas.uberchallenge.model.authentification.LogInRequestDTO;
import kvas.uberchallenge.model.authentification.RegisterRequestDTO;
import kvas.uberchallenge.model.authentification.RegisterResponseDTO;
import kvas.uberchallenge.repository.DriverRepository;
import kvas.uberchallenge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationConstants applicationConstants;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final Environment env;

    public RegisterResponseDTO register(RegisterRequestDTO request) {
        try {
            User user = User.builder()
                    .username(request.getUsername())
                    .passwordHash(passwordEncoder.encode(request.getPassword()))
                    .role(Role.DRIVER) //TODO: different for driver and customer
                    .build();
            user = userRepository.save(user);

            Driver driver = Driver.builder()
                    .user(user)
                    .rating(0.0)
                    .earnerType(request.getEarnerType())
                    .fuelType(request.getFuelType())
                    .vehicleType(request.getVehicleType())
                    .build();
            driver = driverRepository.save(driver);

            return RegisterResponseDTO.builder()
                    .build();
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException("User with username '" + request.getUsername() + "' already exists");
        }
    }

    public Authentication authenticateUser(LogInRequestDTO request)
    {
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(request.getUsername(), request.getPassword());
        return authenticationManager.authenticate(authentication);
    }

    public String generateJWTToken(Authentication authentication)
    {
        String secret = env.getProperty(ApplicationConstants.JWT_SECRET_KEY, ApplicationConstants.JWT_SECRET_DEFAULT_VALUE);
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        int expirationTimeHours = Integer.parseInt(env.getProperty(ApplicationConstants.JWT_EXPIRATION_KEY, String.valueOf(ApplicationConstants.JWT_EXPIRATION_TIME_HOURS)));

        return Jwts.builder().issuer("CMAS TEST SERVER").subject("JWT Token")
                .claim("username", authentication.getName())
                .claim("authorities", authentication.getAuthorities().stream().map(
                        GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + TimeTranslator.hoursToMilliseconds(expirationTimeHours)))
                .signWith(secretKey).compact();
    }
}

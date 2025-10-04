package kvas.uberchallenge.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import kvas.uberchallenge.constant.ApplicationConstants;
import kvas.uberchallenge.entity.enums.EarnerType;
import kvas.uberchallenge.entity.enums.FuelType;
import kvas.uberchallenge.entity.enums.Role;
import kvas.uberchallenge.entity.Driver;
import kvas.uberchallenge.entity.User;
import kvas.uberchallenge.entity.enums.VehicleType;
import kvas.uberchallenge.exception.UserAlreadyExistsException;
import kvas.uberchallenge.helper.EnumMappingHelper;
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
                    .earnerType(EnumMappingHelper.getEarnerTypeByName(request.getEarnerType()))
                    .fuelType(EnumMappingHelper.getFuelTypeByName(request.getFuelType()))
                    .vehicleType(EnumMappingHelper.getVehicleTypeByName(request.getVehicleType()))
                    .isEv(request.getIsEv())
                    .experienceMonths(request.getExperienceMonths())
                    .build();

            driver = driverRepository.save(driver);

            return RegisterResponseDTO.builder()
                    .userId(driver.getId())
                    .driverId(driver.getId())
                    .username(user.getUsername())
                    .earnerType(driver.getEarnerType().name())
                    .fuelType(driver.getFuelType().name())
                    .isEv(driver.getIsEv())
                    .vehicleType(driver.getVehicleType().name())
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

        return Jwts.builder().issuer("Uber Challenge").subject("JWT Token")
                .claim("username", authentication.getName())
                .claim("authorities", authentication.getAuthorities().stream().map(
                        GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + TimeTranslator.hoursToMilliseconds(expirationTimeHours)))
                .signWith(secretKey).compact();
    }
}

package kvas.uberchallenge.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class JWTTokenValidatorFilter extends OncePerRequestFilter {

    @Value("${jwt.secret:mySecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLong12345678}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = request.getHeader("Authorization");

        if (jwt != null && jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7);
            try {
                SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
                Claims claims = Jwts.parser()
                        .verifyWith(secretKey)
                        .build()
                        .parseSignedClaims(jwt)
                        .getPayload();

                String username = claims.getSubject();
                String driverIdStr = claims.get("driverId", String.class);
                UUID driverId = UUID.fromString(driverIdStr);

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER")
                );

                // Set driverId as details for easy access in controllers
                ((UsernamePasswordAuthenticationToken) authentication).setDetails(driverId);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception exception) {
                handleJwtException(response, "Invalid or expired JWT token.", exception);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void handleJwtException(HttpServletResponse response, String message, Throwable exception) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        String errorDetails = (exception != null) ? exception.getClass().getSimpleName() + ": " + exception.getMessage() : "No details";

        String jsonResponse = String.format(
                "{\"timestamp\": \"%s\", \"status\": %d, \"error\": \"%s\", \"message\": \"%s\", \"details\": \"%s\"}",
                timestamp,
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                message.replace("\"", "\\\""),
                errorDetails.replace("\"", "\\\"")
        );

        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.equals("/api/auth/login") || path.equals("/api/auth/register");
    }
}
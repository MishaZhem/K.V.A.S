package kvas.uberchallenge.constant;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "app.jwt")
@Component
public final class JWTConstants {
    public static final String JWT_SECRET_KEY = "JWT_SECRET";
    public static final String JWT_SECRET_DEFAULT_VALUE = "jxgEQeXHuPq8VdbyYFNkANdudQ53YUn4";
    public static final String JWT_HEADER = "Authorization";

    public static final String JWT_EXPIRATION_KEY = "JWT_EXPIRATION_TIME";
    public static final int JWT_EXPIRATION_TIME_HOURS = 5;
}

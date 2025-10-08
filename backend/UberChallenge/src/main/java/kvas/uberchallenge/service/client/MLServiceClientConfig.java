package kvas.uberchallenge.service.client;

import feign.Request;
import feign.codec.ErrorDecoder;
import kvas.uberchallenge.exception.MLModelException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class MLServiceClientConfig {

    @Bean
    public Request.Options options() {
        return new Request.Options(30, TimeUnit.SECONDS, 30, TimeUnit.SECONDS, true);
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            if (response.status() >= 400) {
                return new MLModelException("ML service error: " + response.status());
            }
            return new Exception("Generic error");
        };
    }
}

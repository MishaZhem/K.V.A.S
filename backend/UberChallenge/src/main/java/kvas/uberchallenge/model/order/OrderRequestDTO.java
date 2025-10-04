package kvas.uberchallenge.model.order;

import jakarta.validation.constraints.NotNull;
import kvas.uberchallenge.entity.enums.PaymentType;
import kvas.uberchallenge.entity.enums.ProductType;
import kvas.uberchallenge.entity.enums.WeatherType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDTO {
    @NotNull(message = "Start latitude is required")
    private Double startLat;

    @NotNull(message = "Start longitude is required")
    private Double startLon;

    @NotNull(message = "End latitude is required")
    private Double endLat;

    @NotNull(message = "End longitude is required")
    private Double endLon;

    @NotNull(message = "Starting time is required")
    private LocalDateTime startingTime;

    @NotNull(message = "City ID is required")
    private Integer cityId;

    @NotNull(message = "Product type is required")
    private ProductType productType;

    @NotNull(message = "Payment type is required")
    private PaymentType paymentType;

    @NotNull(message = "Weather type is required")
    private WeatherType weatherType;
}

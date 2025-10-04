package kvas.uberchallenge.model.order;

import kvas.uberchallenge.entity.enums.PaymentType;
import kvas.uberchallenge.entity.enums.ProductType;
import kvas.uberchallenge.entity.enums.WeatherType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {
    private UUID id;
    private Double startLat;
    private Double startLon;
    private Double endLat;
    private Double endLon;
    private LocalDateTime startingTime;
    private Integer cityId;
    private ProductType productType;
    private PaymentType paymentType;
    private WeatherType weatherType;
    private LocalDateTime startingAt;
}

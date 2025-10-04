package kvas.uberchallenge.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kvas.uberchallenge.entity.enums.PaymentType;
import kvas.uberchallenge.entity.enums.ProductType;
import kvas.uberchallenge.entity.enums.WeatherType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "start_lat", nullable = false)
    @NotNull
    private Double startLat;

    @Column(name = "start_lon", nullable = false)
    @NotNull
    private Double startLon;

    @Column(name = "end_lat", nullable = false)
    @NotNull
    private Double endLat;

    @Column(name = "end_lon", nullable = false)
    @NotNull
    private Double endLon;

    @Column(name = "starting_time", nullable = false)
    @NotNull
    private Integer startingTime;

    @Column(name = "product_type")
    @Enumerated(EnumType.ORDINAL)
    @NotNull
    private ProductType productType;

    @Column(name = "payment_type")
    @Enumerated(EnumType.ORDINAL)
    @NotNull
    private PaymentType paymentType;

    @Column(name = "weather_type")
    @Enumerated(EnumType.ORDINAL)
    @NotNull
    private WeatherType weatherType;
}

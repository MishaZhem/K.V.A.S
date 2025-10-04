package kvas.uberchallenge.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
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
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
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
    private LocalDateTime startingTime;

    @Column(name = "city_id", nullable = false)
    @NotBlank
    private String cityId;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}


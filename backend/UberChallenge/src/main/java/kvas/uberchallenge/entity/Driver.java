package kvas.uberchallenge.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import kvas.uberchallenge.entity.enums.EarnerType;
import kvas.uberchallenge.entity.enums.FuelType;
import kvas.uberchallenge.entity.enums.VehicleType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "drivers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private User user;

    @Column(name = "rating")
    @Min(0)
    @Max(5)
    private Double rating;

    @Column(name = "earner_type")
    @Enumerated(EnumType.ORDINAL)
    @NotNull
    private EarnerType earnerType;

    @Column(name = "experience_months")
    @Min(value = 0, message = "Experience months must be non-negative")
    @NotNull
    private Integer experienceMonths;

    @Column(name = "fuel_type")
    @Enumerated(EnumType.ORDINAL)
    @NotNull
    private FuelType fuelType;

    @Column(name = "is_ev")
    @NotNull
    private Boolean isEv;

    @Column(name = "vehicle_type")
    @Enumerated(EnumType.ORDINAL)
    @NotNull(message = "Vehicle type must be specified")
    private VehicleType vehicleType;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

package kvas.uberchallenge.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "heat_map")
public class HeatMapPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    private Integer earnerType;
    private Integer hour;

    private Double latitude;
    private Double longitude;
    private Double moneyPerHour;
}
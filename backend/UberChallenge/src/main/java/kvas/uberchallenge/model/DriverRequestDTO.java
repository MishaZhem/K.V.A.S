package kvas.uberchallenge.model;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DriverRequestDTO {

    @NotNull
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    private Double driverLat;

    @NotNull
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    private Double driverLon;

    @NotNull
    @DecimalMin(value = "0.0", message = "Rating must be >= 0")
    @DecimalMax(value = "5.0", message = "Rating must be <= 5")
    private Double rating;

    @NotNull
    private Integer earnerType;

    @Min(value = 0, message = "experienceMonths must be >= 0")
    private Integer experienceMonths;

    @NotNull
    private Integer fuelType;

    @NotNull
    private Boolean isEv;

    @NotNull
    private Integer vehicleType;

    @NotNull
    private Integer productType;

    @NotNull
    private Integer weatherType;
}

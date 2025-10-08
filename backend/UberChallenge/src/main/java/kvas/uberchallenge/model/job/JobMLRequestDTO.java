package kvas.uberchallenge.model.job;

import jakarta.validation.constraints.*;
import kvas.uberchallenge.model.DriverRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class JobMLRequestDTO extends DriverRequestDTO {
    @NotNull
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private Double startLat;

    @NotNull
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private Double startLon;

    @NotNull
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private Double endLat;

    @NotNull
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private Double endLon;

    @NotNull
    @Min(0)
    @Max(23)
    private Integer startingTime;

    @NotNull
    private Integer paymentType;
}

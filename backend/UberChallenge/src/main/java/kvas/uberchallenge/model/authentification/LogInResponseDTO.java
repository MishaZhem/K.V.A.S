package kvas.uberchallenge.model.authentification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogInResponseDTO {
    private String username;
    private Double rating;
    private String earnerType;
    private Integer experienceMonths;
    private String fuelType;
    private Boolean isEv;
    private String vehicleType;
}

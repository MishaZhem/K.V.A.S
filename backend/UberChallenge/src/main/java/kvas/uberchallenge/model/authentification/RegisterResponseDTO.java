package kvas.uberchallenge.model.authentification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponseDTO {
    private UUID userId;
    private UUID driverId;
    private String username;
    private String earnerType;
    private String vehicleType;
    private String fuelType;
    private Boolean isEv;
}

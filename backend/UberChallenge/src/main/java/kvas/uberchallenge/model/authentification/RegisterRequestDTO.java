package kvas.uberchallenge.model.authentification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max=64, message = "Password must be from 6 to 64 characters long")
    private String password;

    @NotBlank(message = "Earner type is required")
    private String earnerType;
    @NotBlank(message = "Fuel type is required")
    private String fuelType;
    @NotBlank(message = "Vehicle type is required")
    private String vehicleType;
}


package kvas.uberchallenge.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {
    private String username;
    private String password;
    private Double rating;
    private String earnerType;
    private String fuelType;
    private String homeCity;
    private String vehicleType;
}


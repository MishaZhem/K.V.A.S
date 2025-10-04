package kvas.uberchallenge.model.job;

import kvas.uberchallenge.model.LocationDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobItemResponseDTO {
    private Double expectedDistanceFromPickUpToDropOff;
    private Double expectedDistanceFromDriverToPickUp;
    private Double expectedDurationFromPickUpToDropOff;
    private Double expectedDurationFromDriverToPickUp;
    private Double expectedEarnNet;
    private Double moneyPerHour;
}


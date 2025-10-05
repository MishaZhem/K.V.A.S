package kvas.uberchallenge.model.job;

import kvas.uberchallenge.model.LocationDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobItemResponseDTO {
    private Double expectedDistanceFromPickUpToDropOff;
    private Double expectedDurationFromPickUpToDropOff;
    private Double expectedEarnNet;
    private Double expectedDistanceFromDriverToPickUp;
    private Double expectedDurationFromDriverToPickUp;
    private Double moneyPerHour;
}


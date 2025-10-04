package kvas.uberchallenge.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteAssessmentRequestDTO {
    private LocationDTO currentDriverLoc;
    private LocationDTO pickupLoc;
    private LocationDTO dropoffLoc;
}


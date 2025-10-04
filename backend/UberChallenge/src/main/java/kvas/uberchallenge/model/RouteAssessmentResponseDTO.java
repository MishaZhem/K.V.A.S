package kvas.uberchallenge.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteAssessmentResponseDTO {
    private Integer predictedTimeMinutes;
    private Integer expectedEarnNetCents;
    private Integer moneyPerHourCents;
}

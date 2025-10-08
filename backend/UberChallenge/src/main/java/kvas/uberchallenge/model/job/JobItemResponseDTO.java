package kvas.uberchallenge.model.job;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobItemResponseDTO {

    @JsonProperty("dist_Order")
    private Double expectedDistanceFromPickUpToDropOff;

    @JsonProperty("time_Order")
    private Double expectedDurationFromPickUpToDropOff;

    @JsonProperty("earn_Order")
    private Double expectedEarnNet;

    @JsonProperty("dist_Pick")
    private Double expectedDistanceFromDriverToPickUp;

    @JsonProperty("time_Pick")
    private Double expectedDurationFromDriverToPickUp;

    @JsonProperty("moneyPerHour")
    private Double moneyPerHour;
}

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
    private UUID id;
    private LocationDTO from;
    private LocationDTO to;
    private Integer potentialEarningCents;
    private Integer earningRateCents;
    private Long startTimestamp;
}


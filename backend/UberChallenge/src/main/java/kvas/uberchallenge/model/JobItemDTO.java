package kvas.uberchallenge.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobItemDTO {
    private UUID id;
    private LocationDTO from;
    private LocationDTO to;
    private Integer potentialEarningCents;
    private Integer earningRateCents;
    private Long startTimestamp;
}


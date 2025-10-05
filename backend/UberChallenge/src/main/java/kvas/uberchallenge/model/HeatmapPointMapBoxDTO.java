package kvas.uberchallenge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
class HeatmapFeatureProperties {
    private Double sumProfit;
    private Integer hour;
    public HeatmapFeatureProperties(Double sumProfit, Integer hour) {
        this.sumProfit = sumProfit;
        this.hour = hour;
    }
}
@Data
class HeatmapFeatureGeometry {
    private final String type = "Point";
    private Double[] coordinates;
    public HeatmapFeatureGeometry(Double[] coordinates) {
        this.coordinates = coordinates;
    }
}


@Data
@NoArgsConstructor
@AllArgsConstructor
public class HeatmapPointMapBoxDTO {
    private final String type = "FeatureCollection";
    private HeatmapFeature[] features;
    // public HeatmapPointMapBoxDTO(HeatmapFeature[] features) {
    //     this.features = features;
    // }
}


package kvas.uberchallenge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
class HeatmapFeatureProperties {
    private Double sumProfit;
    public HeatmapFeatureProperties(Double sumProfit) {
        this.sumProfit = sumProfit;
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
class HeatmapFeature {
    private final String type = "Feature";
    private HeatmapFeatureProperties properties;
    private HeatmapFeatureGeometry geometry;
    public HeatmapFeature(HeatmapFeatureProperties properties, HeatmapFeatureGeometry geometry) {
        this.properties = properties;
        this.geometry = geometry;
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HeatmapPointMapBoxDTO {
    private final String type = "FeatureCollection";
    private HeatmapFeature[] features;
    public HeatmapPointMapBoxDTO(HeatmapResponseDTO raw) {
        this.features = new HeatmapFeature[raw.getPoints().size()];
        for (int i = 0; i < this.features.length; i++) {
            HeatmapPointDTO p = raw.getPoints().get(i);
            Double[] thingy = {p.getY(), p.getX()};
            HeatmapFeatureGeometry geo = new HeatmapFeatureGeometry(thingy);
            HeatmapFeatureProperties prop = new HeatmapFeatureProperties(p.getValue());
            HeatmapFeature f = new HeatmapFeature(prop, geo);
            this.features[i] = f;
        }
    }
}


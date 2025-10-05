package kvas.uberchallenge.model;

import kvas.uberchallenge.entity.HeatMapPoint;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HeatmapFeature {
    private final String type = "Feature";
    private HeatmapFeatureProperties properties;
    private HeatmapFeatureGeometry geometry;
    public HeatmapFeature(HeatMapPoint point, Integer i) {
        HeatmapFeatureProperties prop = new HeatmapFeatureProperties(point.getMoneyPerHour(), i);
        Double[] coords = {point.getLongitude(), point.getLatitude()};
        HeatmapFeatureGeometry geo = new HeatmapFeatureGeometry(coords);
        this.properties = prop;
        this.geometry = geo;
    }
    
}

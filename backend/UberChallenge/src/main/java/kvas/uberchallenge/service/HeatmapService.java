package kvas.uberchallenge.service;

import kvas.uberchallenge.entity.HeatMapPoint;
import kvas.uberchallenge.entity.enums.EarnerType;
import kvas.uberchallenge.model.heatmap.HeatmapFeature;
import kvas.uberchallenge.model.heatmap.HeatmapPointMapBoxDTO;
import kvas.uberchallenge.repository.DriverRepository;
import kvas.uberchallenge.repository.HeatMapPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class HeatmapService {
    private final DriverRepository driverRepository;
    private final HeatMapPointRepository heatMapPointRepository;

    public HeatmapPointMapBoxDTO getHeatmap(String username)
    {
        EarnerType earnerType = driverRepository.getDriverByUser_Username(username).get().getEarnerType();
        List<HeatmapFeature> allFeatures;
        List<HeatMapPoint> heatMapPoints = heatMapPointRepository.findAllByEarnerType(earnerType.getValue());
        allFeatures = heatMapPoints.stream()
                .map(p -> new HeatmapFeature(p, p.getHour()))
                .toList();

        HeatmapPointMapBoxDTO mapBoxDTO = new HeatmapPointMapBoxDTO(allFeatures.toArray(new HeatmapFeature[1]));
        return mapBoxDTO;
    }
}


package kvas.uberchallenge.service;

import kvas.uberchallenge.entity.Driver;
import kvas.uberchallenge.entity.HeatMapPoint;
import kvas.uberchallenge.entity.enums.EarnerType;
import kvas.uberchallenge.model.HeatmapFeature;
import kvas.uberchallenge.model.HeatmapPointDTO;
import kvas.uberchallenge.model.HeatmapPointMapBoxDTO;
import kvas.uberchallenge.model.HeatmapResponseDTO;
import kvas.uberchallenge.repository.DriverRepository;
import kvas.uberchallenge.repository.HeatMapPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import com.opencsv.CSVWriter;

import static java.util.stream.Collectors.toList;

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


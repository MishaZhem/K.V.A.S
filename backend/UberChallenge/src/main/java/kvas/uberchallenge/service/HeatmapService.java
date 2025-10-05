package kvas.uberchallenge.service;

import kvas.uberchallenge.entity.Driver;
import kvas.uberchallenge.entity.HeatMapPoint;
import kvas.uberchallenge.entity.enums.EarnerType;
import kvas.uberchallenge.model.HeatmapPointDTO;
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

@Service
@RequiredArgsConstructor
public class HeatmapService {
    private final DriverRepository driverRepository;
    private final HeatMapPointRepository heatMapPointRepository;

    public HeatmapResponseDTO getHeatmap(String username, Integer hour)
    {
        EarnerType earnerType = driverRepository.getDriverByUser_Username(username).get().getEarnerType();
        List<HeatMapPoint> heatMapPoints = heatMapPointRepository.findPointsByEarnerTypeAndTime(earnerType.getValue(), hour);

        return HeatmapResponseDTO.builder()
                .points(heatMapPoints.stream()
                        .map(point -> HeatmapPointDTO.builder()
                                .x(point.getLatitude())
                                .y(point.getLongitude())
                                .value(point.getMoneyPerHour())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}


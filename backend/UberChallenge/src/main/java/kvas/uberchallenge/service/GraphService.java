package kvas.uberchallenge.service;

import kvas.uberchallenge.entity.Driver;
import kvas.uberchallenge.model.GraphResponseDTO;
import kvas.uberchallenge.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GraphService {
    private final DriverRepository driverRepository;

    public GraphResponseDTO getGraphData(String username) {
        Driver driver = driverRepository.getDriverByUser_Username(username).get();



        return null;
    }
}

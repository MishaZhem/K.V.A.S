package kvas.uberchallenge.bootstrap;

import kvas.uberchallenge.entity.HeatMapPoint;
import kvas.uberchallenge.entity.Order;
import kvas.uberchallenge.entity.enums.PaymentType;
import kvas.uberchallenge.entity.enums.ProductType;
import kvas.uberchallenge.entity.enums.WeatherType;
import kvas.uberchallenge.repository.DriverRepository;
import kvas.uberchallenge.repository.HeatMapPointRepository;
import kvas.uberchallenge.repository.OrderRepository;
import kvas.uberchallenge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
@Slf4j
public class MockDataInitializer implements CommandLineRunner {
    private final OrderRepository orderRepository;
    private final HeatMapPointRepository heatMapPointRepository;
    private final PathMatchingResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

    @Override
    public void run(String... args) throws Exception {
        generateOrders();
        generateHeatMapData();
    }

    private void generateHeatMapData() {
        if (heatMapPointRepository.count() > 0) {
            log.info("HeatMap points already exist in the database. Skipping data creation.");
            return;
        }

        log.info("Generating HeatMap data from CSV files...");
        List<HeatMapPoint> heatMapPoints = new ArrayList<>();
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:heatmap/HeatMap-*-*.csv");

            for (Resource resource : resources) {
                String filename = resource.getFilename();
                log.info("Reading data from: {}", filename);

                // Parse filename: Assume format "HeatMap-<driverType>-<hour>.csv"
                String baseName = filename.replace("HeatMap-", "").replace(".csv", "");
                String[] parts = baseName.split("-");
                if (parts.length != 2) {
                    log.warn("Skipping file with invalid name format: {}", filename);
                    continue;
                }
                int earnerType = Integer.parseInt(parts[0]);
                int hour;
                try {
                    hour = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    log.warn("Skipping file with invalid hour in name: {}", filename);
                    continue;
                }

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                    String line;
                    reader.readLine(); // Skip header
                    while ((line = reader.readLine()) != null) {
                        String[] data = line.split(",");
                        if (data.length == 3) {
                            try {
                                HeatMapPoint point = HeatMapPoint.builder()
                                        .latitude(Double.parseDouble(data[0]))
                                        .longitude(Double.parseDouble(data[1]))
                                        .moneyPerHour(Double.parseDouble(data[2]))
                                        .earnerType(earnerType)
                                        .hour(hour)
                                        .build();
                                heatMapPoints.add(point);
                            } catch (NumberFormatException e) {
                                log.warn("Skipping malformed line in {}: {}", filename, line);
                            }
                        }
                    }
                }
            }

            if (!heatMapPoints.isEmpty()) {
                heatMapPointRepository.saveAll(heatMapPoints);
                log.info("Successfully created {} HeatMap points.", heatMapPoints.size());
            } else {
                log.info("No HeatMap data found to import.");
            }

        } catch (Exception e) {
            log.error("Failed to read or process HeatMap CSV files.", e);
        }
    }

    private void generateOrders() throws IOException
    {
        if (orderRepository.count() > 0) {
            log.info("Orders already exist in the database. Skipping mock data creation.");
            return;
        }

        log.info("Generating mock orders...");
        List<Order> mockOrders = new ArrayList<>();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        ProductType[] productTypes = ProductType.values();
        PaymentType[] paymentTypes = PaymentType.values();
        WeatherType[] weatherTypes = WeatherType.values();
        // Read from classpath instead of filesystem
        Resource dataGraphResource = resourceResolver.getResource("classpath:data_graph.csv");
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(dataGraphResource.getInputStream()))) {
            lines = reader.lines().toList();
        }

        for (int i = 0; i < 200; i++) {
            String line = lines.get(random.nextInt(lines.size()));
            String[] parts = line.split(",");

            double startLat = Double.parseDouble(parts[4]);
            double startLon = Double.parseDouble(parts[5]);
            double endLat = Double.parseDouble(parts[6]);
            double endLon = Double.parseDouble(parts[7]);

            double startLatOffset = (random.nextDouble() - 0.5);
            double startLonOffset = (random.nextDouble() - 0.5);
            double endLatOffset = (random.nextDouble() - 0.5);
            double endLonOffset = (random.nextDouble() - 0.5);

            Order order = Order.builder()
                    .startLat(startLat + startLatOffset)
                    .startLon(startLon + startLonOffset)
                    .endLat(endLat + endLatOffset)
                    .endLon(endLon + endLonOffset)
                    .startingTime(random.nextInt(0, 23))
                    .productType(productTypes[random.nextInt(productTypes.length)])
                    .paymentType(paymentTypes[random.nextInt(paymentTypes.length)])
                    .weatherType(weatherTypes[random.nextInt(weatherTypes.length)])
                    .build();
            mockOrders.add(order);
        }

        orderRepository.saveAll(mockOrders);
        log.info("Successfully created {} mock orders.", mockOrders.size());
    }
}

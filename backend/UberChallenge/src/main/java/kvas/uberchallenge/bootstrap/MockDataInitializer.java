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
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
@Slf4j
public class MockDataInitializer implements CommandLineRunner {

    private final OrderRepository orderRepository;
    private final HeatMapPointRepository heatMapPointRepository;

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
            Resource[] resources = resolver.getResources("classpath:StaticHeatMap/HeatMap-*-*.csv");

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
                int productType = Integer.parseInt(parts[0]);
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
                                        .productType(productType)
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

    private void generateOrders()
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

        for (int i = 0; i < 500; i++) {
            Order order = Order.builder()
                    .startLat(random.nextDouble(50.00000, 53.00000))
                    .startLon(random.nextDouble(4.00000, 7.00000))
                    .endLat(random.nextDouble(50.00000, 53.00000))
                    .endLon(random.nextDouble(4.00000, 7.00000))
                    .startingTime(random.nextInt(0, 5))
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

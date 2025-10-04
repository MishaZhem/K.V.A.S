package kvas.uberchallenge.bootstrap;

import kvas.uberchallenge.entity.Order;
import kvas.uberchallenge.entity.enums.PaymentType;
import kvas.uberchallenge.entity.enums.ProductType;
import kvas.uberchallenge.entity.enums.WeatherType;
import kvas.uberchallenge.repository.DriverRepository;
import kvas.uberchallenge.repository.OrderRepository;
import kvas.uberchallenge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
@Slf4j
public class MockDataInitializer implements CommandLineRunner {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;

    @Override
    public void run(String... args) throws Exception {
        generateOrders();
        generateDrivers();
    }

    private void generateDrivers()
    {
        if (userRepository.count() > 0 && driverRepository.count() > 0) {
            log.info("Users or Drivers already exist in the database. Skipping mock data creation.");
            return;
        }

        log.info("Generating mock...");
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

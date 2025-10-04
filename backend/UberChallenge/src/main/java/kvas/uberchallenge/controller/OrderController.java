package kvas.uberchallenge.controller;

import kvas.uberchallenge.entity.Order;
import kvas.uberchallenge.entity.enums.PaymentType;
import kvas.uberchallenge.entity.enums.ProductType;
import kvas.uberchallenge.entity.enums.WeatherType;
import kvas.uberchallenge.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderRepository orderRepository;

}

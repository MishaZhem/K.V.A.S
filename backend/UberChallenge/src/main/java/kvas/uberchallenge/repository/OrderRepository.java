package kvas.uberchallenge.repository;

import kvas.uberchallenge.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    @Query(value = "SELECT * FROM orders " +
            "WHERE acos(sin(:driverLat * pi() / 180) * sin(start_lat * pi() / 180) + " +
            "           cos(:driverLat * pi() / 180) * cos(start_lat * pi() / 180) * " +
            "           cos(start_lon * pi() / 180 - :driverLon * pi() / 180)) * 6371 < :radius",
            nativeQuery = true)
    List<Order> findNearbyOrders(@Param("driverLat") double driverLat,
                                 @Param("driverLon") double driverLon,
                                 @Param("radius") double radius);
}


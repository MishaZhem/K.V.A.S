package kvas.uberchallenge.repository;

import kvas.uberchallenge.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RepositoryRestResource(path = "orders", collectionResourceRel = "orders")
public interface OrderRepository extends JpaRepository<Order, UUID> {
    @Query(value = "SELECT * FROM orders o " +
            "WHERE acos(sin(:driverLat * pi() / 180) * sin(o.start_lat * pi() / 180) + " +
            "           cos(:driverLat * pi() / 180) * cos(o.start_lat * pi() / 180) * " +
            "           cos(o.start_lon * pi() / 180 - :driverLon * pi() / 180)) * 6371 < :radius " +
            "AND CASE " +
            "    WHEN :earnerType = 0 THEN o.product_type IN (0, 1, 2, 3) " +
            "    WHEN :earnerType = 1 THEN o.product_type = 4 " +
            "    ELSE FALSE " +
            "END",
            nativeQuery = true)
    List<Order> findNearbyOrders(@Param("driverLat") double driverLat,
                                 @Param("driverLon") double driverLon,
                                 @Param("radius") double radius,
                                 @Param("earnerType") int earnerType);
}

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
    List<Order> findByCityId(String cityId);

    @Query("SELECT o FROM Order o WHERE o.cityId = :cityId")
    List<Order> findOrdersByCityId(@Param("cityId") String cityId);
}


package kvas.uberchallenge.repository;

import kvas.uberchallenge.entity.HeatMapPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HeatMapPointRepository extends JpaRepository<HeatMapPoint, UUID> {
    @Query(value = "SELECT * FROM heat_map hm " +
            "WHERE CASE " +
            "    WHEN :earnerType = 0 THEN hm.product_type IN (0, 1, 2, 3) " +
            "    WHEN :earnerType = 1 THEN hm.product_type = 4 " +
            "    ELSE FALSE " +
            "END " +
            "AND hm.hour = :hour",
            nativeQuery = true)
    List<HeatMapPoint> findPointsByEarnerTypeAndTime(@Param("earnerType") int earnerType,
                                                     @Param("hour") int hour);
}

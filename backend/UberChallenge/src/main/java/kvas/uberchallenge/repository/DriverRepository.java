package kvas.uberchallenge.repository;

import ch.qos.logback.classic.util.LogbackMDCAdapter;
import kvas.uberchallenge.entity.Driver;
import kvas.uberchallenge.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DriverRepository extends JpaRepository<Driver, UUID> {
    Optional<Driver> getDriverByUser_Username(String userUsername);

    String user(User user);
}


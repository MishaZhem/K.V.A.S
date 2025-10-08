package kvas.uberchallenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class UberChallengeApplication {

    public static void main(String[] args) throws Exception
    {
        SpringApplication.run(UberChallengeApplication.class, args);
    }

}

package kvas.uberchallenge;

import kvas.uberchallenge.config.PythonDependencyInstaller;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UberChallengeApplication {

    public static void main(String[] args) throws Exception
    {
        //new PythonDependencyInstaller().run();
        SpringApplication.run(UberChallengeApplication.class, args);
    }

}

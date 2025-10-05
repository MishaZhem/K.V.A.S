package kvas.uberchallenge.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

@Component
public class PythonDependencyInstaller implements CommandLineRunner {
    private static final String PYTHON_EXECUTABLE = System.getProperty("python.executable.path", "/usr/bin/python3");

    @Override
    public void run(String... args) throws Exception {
        // Log the Python executable being used
        System.out.println("Using Python executable: " + PYTHON_EXECUTABLE);

        // Get requirements.txt from classpath
        ClassPathResource requirementsResource = new ClassPathResource("python/requirements.txt");
        if (!requirementsResource.exists()) {
            System.err.println("requirements.txt not found in classpath:python/");
            return;
        }

        File requirementsFile = requirementsResource.getFile();
        System.out.println("Installing Python dependencies from " + requirementsFile.getAbsolutePath() + "...");

        // Run pip install
        ProcessBuilder processBuilder = new ProcessBuilder(
                PYTHON_EXECUTABLE,
                "-m",
                "pip",
                "install",
                "-r",
                requirementsFile.getAbsolutePath()
        );
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode == 0) {
            System.out.println("Python dependencies installed successfully.");
        } else {
            System.err.println("Failed to install Python dependencies. Exit code: " + exitCode);
            throw new RuntimeException("Python dependency installation failed.");
        }
    }
}
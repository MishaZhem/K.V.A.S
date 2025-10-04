package kvas.uberchallenge.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

@Component
public class PythonDependencyInstaller implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        String pythonResourcesPath = "src/main/resources/python";
        File pythonDir = new File(pythonResourcesPath);

        if (!pythonDir.exists() || !pythonDir.isDirectory()) {
            System.err.println("Python resource directory not found at: " + pythonDir.getAbsolutePath());
            return;
        }

        File requirementsFile = new File(pythonDir, "requirments.txt");
        if (!requirementsFile.exists()) {
            System.err.println("requirments.txt not found in: " + pythonDir.getAbsolutePath());
            return;
        }

        System.out.println("Installing python dependencies from requirments.txt...");

        ProcessBuilder processBuilder = new ProcessBuilder("../../../../.venv/bin/python", "-m", "pip", "install", "-r", "requirments.txt");
        processBuilder.directory(pythonDir);
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
            System.err.println("Failed to install python dependencies. Exit code: " + exitCode);
        }
    }
}

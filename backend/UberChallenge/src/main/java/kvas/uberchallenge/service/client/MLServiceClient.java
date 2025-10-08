package kvas.uberchallenge.service.client;

import kvas.uberchallenge.model.DriverRequestDTO;
import kvas.uberchallenge.model.GraphResponseDTO;
import kvas.uberchallenge.model.job.JobListResponseDTO;
import kvas.uberchallenge.model.job.JobMLRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(
        name = "ml-service",
        url = "${ML_SERVICE_URL:http://python-server:5000}",
        configuration = MLServiceClientConfig.class
)
public interface MLServiceClient {

    @PostMapping("/api/ml/jobs")
    public ResponseEntity<JobListResponseDTO> analyzeJobs(@RequestBody List<JobMLRequestDTO> requests);

    @PostMapping("/api/ml/graph")
    public ResponseEntity<GraphResponseDTO> predictGraph(@RequestBody DriverRequestDTO requests);
}

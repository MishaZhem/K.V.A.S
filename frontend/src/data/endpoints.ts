const endpoints = {
    login: "http://localhost:8082/api/auth/login", // POST
    signup: "http://localhost:8082/api/auth/register",
    jobs: {
        // POST get all jobs within 2kms from the user.
        view: "http://localhost:8082/api/driver/jobs", // POST

    // GET the next recommended job (highest rating)
    take: "http://localhost:8082/api/jobs/take", // GET
  },
  getHeatmap: "http://localhost:8082/api/driver/heatmap",
  getGraph: "http://localhost:8082/api/driver/graph",
};

export default endpoints;

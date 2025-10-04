const endpoints = {
    login: "http://localhost:8082/api/auth/login", // POST
    jobs: {
        // POST get all jobs within 2kms from the user.
        view: "http://localhost:8082/api/jobs/view?r=2km", // POST

        // GET the next recommended job (highest rating)
        take: "http://localhost:8082/api/jobs/take", // GET
    },
};

export default endpoints;
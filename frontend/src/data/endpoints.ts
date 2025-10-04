const endpoints = {
    login: "/api/driver/login", // POST
    jobs: {
        // POST get all jobs within 2kms from the user.
        view: "/api/jobs/view?r=2km", // POST

        // GET the next recommended job (highest rating)
        take: "/api/jobs/take", // GET
    },
};

export default endpoints;
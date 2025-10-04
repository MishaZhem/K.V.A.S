import { use, useEffect, useState } from "react";
import DriverLoginWidget from "../../components/DriverLogin";
import type { UserContext } from "../../types/userContext";
import jobs_test from "../../data/test_jobs.json";
import Jobs from "../../components/Jobs";
import type { JobItem } from "../../types/job";
import endpoints from "../../data/endpoints";
const Home = () => {
    const [userContext, setUserContext] =  useState<UserContext | undefined>(undefined);
    const [jobs, setJobs] =  useState<JobItem[]>([]);
    useEffect(() => {
        if (userContext && userContext.username === "Admin") {
            setJobs(jobs_test);
            return;
        }
        if (userContext) {
            fetch(endpoints.jobs.view, {
                method: "GET",
                mode: "cors",
            }).then((v) => v.json()).then((v) => setJobs(v as JobItem[]))
        }
    }, [userContext])
    return (
        <div className="text-3xl font-bold underline">
            <h1>Hi, {userContext ? userContext.username : "nobody"}</h1>
            <DriverLoginWidget updateUserContext={(context) => {
                setUserContext(context);
            }}></DriverLoginWidget>
            <Jobs jobs={jobs}></Jobs>
        </div>
    )
}

export default Home;

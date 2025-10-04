import { useEffect, useState } from "react";
import jobs_test from "../data/test_jobs.json";
import { uber } from "../assets";
import { HeatMap, TopMenu } from "../components";
import type { UserContextType } from "../types/userContext";
import type { JobItem } from "../types/job";
import endpoints from "../data/endpoints";
import Jobs from "../components/Jobs";
import Profile from "../components/Profile";

const Map = ({user}: {user: UserContextType | undefined}) => {
    const [jobs, setJobs] =  useState<JobItem[]>([]);
    const [jobsShown, setJobsShown] =  useState(false);
    const [profileShown, setProfileShown] =  useState(false);    
    useEffect(() => {
        if (user && user.username === "admin") {
            setJobs(jobs_test);
            return;
        }
        if (user) {
            fetch(endpoints.jobs.view, {
                method: "GET",
                mode: "cors",
            }).then((v) => v.json()).then((v) => setJobs(v as JobItem[]))
        }
    }, [user])
    if (user) {
    return (
        <div className="relative">
            <HeatMap />
            <img src={uber} className="absolute left-10 top-10 w-32 opacity-30" alt="" />
            <TopMenu userContext={user} jobsRendered={jobsShown} profileRendered={profileShown} shouldRenderJobs={setJobsShown} shouldRenderProfile={setProfileShown} />
            <Jobs shown={jobsShown} jobs={jobs}></Jobs>
            <Profile shown={profileShown} userContext={user}></Profile>
        </div>
    )} else {
        return <div>ERROR Not logged in!!</div>
    }
}

export default Map;

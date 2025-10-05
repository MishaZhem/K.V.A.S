import { useEffect, useState } from "react";
import type { UserContextType } from "../types/userContext";
import jobs_test from "../data/test_jobs.json";
import Jobs from "../components/Jobs";
import type { JobItem } from "../types/job";
import endpoints from "../data/endpoints";
import Navbar from "../components/Navbar";
import Profile from "../components/Profile";
const Home = ({user}: {user: UserContextType | undefined}) => {

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
        
        <div className="bg-black h-screen">
            <div className="fixed right-0 w-1/4 bg-black shadow-lg z-50 flex flex-col gap-4 overflow-y-auto h-1/2">
                <div className="p-4">
                    <Navbar userContext={user} jobsRendered={jobsShown} profileRendered={profileShown} shouldRenderJobs={setJobsShown} shouldRenderProfile={setProfileShown}></Navbar>
                    <Jobs shown={jobsShown} jobs={jobs}></Jobs>
                    <Profile shown={profileShown} userContext={user}></Profile>
                </div>
            </div>
        </div>
    );
} else {
        <div>ERROR Not logged in!!</div>
    }
}

export default Home;

import { useEffect, useState } from "react";
import { uber } from "../assets";
import { HeatMap, TopMenu } from "../components";
import type { UserContextType } from "../types/userContext";
import type { JobItem } from "../types/job";
import endpoints from "../data/endpoints";
import Jobs from "../components/Jobs";
import Profile from "../components/Profile";
import { AnimatePresence } from "framer-motion";

const Map = ({ user }: { user: UserContextType | undefined }) => {
    const [jobs, setJobs] = useState<JobItem[]>([]);
    const [jobsShown, setJobsShown] = useState(false);
    const [profileShown, setProfileShown] = useState(false);
    useEffect(() => {
        // if (user && user.username === "admin") {
        //     setJobs(jobs_test);
        //     return;
        // }
        if (user) {
            if (window.localStorage.getItem('uberapp-jwt') === null) { return; }
            const token = window.localStorage.getItem('uberapp-jwt') as string;
            fetch(`${endpoints.jobs.view}?currentLat=52&currentLon=4`, {
                method: "GET",
                mode: "cors",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": user.loginToken,
                }
            }).then((v) => v.json()).then((v) => {
                if (!v.jobs) { setJobs([]); return; }
                setJobs(v.jobs as JobItem[]);
            })
        }
    }, [user])
    if (user) {
      if (window.localStorage.getItem('uberapp-jwt') === null) { return; }
      const token = window.localStorage.getItem('uberapp-jwt') as string;
      fetch(endpoints.jobs.view, {
        method: "GET",
        mode: "cors",
        headers: {
          "Authorization": token,
        }
      }).then((v) => v.json()).then((v) => {
        if (v.jobs === null) { setJobs([]); return; }
        setJobs(v.jobs as JobItem[]);
      })
    }
  }, [user])
  if (user) {
    return (
      <div className="relative">
        <HeatMap username={user.username} userToken={user.loginToken} />
        <img src={uber} className="absolute left-10 top-10 w-32 opacity-30" alt="" />
        <AnimatePresence mode="wait">

          <div className="flex flex-col">
            <TopMenu userContext={user} jobsRendered={jobsShown} profileRendered={profileShown} shouldRenderJobs={setJobsShown} shouldRenderProfile={setProfileShown} />
            <Jobs shown={jobsShown} jobs={jobs}></Jobs>
            <Profile shown={profileShown} userContext={user}></Profile>
          </div>

        </AnimatePresence>
      </div >
    )
  } else {
    return <div>ERROR Not logged in!!</div>
  }
}

export default Map;

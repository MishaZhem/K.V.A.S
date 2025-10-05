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
  const [latitude, setLatitude] = useState<number | null>(null);
  const [longitude, setLongitude] = useState<number | null>(null);

  useEffect(() => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          setLatitude(position.coords.latitude);
          setLongitude(position.coords.longitude);
        },
        (err) => {
          setLatitude(52.370216);
          setLongitude(4.895168);
        }
      );
    } else {
      setLatitude(52.370216);
      setLongitude(4.895168);
    }
  }, []);

  useEffect(() => {
    if (user) {
      if (window.localStorage.getItem('uberapp-jwt') === null) { return; }
      if (latitude === null || longitude === null) return;
      const token = window.localStorage.getItem('uberapp-jwt') as string;
      fetch(`${endpoints.jobs.view}?currentLat=${latitude}&currentLon=${longitude}`, {
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
  }, [user, longitude, latitude])

  if (user) {
    return (
      <div className="relative">
        <HeatMap username={user.username} userToken={user.loginToken} />
        <img src={uber} className="absolute hidden small:block left-10 top-10 w-32 opacity-30" alt="" />
        <AnimatePresence mode="wait">

          <div className="flex flex-col">
            <TopMenu userContext={user} jobs={jobs} />
            {/* <Jobs shown={jobsShown} jobs={jobs}></Jobs> */}
            {/* <Profile shown={profileShown} userContext={user}></Profile> */}
          </div>

        </AnimatePresence>
      </div >
    )
  } else {
    return <div>ERROR Not logged in!!</div>
  }
}

export default Map;

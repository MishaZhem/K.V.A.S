import { useState, useEffect, useRef } from "react";
import { motion, type Transition } from "framer-motion";
import { user } from "../assets";
import type { UserContextType } from "../types/userContext";
import type { JobItem } from "../types/job"; // Make sure the path is correct

const layoutSpring: Transition = {
  type: "spring",
  stiffness: 500,
  damping: 35,
};

// Dummy data for the job list
const dummyJobs: JobItem[] = [
  {
    id: "1",
    from: { lat: 34.0522, lon: -118.2437 }, // Los Angeles
    to: { lat: 34.1522, lon: -118.3437 }, // North of LA
    potentialEarningCents: 2500, // $25.00
    startTimestamp: Date.now() + 1000 * 60 * 5, // 5 minutes from now
  },
  {
    id: "2",
    from: { lat: 40.7128, lon: -74.006 }, // New York City
    to: { lat: 40.7528, lon: -73.986 }, // Midtown Manhattan
    potentialEarningCents: 1850, // $18.50
    startTimestamp: Date.now() + 1000 * 60 * 10, // 10 minutes from now
  },
  {
    id: "3",
    from: { lat: 41.8781, lon: -87.6298 }, // Chicago
    to: { lat: 41.9081, lon: -87.6598 }, // North side of Chicago
    potentialEarningCents: 3200, // $32.00
    startTimestamp: Date.now() + 1000 * 60 * 15, // 15 minutes from now
  },
];

const JobList = ({ jobs }: { jobs: JobItem[] }) => (
  <div>
    {jobs.map(job => (
      <div key={job.id} className="mb-3 p-3 bg-[#2a2a2a] rounded-lg">
        <p className="font-semibold text-white_primary">
          From: {job.from.lat.toFixed(2)}, {job.from.lon.toFixed(2)}
        </p>
        <p className="font-semibold text-white_primary">
          To: {job.to.lat.toFixed(2)}, {job.to.lon.toFixed(2)}
        </p>
        <p className="text-green-400">
          Potential Earning: ${(job.potentialEarningCents / 100).toFixed(2)}
        </p>
        <p className="text-white_secondary/80 text-sm">
          Starts: {new Date(job.startTimestamp).toLocaleTimeString()}
        </p>
      </div>
    ))}
  </div>
);

export default function TopMenu({
  userContext,
}: {
  userContext: UserContextType;
}) {
  const [activeView, setActiveView] = useState<"jobs" | "profile" | null>(
    null
  );
  const menuRef = useRef<HTMLDivElement>(null);

  const toggleView = (view: "jobs" | "profile") =>
    setActiveView(prev => (prev === view ? null : view));

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(e.target as Node)) {
        setActiveView(null);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  return (
    <motion.div
      ref={menuRef}
      layout
      transition={layoutSpring}
      className="text-[#ABABAB] bg-[#171717] absolute right-10 top-10 shadow-xl/30 overflow-hidden border-2 border-[#434343]"
      style={{ borderRadius: 24 }}
    >
      <motion.div layout="position" className="p-3 pl-4 font-mono">
        <div className="flex gap-4 justify-around items-center">
          <button
            onClick={() => toggleView("jobs")}
            className={`transition-colors hover:text-white_primary duration-300 ${activeView === "jobs"
                ? "text-white_primary"
                : "text-white_secondary"
              }`}
          >
            {activeView === "jobs" ? "> jobs <" : "[ jobs ]"}
          </button>

          <button
            onClick={() => toggleView("profile")}
            className={`transition-colors hover:text-white_primary duration-300 ${activeView === "profile"
                ? "text-white_primary"
                : "text-white_secondary"
              }`}
          >
            {activeView === "profile" ? "> profile <" : "[ profile ]"}
          </button>

          <img src={user} className="w-8 opacity-80" alt="user avatar" />
        </div>
      </motion.div>

      {activeView && (
        <motion.div
          key={activeView}
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          transition={{ duration: 0.2 }}
        >
          <hr className="border-[#434343] mx-5 mb-2" />

          {activeView === "profile" && (
            <div className="font-sans p-2 pt-0 pl-5 pr-4 pb-4">
              <h2 className="font-bold">Profile and settings</h2>
              <p className="mt-2">Name: {userContext.username}</p>
              <button className="opacity-70">Change name</button>
              <hr className="border-[#434343] my-2" />
              <p>
                You are registered as a{" "}
                {userContext.isCourier ? "courier" : "driver"} <br /> at Uber.
              </p>
              <button className="opacity-70">Change registration</button>
            </div>
          )}

          {activeView === "jobs" && (
            <div className="font-sans p-2 pt-0 pl-5 pr-4 pb-4">
              <h2 className="font-bold mb-2">Available Jobs</h2>
              {dummyJobs.length > 0 ? (
                <JobList jobs={dummyJobs} />
              ) : (
                <p className="text-white_secondary/80">
                  No jobs available right now.
                </p>
              )}
            </div>
          )}
        </motion.div>
      )}
    </motion.div>
  );
}

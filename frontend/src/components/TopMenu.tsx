
import { useState } from "react";
import { motion, type Transition } from "framer-motion";
import { user } from "../assets";
import type { UserContextType } from "../types/userContext";
import type { JobItem } from "../types/job";
import JobItemDisplay from "./JobItem";

// A slightly softer spring for a smoother layout animation
const layoutSpring: Transition = { type: "spring", stiffness: 500, damping: 35 };

const JobList = ({ jobs }: { jobs: JobItem[] }) => (
  <div>
    {jobs.map((job, index) => (
      <JobItemDisplay jobs={jobs} jobi={index} />
    ))}
  </div>
);


export default function TopMenu({ userContext, jobs }: { userContext: UserContextType, jobs: JobItem[] }) {
  const [activeView, setActiveView] = useState<"jobs" | "profile" | null>(null);

  const toggleView = (view: "jobs" | "profile") =>
    setActiveView(prev => (prev === view ? null : view));

  return (
    <motion.div
      layout
      transition={layoutSpring}
      className="text-[#ABABAB] bg-[#171717] absolute w-max centerMes top-10 shadow-xl/30 overflow-hidden border-2 border-[#434343]"
      style={{ borderRadius: 24 }}
    >
      <motion.div layout="position" className="p-3 pl-4 font-mono">
        <div className="flex gap-4 opacity-70 justify-around items-center">
          <button onClick={() => toggleView("jobs")}>
            {activeView === "jobs" ? "> jobs <" : "[ jobs ]"}
          </button>
          <button onClick={() => toggleView("profile")}>
            {activeView === "profile" ? "> profile <" : "[ profile ]"}
          </button>
          <img src={user} className="w-8" alt="user avatar" />
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
            <div className="p-2 pt-0 pl-5 pr-4 pb-4">
              <h2 className="font-bold">Profile and settings</h2>
              <p className="mt-2">Name: {userContext.username}</p>
              <p className="mt-2">Type: {userContext.isCourier ? "Courier" : "Driver"}</p>
              <hr className="border-[#434343] my-2" />
              <p>
                You are registered as a {userContext.isCourier ? "courier" : "driver"} <br /> at Uber.
              </p>
            </div>
          )}

          {activeView === "jobs" && (
            <div className="font-sans p-2 pt-0 pl-5 pr-4 pb-4">
              <h2 className="font-bold mb-2">Available Jobs</h2>
              {jobs.length > 0 ? (
                <JobList jobs={jobs} />
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

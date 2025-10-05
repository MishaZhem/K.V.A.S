import { useState, useRef, useEffect } from "react";
import { motion, type Transition } from "framer-motion";
import { user } from "../assets";
import type { UserContextType } from "../types/userContext";
import type { JobItem } from "../types/job";
import JobItemDisplay from "./JobItem";

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
      className="text-[#ABABAB] bg-[#171717] absolute w-max centerMes top-10 shadow-xl/30 overflow-hidden border-2 border-[#434343]"
      style={{ borderRadius: 24 }}
    >
      <motion.div layout="position" className="p-3 pl-4 font-mono">
        <div className="flex gap-4 justify-around items-center">
          <button
            onClick={() => toggleView("jobs")}
            className={activeView === "jobs" ? "opacity-100 font-bold" : "opacity-70"}
          >
            {activeView === "jobs" ? "> jobs <" : "[ jobs ]"}
          </button>
          <button
            onClick={() => toggleView("profile")}
            className={activeView === "profile" ? "opacity-100 font-bold" : "opacity-70"}
          >
            {activeView === "profile" ? "> profile <" : "[ profile ]"}
          </button>
          <img src={user} className="w-8 opacity-70" alt="user avatar" />
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
            <div className="p-2 pt-0 pl-5 pr-4 pb-4 text-white_primary">
              <p className="mt-2 font-bold">Name: <span className="font-mono font-light">{userContext.username}</span></p>
              <p className="mt-2 font-bold">Type: <span className="font-mono font-light">{userContext.isCourier ? "Courier" : "Driver"}</span></p>
              <hr className="border-[#434343] my-2" />
              <p className="text-white_secondary/80">
                You are registered as a {userContext.isCourier ? "courier" : "driver"} <br /> at Uber.
              </p>
            </div>
          )}

          {activeView === "jobs" && (
            <div className="font-sans p-2 pt-0 pl-5 pr-4 pb-4">
              <h2 className="font-bold text-white_primary mb-2">Available Jobs</h2>
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
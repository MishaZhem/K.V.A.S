import { useState, useEffect, useRef } from "react";
import { motion, type Transition } from "framer-motion";
import { user } from "../assets";
import type { UserContextType } from "../types/userContext";

const layoutSpring: Transition = { type: "spring", stiffness: 500, damping: 35 };

export default function TopMenu({ userContext }: { userContext: UserContextType }) {
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
      className="text-[#ABABAB] bg-[#171717] absolute right-10 top-10 shadow-xl/30 overflow-hidden border-2 border-[#434343]"
      style={{ borderRadius: 24 }}
    >
      <motion.div layout="position" className="p-3 pl-4 font-mono">
        <div className="flex gap-4 justify-around items-center">
          <button
            onClick={() => toggleView("jobs")}
            className={`transition-colors hover:text-white_primary duration-300 ${activeView === "jobs" ? "text-white_primary" : "text-white_secondary"
              }`}
          >
            {activeView === "jobs" ? "> jobs <" : "[ jobs ]"}
          </button>

          <button
            onClick={() => toggleView("profile")}
            className={`transition-colors hover:text-white_primary duration-300 ${activeView === "profile" ? "text-white_primary" : "text-white_secondary"
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
                You are registered as a {userContext.isCourier ? "courier" : "driver"} <br /> at Uber.
              </p>
              <button className="opacity-70">Change registration</button>
            </div>
          )}

          {activeView === "jobs" && (
            <div className="font-sans p-2 pt-0 pl-5 pr-4 pb-4">
              <h2 className="font-bold">Available Jobs</h2>
              <p className="text-white_secondary/80">No jobs available right now.</p>
            </div>
          )}
        </motion.div>
      )}
    </motion.div>
  );
}

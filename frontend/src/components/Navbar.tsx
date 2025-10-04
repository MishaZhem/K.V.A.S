import { useState } from "react";
import type { UserContextType } from "../types/userContext";

const Navbar = ({userContext, shouldRenderJobs, jobsRendered, shouldRenderProfile, profileRendered}: {
    userContext: UserContextType, 
    shouldRenderJobs: (b: boolean) => void, 
    shouldRenderProfile: (b: boolean) => void,
    jobsRendered: boolean;
    profileRendered: boolean;
}) => {
    const [activePageIndex, setActivePageIndex] = useState<number | null>(null);
    return (
        <div className="navbar bg-gray-900 text-gray-400 shadow-lg rounded-4xl border-gray-700 border-5 p-2 pl-8 pr-8">
            <span className="navbar-items flex justify-evenly">
            <button onClick={() => {
                shouldRenderJobs(!jobsRendered); 
                shouldRenderProfile(false); 
                setActivePageIndex(0)}
                }>{jobsRendered ? "> jobs <" : "[ jobs ]"}</button>
            <button onClick={() => {
                shouldRenderProfile(!profileRendered); 
                shouldRenderJobs(false); 
                setActivePageIndex(1)}
                }>{profileRendered ? "> profile <" : "[ profile ]"}</button>
            <text>{userContext.username}</text></span>
        </div>
    )
}

export default Navbar;
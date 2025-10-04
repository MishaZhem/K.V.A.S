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
        <div className="navbar bg-[#171717] absolute rounded-[999px] border-3 border-[#434343] right-10 top-10 p-2 pl-3 shadow-xl/30">
            <span className="navbar-items flex gap-3 items-center opacity-70">
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
import { useState } from "react";
import { user } from "../assets";
import type { UserContextType } from "../types/userContext";

const TopMenu = ({ userContext, shouldRenderJobs, jobsRendered, shouldRenderProfile, profileRendered }: {
    userContext: UserContextType,
    shouldRenderJobs: (b: boolean) => void,
    shouldRenderProfile: (b: boolean) => void,
    jobsRendered: boolean;
    profileRendered: boolean;
}) => {
    const [activePageIndex, setActivePageIndex] = useState<number | null>(null);
    return (
        <>
            <div className="text-[#ABABAB] bg-[#171717] absolute rounded-[999px] border-3 border-[#434343] right-10 top-10 p-2 pl-4 shadow-xl/30">
                <div className="pClass flex gap-4 opacity-70 justify-around items-center">
                    <button onClick={() => {
                        shouldRenderJobs(!jobsRendered);
                        shouldRenderProfile(false);
                        setActivePageIndex(0)
                    }
                    }>{jobsRendered ? "> jobs <" : "[ jobs ]"}</button>
                    <button onClick={() => {
                        shouldRenderProfile(!profileRendered);
                        shouldRenderJobs(false);
                        setActivePageIndex(1)
                    }
                    }>{profileRendered ? "> profile <" : "[ profile ]"}</button>
                    <img src={user} className="w-8" alt="" />
                </div>
            </div>
        </>
    );
};

export default TopMenu;
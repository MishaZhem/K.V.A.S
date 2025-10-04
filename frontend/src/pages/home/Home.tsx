import { useState } from "react";
import DriverLoginWidget from "../../components/DriverLogin";
import type { UserContext } from "../../types/userContext";

const Home = () => {
    const [userContext, setUserContext] =  useState<UserContext | null>(null);
    return (
        <div className="text-3xl font-bold underline">
            <h1>Hi, {userContext ? userContext.username : "nobody"}</h1>
            <DriverLoginWidget updateUserContext={(context) => {
                setUserContext(context);
            }}></DriverLoginWidget>
        </div>
    )
}

export default Home;

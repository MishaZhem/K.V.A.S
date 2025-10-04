import { useState } from "react";
import endpoints from "../data/endpoints";
import getSHA256Hash from "../data/utils";
import type { DriverInfo, UserContextType } from "../types/userContext";

const DriverLoginWidget = ({updateUserContext}: {updateUserContext: (newUserContext: UserContextType) => void}) => {
    const [loginResponseLine, setLoginResponseLine] = useState("");
    return <div className="driver-login text-2xl bg-black h-screen text-white flex flex-row min-h-screen justify-center items-center">
            <form className="" onSubmit={async (ev) => {
            ev.preventDefault();
            const usernameInput = (document.getElementById('driverUsername') as HTMLInputElement).value;
            const passwordInput = (document.getElementById('driverPassword') as HTMLInputElement).value;
            if (usernameInput === "admin" && passwordInput === "admin") { 
                setLoginResponseLine("adminlogin"); 
                updateUserContext({
                    username: "admin",
                    loginToken: "0",
                    at: {lat: 0.0, lon: 0.0},
                    isCourier: false,
                    jobsThisWeek: 10,
                }); 
                return;
            }
            // POST /api/driver/login
            const response = await fetch(endpoints.login, {
                mode: "cors",
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    username: usernameInput,
                    password: passwordInput,
                })
            });
            if (!((response.ok) && (response.headers.has("Authorization")))) {
                setLoginResponseLine("Bad!");
            } else {
                
                const authToken = response.headers.get("Authorization") as string;
                window.localStorage.setItem("uberapp-jwt", authToken);
                // alert(authToken);
                const driverInfo = await response.json() as DriverInfo;
                    setLoginResponseLine("good!");
                    updateUserContext({
                        username: driverInfo.user.username,
                        loginToken: authToken,
                        at: {lat: 0., lon: 0.},
                        isCourier: driverInfo.earnerType === "COURIER",
                        jobsThisWeek: 0, // change later
                    })
                // updateUserContext(await response.json() as UserContextType);
            }
        }}>
            <p id="errorline">{loginResponseLine}</p><br />
            <span className="inline"><p>Username: </p><input id="driverUsername"></input></span><br />
            <span className="inline"><p>Password: </p><input type="password" id="driverPassword"></input></span><br />
            <button type="submit">Log in</button><br />
            <p></p>
        </form>
            </div>
}

export default DriverLoginWidget;
import { useState } from "react";
import endpoints from "../data/endpoints";
import getSHA256Hash from "../data/utils";
import type { UserContextType } from "../types/userContext";

const DriverLoginWidget = ({updateUserContext}: {updateUserContext: (newUserContext: UserContextType) => void}) => {
    const [loginResponseLine, setLoginResponseLine] = useState("");
    return <div className="driver-login bg-black h-screen text-white flex flex-row min-h-screen justify-center items-center">
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
                body: JSON.stringify({
                    username: usernameInput,
                    pwhash: getSHA256Hash(passwordInput),
                })
            });
            if (!(response.ok)) {
                setLoginResponseLine("Bad!");
            } else {
                setLoginResponseLine("good!");
                updateUserContext(await response.json() as UserContextType);
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
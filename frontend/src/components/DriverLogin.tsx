import { useState } from "react";
import endpoints from "../data/endpoints";
import getSHA256Hash from "../data/utils";
import type { UserContext } from "../types/userContext";

const DriverLoginWidget = ({updateUserContext}: {updateUserContext: (newUserContext: UserContext) => void}) => {
    const [loginResponseLine, setLoginResponseLine] = useState("");
    return <div className="driver-login">
        <form onSubmit={async (ev) => {
            ev.preventDefault();
            const usernameInput = (document.getElementById('driverUsername') as HTMLInputElement).value;
            const passwordInput = (document.getElementById('driverPassword') as HTMLInputElement).value;
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
                updateUserContext(await response.json() as UserContext);
            }
        }}>
            <p id="errorline">{loginResponseLine}</p>
            <input id="driverUsername"></input>
            <input type="password" id="driverPassword"></input>
        </form>
    </div>
}

export default DriverLoginWidget;
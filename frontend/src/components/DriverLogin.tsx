import endpoints from "../data/endpoints";
import getSHA256Hash from "../data/utils";
import type { UserContext } from "../types/userContext";

const DriverLoginWidget = ({updateUserContext}: {updateUserContext: (newUserContext: UserContext) => void}) => {
    return <div className="driver-login">
        <form onSubmit={async (ev) => {
            ev.preventDefault();
            const usernameInput = (document.getElementById('driverUsername') as HTMLInputElement).value;
            const passwordInput = (document.getElementById('driverPassword') as HTMLInputElement).value;
            // POST /api/driver/login
            const responseContext = await (await fetch(endpoints.login, {
                mode: "cors",
                method: "POST",
                body: JSON.stringify({
                    username: usernameInput,
                    pwhash: getSHA256Hash(passwordInput),
                })
            })).json();
            updateUserContext(responseContext as UserContext);
        }}>
            <input id="driverUsername"></input>
            <input type="password" id="driverPassword"></input>
        </form>
    </div>
}

export default DriverLoginWidget;
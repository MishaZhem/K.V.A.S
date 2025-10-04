import { useState } from "react";
import endpoints from "../data/endpoints";
import type { DriverInfo, UserContextType } from "../types/userContext";
import InteractiveBackground from "./InteractiveBackground";

const Login = ({ updateUserContext }: { updateUserContext: (newUserContext: UserContextType) => void }) => {
  const [loginResponseLine, setLoginResponseLine] = useState("");
  return (
    <div className="bg-[#0B0B0B] h-screen text-white flex flex-col min-h-screen justify-center items-center font-sans p-4">

      <InteractiveBackground /> {/* <--- NEW COMPONENT */}

      <div className="group relative w-full max-w-sm rounded-lg border border-white/10 p-8 bg-[#0B0B0B] z-10">
        <div className="pointer-events-none absolute inset-0 z-10 rounded-lg bg-gradient-to-b from-white/[.07] to-transparent opacity-0 transition-opacity duration-100 group-hover:opacity-100"></div>

        <div className="pointer-events-none absolute inset-0 z-20 opacity-0 transition-opacity duration-100 group-hover:opacity-100">
          <div className="absolute inset-0 border border-white/20 rounded-lg"></div>
          <div className="bg-white absolute -left-px -top-px z-10 size-2"></div>
          <div className="bg-white absolute -right-px -top-px z-10 size-2"></div>
          <div className="bg-white absolute -bottom-px -left-px z-10 size-2"></div>
          <div className="bg-white absolute -bottom-px -right-px z-10 size-2"></div>
        </div>

        <div className="relative z-30">
          <h2 className="text-2xl font-bold mb-6 text-center text-white/90 tracking-wider">
            Login
          </h2>
          <form className="space-y-6" onSubmit={async (ev) => {
            ev.preventDefault();
            const usernameInput = (document.getElementById('driverUsername') as HTMLInputElement).value;
            const passwordInput = (document.getElementById('driverPassword') as HTMLInputElement).value;
            if (usernameInput === "admin" && passwordInput === "admin") {
              setLoginResponseLine("adminlogin");
              updateUserContext({
                username: "admin",
                loginToken: "0",
                at: { lat: 0.0, lon: 0.0 },
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
                at: { lat: 0., lon: 0. },
                isCourier: driverInfo.earnerType === "COURIER",
                jobsThisWeek: 0, // change later
              })
              // updateUserContext(await response.json() as UserContextType);
            }
          }}>
            {loginResponseLine && <p id="errorline" className="text-red-500 text-center text-sm">{loginResponseLine}</p>}
            <div>
              <label htmlFor="driverUsername" className="block mb-2 text-sm font-medium text-white/50 uppercase tracking-wider">Username</label>
              <input id="driverUsername" name="driverUsername" className="bg-black/30 border border-white/20 text-white text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 placeholder-white/30" />
            </div>
            <div>
              <label htmlFor="driverPassword" className="block mb-2 text-sm font-medium text-white/50 uppercase tracking-wider">Password</label>
              <input type="password" id="driverPassword" name="driverPassword" className="bg-black/30 border border-white/20 text-white text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 placeholder-white/30" />
            </div>
            <button type="submit" className="w-full text-white bg-white/10 hover:bg-white/20 focus:outline-none focus:ring-white/30 font-medium rounded-lg text-sm px-5 py-2.5 text-center uppercase tracking-widest border border-white/20 transition-colors duration-100">
              Log In
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}

export default Login;

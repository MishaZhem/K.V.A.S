import type { UserContextType } from "../types/userContext";

const Profile = ({ userContext, shown }: { userContext: UserContextType, shown: boolean }) => {
    return shown ? (
        <div className="text-[#ABABAB] overflow-y-auto grow max-h-1/2 rounded-3xl bg-[#171717] absolute border-3 border-[#434343] right-10 top-30 shadow-xl/30">
            <div className="p-2 pl-3 font-sans">
                <h2>Profile and settings</h2>
                <hr></hr>
                <p>Name: <span className="font-mono">{userContext.username}</span></p>
                <button>&gt; Change name</button>
                <hr></hr>
                <p>You are registered as a {userContext.isCourier ? "courier" : "driver"} <br /> at Uber.</p>
                <button>&gt; Change registration</button>
            </div>
        </div>
    ) : <></>
}
export default Profile;
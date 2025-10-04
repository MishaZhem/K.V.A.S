import type { UserContextType } from "../types/userContext";

const Profile = ({userContext, shown}: {userContext: UserContextType, shown: boolean}) => {
    return shown ? (
        <div className="bg-gray-800 rounded-4xl p-4 border-5 border-gray-700 text-gray-400">
            <h2>Profile and settings</h2>
            <hr></hr>
            <p>Name: {userContext.username}</p>
            <button>&gt; Change name</button>
            <hr></hr>
            <p>You are registered as a {userContext.isCourier ? "courier" : "driver"} at Uber.</p>
            <button>&gt; Change registration</button>
        </div>
    ) : <></>
}
export default Profile;
import { user } from "../assets";

const TopMenu = () => {
    return (
        <>
            <div className="bg-[#171717] absolute rounded-[999px] border-3 border-[#434343] right-10 top-10 p-2 pl-3 shadow-xl/30">
                <div className="flex gap-3 items-center opacity-70">
                    <p>[ Jobs ]</p>
                    <p>[ Profile ]</p>
                    <img src={user} className="w-8" alt="" />
                </div>
            </div>
        </>
    );
};

export default TopMenu;
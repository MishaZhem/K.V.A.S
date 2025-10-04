import { uber } from "../assets";
import { HeatMap, TopMenu } from "../components";

const Map = () => {
    return (
        <div className="relative">
            <HeatMap />
            <img src={uber} className="absolute left-10 top-10 w-32 opacity-30" alt="" />
            <TopMenu />
        </div>
    )
}

export default Map;

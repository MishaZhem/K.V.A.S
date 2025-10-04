import type { JobItem } from "../types/job";

const JobItemDisplay = ({job}: {job: JobItem}) => {
    const time = new Date(job.startTimestamp);
    return (
        <div className="grid grid-cols-2 gap-4">
            <div className="job-item
            bg-white
            border
            border-gray-200
            rounded-lg
            shadow-sm
            p-4
            mb-4
            hover:shadow-md
            transition-shadow
            duration-200
            cursor-pointer
            flex
            flex-col
            space-y-2
">
                <p>City: {job.city}</p>
                <p>From {job.from.lat},{job.from.lon}</p>
                <p>To {job.to.lat},{job.to.lon}.</p>
                <p>Expected earning is ${job.potentialEarningCents / 100}</p>
                <p>Starts at {`${time.getHours()}:${time.getMinutes()}:${time.getSeconds()}`}</p>
                
            </div>
            <div>
                <button className="primary">See distance to</button>
                <button className="">Go to pickup</button>
            </div>
        </div>);
}
export default JobItemDisplay;
import type { JobItem } from "../types/job";

const JobItemDisplay = ({jobs, jobi}: {jobs: JobItem[], jobi: number}) => {
    const job = jobs[jobi];
    const time = new Date(jobs[jobi].startTimestamp);
    return (
        <div className="gap-8">
            <div className={`job-item
            text-gray-400
            bg-[#171717]
            border-2
            rounded-2xl
            border-[#434343]
            shadow-sm
            p-4
            ${jobi >= jobs.length - 1 ? "" : "mb-4 pb-5"
            }
            hover:shadow-md
            transition-shadow
            duration-200
            cursor-pointer
`}>
                <p className="text">From {job.from.lat},{job.from.lon}</p>
                <p className="text">To {job.to.lat},{job.to.lon}.</p>
                <p>Expected earning is </p>
                <p className="text-green-400 text-3xl"><b>EUR {job.potentialEarningCents / 100}</b></p>
                <p>Starts in {`${time.getHours()}:${time.getMinutes()}:${time.getSeconds()}`}</p>
                <button type="button" className="text-white bg-blue-700 hover:bg-blue-800 focus:outline-none focus:ring-4 focus:ring-blue-300 font-medium rounded-full text-m px-5 py-2.5 text-center me-2 mb-2 dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800">See distance</button>
                <button type="button" className="text-white bg-blue-700 hover:bg-blue-800 focus:outline-none focus:ring-4 focus:ring-blue-300 font-medium rounded-full text-m px-5 py-2.5 text-center me-2 mb-2 dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800">Go!</button>
            </div>
        </div>);
}
export default JobItemDisplay;
import type { JobItem } from "../types/job";

const JobItemDisplay = ({ jobs, jobi }: { jobs: JobItem[], jobi: number }) => {
    const job = jobs[jobi];
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
            font-sans
`}>
                <p className="font-sans text-white_primary/90">~{Math.round(job.expectedDistanceFromPickUpToDropOff * 100) / 100}km / ~{Math.round(job.expectedDurationFromPickUpToDropOff * 100) / 100} min Job </p>
                <p className="font-sans text-white_primary/90">~{Math.round(job.expectedDurationFromDriverToPickUp * 100) / 100} min away from pick-up location.</p>
                <p className="font-sans text-white_primary/70">Expected earning:</p>
                <div className="flex justify-between font-mono">
                    <p className="text-[#50c58d] text-lg"><b>EUR {Math.round(job.expectedEarnNet * 100) / 100}</b></p>
                    <p className="font-mono text-lg opacity-50">—</p>
                    <p className=" text-[#50c58d] text-lg"><b>EUR {Math.round(job.moneyPerHour * 100) / 100}/h</b></p>
                </div>
                <div className="flex justify-between mt-2">
                    <button type="button" className="text-white_primary bg-secondary_light focus:saturate-50 font-medium rounded-lg w-full text-m px-5 py-2.5 text-center me-2 mb-2">See distance</button>
                    <button type="button" className="text-white_primary bg-secondary_light focus:outline-none focus:ring-4 font-medium rounded-lg text-m px-5 py-2.5 text-center me-2 mb-2">Go!</button>
                </div>
            </div>
        </div>);
}
export default JobItemDisplay;
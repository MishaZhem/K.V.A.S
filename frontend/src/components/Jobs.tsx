import type { JobItem } from "../types/job"
import JobItemDisplay from "./JobItem"

const Jobs = ({ jobs, shown }: { jobs: JobItem[], shown: boolean }) => {
    return shown ? (
        <div className={`overflow-y-auto max-h-1/2 rounded-3xl bg-[#171717] absolute border-3 border-[#434343] right-10 top-30 p-2 pl-3 shadow-xl/30`}>
            {jobs.map((j, i) => { return (<JobItemDisplay jobs={jobs} jobi={i}></JobItemDisplay>) }).slice(0, 5)}
        </div>
    ) : <></>
}
export default Jobs;
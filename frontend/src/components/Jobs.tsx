import type { JobItem } from "../types/job"
import JobItemDisplay from "./JobItem"

const Jobs = ({jobs, shown}: {jobs: JobItem[], shown: boolean}) => {
    return shown ? (
    <div className={`bg-gray-800 rounded-4xl p-4 border-5 border-gray-700 text-gray-400`}>
        {jobs.map((j, i) => {return (<JobItemDisplay jobs={jobs} jobi={i}></JobItemDisplay>)}).slice(0, 5)}
    </div>
    ) : <></>
}
export default Jobs;
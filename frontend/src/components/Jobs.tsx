import type { JobItem } from "../types/job"
import JobItemDisplay from "./JobItem"

const Jobs = ({jobs}: {jobs: JobItem[]}) => {
    return (
    <div>
        <p>Next jobs near you (within 2km): </p>
        {jobs.map((j) => {return (<JobItemDisplay job={j}></JobItemDisplay>)}).slice(0, 5)}
    </div>
    )
}
export default Jobs;
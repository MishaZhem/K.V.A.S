import type { JobItem } from "../types/job"
import JobItemDisplay from "./JobItem"

const Jobs = ({jobs, shown}: {jobs: JobItem[], shown: boolean}) => {
    if (!(shown)) { return <></>}
    const contents = jobs.length === 0 ? <div className={`job-item
            text-gray-400
            bg-[#171717]
            border-2
            rounded-2xl
            border-[#434343]
            shadow-sm
            p-4
            hover:shadow-md
            transition-shadow
            duration-200
            cursor-pointer
`}>
    <p>There are currently no jobs.</p>
</div> : jobs.map((j, i) => {return (<JobItemDisplay jobs={jobs} jobi={i}></JobItemDisplay>)}).slice(0, 5);
    return (
    <div className="
    overflow-y-auto 
    max-h-1/2 
    rounded-3xl 
    w-1/8 
    bg-[#171717] 
    absolute 
    border-3 
    border-[#434343] 
    right-10 
    top-30 
    p-2 
    pl-3 
    shadow-xl/30">{contents}</div>
    )
}
export default Jobs;
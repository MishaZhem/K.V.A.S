import type { JobItem } from "../types/job";
import JobLocation from "./JobLocation";
import JobEarnings from "./JobEarnings";
import JobStartTime from "./JobStartTime";
import JobActions from "./JobActions";

const JobItemDisplay = ({ jobs, jobi }: { jobs: JobItem[], jobi: number }) => {
  const job = jobs[jobi];

  const containerClasses = `
        job-item text-gray-400 bg-[#171717] border-2 rounded-2xl
        border-[#434343] shadow-sm p-4
        ${jobi >= jobs.length - 1 ? "" : "mb-4 pb-5"}
        hover:shadow-md transition-shadow duration-200 cursor-pointer
    `;

  return (
    <div className="gap-8">
      <div className={containerClasses}>
        <JobLocation from={job.from} to={job.to} />
        <JobEarnings potentialEarningCents={job.potentialEarningCents} />
        <JobStartTime startTimestamp={job.startTimestamp} />
        <JobActions />
      </div>
    </div>
  );
}

export default JobItemDisplay;

import type { Loc } from "./userContext"

type JobItem = {
    id: string,
    from: Loc,
    to: Loc,
    city: string,
    potentialEarningCents: number,
    startTimestamp: Date,
}
export { type JobItem };
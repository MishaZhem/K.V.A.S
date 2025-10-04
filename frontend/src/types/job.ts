import type { Loc } from "./userContext"

type JobItem = {
    id: string,
    from: Loc,
    to: Loc,
    city: string,
    potentialEarningCents: number,
    startTimestamp: string,
}
export { type JobItem };
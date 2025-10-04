import type { Loc } from "./userContext"

type JobItem = {
    id: string,
    from: Loc, // Loc = {lat: number; lon: number}
    to: Loc,
    potentialEarningCents: number,
    // earningRateCents: number,
    startTimestamp: number,
}
export { type JobItem };
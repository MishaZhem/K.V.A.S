import type { Loc } from "./userContext"

type JobItem = {
  expectedDistanceFromPickUpToDropOff: number;
  expectedDistanceFromDriverToPickUp: number;
  expectedDurationFromPickUpToDropOff: number;
  expectedDurationFromDriverToPickUp: number;
  expectedEarnNet: number;
  moneyPerHour: number;
};

export { type JobItem };
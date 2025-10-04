type UserContextType = {
    username: string,
    loginToken: string,
    at: Loc,
    isCourier: boolean,
    jobsThisWeek: number, // this can be always zero until we start talking about bonuses.
}
type DriverInfo = {
    username: string,
    rating: number,
    earnerType: string,
    experienceMonths: number,
    fuelType: string,
    isEv: boolean,
    vehicleType: string,
}
type Loc = {
    lat: number,
    lon: number,
}

export {type UserContextType, type Loc, type DriverInfo}
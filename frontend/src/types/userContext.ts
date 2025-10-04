type UserContextType = {
    username: string,
    loginToken: string,
    at: Loc,
    isCourier: boolean,
    jobsThisWeek: number, // this can be always zero until we start talking about bonuses.
}
type Loc = {
    lat: number,
    lon: number,
}

export {type UserContextType, type Loc}
type UserContext = {
    username: string,
    loginToken: string,
    at: Loc,
    jobsThisWeek: number,
}
type Loc = {
    lat: number,
    lon: number,
}

export {type UserContext, type Loc}
type UserContext = {
    username: string,
    loginToken: string,
    at: Loc,
}
type Loc = {
    lat: number,
    lon: number,
}

export {type UserContext, type Loc}
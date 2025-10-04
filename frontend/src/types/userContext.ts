type UserContextType = {
    username: string,
    loginToken: string,
    at: Loc,
    isCourier: boolean,
    jobsThisWeek: number, // this can be always zero until we start talking about bonuses.
}
type DriverInfo = {
    id: string,
    user: {
        id: string,
        username: string,
        passwordHash: string,
        createdAt: Date,
        updatedAt: Date,
        role: string,
    },
    rating: number,
    earnerType: string,
    fuelType: string,
    isEv: boolean,
    vehicleType: string,
    createdAt: Date,
    updatedAt: Date,
}
type Loc = {
    lat: number,
    lon: number,
}

export {type UserContextType, type Loc, type DriverInfo}
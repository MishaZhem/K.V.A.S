type HeatmapPoint = {
    x: number, // lat
    y: number, // lon
    value: number, // sumMoney
}
type HeatmapPointsResponse = {
    points: HeatmapPoint[],
}

export {type HeatmapPoint, type HeatmapPointsResponse};
import { useRef, useEffect, useState } from 'react';
import mapboxgl from 'mapbox-gl';
import { points } from '../assets';
import { ButtonSleep, ButtonMoney } from "../components";
import type { HeatmapPointsResponse } from '../types/responses';
import Skeleton, { SkeletonTheme } from 'react-loading-skeleton'

mapboxgl.accessToken = import.meta.env.VITE_MAPBOX_TOKEN;

const encodeGeoJSON = (response: HeatmapPointsResponse) => {
    return {
        type: "FeatureCollection",
        features: response.points.map((p) => {
            return {
                type: "Feature",
                properties: {
                    sumProfit: p.value,
                },
                geometry: {
                    type: "Point",
                    coordinates: [p.y, p.x],
                }
            };
        })
    }
}

function getWidth(start: number, end: number) {
    var element = document.getElementById("elementsSlider") as HTMLElement;
    if (!element) {
        console.warn("Element with ID 'elementsSlider' not found");
        return 0;
    }
    return element.offsetWidth / 23 * (end - start);
}

function getLeftMargin(start: number) {
    var element = document.getElementById("elementsSlider") as HTMLElement;
    if (!element) {
        console.warn("Element with ID 'elementsSlider' not found");
        return 0;
    }
    let offset = element.offsetWidth / 23 * start
    return offset;
}

const MapAnimation = ({ username, userToken }: { username: string, userToken: string }) => {
    const mapContainer = useRef(null);
    const map = useRef<mapboxgl.Map | null>(null);
    const [curHour, setCurHour] = useState(11);
    const [toSleep, setToSleep] = useState<number[][]>([]);
    const [toWork, setToWork] = useState<number[][]>([]);
    const [loadingGraph, setLoadingGraph] = useState(true);
    const [latitude, setLatitude] = useState<number | null>(null);
    const [longitude, setLongitude] = useState<number | null>(null);

    useEffect(() => {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                (position) => {
                    setLatitude(position.coords.latitude);
                    setLongitude(position.coords.longitude);
                },
                (err) => {
                    setLatitude(52.370216);
                    setLongitude(4.895168);
                }
            );
        } else {
            setLatitude(52.370216);
            setLongitude(4.895168);
        }
    }, []);

    useEffect(() => {
        async function getGraph() {
            if (latitude === null || longitude === null) return;

            const response = await fetch(`http://localhost:8082/api/driver/graph?currentLat=${latitude}&currentLon=${longitude}`, {
                mode: "cors",
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": userToken
                },

            });
            const data = await response.json();
            let body = data.graphData as boolean[];
            let prev = 0;
            let work = body[0];
            console.log(body);
            let workArray = []
            let sleepArray = []
            for (let i = 0; i < body.length; i++) {
                if (body[i] != work) {
                    if (work) {
                        workArray.push([prev, i]);
                        prev = i;
                    } else {
                        sleepArray.push([prev, i]);
                        prev = i;
                    }
                }
                work = body[i];
            }
            if (work) {
                workArray.push([prev, 23]);
            } else {
                sleepArray.push([prev, 23]);
            }
            setToWork(workArray);
            setToSleep(sleepArray);
            setLoadingGraph(false);
            console.log(workArray);
            console.log(sleepArray);
        }
        getGraph();
    }, [userToken, latitude, longitude]);

    useEffect(() => {
        console.log('Map container:', map.current);
        if (map.current) return;

        map.current = new mapboxgl.Map({
            container: mapContainer.current as unknown as HTMLElement,
            style: 'mapbox://styles/misha111/cmgccqs5000d601sabvn4hnli',
            center: [4.895168, 52.370216],
            zoom: 12
        });

        const m = map.current;
        m.on('load', () => {
            m.addLayer({
                id: 'collisions',
                type: 'circle',
                source: {
                    type: 'geojson',
                    data: `http://localhost:8082/api/driver/heatmap/url?username=${username}`
                },
                paint: {
                    'circle-radius': 5
                    // [
                    //     'interpolate',
                    //     ['linear'],
                    //     ['number', ['get', 'Casualty']],
                    //     0,
                    //     4,
                    //     5,
                    //     24
                    // ]
                    ,
                    'circle-color': '#2DC4B2',
                    // [
                    //     'interpolate',
                    //     ['linear'],
                    //     ['number', ['get', 'Casualty']],
                    //     0,
                    //     '#2DC4B2',
                    //     1,
                    //     '#3BB3C3',
                    //     2,
                    //     '#669EC4',
                    //     3,
                    //     '#8B88B6',
                    //     4,
                    //     '#A2719B',
                    //     5,
                    //     '#AA5E79'
                    // ],
                    'circle-opacity': 1
                },
                filter: ['==', ['number', ['get', 'hour']], 11]
            });

            const slider = document.getElementById('slider');
            if (slider) {
                slider.addEventListener('input', (event) => {
                    const hour = parseInt((event.target as HTMLInputElement).value);
                    // update the map
                    m.setFilter('collisions', ['==', ['number', ['get', 'hour']], hour]);
                });
            }


            // m.addLayer({
            //         id: 'collisions-hexagons',
            //         type: 'fill',
            //         source: {
            //             type: 'geojson',
            //             data: 'https://raw.githubusercontent.com/MishaZhem/jsonTest/refs/heads/main/collisions_hexagons.json'
            //         },
            //         paint: {
            //             'fill-color': '#ff0000', // Яркий красный для теста
            //             'fill-opacity': 0.8,
            //             'fill-outline-color': '#000000' // Чёрная обводка
            //         }
            //     });

            //     // Отладочные логи
            //     console.log('Source:', m.getSource('collisions-hexagons'));
            //     console.log('Layer:', m.getLayer('collisions-hexagons'));

            //     // Проверка ошибок загрузки источника
            //     m.on('error', (e) => console.error('Map error:', e.error));

        });


    }, []);

    useEffect(() => {
        function resizeElements() {
            console.log(111);
            let elems = document.getElementsByClassName("buttonAct");
            for (let i = 0; i < elems.length; i++) {
                let tempElem = elems[i] as HTMLElement;
                for (const className of tempElem.classList) {
                    if (className.includes(",")) {
                        let s = className.split(",")
                        tempElem.style.width = Math.ceil(getWidth(parseInt(s[0]), parseInt(s[1]))).toString() + "px";
                        tempElem.style.left = Math.ceil(getLeftMargin((parseInt(s[0])))).toString() + "px";
                    }
                }
            }
            elems = document.getElementsByClassName("buttonAct2");
            for (let i = 0; i < elems.length; i++) {
                let tempElem = elems[i] as HTMLElement;
                for (const className of tempElem.classList) {
                    if (className.includes(",")) {
                        let s = className.split(",")
                        tempElem.style.width = Math.ceil(getWidth(parseInt(s[0]), parseInt(s[1]))).toString() + "px";
                    }
                }
            }
        }

        window.addEventListener("resize", resizeElements);
        resizeElements();
    }, [toSleep, toWork]);

    return (
        <div className="map-container">
            <div ref={mapContainer} className="map" />
            <div className="absolute right-0 left-0 bottom-0 h-[300px] w-full z-10 md:w-full overflow-x-auto overflow-y-hidden">
                <div id="elementsSlider" className='flex flex-col gap-1 absolute left-10 right-10 bottom-4 w-[1500px] pr-10 md:w-auto md:pr-0'>
                    <div className='graph z-10 mb-[20px]'>
                        <div className='relative flex justify-between w-full mb-[-25px]'>
                            <div className={`opacity-0 top-0 buttonAct flex flex-col gap-2`}>
                                <ButtonSleep start={0} end={0}></ButtonSleep>
                                <div className='bg-white h-[25px]'></div>
                            </div>
                            {loadingGraph ? (<Skeleton className='skeletonGraph' baseColor="#202020" highlightColor="#333" />) : (
                                <>
                                    {toSleep.map((x) => (
                                        <div className={`absolute top-0 buttonAct ${x} flex flex-col gap-2`}>
                                            <div className={`buttonAct2 ${x}`}><ButtonSleep start={x[0]} end={x[1]}></ButtonSleep></div>
                                            <div className={`${curHour >= x[0] && curHour < x[1] ? 'bg-white' : 'bg-transparent'} h-[25px]`}></div>
                                        </div>
                                    ))}
                                    {toWork.map((x) => (
                                        <div className={`absolute top-0 buttonAct ${x} flex flex-col gap-2`}>
                                            <div className={`buttonAct2`}><ButtonMoney start={x[0]} end={x[1]}></ButtonMoney></div>
                                            <div className={`${curHour >= x[0] && curHour < x[1] ? 'bg-white' : 'bg-transparent'} h-[25px]`}></div>
                                        </div>
                                    ))}
                                </>
                            )}

                        </div>
                        <div className='w-full sliderTime'></div>
                    </div>
                    <div className="flex justify-between items-center">
                        <input
                            id="slider"
                            className="slider absolute w-full h-[300px] z-[100]"
                            type="range"
                            min="0"
                            max="23"
                            step="1"
                            value={curHour}
                            onChange={(e) => setCurHour(Number(e?.target?.value))}
                        />
                        {Array(24).fill(1).map((_, index) => (
                            <div className={`flex flex-col items-center w-[18px] ${index % 6 != 0 ? 'mt-[4px] mb-auto' : ''}`}>
                                <div key={index} className={`column bg-[#D9D9D9] w-[6px] ${index % 6 == 0 ? "h-[20px]" : "h-[12px]"} ${curHour == index ? 'opacity-100 bg-[#FFFFFF]' : "opacity-20"}`}></div>
                                {index % 6 == 0 && (<h1 className={`w-[18px] flex items-center justify-center Jet ${curHour == index ? '' : "opacity-50"}`}>{index}</h1>)}
                            </div>
                        ))}
                    </div>
                </div>
            </div>
            <div className="absolute right-0 left-0 bottom-0 h-[300px] w-full z-0" style={{ maskImage: 'linear-gradient(to top, black, transparent)' }}>
                <img src={points} alt="" className="opacity-30 z-[-1] absolute inset-0 object-cover" />
                <div className="backdrop-blur-lg absolute left-0 right-0 bottom-0 w-full h-full top-0 z-[5] bg-black/60 blur-xl">
                </div>
            </div>
        </div>
    );
};

export default MapAnimation;
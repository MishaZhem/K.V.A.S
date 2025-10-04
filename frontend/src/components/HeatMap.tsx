import { useRef, useEffect, useState } from 'react';
import mapboxgl from 'mapbox-gl';
import { points } from '../assets';
import { ButtonSleep, ButtonMoney } from "../components";

mapboxgl.accessToken = import.meta.env.VITE_MAPBOX_TOKEN;

const MapAnimation = () => {
    const mapContainer = useRef(null);
    const map = useRef<mapboxgl.Map | null>(null);
    const labelRef = useRef(null);
    const hours = ["00", "06", "12", "18", "24"];
    const [curHour, setCurHour] = useState(11);

    useEffect(() => {
        console.log('Map container:', map.current);
        if (map.current) return;

        map.current = new mapboxgl.Map({
            container: mapContainer.current as unknown as HTMLElement,
            style: 'mapbox://styles/misha111/cmgcoero500fa01r02s4x2e8d',
            center: [-74.0059, 40.7128],
            zoom: 12
        });

        const m = map.current;

        m.on('load', () => {
            m.addLayer({
                id: 'collisions',
                type: 'circle',
                source: {
                    type: 'geojson',
                    data: 'https://raw.githubusercontent.com/MishaZhem/jsonTest/refs/heads/main/collisions1601.json'
                },
                paint: {
                    'circle-radius': [
                        'interpolate',
                        ['linear'],
                        ['number', ['get', 'Casualty']],
                        0,
                        4,
                        5,
                        24
                    ],
                    'circle-color': [
                        'interpolate',
                        ['linear'],
                        ['number', ['get', 'Casualty']],
                        0,
                        '#2DC4B2',
                        1,
                        '#3BB3C3',
                        2,
                        '#669EC4',
                        3,
                        '#8B88B6',
                        4,
                        '#A2719B',
                        5,
                        '#AA5E79'
                    ],
                    'circle-opacity': 1
                },
                filter: ['==', ['number', ['get', 'Hour']], 12]
            });

            const slider = document.getElementById('slider');
            if (slider) {
                slider.addEventListener('input', (event) => {
                    const hour = parseInt((event.target as HTMLInputElement).value);
                    // update the map
                    m.setFilter('collisions', ['==', ['number', ['get', 'Hour']], hour]);
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

    return (
        <div className="map-container">
            <div ref={mapContainer} className="map" />
            <div className="absolute right-0 left-0 bottom-0 h-[300px] w-full z-10 md:w-full overflow-x-auto overflow-y-hidden">
                <div className='flex flex-col gap-1 absolute left-10 right-10 bottom-4 w-[1500px] pr-10 md:w-auto md:pr-0'>
                    <div className='graph z-10 mb-[20px]'>
                        <div className='flex justify-between w-full mb-[-25px]'>
                            <div className='flex flex-col gap-2 w-[500px]'>
                                <ButtonSleep></ButtonSleep>
                                <div className='bg-white h-[25px]'></div>
                            </div>
                            <div className='flex flex-col gap-2 w-[250px]'>
                                <ButtonMoney></ButtonMoney>
                                <div className='bg-white h-[25px]'></div>
                            </div>

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
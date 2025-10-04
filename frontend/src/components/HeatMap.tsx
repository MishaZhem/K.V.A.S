import React, { useRef, useEffect } from 'react';
import mapboxgl from 'mapbox-gl';

mapboxgl.accessToken = import.meta.env.VITE_MAPBOX_TOKEN;

const MapAnimation = () => {
    const mapContainer = useRef(null);
    const map = useRef<mapboxgl.Map | null>(null);
    const labelRef = useRef(null);

    useEffect(() => {
        console.log('Map container:', map.current);
        if (map.current) return;

        map.current = new mapboxgl.Map({
            container: mapContainer.current as unknown as HTMLElement,
            style: 'mapbox://styles/misha111/cmgccqs5000d601sabvn4hnli',
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

                    // converting 0-23 hour to AMPM format
                    const ampm = hour >= 12 ? 'PM' : 'AM';
                    const hour12 = hour % 12 ? hour % 12 : 12;

                    // update text in the UI
                    const activeHour = document.getElementById('active-hour');
                    if (activeHour) {
                        activeHour.innerText = hour12 + ampm;
                    }
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
            {/* <div ref={labelRef} className="country-label">
                <input
                    id="slider"
                    className="slider opacity-30 bg-[#D9D9D9]"
                    type="range"
                    min="0"
                    max="23"
                    step="1"
                />
            </div> */}
            <div className="absolute right-0 left-0 bottom-0 h-[100px] w-full backdrop-blur-md" style={{ maskImage: 'linear-gradient(to top, black, transparent)' }}>
                <div className="flex justify-between items-center absolute left-6 right-6 bottom-4">
                    {Array(20).fill(1).map((_, index) => (
                        <div key={index} className="column bg-[#D9D9D9] opacity-20 w-[6px] h-[13px]"></div>
                    ))}
                </div>
            </div>

        </div>
    );
};

export default MapAnimation;
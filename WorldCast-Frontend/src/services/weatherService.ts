import { WeatherData } from '../types/weather.js';

const VITE_BACKEND_URL = import.meta.env.VITE_BACKEND_URL || 'http://localhost:8080';

export function fetchWeatherData(lat: string, lon: string): Promise<WeatherData> {
  const url = `${VITE_BACKEND_URL}/weather?location=${encodeURIComponent(`${lat},${lon}`)}`;
  return fetch(url)
    .then((response) => {
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return response.json();
    })
    .then((data) => {
      // Validate data structure
      if (!data || !data.location || !data.current || !data.forecast) {
        throw new Error('Invalid weather data format');
      }
      return data as WeatherData;
    });
}

export function checkCompleteResponse(response: WeatherData): boolean {
  return response.current.weatherDescription != null;
}

export interface WeatherData {
  location: {
    name: string;
    lat: number;
    lon: number;
  };
  current: {
    temperature: number;
    humidity: number;
    pressure: number;
    windSpeed: number;
    windDirection: number;
    weatherDescription: string;
    icon: string;
    timestamp: string;
  };
  forecast: ForecastDay[];
}

export interface ForecastDay {
  date: string;
  minTemperature: number;
  maxTemperature: number;
  weatherDescription: string;
  icon: string;
}

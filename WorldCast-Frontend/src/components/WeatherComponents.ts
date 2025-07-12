import { WeatherData, ForecastDay } from '../types/weather.js';
import { createExpandableText } from '../utils/textUtils.js';


export function createCurrentWeatherComponent(
  current: WeatherData['current'],
  location: WeatherData['location']
): HTMLElement {
  const container = document.createElement('div');
  container.className = 'current-weather';
  container.style.cssText = `
    background: linear-gradient(135deg, #74b9ff, #0984e3);
    color: white;
    padding: 20px;
    border-radius: 12px;
    margin-bottom: 20px;
    box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  `;

  const iconMap: { [key: string]: string } = {
    'sunny': '☀️',
    'cloudy': '☁️',
    'rainy': '🌧️',
    'snowy': '❄️',
    'partly-cloudy': '⛅'
  };

  container.innerHTML = `
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px;">
      <div>
        <h3 style="margin: 0; font-size: 1.2em;">📍 ${location.name}</h3>
        <p style="margin: 5px 0 0 0; opacity: 0.8; font-size: 0.9em;">${location.lat.toFixed(4)}, ${location.lon.toFixed(4)}</p>
      </div>
      <div style="font-size: 3em;">${iconMap[current.icon] || '🌤️'}</div>
    </div>
    
    <div style="display: flex; justify-content: space-between; align-items: center;">
      <div>
        <div style="font-size: 3em; font-weight: bold; margin: 0;">${current.temperature}°C</div>
        <p style="margin: 5px 0 0 0; opacity: 0.9;">${current.weatherDescription || 'Weather information not available'}</p>
      </div>
      
      <div style="text-align: right; font-size: 0.9em;">
        <div>💧 Humidity: ${current.humidity}%</div>
        <div>🌪️ Wind: ${current.windSpeed} m/s</div>
        <div>📊 Pressure: ${current.pressure} hPa</div>
        <div style="opacity: 0.8; margin-top: 5px;">Updated: ${new Date(current.timestamp).toLocaleTimeString()}</div>
      </div>
    </div>
  `;

  return container;
}

export function createForecastComponent(forecast: ForecastDay[]): HTMLElement {
  const container = document.createElement('div');
  container.className = 'forecast-weather';
  container.style.cssText = `
    background: white;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  `;

  const iconMap: { [key: string]: string } = {
    'sunny': '☀️',
    'cloudy': '☁️',
    'rainy': '🌧️',
    'snowy': '❄️',
    'partly-cloudy': '⛅'
  };

  let forecastHTML = '<h3 style="margin: 0 0 15px 0; color: #2d3436;">📅 7-Day Forecast</h3>';

  forecast.forEach((day, index) => {
    const date = new Date(day.date);
    const dayLabel = index === 0 ? 'Today' :
      index === 1 ? 'Tomorrow' :
        date.toLocaleDateString('en-US', { weekday: 'short', month: 'short', day: 'numeric' });

    forecastHTML += `
      <div style="
        display: flex; 
        justify-content: space-between; 
        align-items: center; 
        padding: 12px 0; 
        border-bottom: 1px solid #f0f0f0;
        ${index === forecast.length - 1 ? 'border-bottom: none;' : ''}
      ">
        <div style="flex: 1;">
          <div style="font-weight: bold; color: #2d3436;">${dayLabel}</div>
          <div style="font-size: 0.85em; color: #636e72; margin-top: 2px;">${date.toLocaleDateString()}</div>
        </div>
        
        <div style="flex: 2; font-size: 0.85em; color: #636e72; margin: 0 15px;">
          ${createExpandableText(day.weatherDescription, 80)}
        </div>
        
        <div style="display: flex; align-items: center; gap: 10px;">
          <div style="font-size: 1.5em;">${iconMap[day.icon] || '🌤️'}</div>
          <div style="text-align: right;">
            <div style="font-weight: bold; color: #e17055;">${day.maxTemperature}°</div>
            <div style="color: #74b9ff;">${day.minTemperature}°</div>
          </div>
        </div>
      </div>
    `;
  });

  container.innerHTML = forecastHTML;
  return container;
}

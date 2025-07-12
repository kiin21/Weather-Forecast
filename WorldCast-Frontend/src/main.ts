/**
 * Main map functionality and geocoding
 */
// @ts-nocheck TODO remove when fixed

import { fetchWeatherData, checkCompleteResponse } from './services/weatherService.js';
import { createLoadingAnimation, updateLoadingMessage } from './components/LoadingAnimation.js';
import { createCurrentWeatherComponent, createForecastComponent } from './components/WeatherComponents.js';
import { initResizablePanel } from './components/ResizablePanel.js';
import { toggleDescription, sleep } from './utils/textUtils.js';
import { WeatherData } from './types/weather.js';

let map: google.maps.Map;
let marker: google.maps.Marker;
let geocoder: google.maps.Geocoder;
let weatherPanel: HTMLDivElement;
let weatherContent: HTMLDivElement;

export function initMap(): void {
  map = new google.maps.Map(document.getElementById("map") as HTMLElement, {
    zoom: 8,
    center: { lat: 10.762622, lng: 106.660172 },
    mapTypeControl: false,
  });
  geocoder = new google.maps.Geocoder();

  // Get weather panel elements
  weatherPanel = document.getElementById("weather-panel") as HTMLDivElement;
  weatherContent = document.getElementById("weather-content") as HTMLDivElement;

  // Initialize resizable panel
  initResizablePanel(weatherPanel);

  const inputText = document.createElement("input");
  inputText.type = "text";
  inputText.placeholder = "Enter a location";

  const submitButton = document.createElement("input");
  submitButton.type = "button";
  submitButton.value = "Geocode";
  submitButton.classList.add("button", "button-primary");

  const clearButton = document.createElement("input");
  clearButton.type = "button";
  clearButton.value = "Clear";
  clearButton.classList.add("button", "button-secondary");

  const instructionsElement = document.createElement("p");
  instructionsElement.id = "instructions";
  instructionsElement.innerHTML =
    "<strong>Instructions</strong>: Enter an address in the textbox to geocode or click on the map to reverse geocode.";

  map.controls[google.maps.ControlPosition.TOP_LEFT].push(inputText);
  map.controls[google.maps.ControlPosition.TOP_LEFT].push(submitButton);
  map.controls[google.maps.ControlPosition.TOP_LEFT].push(clearButton);
  map.controls[google.maps.ControlPosition.LEFT_TOP].push(instructionsElement);

  marker = new google.maps.Marker({
    map,
  });

  map.addListener("click", (e: google.maps.MapMouseEvent) => {
    geocode({ location: e.latLng });
  });

  submitButton.addEventListener("click", () =>
    geocode({ address: inputText.value })
  );

  clearButton.addEventListener("click", () => {
    clear();
  });

  clear();
}

function clear() {
  marker.setMap(null);

  // Ensure panel is properly hidden
  weatherPanel.classList.add("weather-panel-hidden");
  weatherPanel.style.width = '';

  // Clear content to prevent any lingering elements
  weatherContent.innerHTML = '';
}

function geocode(request: google.maps.GeocoderRequest): void {
  clear();

  geocoder
    .geocode(request)
    .then((result) => {
      const { results } = result;

      map.setCenter(results[0].geometry.location);
      marker.setPosition(results[0].geometry.location);
      marker.setMap(map);

      weatherPanel.classList.remove("weather-panel-hidden");

      if (!weatherPanel.style.width || weatherPanel.style.width === '0px') {
        weatherPanel.style.width = '400px';
      }

      // Call backend API to get weather data
      const { lat, lng } = results[0].geometry.location.toJSON();

      // Show loading animation
      const loadingAnimation = createLoadingAnimation();
      weatherContent.innerHTML = '';
      weatherContent.appendChild(loadingAnimation);

      // Poll for weather data with async/await
      const pollWeatherData = async () => {
        let pollCount = 0;
        const loadingMessages = [
          { text: "Fetching Weather Data", subtext: "Connecting to weather services..." },
          { text: "Processing Location", subtext: "Analyzing geographic coordinates..." },
          { text: "Gathering Forecasts", subtext: "Collecting 7-day weather predictions..." },
          { text: "Generating Insights", subtext: "Creating AI-powered descriptions..." },
          { text: "Finalizing Data", subtext: "Preparing your weather report..." }
        ];

        let receiveBasicResponse: boolean = false;
        let currentWeatherShown = false;
        let forecastShown = false;

        try {
          while (true) {
            // Fix: Remove .then() - it's incorrect with await
            const data: WeatherData = await fetchWeatherData(lat.toString(), lng.toString());

            // Check if data exists before using it
            if (!data) {
              console.log("No data received, retrying...");
              pollCount++;
              const messageIndex = Math.min(pollCount - 1, loadingMessages.length - 1);
              updateLoadingMessage(loadingAnimation, loadingMessages[messageIndex].text, loadingMessages[messageIndex].subtext);
              await sleep(2000);
              continue;
            }

            receiveBasicResponse = true;

            // Show weather data immediately when available
            if (!currentWeatherShown && (data.current || data.location)) {
              // Clear loading animation and set up the panel
              weatherContent.innerHTML = '';

              // Add close button
              const closeButton = document.createElement('button');
              closeButton.className = 'close-weather-btn';
              closeButton.innerHTML = 'x';
              closeButton.addEventListener('click', () => {
                clear();
              });
              weatherContent.appendChild(closeButton);

              currentWeatherShown = true;
            }

            // Add or update current weather component if available
            if (data.current && data.location) {
              const existingCurrent = weatherContent.querySelector('.current-weather');
              const currentWeatherComponent = createCurrentWeatherComponent(data.current, data.location);

              if (existingCurrent) {
                existingCurrent.replaceWith(currentWeatherComponent);
              } else {
                weatherContent.appendChild(currentWeatherComponent);
              }
            }

            // Add or update forecast component if available
            if (data.forecast) {
              const existingForecast = weatherContent.querySelector('.forecast');
              const forecastComponent = createForecastComponent(data.forecast);

              if (existingForecast) {
                existingForecast.replaceWith(forecastComponent);
              } else {
                weatherContent.appendChild(forecastComponent);
                forecastShown = true;
              }
            }

            // Check if we have complete response
            if (checkCompleteResponse(data)) {
              console.log("Complete weather data received");
              break;
            }

            // Update loading message only if needed
            pollCount++;
            const messageIndex = Math.min(pollCount - 1, loadingMessages.length - 1);

            // Only show loading updates if we haven't shown the basic weather yet
            if (!currentWeatherShown && loadingAnimation.parentNode) {
              updateLoadingMessage(loadingAnimation, loadingMessages[messageIndex].text, loadingMessages[messageIndex].subtext);
            }

            console.log("Partial data received, continuing to poll for complete response...");

            // Wait 2 seconds before next poll
            await sleep(2000);
          }
        } catch (error: any) {
          console.error("Weather data fetch error:", error);
          weatherContent.innerHTML = `
            <div style="
              background: #ff7675; 
              color: white; 
              padding: 15px; 
              border-radius: 8px; 
              text-align: center;
              margin: 20px;
            ">
              <div style="font-size: 1.2em; margin-bottom: 8px;">⚠️ Error</div>
              <div>Failed to fetch weather data: ${error?.message || 'Unknown error'}</div>
              <div style="font-size: 0.9em; margin-top: 8px; opacity: 0.9;">Please try again later</div>
            </div>
          `;
        }
      };

      // Start polling
      pollWeatherData();
    })
    .catch((e) => {
      alert("Geocode was not successful for the following reason: " + e);
    });
}

// Make functions available globally for HTML callbacks
declare global {
  interface Window {
    initMap: () => void;
    toggleDescription: (id: string) => void;
  }
}

window.initMap = initMap;
window.toggleDescription = toggleDescription;

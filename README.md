# WorldCast

This project is a Spring Boot application called **WorldCast**. It provides weather data and location information using external APIs (TomorrowIO, OpenStreetMap), with caching via Redis. The application is structured with REST controllers, service layers, and DTOs for clean separation of concerns.

## Features

- Fetch current and forecast weather data
- Geolocation services using OpenStreetMap
- Integration with TomorrowIO weather API
- Generate weather descriptions using Google Cloud Vertex AI
- Redis caching for performance optimization
- Global exception handling
- Interactive Google Maps interface with resizable weather panel
- Real-time weather polling with AI-generated descriptions

## Tech Stack

### Backend

- **Spring Boot** - Application framework
- **Redis** - In-memory caching

### Frontend - forked from [WorldCast-Frontend](https://github.com/googlemaps/js-samples/tree/sample-geocoding-simple)

- **TypeScript** - Programming language
- **Vite** - Build tool and development server
- **HTML5/CSS3** - Markup and styling
- **Google Maps JavaScript API** - Interactive maps

### External Services & APIs

- **TomorrowIO API** - Weather data provider
- **Google Cloud Vertex AI** - AI-powered weather descriptions
- **OpenStreetMap** - Geolocation services
- **Google Maps API** - Map visualization

## Prerequisites

- Docker
- Google Cloud Platform account (for Vertex AI)
- TomorrowIO API key
- Google Maps API key

## Configuration

Before running the application, you need to configure the following:

1. **Google Cloud Service Account**

   - Create a service account in Google Cloud Console
   - Enable Vertex AI API
   - Download the service account key JSON file
   - Place it as `WorldCast-Backend/src/main/resources/service-account-key.json`

2. **API Keys**

   - Get a TomorrowIO API key from [tomorrow.io](https://www.tomorrow.io/)
   - Get a Google Maps API key from [Google Cloud Console](https://console.cloud.google.com/)

3. **Environment Variables**
   - Update the environment variables in `compose.yml`:
     - `GCLOUD_PROJECT_ID`: Your Google Cloud project ID
     - `GCLOUD_PROJECT_LOCATION`: Your preferred location (e.g., us-central1)
     - `WEATHER_API_KEY`: Your TomorrowIO API key
     - `VITE_GOOGLE_MAPS_API_KEY`: Your Google Maps API key

## How to Run with Docker

1. **Clone the repository**

   ```sh
   git clone https://github.com/kiin21/Weather-Forecast
   cd WorldCast
   ```

2. **Configure the application**

   - Place your service account key in `WorldCast-Backend/src/main/resources/service-account-key.json`
   - Update the environment variables in `compose.yml` with your actual API keys and project details

3. **Start with Docker Compose**

   ```sh
   docker-compose up -d
   ```

   This will start:

   - Redis instance (port 6379)
   - Spring Boot backend (port 8080)
   - Frontend application (port 5173)

4. **Access the application**
   - **Frontend**: `http://localhost:5173`
   - **Backend API**: `http://localhost:8080`

## API Endpoints

- `GET /weather?location={lat,lon}` - Get weather data for a specific location

## Demo
![Demo](./docs/demo.gif)

## License

MIT License

Copyright (c) 2025 Pham Dao Anh Khoa

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

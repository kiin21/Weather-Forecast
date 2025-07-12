package org.example.worldcast.domain.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.worldcast.domain.dto.common.Location;

import java.util.List;

@Data
public class TomorrowIOWeatherResponse {
    private Timelines timelines;
    private Location location;

    // timelines block
    @Data
    public static class Timelines {
        private List<MinuteForecast> minutely;
        private List<HourlyForecast> hourly;
        private List<DailyForecast> daily;
    }

    // Minute-level forecast
    @Data
    public static class MinuteForecast {
        private String time;
        private MinuteValues values;
    }

    @Data
    public static class MinuteValues {
        private double altimeterSetting;
        private double cloudBase;
        private double cloudCeiling;
        private int cloudCover;
        private double dewPoint;
        private double freezingRainIntensity;
        private int humidity;
        private int precipitationProbability;
        private double pressureSeaLevel;
        private double pressureSurfaceLevel;
        private double rainIntensity;
        private double sleetIntensity;
        private double snowIntensity;
        private double temperature;
        private double temperatureApparent;
        private int uvHealthConcern;
        private int uvIndex;
        private double visibility;
        private int weatherCode;
        private int windDirection;
        private double windGust;
        private double windSpeed;
    }

    // Hourly-level forecast

    @Data
    public static class HourlyForecast {
        private String time;
        private HourlyValues values;
    }


    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class HourlyValues extends MinuteValues {
        private double evapotranspiration;
        private double iceAccumulation;
        private double iceAccumulationLwe;
        private double rainAccumulation;
        private double sleetAccumulation;
        private double sleetAccumulationLwe;
        private double snowAccumulation;
        private double snowAccumulationLwe;
    }

    // Daily-level forecast

    @Data
    public static class DailyForecast {
        private String time;
        private DailyValues values;
    }


    @Data
    public static class DailyValues {
        private double altimeterSettingAvg;
        private double altimeterSettingMax;
        private double altimeterSettingMin;
        private double cloudBaseAvg;
        private double cloudBaseMax;
        private double cloudBaseMin;
        private double cloudCeilingAvg;
        private double cloudCeilingMax;
        private double cloudCeilingMin;
        private int cloudCoverAvg;
        private int cloudCoverMax;
        private int cloudCoverMin;
        private double dewPointAvg;
        private double dewPointMax;
        private double dewPointMin;
        private double evapotranspirationAvg;
        private double evapotranspirationMax;
        private double evapotranspirationMin;
        private double evapotranspirationSum;
        private double freezingRainIntensityAvg;
        private double freezingRainIntensityMax;
        private double freezingRainIntensityMin;
        private int humidityAvg;
        private int humidityMax;
        private int humidityMin;
        private double iceAccumulationAvg;
        private double iceAccumulationLweAvg;
        private double iceAccumulationLweMax;
        private double iceAccumulationLweMin;
        private double iceAccumulationLweSum;
        private double iceAccumulationMax;
        private double iceAccumulationMin;
        private double iceAccumulationSum;
        private String moonriseTime;
        private String moonsetTime;
        private double precipitationProbabilityAvg;
        private double precipitationProbabilityMax;
        private double precipitationProbabilityMin;
        private double pressureSeaLevelAvg;
        private double pressureSeaLevelMax;
        private double pressureSeaLevelMin;
        private double pressureSurfaceLevelAvg;
        private double pressureSurfaceLevelMax;
        private double pressureSurfaceLevelMin;
        private double rainAccumulationAvg;
        private double rainAccumulationMax;
        private double rainAccumulationMin;
        private double rainAccumulationSum;
        private double rainIntensityAvg;
        private double rainIntensityMax;
        private double rainIntensityMin;
        private double sleetAccumulationAvg;
        private double sleetAccumulationLweAvg;
        private double sleetAccumulationLweMax;
        private double sleetAccumulationLweMin;
        private double sleetAccumulationLweSum;
        private double sleetAccumulationMax;
        private double sleetAccumulationMin;
        private double sleetIntensityAvg;
        private double sleetIntensityMax;
        private double sleetIntensityMin;
        private double snowAccumulationAvg;
        private double snowAccumulationLweAvg;
        private double snowAccumulationLweMax;
        private double snowAccumulationLweMin;
        private double snowAccumulationLweSum;
        private double snowAccumulationMax;
        private double snowAccumulationMin;
        private double snowAccumulationSum;
        private double snowIntensityAvg;
        private double snowIntensityMax;
        private double snowIntensityMin;
        private String sunriseTime;
        private String sunsetTime;
        private double temperatureApparentAvg;
        private double temperatureApparentMax;
        private double temperatureApparentMin;
        private double temperatureAvg;
        private double temperatureMax;
        private double temperatureMin;
        private int uvHealthConcernAvg;
        private int uvHealthConcernMax;
        private int uvHealthConcernMin;
        private int uvIndexAvg;
        private int uvIndexMax;
        private int uvIndexMin;
        private double visibilityAvg;
        private double visibilityMax;
        private double visibilityMin;
        private int weatherCodeMax;
        private int weatherCodeMin;
        private int windDirectionAvg;
        private double windGustAvg;
        private double windGustMax;
        private double windGustMin;
        private double windSpeedAvg;
        private double windSpeedMax;
        private double windSpeedMin;
    }
}



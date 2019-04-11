package com.wheaterapp.jeeho.weatherapp;

public class WeatherAPI {
    private static String key = "4c0d0ca0c1c68761d3994aeba9e1c602";
    private static String linkCity = "http://api.openweathermap.org/data/2.5/weather?q=";
    private static String linkGPS = "http://api.openweathermap.org/data/2.5/weather?"; //lat={lat}&lon={lon}

    public static String cityWeatherData(String cityName)
    {
        return linkCity+cityName+"&appid="+key;
    }

    public static String cityWeatherGPS(double lat, double lon)
    {
        return linkGPS+"lat="+lat+"&lon="+lon+"&appid="+key;
    }
}

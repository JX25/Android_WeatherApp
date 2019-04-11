package com.wheaterapp.jeeho.weatherapp;

import android.widget.ArrayAdapter;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WeatherAdapter {

    private static String stream;

    public List<String> getCities(){
        List<String> cities = new ArrayList<>();
        cities.add("GPS");
        cities.add("Warszawa");
        cities.add("Wrocław");
        cities.add("Kraków");
        cities.add("Gdańsk");
        cities.add("Poznań");
        cities.add("Pekin");
        cities.add("Jersey");
        cities.add("Wellington");
        cities.add("Sydney");
        return cities;
    }

    public String getWeatherData(String urlString){
       String data = null;
       try{
           URL url = new URL(urlString);
           HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
           if (httpURLConnection.getResponseCode() == 200){
               BufferedReader buff = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
               StringBuilder stringBuilder = new StringBuilder();
               String line;
               while((line = buff.readLine())!= null)
                   stringBuilder.append(line);
               data = stringBuilder.toString();
               httpURLConnection.disconnect();
           }
       }catch(MalformedURLException e)
       {
           e.printStackTrace();
       }catch(IOException e){
           e.printStackTrace();
       }
        return data;
    }


    public City createCityFromJsonData(String data){
        City city = new City();
        try{
            JSONObject json = new JSONObject(data);

            city.setName(json.getString("name"));

            JSONObject jsonBuild = json.getJSONObject("coord");
            city.setLon(Double.valueOf(jsonBuild.getString("lon")));
            city.setLat(Double.valueOf(jsonBuild.getString("lat")));

            jsonBuild = json.getJSONObject("main");
            city.setPressure(Double.valueOf(jsonBuild.getString("pressure")));
            city.setHumidity(Integer.valueOf(jsonBuild.getString("humidity")));
            city.setTemperature(Double.valueOf(jsonBuild.getString("temp")));

            jsonBuild = json.getJSONObject("wind");
            city.setWindSpeed(Double.valueOf(jsonBuild.getString("speed")));


            JSONArray jsonArray = json.getJSONArray("weather");
            jsonBuild =(JSONObject) jsonArray.get(0);
            city.setIconName("p"+jsonBuild.getString("icon"));
            city.setWeatherDescription(jsonBuild.getString("description"));
            city.setWeatherStatus(jsonBuild.getString("main"));


        }catch (JSONException e) {
            e.printStackTrace();
        }
        return city;
    }

}

package com.wheaterapp.jeeho.weatherapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private List<String> cities;
    private ListView cityList;
    private City city;
    TextView cityName;
    TextView weatherStatus;
    ImageView weatherImage;
    TextView len;
    TextView lon;
    TextView temperature;
    TextView humidity;
    TextView pressure;
    TextView wind;
    WeatherAdapter mAdapter;
    LocationManager locationManager;
    Location location;
    ConstraintLayout managerLayout;
    private String provider;
    private int MY_PERMISSION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAdapter = new WeatherAdapter();
        cities = mAdapter.getCities();
        cityList = findViewById(R.id.cityList);
        cityName = findViewById(R.id.cityName);
        len = findViewById(R.id.len);
        lon = findViewById(R.id.lon);
        weatherStatus = findViewById(R.id.weatherDesc);
        weatherImage = findViewById(R.id.weatherStatus);
        temperature = findViewById(R.id.temperature);
        humidity = findViewById(R.id.humidity);
        pressure = findViewById(R.id.pressure);
        wind = findViewById(R.id.wind);
        managerLayout = findViewById(R.id.layout);

        ArrayAdapter<String> cityAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, cities);
        cityList.setAdapter(cityAdapter);

        //get coords
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        if ((ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION);
        }
        location = locationManager.getLastKnownLocation(provider);
        if (location == null)
            Log.e("TAG", "No location found");

        cityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = (String) parent.getItemAtPosition(position);
                if( selectedOption.equals("GPS")) new AsyncClass().execute(selectedOption,String.valueOf(location.getLongitude()),String.valueOf(location.getLatitude()));
                else new AsyncClass().execute(selectedOption,"0","0");
            }
        });
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION);
        }
        locationManager.removeUpdates(this);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION);
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    private class AsyncClass extends AsyncTask<String,Void,String>{

        ProgressDialog pd = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setTitle("Wczytywanie...");
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String cityName = params[0];
            double len = Double.valueOf(params[1]);
            double lat = Double.valueOf(params[2]);
            String data = null;
            if(cityName != "GPS")  data = mAdapter.getWeatherData(WeatherAPI.cityWeatherData(cityName));
            else  data = mAdapter.getWeatherData(WeatherAPI.cityWeatherGPS(lat,len));
            return data;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            pd.dismiss();
            city = mAdapter.createCityFromJsonData(data);
            if(city.getIconName().charAt(city.getIconName().length()-1) == 'n')
                    managerLayout.setBackgroundResource(R.drawable.gradientnight);
            else
                managerLayout.setBackgroundResource(R.drawable.gradient);
            cityName.setText(city.getName());
            weatherStatus.setText(city.getWeatherStatus());
            weatherImage.setImageResource(android.R.color.transparent);
            int res = getResources().getIdentifier(city.getIconName(), "drawable", getPackageName());
            weatherImage.setImageResource(res);
            len.setText("Len: "+String.valueOf(city.getLat()));
            lon.setText("Lon: "+String.valueOf(city.getLon()));
            temperature.setText(String.valueOf(Math.round(city.getTemperature()-273))+"°C");
            humidity.setText(String.valueOf(city.getHumidity())+" %");
            pressure.setText(String.valueOf(city.getPressure())+" hPa");
            wind.setText(String.valueOf(city.getWindSpeed())+" m/s");
        }
    }
}
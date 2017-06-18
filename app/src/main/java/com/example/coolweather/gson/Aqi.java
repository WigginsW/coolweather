package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Wiggins on 2017/6/17.
 */

public class Aqi {
    public AQICity aqiCity;

    public class AQICity{

        public String aqi;
        public String pm25;
    }
}

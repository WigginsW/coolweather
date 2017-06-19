package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Wiggins on 2017/6/17.
 */

public class Aqi {

    //这个类的名字可以随便取,但是初始化名字必须和gson数据的一致？
    public City city;

    public class City{
        @SerializedName("aqi")
        public String aqi;
        @SerializedName("pm25")
        public String pm25;
    }
}

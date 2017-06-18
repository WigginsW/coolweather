package com.example.coolweather.gson;

/**
 * Created by Wiggins on 2017/6/17.
 */

import com.google.gson.annotations.SerializedName;

/***
 *  "daily_forecast":[{"date":"2016-08-08""cond":{"txt_d":"阵雨"},
 *  "temp":{"max":"34","min":"27"}},{"date":"2016-08-09"
 *  "cond":{"txt_d":"多云"},"temp":{"max":"35","min":"29"}]
 */
public class Forecast {
    public String date;

    @SerializedName("cond")
    public More more;

    @SerializedName("tmp")
    public Temperature temperature;

    public class More{
        @SerializedName("txt_d")
        public String info;
    }
    public class Temperature{
        @SerializedName("max")
        public String maxTem;
        @SerializedName("min")
        public String minTem;
    }

}

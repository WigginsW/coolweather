package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 本大爷 on 2017/6/17.
 */

public class Now {
    @SerializedName("tep")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public String info;
    }
}

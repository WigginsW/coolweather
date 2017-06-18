package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Wiggins on 2017/6/17.
 */

public class Basic {
    /***
     * 使用@SerializedName注解的方式来让JSON字段和Java字段直接建立映射关系
     */
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;

    public Update update;
    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}

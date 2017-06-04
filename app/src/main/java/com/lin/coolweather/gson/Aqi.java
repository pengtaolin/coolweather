package com.lin.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 当前空气质量实体
 */
public class Aqi {
    /**
     * 城市的空气质量信息
     */
    @SerializedName("city")
    public AqiCity city;

    /**
     * 空气质量
     */
    public class AqiCity {
        /**
         * 空气质量
         */
        @SerializedName("aqi")
        public String aqi;
        /**
         * pm2.5
         */
        @SerializedName("pm25")
        public String pm25;
    }
}

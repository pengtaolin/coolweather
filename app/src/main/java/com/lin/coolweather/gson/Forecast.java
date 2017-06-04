package com.lin.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 未来天气实体类
 */
public class Forecast {
    /**
     * 日期
     */
    public String date;
    /**
     * 气温
     */
    @SerializedName("tmp")
    public Tmperature tmperature;
    /**
     * 天气
     */
    @SerializedName("cond")
    public More more;

    /**
     * 气温
     */
    public class Tmperature {
        /**
         * 最高气温
         */
        public String max;
        /**
         * 最低温度
         */
        public String min;
    }

    /**
     * 天气
     */
    public class More {
        /**
         * 天气描述
         */
        @SerializedName("txt_d")
        public String info;
    }
}

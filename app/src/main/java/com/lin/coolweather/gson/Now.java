package com.lin.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 实时的信息
 */
public class Now {
    /**
     * 温度
     */
    @SerializedName("tmp")
    public String temperature;

    /**
     * 天气情况
     */
    @SerializedName("cond")
    public More more;

    /**
     * 天气
     */
    public class More {
        /**
         * 天气描述
         */
        @SerializedName("txt")
        public String info;
    }
}

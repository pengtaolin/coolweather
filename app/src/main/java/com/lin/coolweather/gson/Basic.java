package com.lin.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 天气基本信息实体类
 * json与java建立映射关系
 */
public class Basic {
    /**
     * 城市名称
     */
    @SerializedName("city")
    public String cityName;
    /**
     * 城市天气ID
     */
    @SerializedName("id")
    public String weatherId;
    /**
     * 天气更新
     */
    @SerializedName("update")
    public Update update;

    /**
     * 天气更新类
     */
    public class Update {
        /**
         * 更新时间
         */
        @SerializedName("loc")
        public String updateTime;
    }
}

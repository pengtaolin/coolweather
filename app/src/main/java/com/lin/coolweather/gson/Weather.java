package com.lin.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 天气实体类
 */
public class Weather {
    /**
     * 状态信息
     */
    public String status;
    /**
     * 基本信息
     */
    public Basic basic;
    /**
     * 空气质量
     */
    public Aqi aqi;
    /**
     * 当前天气
     */
    public Now now;
    /**
     * 天气建议
     */
    public Suggestion suggestion;
    /**
     * 未来天气
     */
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}

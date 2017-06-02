package com.lin.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * 县乡
 */
public class County extends DataSupport {
    /**
     * 默认ID
     */
    private int id;
    /**
     * 县名称
     */
    private String countyName;
    /**
     * 天气ID
     */
    private int weatherId;
    /**
     * 所属的城市ID（TODO 城市对象）
     */
    private int cityId;

    public int getId() {
        return id;
    }

    public int getCityId() {
        return cityId;
    }

    public int getWeatherId() {
        return weatherId;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public void setWeatherId(int weatherId) {
        this.weatherId = weatherId;
    }
}

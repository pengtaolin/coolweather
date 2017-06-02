package com.lin.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * 城市
 */
public class City extends DataSupport {
    /**
     * 默认ID
     */
    private int id;
    /**
     * 城市名称
     */
    private String cityName;
    /**
     * 城市代号
     */
    private int cityCode;
    /**
     * 所属省份代号（TODO 所属省份对象）
     */
    private  int provinceId;

    public int getId() {
        return id;
    }

    public int getCityCode() {
        return cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}

package com.lin.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * 省份
 */
public class Province extends DataSupport {
    /**
     * 默认ID
     */
    private int id;
    /**
     * 省份名称
     */
    private String provinceName;
    /**
     * 省份代号
     */
    private int provinceCode;

    public int getId() {
        return id;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }
}

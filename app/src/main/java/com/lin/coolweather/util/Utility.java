package com.lin.coolweather.util;

import android.text.TextUtils;
import android.util.Log;

import com.lin.coolweather.db.City;
import com.lin.coolweather.db.County;
import com.lin.coolweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 解析省、市、县的json数据
 */
public class Utility {
    /**
     * 解析和处理服务器端返回的省级数据
     * @param response
     * @return
     */
    public static boolean handleProvinceResponse(String response) {
        //判断响应是否为空
        if (!TextUtils.isEmpty(response)) {
            try {
                //获得json数组
                JSONArray allProvinces = new JSONArray(response);
                //循环遍历数组
                for (int i=0; i<allProvinces.length(); i++) {
                    //获得json对象
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    //实例化省份对象
                    Province province = new Province();
                    //设置省份名称
                    province.setProvinceName(provinceObject.getString("name"));
                    //设置省份名称
                    province.setProvinceCode(provinceObject.getInt("id"));
                    //保存数据到数据库中
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器端返回的市级数据
     * @param response
     * @param provinceId
     * @return
     */
    public static boolean handleCityResponse(String response, int provinceId) {
        //判断响应是否为空
        if (!TextUtils.isEmpty(response)) {
            try {
                //获得json数组
                JSONArray allCities = new JSONArray(response);
                //循环遍历数组
                for (int i=0; i<allCities.length(); i++) {
                    //获得json对象
                    JSONObject cityObject = allCities.getJSONObject(i);
                    //实例化城市对象
                    City city = new City();
                    //设置城市名称
                    city.setCityName(cityObject.getString("name"));
                    //设置城市ID
                    city.setCityCode(cityObject.getInt("id"));
                    //设置省份ID
                    city.setProvinceId(provinceId);
                    //保存数据到数据库中
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器端返回的县级数据
     * @param response
     * @param cityId
     * @return
     */
    public static boolean handleCountyResponse(String response, int cityId) {
        //判断响应是否为空
        if (!TextUtils.isEmpty(response)) {
            try {
                //获得json数组
                JSONArray allCounties = new JSONArray(response);
                //循环遍历数组
                for (int i=0; i<allCounties.length(); i++) {
                    //获得json对象
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    //实例化县级对象
                    County county = new County();
                    //设置县级名称
                    county.setCountyName(countyObject.getString("name"));
                    //设置天气ID
                    county.setWeatherId(countyObject.getString("weather_id"));
                    //设置城市ID
                    county.setCityId(cityId);
                    //保存数据到数据库中
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}

package com.lin.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 建议信息实体
 */
public class Suggestion {
    /**
     *舒适度
     */
    @SerializedName("comf")
    public Comfort comfort;

    /**
     * 洗车
     */
    @SerializedName("cw")
    public CarWash carWash;

    /**
     * 运动
     */
    @SerializedName("sport")
    public Sport sport;

    /**
     * 舒适度
     */
    public class Comfort {
        /**
         * 描述信息
         */
        @SerializedName("txt")
        public String info;
    }

    /**
     * 洗车
     */
    public class CarWash {
        /**
         * 描述信息
         */
        @SerializedName("txt")
        public String info;
    }

    /**
     * 运动
     */
    public class Sport {
        /**
         * 描述信息
         */
        @SerializedName("txt")
        public String info;
    }
}

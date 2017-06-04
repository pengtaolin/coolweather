package com.lin.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;

import com.bumptech.glide.Glide;
import com.lin.coolweather.WeatherActivity;
import com.lin.coolweather.gson.Weather;
import com.lin.coolweather.util.HttpUtil;
import com.lin.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 自动更新服务
 */
public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        //获得全局定时器服务
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        //定时为2小时
        int anHour = 2*60*60*1000;
        //设置的定时长度
        long triggerAtTime = SystemClock.elapsedRealtime()+anHour;
        //创建intent对象
        Intent i = new Intent(this, AutoUpdateService.class);
        //构建PendingIntent对象
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        //取消PendingIntent
        manager.cancel(pi);
        //设置定时器
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新天气信息
     */
    private void updateWeather() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //获得缓存数据
        String weatherString = preferences.getString("weather", null);
        //缓存不为空
        if (weatherString!=null) {
            //直接解析缓存的数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            //获得天气ID
            String weatherId = weather.basic.weatherId;
            //key值
            String key = "970dec952b264d22acbf159d86571b13";
            //请求接口
            String weatherUrl = "http://guolin.tech/api/weather?cityid="+ weatherId +"&key="+key;
            //重新发送请求
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    //解析天气信息
                    Weather weather1 = Utility.handleWeatherResponse(responseText);
                    //天气信息正确
                    if (weather1 != null && "ok".equals(weather1.status)) {
                        //放入数据
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                    }
                }
            });
        }
    }

    /**
     * 更新背景图
     */
    private void updateBingPic() {
        //图片请求地址
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        //请求数据获得相应
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            //请求失败
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            //获得响应
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //获得相应体内容
                final String bingPic = response.body().string();
                //添加到缓存中
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                //添加数据
                editor.putString("bing_pic", bingPic);
                //提交数据
                editor.apply();
            }
        });

    }
}

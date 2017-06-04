package com.lin.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获得内容提供者
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //选择的城市天气数据缓存已经存在
        if (preferences.getString("weather", null) != null) {
            //直接跳转到界面
            Intent intent = new Intent(this, WeatherActivity.class);
            //启动活动
           startActivity(intent);
           this.finish();
        }
    }
}

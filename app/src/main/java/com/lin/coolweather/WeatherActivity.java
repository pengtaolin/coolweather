package com.lin.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lin.coolweather.gson.Forecast;
import com.lin.coolweather.gson.Weather;
import com.lin.coolweather.service.AutoUpdateService;
import com.lin.coolweather.util.HttpUtil;
import com.lin.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.R.attr.x;


public class WeatherActivity extends AppCompatActivity {
    /**
     * 滑动窗口
     */
    private ScrollView weatherLayout;
    /**
     * 标题
     */
    private TextView titleCity;
    /**
     * 更新时间
     */
    private TextView titleUpdateTime;
    /**
     * 天气气温
     */
    private TextView degreeText;
    /**
     * 天气信息描述
     */
    private TextView weatherInfoText;
    /**
     * 未来几天布局
     */
    private LinearLayout forecastLayout;
    /**
     * 空气质量
     */
    private TextView aqiText;
    /**
     * pm2.5
     */
    private TextView pm25Text;
    /**
     * 舒适度
     */
    private TextView comfortText;
    /**
     * 洗车建议
     */
    private TextView carWashText;
    /**
     * 运动建议
     */
    private TextView sportText;
    /**
     * 天气背景
     */
    private ImageView bingPicImg;
    /**
     * 下拉刷新
     */
    public SwipeRefreshLayout swipeRefreshLayout;
    /**
     * 城市天气号
     */
    private String mWeatherId;
    /**
     * 侧滑动布局
     */
    public DrawerLayout drawerLayout;
    /**
     * 导航栏按键
     */
    private Button navButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //安卓的版本大于5.0
        if (Build.VERSION.SDK_INT >= 21) {
            //获得当前窗口的装饰界面
            View decorView = getWindow().getDecorView();
            //设置界面显示方式
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //设置状态栏透明色
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        //初始化控件
        initView();
        //设置下拉的颜色
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        //获得内容提供者
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //获得缓存的weather天气数据
        String weatherString = preferences.getString("weather", null);
        //缓存的天气数据不为空
        if (weatherString!=null) {
            //有缓存直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            if (weather != null) {//显示天气信息
                //保存天气ID
                mWeatherId = weather.basic.weatherId;
                //显示天气信息
                showWeatherInfo(weather);
            }
        } else {
            //无缓存数据去服务器中请求数据
            mWeatherId = getIntent().getStringExtra("weather_id");
            //滑动页面显示
            weatherLayout.setVisibility(View.VISIBLE);
            //请求天气数据
            requestWeather(mWeatherId);
        }
        //下拉刷新回调
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //请求天气信息
                requestWeather(mWeatherId);
            }
        });
        //获得缓存的图片
        String bingPic = preferences.getString("bing_pic", null);
        //缓存不为空
        if (bingPic!=null) {
            //设置背景图
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            //请求图片
            loadBingPic();
        }
        //导航栏按键返回
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开侧滑视图
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    /**
     * 初始化控件
     */
    private void initView() {
        //滑动窗口
        weatherLayout = (ScrollView)findViewById(R.id.weather_layout);
        //标题
        titleCity = (TextView)findViewById(R.id.title_city);
        //更新时间
        titleUpdateTime = (TextView)findViewById(R.id.title_update_time);
        //天气气温
        degreeText = (TextView)findViewById(R.id.degree_text);
        //天气
        weatherInfoText = (TextView)findViewById(R.id.weather_info_text);
        //未来几天
        forecastLayout = (LinearLayout)findViewById(R.id.forecast_layout);
        //空气质量
        aqiText = (TextView)findViewById(R.id.aqi_text);
        //pm2.5
        pm25Text = (TextView)findViewById(R.id.pm25_text);
        //舒适度
        comfortText = (TextView)findViewById(R.id.comfort_text);
        //洗车
        carWashText = (TextView)findViewById(R.id.car_wash_text);
        //运动
        sportText = (TextView)findViewById(R.id.sport_text);
        //天气背景
        bingPicImg = (ImageView)findViewById(R.id.bing_pic_img);
        //下拉刷新
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        //侧滑
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        //导航栏返回
        navButton = (Button)findViewById(R.id.nav_button);
    }

    /**
     * 请求加载背景图片
     */
    private void loadBingPic() {
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
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                //添加数据
                editor.putString("bing_pic", bingPic);
                //提交数据
                editor.apply();
                //更新界面数据
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }


    /**
     * 处理并显示weather实体中的数据据
     * @param weather
     */
    private void showWeatherInfo(Weather weather) {
        //城市名称
        String cityName = weather.basic.cityName;
        //更新时间
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        //天气温度
        String degree = weather.now.temperature + "℃";
        //天气
        String weatherInfo = weather.now.more.info;
        //标题栏显示
        titleCity.setText(cityName);
        //信息更新
        titleUpdateTime.setText(updateTime);
        //显示气温
        degreeText.setText(degree);
        //显示天气
        weatherInfoText.setText(weatherInfo);
        //清除所有的未来几天信息的界面
        forecastLayout.removeAllViews();
        //添加未来几天的界面
        for (Forecast forecast:weather.forecastList) {
            //创建界面
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            //日期时间
            TextView dateView = (TextView)view.findViewById(R.id.date_text);
            //天气信息
            TextView infoText = (TextView)view.findViewById(R.id.info_text);
            //最高气温
            TextView maxText = (TextView)view.findViewById(R.id.max_text);
            //最低气温
            TextView minText = (TextView)view.findViewById(R.id.min_text);
            //设置数据
            dateView.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.tmperature.max+"℃");
            minText.setText(forecast.tmperature.min+"℃");
            //添加未来几天天气的页面
            forecastLayout.addView(view);
        }
        //空气质量不为空
        if (weather.aqi != null) {
            //设置空气质量
            aqiText.setText(weather.aqi.city.aqi);
            //设置pm2.5
            pm25Text.setText(weather.aqi.city.pm25);
        }
        //设置建议数据
        String comfort = "舒适度："+weather.suggestion.comfort.info;
        String carWash = "洗车指数："+weather.suggestion.carWash.info;
        String sport = "运动指数："+weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        //界面显示
        weatherLayout.setVisibility(View.VISIBLE);
        //启动定时服务
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    /**
     * 根据天气ID请求天气信息
     * @param weatherId
     */
    public void requestWeather(String weatherId) {
        String key = "970dec952b264d22acbf159d86571b13";
        //请求接口
        String weatherUrl = "http://guolin.tech/api/weather?cityid="+ weatherId +"&key="+key;
        //发送请求
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            //请求错误
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        //停止加载
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

            }
            //响应数据
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //获得响应体的字符串
                final String responseText = response.body().string();
                //根据响应获得天气信息实体
                final Weather weather = Utility.handleWeatherResponse(responseText);
                //调用主线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //判断是否为空，且状态为OK
                        if (weather!=null && "ok".equals(weather.status)) {
                            //创建天气数据缓存
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            //添加数据缓存
                            editor.putString("weather", responseText);
                            //提交
                            editor.apply();
                            //显示天气数据
                            showWeatherInfo(weather);
                        } else {
                            //提示错误信息
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        //停止加载
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
        //加载背景图片
        loadBingPic();
    }

}

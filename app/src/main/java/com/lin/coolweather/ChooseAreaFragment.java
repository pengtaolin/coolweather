package com.lin.coolweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lin.coolweather.db.City;
import com.lin.coolweather.db.County;
import com.lin.coolweather.db.Province;
import com.lin.coolweather.util.HttpUtil;
import com.lin.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;



/**
 * 遍历省市县选择区域的碎片
 */
public class ChooseAreaFragment extends Fragment {
    /**
     * 省份等级
     */
    public static final int LEVEL_PROVINCE = 0;
    /**
     * 城市等级
     */
    public static final int LEVEL_CITY = 1;
    /**
     * 县乡等级
     */
    public static final int LEVEL_COUNTY = 2;
    /**
     * 进度条对话框
     */
    private ProgressDialog progressDialog;
    /**
     * 标题栏
     */
    private TextView titleText;
    /**
     * 返回按键
     */
    private Button backButton;
    /**
     * 列表
     */
    private ListView listView;
    /**
     * 数据适配器
     */
    private ArrayAdapter<String> adapter;
    /**
     * 列表中的数据源
     */
    private List<String> dataList = new ArrayList<>();
    /**
     * 省份数据源
     */
    private List<Province> provinceList;
    /**
     * 城市数据源
     */
    private List<City> cityList;
    /**
     * 县乡数据源
     */
    private List<County> countyList;
    /**
     * 选择的省份
     */
    private Province selectProvince;
    /**
     * 选择的城市
     */
    private City selectCity;
    /**
     * 选择的县乡
     */
    private County selectCounty;
    /**
     * 当前所属的页面
     */
    private int currentLevel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //根据布局创建界面
        View view = inflater.inflate(R.layout.choose_area,container,false);
        //获得界面中的标题栏
        titleText = (TextView) view.findViewById(R.id.title_text);
        //获得界面中的返回按键
        backButton = (Button)view.findViewById(R.id.back_button);
        //获得界面中的列表
        listView = (ListView)view.findViewById(R.id.list_view);
        //创建数据适配器
        adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,dataList);
        //添加数据设配器
        listView.setAdapter(adapter);
        //返回界面
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //列表加入点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //如果当前的是省份界面
                if (currentLevel==LEVEL_PROVINCE) {
                    //获得中的省份ID
                    selectProvince = provinceList.get(position);
                    //查询所有的城市
                    queryCities();
                } else if (currentLevel==LEVEL_CITY) {
                    //获得当前的选中的城市
                    selectCity = cityList.get(position);
                    //获得所有的县乡
                    queryCounties();
                } else if (currentLevel==LEVEL_COUNTY) { //县乡
                    //获得天气城市号
                    String weatherId = countyList.get(position).getWeatherId();
                    //当前界面是主界面
                    if (getActivity() instanceof MainActivity) {
                        //获得intent对象
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        //添加数据
                        intent.putExtra("weather_id", weatherId);
                        //发送intent
                        startActivity(intent);
                        //活动结束
                        getActivity().finish();
                    } else if (getActivity() instanceof WeatherActivity) {  //当前界面是天气界面
                        WeatherActivity weatherActivity = (WeatherActivity)getActivity();
                        //关闭侧滑界面
                        weatherActivity.drawerLayout.closeDrawers();
                        //进度条显示
                        weatherActivity.swipeRefreshLayout.setRefreshing(true);
                        //请求数据
                        weatherActivity.requestWeather(weatherId);
                    }

                }
            }
        });
        //返回按键监听
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //当前界面是县乡
                if (currentLevel==LEVEL_COUNTY) {
                    //获得所有的城市
                    queryCities();
                } else if (currentLevel==LEVEL_CITY) { //当前是城市
                    //获得所有的省份
                    queryProvinces();
                }
            }
        });
        //显示省份
        queryProvinces();
    }

    /**
     * 查询所有城市
     * 优先查找数据库
     * 再查找服务器
     */
    private void queryCities() {
        //设置所属的省份名称
        titleText.setText(selectProvince.getProvinceName());
        //返回键可见
        backButton.setVisibility(Button.VISIBLE);
        //根据省份ID查询City数据库
        cityList = DataSupport.where("provinceid = ?", String.valueOf(selectProvince.getId())).find(City.class);
        //结果不为空,数据库中有数据
        if (cityList.size() > 0) {
            //数据列表清空
            dataList.clear();
            //显示的数据列表重新赋值
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            //更新数据
            adapter.notifyDataSetChanged();
            //默认选择第一个
            listView.setSelection(0);
            //当前等级为城市
            currentLevel = LEVEL_CITY;
        } else {  //向服务器中查找数据
            //获得省份的ID
            int provinceCode = selectProvince.getProvinceCode();
            //获得请求地址
            String address = "http://guolin.tech/api/china/"+provinceCode;
            //从服务器中获得数据
            queryFromServer(address, "city");
        }
    }

    /**
     * 查询所有省份
     * 优先查找数据库
     * 再查找服务器
     */
    private void queryProvinces() {
        //设置标题名称
        titleText.setText("中国");
        //返回键可见
        backButton.setVisibility(Button.GONE);
        //查询Province数据库的所有数据
        provinceList = DataSupport.findAll(Province.class);
        //结果不为空,数据库中有数据
        if (provinceList.size() > 0) {
            //数据列表清空
            dataList.clear();
            //显示的数据列表重新赋值
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            //更新数据
            adapter.notifyDataSetChanged();
            //默认选择第一个
            listView.setSelection(0);
            //当前等级为省份
            currentLevel = LEVEL_PROVINCE;
        } else {  //向服务器中查找数据
            //获得请求地址
            String address = "http://guolin.tech/api/china";
            //从服务器中获得数据
            queryFromServer(address, "province");
        }
    }

    /**
     * 查询所有县乡
     * 优先查找数据库
     * 再查找服务器
     */
    private void queryCounties() {
        //设置所属的城市名称
        titleText.setText(selectCity.getCityName());
        //返回键可见
        backButton.setVisibility(Button.VISIBLE);
        //根据CityID查询county数据库
        countyList = DataSupport.where("cityid = ?", String.valueOf(selectCity.getId())).find(County.class);
        //结果不为空,数据库中有数据
        if (countyList.size() > 0) {
            //数据列表清空
            dataList.clear();
            //显示的数据列表重新赋值
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            //更新数据
            adapter.notifyDataSetChanged();
            //默认选择第一个
            listView.setSelection(0);
            //当前等级为县乡
            currentLevel = LEVEL_COUNTY;
        } else {  //向服务器中查找数据
            //获得省份的ID
            int provinceCode = selectProvince.getProvinceCode();
            //获得城市的ID
            int CityCode = selectCity.getCityCode();
            //获得请求地址
            String address = "http://guolin.tech/api/china/"+provinceCode+"/"+CityCode;
            //从服务器中获得数据
            queryFromServer(address, "county");
        }
    }

    /**
     * 根据传入的地址与类型查询省市县数据
     * @param address
     * @param type
     */
    private void queryFromServer(String address, final String type) {
        //显示进度条
        showProgressDialog();
        //发送数据请求
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            //请求失败
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //关闭进度条
                        closeProgressDialog();
                        //显示提示信息
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            //获得响应
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //获得响应体
                String responseText = response.body().string();
                //解析数据的结果
                boolean result = false;
                //解析省份
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {       //解析城市
                    result = Utility.handleCityResponse(responseText, selectProvince.getId());
                } else if("county".equals(type)) {  //解析县乡
                    result = Utility.handleCountyResponse(responseText, selectCity.getId());
                }

                //解析数据成功
                if (result) {
                    //数据更新，回到主线程
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //关闭进度条
                            closeProgressDialog();
                            //更新数据
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });

    }

    /**
     * 显示进度条
     */
    private void showProgressDialog() {
        //进度条为空，创建进度条
        if (progressDialog == null) {
            //创建对象
            progressDialog = new ProgressDialog(getActivity());
            //设置显示的信息
            progressDialog.setMessage("正在加载中...");
            //点击其他区域取消为false
            progressDialog.setCanceledOnTouchOutside(false);
        }
        //显示进度条
        progressDialog.show();
    }

    /**
     * 关闭进度条
     */
    private void closeProgressDialog() {
        //进度条不为空就销毁
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}

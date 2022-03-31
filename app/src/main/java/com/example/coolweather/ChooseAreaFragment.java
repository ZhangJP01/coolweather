package com.example.coolweather;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coolweather.db.City;
import com.example.coolweather.db.County;
import com.example.coolweather.db.Province;
import com.example.coolweather.utils.HttpUtil;
import com.example.coolweather.utils.Utility;

import org.json.JSONException;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 选择地区的 Fragment
 */
public class ChooseAreaFragment extends Fragment {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;

    private ArrayAdapter<String> adapter;
    // atapter 适配ListView的数据
    private List<String> dataList = new ArrayList<>();

    //省份列表
    private List<Province> provinceList;
    //市 列表
    private List<City> cityList;
    //区(县) 列表
    private List<County> countyList;


    //选中的 省份
    private Province selecedProvince;
    //选中的 市
    private City selectedCity;
    //选中的 区(县)
    private County selectedCounty;

    //当前选中的级别
    private int currentLevel;


    /**
     * 初始化 控件
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.choose_area_fragment, container, false);

        titleText = view.findViewById(R.id.title_text);
        backButton = view.findViewById(R.id.back_button);

        listView = view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selecedProvince = provinceList.get(i);
                    quertCity();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(i);
                    queryCounty();
                } else {
                    selectedCounty = countyList.get(i);
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLevel == LEVEL_COUNTY) {
                    quertCity();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvince();
                }
            }
        });

        queryProvince();
    }

    /**
     * 初始化 Activity(Fragment)
     *
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selecedProvince = provinceList.get(i);
                    quertCity();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(i);
                    queryCounty();
                } else {
                    selectedCounty = countyList.get(i);
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLevel == LEVEL_COUNTY) {
                    quertCity();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvince();
                }
            }
        });

        queryProvince();
    }


    /**
     * 查询全国的所有省，优先从数据库查询，没有的话区服务器上查询
     */
    private void queryProvince() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);   //设置back按钮不可见
        provinceList = DataSupport.findAll(Province.class); //数据库查找
        if (provinceList.size() > 0) {    //查找到了,将数据给Lsitview的lsit填充
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {   //网上服务器查找
            String address = "http:/guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }


    /**
     * 查询选中省的下属 所有 市，优先从数据库查询，没有的话区服务器查询
     */
    private void quertCity() {
        titleText.setText(selecedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceId=?",String.valueOf(selecedProvince.getId())).find(City.class);
        if (cityList.size()>0){
            dataList.clear();
            for (City city:cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else {
            int provinceCode = selecedProvince.getProvinceCode();
            String address = "http:/guolin.tech/api/china/"+provinceCode;
            queryFromServer(address, "city");
        }
    }


    /**
     * 查询选中 市的下属所有 区(县)，优先从数据库查询，没有的话从服务器查询
     */
    private void queryCounty() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityId=?",String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size()>0){
            dataList.clear();
            for (County county:countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else {
            int provinceCode = selecedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http:/guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(address, "county");
        }
    }


    /**
     * 根据传入的地址和类型从服务器上查询省市县数据
     */
    private void queryFromServer(String address, final String type) {
        //显示进度对话框
        showProgressDialog();
        //进行http请求
        HttpUtil.sendOhHttpRequst(address, new Callback() {
            //响应成功
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseText = response.body().string();   //获取相应内容
                    boolean result = false;   //判断相应结果是否解析并储存在数据库.
                    //根据查询类型进行解析存储，并确认是否成功解析result
                    if (type.equals("province")) {
                        result = Utility.handleProvinceResponse(responseText);
                    } else if (type.equals("city")) {
                        result = Utility.handleCityResponse(responseText, selecedProvince.getId());
                    } else {
                        result = Utility.handleCountyResponse(responseText, selectedCity.getId());
                    }

                    if (result) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closeProgressDialog();   //关闭进度框
                                //根据查询类型，指定对应的query，并更新listView UI
                                if (type.equals("province")) {
                                    queryProvince();
                                } else if (type.equals("city")) {
                                    quertCity();
                                } else {
                                    queryCounty();
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //响应失败
            @Override
            public void onFailure(Call call, IOException e) {
                //通过 runObThread() 方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }


    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


}
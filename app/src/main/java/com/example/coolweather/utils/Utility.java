package com.example.coolweather.utils;

import android.text.TextUtils;

import com.example.coolweather.db.City;
import com.example.coolweather.db.County;
import com.example.coolweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 解析 gson 数据
 */
public class Utility {


    /**
     * 解析和处理服务器返回的  省级数据
     */
    public static boolean handleProvinceResponse(String response) throws JSONException {
        if (!TextUtils.isEmpty(response)){
            JSONArray allProvinces = new JSONArray(response);
            for (int i = 0; i < allProvinces.length(); i++) {
                JSONObject provinceObject = allProvinces.getJSONObject(i);

                Province province = new Province();
                province.setProvinceName(provinceObject.getString("name"));
                province.setProvinceCode(provinceObject.getInt("id"));

                province.save();
            }
            return true;
        }
        return false;
    }



    /**
     * 解析和处理服务器返回的  市级数据
     */
    public static boolean handleCityResponse(String response,int provinceID) throws JSONException {
        if (!TextUtils.isEmpty(response)){
            JSONArray allProvinces = new JSONArray(response);
            for (int i = 0; i < allProvinces.length(); i++) {
                JSONObject provinceObject = allProvinces.getJSONObject(i);

                City city = new City();
                city.setCityName(provinceObject.getString("name"));
                city.setCityCode(provinceObject.getInt("id"));
                city.setProvinceId(provinceID);
                city.save();
            }
            return true;
        }
        return false;
    }



    /**
     * 解析和处理服务器返回的  区(县)级数据
     */
    public static boolean handleCountyResponse(String response,int cityID) throws JSONException {
        if (!TextUtils.isEmpty(response)){
            JSONArray allProvinces = new JSONArray(response);
            for (int i = 0; i < allProvinces.length(); i++) {
                JSONObject provinceObject = allProvinces.getJSONObject(i);

                County county = new County();
                county.setCountyName(provinceObject.getString("name"));
                county.setCountyCode(provinceObject.getInt("id"));
                county.setCityId(cityID);
                county.save();
            }
            return true;
        }
        return false;
    }

}

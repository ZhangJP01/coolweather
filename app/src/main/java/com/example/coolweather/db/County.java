package com.example.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * 区(县)  实体类
 */
public class County extends DataSupport {

    private int id;
    private String countyName;
    private int countyCode;
    private int cityID;   //上级 市 的id

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public int getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(int countyCode) {
        this.countyCode = countyCode;
    }

    public int getCityID() {
        return cityID;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }
}

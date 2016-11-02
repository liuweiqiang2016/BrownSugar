package com.alex.myapp.brownsugar.model;

import com.zeone.framework.db.annotation.Column;
import com.zeone.framework.db.annotation.Table;

/**
 * Created by liuweiqiang on 2016/9/23.
 * 开销表
 */
@Table(name = "B_DateModel")
public class DateModel {
    private int id;
    //日期编号
    @Column(column="C_CID")
    private String cid;
    //日期
    @Column(column="C_Date")
    private String date;
    //农历
    @Column(column="C_CH")
    private String ch;
    //状态 0未设定 1开始 2结束3危险
    @Column(column="C_State")
    private int state;
    //颜色
    @Column(column="C_Color")
    private int color;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCh() {
        return ch;
    }

    public void setCh(String ch) {
        this.ch = ch;
    }
    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

}

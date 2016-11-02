package com.alex.myapp.brownsugar.model;

import com.zeone.framework.db.annotation.Column;
import com.zeone.framework.db.annotation.Table;

/**
 * Created by liuweiqiang on 2016/9/23.
 */
@Table(name = "B_PersonModel")
public class PersonModel {
    private int id;
    //编号
    @Column(column="C_CID")
    private String cid;
    //月经周期
    @Column(column="C_Cycle")
    private int cycle;
    //持续时间
    @Column(column="C_Last")
    private int last;

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public int getCycle() {
        return cycle;
    }

    public void setCycle(int cycle) {
        this.cycle = cycle;
    }

    public int getLast() {
        return last;
    }

    public void setLast(int last) {
        this.last = last;
    }
}

package com.alex.myapp.brownsugar.util;

import android.content.Context;
import android.os.Environment;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.widget.Toast;

import com.alex.myapp.brownsugar.R;
import com.alex.myapp.brownsugar.model.DateModel;
import com.alex.myapp.brownsugar.model.PersonModel;
import com.zeone.framework.db.sqlite.DbUtils;
import com.zeone.framework.db.sqlite.Selector;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by liuweiqiang on 2016/10/27.
 */

public class AppUtils {

    public static final String APP_DIR = Environment.getExternalStorageDirectory() + "/BrownSugar/";
    public static final String APP_DBNAME = "BSDB";
    public static final String APP_UPDATE_URL = "https://raw.githubusercontent.com/liuweiqiang2016/Business-Assistant/master/app/versioninfo.xml";
    public static final String APP_DOWNFILE_NAME = "BrownSugar.apk";
    public static final String APP_XML_NAME = "versioninfo.xml";

    //根据日期取得星期几
    public static int getWeek(Context context, Date date) {
//        String[] weeks = {"日","一","二","三","四","五","六"};
        String[] weeks = context.getResources().getStringArray(R.array.array_weeks);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week_index < 0) {
            week_index = 0;
        }
        return week_index;
    }

    // 获取当前时间
    public static String getTime() {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式

        return df.format(new Date());// new Date()为获取当前系统时间

    }

    // 获取当前时间
    public static String getDate() {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式

        return df.format(new Date());// new Date()为获取当前系统时间

    }

    public static Date formatStringDate(String strDate) {
        Date date = null;
//        String strDate = "2013-03-08";// 定义日期字符串
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");// 定义日期格式
        try {
            date = format.parse(strDate);// 将字符串转换为日期
        } catch (ParseException e) {
            date = new Date();
        }
        return date;
    }

    //取得某个月有多少天
    public static int getDaysOfMonth(int year, int month) {
        int days = 0;
        days = new Date(year, month, 0).getDate();
        return days;
    }

    //获取前一个月有多少天
    public static int getDaysBefore(int year, int month) {
        int days_of_month = 0;
        if (month > 1) {
            days_of_month = getDaysOfMonth(year, month - 1);
        } else {
            days_of_month = getDaysOfMonth(year - 1, 12);
        }
        return days_of_month;
    }

    /**
     * 弹出toast消息
     *
     * @param context context
     * @return string 展示的内容
     */
    public static void showToast(Context context, String string) {

        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }


    //根据年月构造list
    public static List<DateModel> getDateModelByYM(Context context, int year, int month) {
        List<DateModel> list = new ArrayList<>();
        Date date = AppUtils.formatStringDate(year + "-" + month + "-" + "01");
        //本月1号是周几
        int p1 = AppUtils.getWeek(context, date);
        //上个月共多少天
        int beforeDays = AppUtils.getDaysBefore(year, month);
        for (int j = p1 - 1; j >= 0; j--) {
            String strDate = getBeforeYearAndMonth(year, month) + "-" + (beforeDays - j);
            //根据日期，从数据库中查找数据
            DateModel model = new DateModel();
            //获得当前日期 2016-10-31
            model.setDate(strDate);
            //默认正常
            model.setState(0);
            model.setCh(ChinaDateUtil.getCh(strDate));
            model.setColor(context.getResources().getColor(R.color.darker_gray));
            model.setCid(System.currentTimeMillis() + "");
            //增加到list中
            list.add(model);
        }
        //从此处：本月份日期开始
        int p2 = AppUtils.getDaysOfMonth(year, month);
        for (int k = 1; k < p2 + 1; k++) {
            String strDate = "";
            if (k < 10) {
                if (month > 9) {
                    strDate = year + "-" + month + "-0" + k;
                } else {
                    strDate = year + "-0" + month + "-0" + k;
                }

            } else {
                if (month > 9) {
                    strDate = year + "-" + month + "-" + k;
                } else {
                    strDate = year + "-0" + month + "-" + k;
                }
            }
            //根据日期，从数据库中查找数据
            DateModel model = new DateModel();
            model.setDate(strDate);
            //默认正常
            model.setState(0);
            model.setCh(ChinaDateUtil.getCh(strDate));
            model.setColor(context.getResources().getColor(R.color.black));
            model.setCid(System.currentTimeMillis() + "");
            //增加到list中
            list.add(model);
        }
        //从此处，本月份结束
        int a = list.size() % 7;
        if (a != 0) {
            a = 7 - a;
            for (int i = 1; i < a + 1; i++) {
                //获得当前日期 2016-10-31
                String strDate = getAfterYearAndMonth(year, month) + "-0" + i;
                DateModel model = new DateModel();
                model.setDate(strDate);
                //默认正常
                model.setState(0);
                model.setCh(ChinaDateUtil.getCh(strDate));
                model.setColor(context.getResources().getColor(R.color.darker_gray));
                model.setCid(System.currentTimeMillis() + "");
                //增加到list中
                list.add(model);
            }
        }

        return list;
    }


    //根据当前年月，返回上个月年月
    public static String getBeforeYearAndMonth(int year, int month) {
        String strDate = "";
        if (month > 1) {
            if (month >= 11) {
                strDate = year + "-" + (month - 1);
            } else {
                strDate = year + "-0" + (month - 1);
            }
        } else {
            strDate = (year - 1) + "-" + 12;
        }
        return strDate;
    }

    //根据当前年月，返回下个月年月
    public static String getAfterYearAndMonth(int year, int month) {
        String strDate = "";
        if (month > 11) {
            strDate = (year + 1) + "-" + 01;
        } else {
            if (month < 9) {
                strDate = year + "-0" + (month + 1);
            } else {
                strDate = year + "-" + (month + 1);
            }
        }
        return strDate;
    }

    //根据日期查找对应的model
    public static DateModel getModelByData(DbUtils db, String strDate) {
        DateModel model = null;
        model = db.findFirst(Selector.from(DateModel.class).where("C_Date", "=", strDate));
        return model;
    }

    //根据状态设定颜色
    public static int getColorByState(Context context, int state) {
        int color = 0;
        switch (state) {
            //正常
            case 0:
                color = context.getResources().getColor(R.color.black);
                break;
            //月经开始
            case 1:
                color = context.getResources().getColor(R.color.date_start);
                break;
            //经期结束
            case 2:
                color = context.getResources().getColor(R.color.date_end);
                break;
            case 3:
                color = context.getResources().getColor(R.color.date_red);
                break;
            default:
                color = context.getResources().getColor(R.color.black);
                break;
        }
        return color;
    }


    //计算日期
    public static String getDataByCount(String strData, int count) {
        String result = "";
        Date date = formatStringDate(strData);
        // 毫秒转日期
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date.getTime());
        c.add(c.DATE,count);//把日期往后增加一天.整数往后推,负数往前移动
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        result = sdf.format(c.getTime());
        return result;
    }

    //获得月经周期
    public static int getCycle(DbUtils db) {
        int cycle = 28;
        if (db != null) {
            PersonModel personModel = db.findFirst(Selector.from(PersonModel.class).where("C_CID", "=", "personId"));
            if (personModel != null) {
                cycle = personModel.getCycle();
            }
        }
        return cycle;
    }

    //获得月经持续时间
    public static int getLast(DbUtils db) {
        int last = 5;
        if (db != null) {
            PersonModel personModel = db.findFirst(Selector.from(PersonModel.class).where("C_CID", "=", "personId"));
            if (personModel != null) {
                last = personModel.getLast();
            }
        }
        return last;
    }

    //查找指定时间内所有的记录
    public static List<DateModel> getDateModelList(DbUtils db, String start, String end) {
        List<DateModel> list = new ArrayList<>();
        list = db.findAll(Selector.from(DateModel.class).where("C_Date", ">=", start).and("C_Date", "<=", end).orderBy("C_Date"));
        return list;
    }

    //查找指定时间内，所有的经期开始或近期结束记录
    public static List<DateModel> getMenstrualList(DbUtils db, String start, String end) {
        List<DateModel> list = new ArrayList<>();
        list = db.findAll(Selector.from(DateModel.class).where("C_Date", ">=", start).and("C_Date", "<=", end).and("C_State", "<", 3).and("C_State", ">", 0).orderBy("C_Date"));
        return list;
    }
    //查找指定年月内，所有的经期开始或近期结束记录
    public static List<DateModel> getMenstrualListByYM(DbUtils db,int year,int month) {
        String start="" ,end="";
        if (month>9){
            start=year+"-"+month+"-01";
            end=year+"-"+month+"-"+getDaysOfMonth(year,month);
        }else {
            start=year+"-0"+month+"-01";
            end=year+"-0"+month+"-"+getDaysOfMonth(year,month);
        }
        List<DateModel> list = new ArrayList<>();
        list = db.findAll(Selector.from(DateModel.class).where("C_Date", ">=", start).and("C_Date", "<=", end).and("C_State", "<", 3).and("C_State", ">", 0).orderBy("C_Date"));
        return list;
    }

    //查找所有的经期开始或近期结束记录
    public static List<DateModel> getMenstrualList(DbUtils db) {
        List<DateModel> list = new ArrayList<>();
        list = db.findAll(Selector.from(DateModel.class).where("C_State", "<", 3).and("C_State", ">", 0).orderBy("C_Date"));
        return list;
    }

    //获取指定日期对应的pos
    public static int getPos(String strData, List<DateModel> list) {
        int pos = -1;
        for (int i = 0; i < list.size(); i++) {
            if (strData.equals(list.get(i).getDate())) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    //去除滑动模块
    public static void disableNavigationViewScrollbars(NavigationView navigationView) {
        if (navigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }
    }

    //获取本月初始化日历
    public static List<DateModel> getInitList(Context context, List<DateModel> list, int start_pos, int end_pos) {
        for (int i = 0; i < list.size(); i++) {
            if (i >= start_pos && i <= end_pos) {
                list.get(i).setColor(context.getResources().getColor(R.color.black));
            } else {
                list.get(i).setColor(context.getResources().getColor(R.color.darker_gray));
            }

            if (list.get(i).getState() == 1) {
                list.get(i).setColor(context.getResources().getColor(R.color.date_start));
            }
            if (list.get(i).getState() == 2) {
                list.get(i).setColor(context.getResources().getColor(R.color.date_end));
            }
        }

        return list;
    }

    //根据某次经期记录(开始或结束)，计算危险期
    public static List<String> getRiskData(DbUtils db,String strData,int state){
        List<String> list=new ArrayList<>();

        //根据上次的经期，得到的下次经期
        String str="";
        if (state==1){
            str=AppUtils.getDataByCount(strData,getCycle(db)-14);
        }else {
            str=AppUtils.getDataByCount(strData,getCycle(db)-getLast(db)-14);
        }
        for (int i=-5;i<5;i++){
            list.add(getDataByCount(str,i));
        }
        return list;
    }

    //得到标准格式年月
    public static String getYYMM(int year,int month){
        String str="";
        if (month>9){
            str=year+"-"+month;
        }else {
            str=year+"-0"+month;
        }
        return str;
    }


}

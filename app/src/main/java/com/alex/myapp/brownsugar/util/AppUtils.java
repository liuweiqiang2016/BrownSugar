package com.alex.myapp.brownsugar.util;

import android.content.Context;
import android.os.Environment;
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

    public static final String APP_DIR= Environment.getExternalStorageDirectory()+"/BrownSugar/";
    public static final String APP_DBNAME= "BSDB";
    public static final String APP_UPDATE_URL= "https://raw.githubusercontent.com/liuweiqiang2016/Business-Assistant/master/app/versioninfo.xml";
    public static final String APP_DOWNFILE_NAME ="BrownSugar.apk";
    public static final String APP_XML_NAME ="versioninfo.xml";

    //根据日期取得星期几
    public static int getWeek(Context context,Date date){
//        String[] weeks = {"日","一","二","三","四","五","六"};
        String[] weeks=context.getResources().getStringArray(R.array.array_weeks);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if(week_index<0){
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

    public static Date formatStringDate(String strDate){
        Date date=null;
//        String strDate = "2013-03-08";// 定义日期字符串
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");// 定义日期格式
        try {
            date = format.parse(strDate);// 将字符串转换为日期
        } catch (ParseException e) {
            date=new Date();
        }
        return date;
    }

    //取得某个月有多少天
    public static int getDaysOfMonth(int year,int month){
        int days=0;
        days=new Date(year,month,0).getDate();
        return days;
    }

    //获取前一个月有多少天
    public static int getDaysBefore(int year,int month){
        int days_of_month=0;
        if (month>1){
            days_of_month=getDaysOfMonth(year,month-1);
        }else{
            days_of_month=getDaysOfMonth(year-1,12);
        }
        return days_of_month;
    }

    /**
     * 弹出toast消息
     * @param context  context
     * @return string 展示的内容
     */
    public static void showToast(Context context,String string){

        Toast.makeText(context,string,Toast.LENGTH_SHORT).show();
    }

    //获得日期标记状态
    public static String getOpState(Context context,int pos){
        //从数据库中查，是否存在本条数据，不存在 pos=0
        String state="";
        state=context.getResources().getStringArray(R.array.array_op)[pos];
        return state;
    }

    public static List<DateModel> getDateModelByDB(Context context, DbUtils db,int year, int month, int day){
        List<DateModel> list=new ArrayList<>();
        Date date=AppUtils.formatStringDate(year+"-"+month+"-"+"01");
        //本月1号是周几
        int p1=AppUtils.getWeek(context,date);
        //上个月共多少天
        int beforeDays=AppUtils.getDaysBefore(year,month);
        for (int j=p1-1;j>=0;j--){
            DateModel model;
            String strDate=getBeforeYearAndMonth(year,month)+"-"+(beforeDays-j);
            //根据日期，从数据库中查找数据
            model=getModelByData(db,strDate);
            if (model==null){
                model=new DateModel();
                //获得当前日期 2016-10-31
                model.setDate(strDate);
                //默认正常
                model.setState(0);
                model.setCh(ChinaDateUtil.getCh(strDate));
                model.setColor(context.getResources().getColor(R.color.darker_gray));
                model.setCid(System.currentTimeMillis()+"");
                //存储到数据库
                db.save(model);
            }
            //增加到list中
            list.add(model);
        }
        //从此处：本月份日期开始
        int p2=AppUtils.getDaysOfMonth(year,month);
        for (int k=1;k<p2+1;k++){
            DateModel model=null;
            String strDate="";
            if (k<10){
                strDate=year+"-"+month+"-0"+k;
            }else{
                strDate=year+"-"+month+"-"+k;
            }
            //根据日期，从数据库中查找数据
            model=getModelByData(db,strDate);
            if (model==null){
                model=new DateModel();
                model.setDate(strDate);
                //默认正常
                model.setState(0);
                model.setCh(ChinaDateUtil.getCh(strDate));
//                if (k==day){
//                    model.setColor(context.getResources().getColor(R.color.date_today));
//                }else {
                    model.setColor(context.getResources().getColor(R.color.black));
//                }
                model.setCid(System.currentTimeMillis()+"");
                //存储到数据库
                db.save(model);
            }
            //增加到list中
            list.add(model);
        }
        //从此处，本月份结束
        int a=list.size()%7;
        if (a!=0){
            a=7-a;
            for (int i=1;i<a+1;i++){
                DateModel model=null;
                //获得当前日期 2016-10-31
                String strDate=getAfterYearAndMonth(year,month)+"-0"+i;
                model=getModelByData(db,strDate);
                if (model==null){
                    model=new DateModel();
                    model.setDate(strDate);
                    //默认正常
                    model.setState(0);
                    model.setCh(ChinaDateUtil.getCh(strDate));
                    model.setColor(context.getResources().getColor(R.color.darker_gray));
                    model.setCid(System.currentTimeMillis()+"");
                    //存储到数据库
                    db.save(model);
                }
                //增加到list中
                list.add(model);
            }
        }

        return list;
    }

    //根据当前年月，返回上个月年月
    public static String getBeforeYearAndMonth(int year,int month){
        String strDate="";
        if (month>1){
            strDate=year+"-"+(month-1);
        }else{
            strDate=(year-1)+"-"+12;
        }
        return strDate;
    }
    //根据当前年月，返回下个月年月
    public static String getAfterYearAndMonth(int year,int month){
        String strDate="";
        if (month>11){
            strDate=(year+1)+"-"+01;
        }else{
            strDate=year+"-"+(month+1);
        }
        return strDate;
    }

    //根据日期查找对应的model
    public static DateModel getModelByData(DbUtils db,String strDate){
        DateModel model=null;
        model=db.findFirst(Selector.from(DateModel.class).where("C_Date","=",strDate));
        return model;
    }
    //根据状态设定颜色
    public static int getColorByState(Context context,int state){
        int color=0;
        switch (state){
            //正常
            case 0:
                color=context.getResources().getColor(R.color.black);
                break;
            //月经开始
            case 1:
                color=context.getResources().getColor(R.color.date_start);
                break;
            //经期结束
            case 2:
                color=context.getResources().getColor(R.color.date_end);
                break;
            case 3:
                color=context.getResources().getColor(R.color.date_red);
                break;
            default:
                color=context.getResources().getColor(R.color.black);
                break;
        }
        return color;
    }

    //根据状态预估未来
    public static String getForecastByState(int state,String strData){
        String forecast="";
        switch (state){
            //正常
            case 0:
                forecast="正常";
                break;
            //月经开始
            case 1:

                forecast="经期将于"+getDataByCount(strData,4)+"结束,"+"危险期为"+getDataByCount(strData,9)+"至"+getDataByCount(strData,18);
                break;
            //经期结束
            case 2:
//                forecast="(strData-5+28)+（排卵日及其前5天和后4天加在一起称为排卵期）";
                forecast="危险期为"+getDataByCount(strData,4)+"至"+getDataByCount(strData,13)+",下次经期将于"+getDataByCount(strData,23)+"开始";
                break;
            default:
                forecast="正常";
                break;
        }
        return forecast;
    }

    //计算日期
    public static String getDataByCount(String strData,int count){
        String result="";
        Date date=formatStringDate(strData);
        long l=count*(24*60*60*1000);
        long times=date.getTime()+l;
        // 毫秒转日期
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(times);
//        Date date = c.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        result=sdf.format(c.getTime());
        return result;
    }

    //获得月经周期
    public static int getCycle(DbUtils db){
        int cycle=28;
        if (db!=null){
            PersonModel personModel=db.findFirst(Selector.from(PersonModel.class).where("C_CID","=","personId"));
            if (personModel!=null){
                cycle=personModel.getCycle();
            }
        }
        return cycle;
    }

    //获得月经持续时间
    public static int getLast(DbUtils db){
        int last=28;
        if (db!=null){
            PersonModel personModel=db.findFirst(Selector.from(PersonModel.class).where("C_CID","=","personId"));
            if (personModel!=null){
                last=personModel.getLast();
            }
        }
        return last;
    }

    //查找指定时间内所有的记录
    public static List<DateModel> getDateModelList(DbUtils db,String start,String end){
        List<DateModel> list=new ArrayList<>();
        list=db.findAll(Selector.from(DateModel.class).where("C_Date",">=",start).and("C_Date","<=",end).orderBy("C_Date"));
        return list;
    }

    //查找指定时间内，所有的经期开始或近期结束记录
    public static List<DateModel> getMenstrualList(DbUtils db,String start,String end){
        List<DateModel> list=new ArrayList<>();
        list=db.findAll(Selector.from(DateModel.class).where("C_Date",">=",start).and("C_Date","<=",end).and("C_State","<",3).and("C_State",">",0).orderBy("C_Date"));
        return list;
    }

    //获取指定日期对应的pos
    public static int getPos(String strData,List<DateModel> list){
        int pos=0;
        for (int i = 0; i < list.size(); i++) {
            if (strData.equals(list.get(i).getDate())){
                pos=i;
                break;
            }
        }
        return pos;
    }

}
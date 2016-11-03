package com.alex.myapp.brownsugar.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.alex.myapp.brownsugar.R;
import com.alex.myapp.brownsugar.adapter.HomeAdapter;
import com.alex.myapp.brownsugar.adapter.WeekAdapter;
import com.alex.myapp.brownsugar.fragment.AboutFragment;
import com.alex.myapp.brownsugar.fragment.MarkDateFragment;
import com.alex.myapp.brownsugar.model.DateModel;
import com.alex.myapp.brownsugar.model.PersonModel;
import com.alex.myapp.brownsugar.util.AppUtils;
import com.alex.myapp.brownsugar.util.ChinaDateUtil;
import com.alex.myapp.brownsugar.util.MyDbUtils;
import com.zeone.framework.db.sqlite.DbUtils;
import com.zeone.framework.db.sqlite.Selector;
import com.zeone.framework.db.sqlite.WhereBuilder;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MarkDateFragment.MarkDateListener {

    private AboutFragment fragment;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private RecyclerView rv, rv_week;
    private HomeAdapter mAdapter;
    private TextView tv_title, tv_cur, tv_ch;
    private String title, strDate;
    private int year, month, day;

    private DbUtils db;
    private List<DateModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化数据
        initData();
        //初始化布局
        initView();
        //绑定事件
        initEvent();
        initAdapterEvent();

    }

    private void initData() {
        //初始化数据库
        //初始化可修改项目（服务和成本）
        db = MyDbUtils.getInstance().Db(this);
        //初始化个人生理周期
        PersonModel personModel=new PersonModel();
        //默认id为personId
        personModel.setCid("personId");
        //默认经期周期为28
        personModel.setCycle(28);
        //默认经期持续时间为5
        personModel.setLast(5);
        db.save(personModel);
        strDate = AppUtils.getDate();
        Date date = AppUtils.formatStringDate(strDate);
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        year = now.get(Calendar.YEAR);
        month = now.get(Calendar.MONTH) + 1; // 0-based!
        day = now.get(Calendar.DAY_OF_MONTH);
        title = year + "年" + month + "月";
    }

    private void initView() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        rv = (RecyclerView) findViewById(R.id.recyclerView);
        rv_week = (RecyclerView) findViewById(R.id.rv_week);
        tv_title = (TextView) findViewById(R.id.home_title);
        tv_cur = (TextView) findViewById(R.id.home_tv_cur);
        tv_ch = (TextView) findViewById(R.id.home_tv_ch);
//        tv_state = (TextView) findViewById(R.id.home_tv_state);
//        tv_forecast= (TextView) findViewById(R.id.home_tv_forecast);

        //绘制星期
        String[] array = getResources().getStringArray(R.array.array_weeks);
        WeekAdapter adapter = new WeekAdapter(this, array);
        rv_week.setLayoutManager(new StaggeredGridLayoutManager(7,
                StaggeredGridLayoutManager.VERTICAL));
        rv_week.setAdapter(adapter);
        //绘制日历
//        List<DateModel> list=AppUtils.getDateModels(this,year,month,day);
        list = AppUtils.getDateModelByDB(this, db, year, month, day);

        //2016-10-27
        tv_title.setText(title);
        tv_cur.setText("今日:" + strDate);
        tv_ch.setText("农历:" + ChinaDateUtil.oneDay(year, month, day));
        //查看今天是否被标记
        DateModel model = db.findFirst(Selector.from(DateModel.class).where("C_Date", "=", strDate));
        int state = 0;
        if (model != null) {
            state = model.getState();
        }
//        tv_state.setText("状态:" + AppUtils.getOpState(this, state));
//        tv_forecast.setText("预测:"+AppUtils.getForecastByState(state,model.getDate()));
//        int curPos=0;
//        for (int i = 0; i < list.size(); i++) {
//            if (model.getDate().equals(list.get(i).getDate())){
//                curPos=i;
//                break;
//            }
//        }
        getForecastView(AppUtils.getPos(strDate,list),model.getState());
        mAdapter = new HomeAdapter(this, list);
        rv.setLayoutManager(new StaggeredGridLayoutManager(7,
                StaggeredGridLayoutManager.VERTICAL));
        rv.setAdapter(mAdapter);

    }

    private void initEvent() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void initAdapterEvent() {
        mAdapter.setOnItemClickLitener(new HomeAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, DateModel model, int pos) {
                //判断是否是本月数据
                String strDate = model.getDate();
                Date date = AppUtils.formatStringDate(strDate);
                Calendar now = Calendar.getInstance();
                now.setTime(date);
                int currentMonth = now.get(Calendar.MONTH) + 1; // 0-based!
                int currentDay = now.get(Calendar.DAY_OF_MONTH);
                if (currentMonth == month) {
                    MarkDateFragment fragment = MarkDateFragment.newInstance(MainActivity.this, model, pos);
                    fragment.show(getFragmentManager(), "tag");

                } else {
                    AppUtils.showToast(MainActivity.this, "只可操作本月数据!");
                }
            }
        });
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void aboutAPP(String title) {

        AboutFragment fragment = new AboutFragment();
        toolbar.setTitle(title);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
        drawer.closeDrawers();

    }

    ;

    //根据点击，生成一个对应Fragment
    private void setFragment(String url, String title) {
        toolbar.setTitle(title);
//        fragment=new HolderFragment(url);
        fragment = AboutFragment.newInstance("1", "2");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();

        drawer.closeDrawers();
    }

    @Override
    public void onMarkDateComplete(DateModel model, int pos) {
        //标记完成后
        Message msg = mHandler.obtainMessage();
        DateModel temp = new DateModel();
        temp.setCid(model.getCid());
        temp.setDate(model.getDate());
        temp.setCh(model.getCh());
        switch (model.getState()) {
            case 0:
                //正常
                temp.setState(0);
//                if (strDate.equals(model.getDate())){
//                    temp.setColor(getResources().getColor(R.color.date_today));
////                    model.setColor(getResources().getColor(R.color.date_today));
//                }else {
                    temp.setColor(AppUtils.getColorByState(this, 0));
//                    model.setColor(AppUtils.getColorByState(this, 0));
//            }
                break;
            case 1:
                //经期开始
                temp.setState(1);
                temp.setColor(AppUtils.getColorByState(this, 1));
                break;
            case 2:
                //经期结束
                temp.setState(2);
                temp.setColor(AppUtils.getColorByState(this, 2));
                break;

        }
        //直接update 不行
        //先删除
        db.delete(DateModel.class, WhereBuilder.b("C_Date", "=", model.getDate()));
        //再添加
        db.save(temp);
        //更新view
        msg.what = 1;
        //pos
        msg.arg1 = pos;
        //state
        msg.arg2=model.getState();
        msg.obj=temp.getDate();
        mHandler.sendMessage(msg);

        AppUtils.showToast(this, "日期标记成功!");


    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 1) {

                int pos = msg.arg1;
                int state=msg.arg2;
//                mAdapter.upData(pos);
                //如果标记是今天的数据，view更新
//                if (strDate.equals((String) (msg.obj))) {
//
////                    tv_state.setText("状态:" + AppUtils.getOpState(MainActivity.this,state));
////                    tv_forecast.setText("预测:"+AppUtils.getForecastByState(state,list.get(pos).getDate()));
//                }

                getForecastView(pos,state);

//                mAdapter.upData(pos,list.size()-pos+1);


                mAdapter = new HomeAdapter(MainActivity.this, list);
                rv.setLayoutManager(new StaggeredGridLayoutManager(7,
                        StaggeredGridLayoutManager.VERTICAL));
                rv.setAdapter(mAdapter);
                initAdapterEvent();
            }
        }
    };

    //绘制预测日历
    private void getForecastView(int pos,int state){
        switch (state){
            case 0:
                //由经期开始、结束修改为正常
                List<DateModel> temp=AppUtils.getMenstrualList(db,list.get(0).getDate(),list.get(list.size()-1).getDate());
//                getForecastView(0,0);
                int mp=0;
                int ms=0;

                if (temp!=null&&temp.size()>0){
                    DateModel model=temp.get(temp.size()-1);
                    mp=AppUtils.getPos(model.getDate(),list);
                    ms=model.getState();
                    getForecastView(mp,ms);
                }else {
                    list=AppUtils.getDateModelList(db,list.get(0).getDate(),list.get(list.size()-1).getDate());
                }
                break;
            //由经期开始，预测经期结束及危险期
            case 1:

                //经期预测 暂时去除
//                if (pos+4>list.size()-1){
//                    for (int i=pos;i<list.size();i++){
//                        list.get(i).setState(1);
//                        list.get(i).setColor(AppUtils.getColorByState(MainActivity.this,1));
//                    }
//                }else {
//                    for (int i=pos;i<pos+5;i++){
//
//                        if (i==pos+4){
//                            list.get(i).setState(2);
//                            list.get(i).setColor(AppUtils.getColorByState(MainActivity.this,2));
//                        }else{
//                            list.get(i).setState(1);
//                            list.get(i).setColor(AppUtils.getColorByState(MainActivity.this,1));
//                        }
//                    }
//                }

//                list.clear();
//                list=AppUtils.getDateModelByDB(this, db, year, month, day);
                // 获取最新的list
                list=AppUtils.getDateModelList(db,list.get(0).getDate(),list.get(list.size()-1).getDate());
                //危险期预测
                int a=pos+AppUtils.getCycle(db)-14-5;//(pos+9)
                int b=pos+AppUtils.getCycle(db)-14+4;//(pos+18)
                if (a>list.size()-1){
                    return;
                }else {
                    if (b>list.size()-1){
                        for (int i=a;i<list.size();i++){
                            list.get(i).setState(3);
                            list.get(i).setColor(AppUtils.getColorByState(MainActivity.this,3));
                        }
                    }else{
                        for (int i=a;i<b;i++){
                            list.get(i).setState(3);
                            list.get(i).setColor(AppUtils.getColorByState(MainActivity.this,3));
                        }
                    }
                }

                break;
            //由经期结束，预测危险期
            case 2:
                // 获取最新的list
                list=AppUtils.getDateModelList(db,list.get(0).getDate(),list.get(list.size()-1).getDate());
                int m=pos-AppUtils.getLast(db)+28-14-5;//(pos+4)
                int n=pos-AppUtils.getLast(db)+28-14+4;//(pos+14)
                //危险期预测
                if (m>list.size()-1){
                    return;
                }else {
                    if (n>list.size()-1){
                        for (int i=m;i<list.size();i++){
                            list.get(i).setState(3);
                            list.get(i).setColor(AppUtils.getColorByState(MainActivity.this,3));
                        }
                    }else{
                        for (int i=m;i<n;i++){
                            list.get(i).setState(3);
                            list.get(i).setColor(AppUtils.getColorByState(MainActivity.this,3));
                        }
                    }
                }

                break;
        }
        //最后得到的list，回之前，今天调色
        int tp=AppUtils.getPos(strDate,list);
        if (tp<list.size()){
            list.get(tp).setColor(getResources().getColor(R.color.date_today));
        }

    }

}

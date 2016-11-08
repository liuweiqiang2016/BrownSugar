package com.alex.myapp.brownsugar.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
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
import com.alex.myapp.brownsugar.fragment.CurrentFragment;
import com.alex.myapp.brownsugar.fragment.FutureFragment;
import com.alex.myapp.brownsugar.fragment.HistoryFragment;
import com.alex.myapp.brownsugar.fragment.MarkDateFragment;
import com.alex.myapp.brownsugar.fragment.QueryHistoryFragment;
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
        implements NavigationView.OnNavigationItemSelectedListener, MarkDateFragment.MarkDateListener, QueryHistoryFragment.QueryHistoryListener {

    private AboutFragment fragment;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private CurrentFragment currentFragment;
    private FutureFragment futureFragment;
    private HistoryFragment historyFragment;

    private int year, month, day;
    private DbUtils db;

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

    }

    private void initData() {
        //初始化数据库
        //初始化可修改项目（服务和成本）
        db = MyDbUtils.getInstance().Db(this);
        PersonModel personModel = db.findFirst(Selector.from(PersonModel.class).where("C_CID", "=", "personId"));
        if (personModel == null) {
            //初始化个人生理周期
            personModel = new PersonModel();
            //默认id为personId
            personModel.setCid("personId");
            //默认经期周期为28
            personModel.setCycle(28);
            //默认经期持续时间为5
            personModel.setLast(5);
            db.save(personModel);
        }

        String strDate = AppUtils.getDate();
        Date date = AppUtils.formatStringDate(strDate);
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        year = now.get(Calendar.YEAR);
        month = now.get(Calendar.MONTH) + 1; // 0-based!
        day = now.get(Calendar.DAY_OF_MONTH);
    }

    private void initView() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setItemIconTintList(null);//设置菜单图标恢复本来的颜色
        navigationView.setCheckedItem(R.id.nav_now);//设置默认选择第一个menu子项
        AppUtils.disableNavigationViewScrollbars(navigationView);//去除滑动块

        currentFragment = CurrentFragment.newInstance(db, year, month, day);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, currentFragment)
                .commit();

    }

    private void initEvent() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

    }


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
        //设置选择menu项
        navigationView.setCheckedItem(id);
        drawer.closeDrawers();
        if (id == R.id.nav_now) {
            // Handle the camera action
            //消除卡顿时，drawer仍展开
            mHandler.sendEmptyMessageDelayed(1, 250);
        } else if (id == R.id.nav_future) {
            //消除卡顿时，drawer仍展开
            mHandler.sendEmptyMessageDelayed(2, 250);

        } else if (id == R.id.nav_history) {
            mHandler.sendEmptyMessageDelayed(3, 250);

        } else if (id == R.id.nav_note) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_update) {

        }

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
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

    //切换fragment
    private void replaceFragment(String title, Fragment fragment) {
        toolbar.setTitle(title);
//        drawer.closeDrawers();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();


    }

    @Override
    public void onMarkDateComplete(DateModel model, int pos) {
        //标记完成后
        if (currentFragment == null) {
            currentFragment = CurrentFragment.newInstance(db, year, month, day);
        }
        currentFragment.onMarkDateComplete(model, pos);
    }

    @Override
    public void QueryHistoryComplete(int state, String start, String end) {
        //历史数据查询条件设置完成
        List<DateModel> list;
        if (state == 0) {
            if (start.equals("")) {
                if (end.equals("")) {
                    //状态为全部，开始为空，结束为空
                    list = db.findAll(Selector.from(DateModel.class).orderBy("C_Date"));

                } else {
                    //状态为全部，开始为空，结束不为空
                    list = db.findAll(Selector.from(DateModel.class).where("C_Date", "<=", end).orderBy("C_Date"));
                }

            } else {
                if (end.equals("")) {
                    //状态为全部，开始不为空，结束为空
                    list = db.findAll(Selector.from(DateModel.class).where("C_Date", ">=", start).orderBy("C_Date"));

                } else {
                    //状态为全部，开始不为空，结束不为空
                    list = db.findAll(Selector.from(DateModel.class).where("C_Date", ">=", start).and("C_Date", "<=", end).orderBy("C_Date"));
                }

            }
        } else {
            if (start.equals("")) {
                if (end.equals("")) {
                    //状态指定，开始为空，结束为空
                    list = db.findAll(Selector.from(DateModel.class).where("C_State", "=", state).orderBy("C_Date"));

                } else {
                    //状态指定，开始为空，结束不为空
                    list = db.findAll(Selector.from(DateModel.class).where("C_Date", "<=", end).and("C_State", "=", state).orderBy("C_Date"));
                }

            } else {
                if (end.equals("")) {
                    //状态指定，开始不为空，结束为空
                    list = db.findAll(Selector.from(DateModel.class).where("C_Date", ">=", start).and("C_State", "=", state).orderBy("C_Date"));

                } else {
                    //状态指定，开始不为空，结束不为空
                    list = db.findAll(Selector.from(DateModel.class).where("C_Date", ">=", start).and("C_Date", "<=", end).and("C_State", "=", state).orderBy("C_Date"));
                }

            }
        }

        if (historyFragment != null) {
            historyFragment.QueryHistoryComplete(state, start, end, list);
        }


    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 1) {
                if (currentFragment == null) {
                    currentFragment = CurrentFragment.newInstance(db, year, month, day);
                }
                replaceFragment("本月信息", currentFragment);
            }

            if (msg.what == 2) {
                if (futureFragment == null) {
                    futureFragment = FutureFragment.newInstance(db, year, month);
                }
                replaceFragment("下月预测", futureFragment);
            }

            if (msg.what == 3) {
                List<DateModel> list = AppUtils.getMenstrualList(db);
                historyFragment = HistoryFragment.newInstance(list);
                replaceFragment("历史数据", historyFragment);
            }
        }
    };


}

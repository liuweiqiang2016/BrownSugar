package com.alex.myapp.brownsugar.activity;

import android.Manifest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.alex.myapp.brownsugar.R;
import com.alex.myapp.brownsugar.fragment.AboutFragment;
import com.alex.myapp.brownsugar.fragment.CheckVersionFragment;
import com.alex.myapp.brownsugar.fragment.CurrentFragment;
import com.alex.myapp.brownsugar.fragment.ExitFragment;
import com.alex.myapp.brownsugar.fragment.FileProgressFragment;
import com.alex.myapp.brownsugar.fragment.FutureFragment;
import com.alex.myapp.brownsugar.fragment.HistoryFragment;
import com.alex.myapp.brownsugar.fragment.MarkDateFragment;
import com.alex.myapp.brownsugar.fragment.NoteFragment;
import com.alex.myapp.brownsugar.fragment.QueryNoteFragment;
import com.alex.myapp.brownsugar.fragment.SettingFragment;
import com.alex.myapp.brownsugar.fragment.SettingHistoryFragment;
import com.alex.myapp.brownsugar.fragment.SettingNoteFragment;
import com.alex.myapp.brownsugar.fragment.SoftUpdateFragment;
import com.alex.myapp.brownsugar.model.DateModel;
import com.alex.myapp.brownsugar.model.NoteModel;
import com.alex.myapp.brownsugar.model.PersonModel;
import com.alex.myapp.brownsugar.model.VersionInfoModel;
import com.alex.myapp.brownsugar.model.VersionModel;
import com.alex.myapp.brownsugar.util.AppUtils;
import com.alex.myapp.brownsugar.util.DownFileUtil;
import com.alex.myapp.brownsugar.util.MyDbUtils;
import com.alex.myapp.brownsugar.util.ParseXMLUtils;
import com.zeone.framework.db.sqlite.DbUtils;
import com.zeone.framework.db.sqlite.Selector;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;
import com.zhy.m.permission.ShowRequestPermissionRationale;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MarkDateFragment.MarkDateListener, SettingHistoryFragment.QueryHistoryListener,
        SettingNoteFragment.SettingNoteListener,SoftUpdateFragment.SoftUpdateListener {

    //    private AboutFragment fragment;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private CurrentFragment currentFragment;
    private FutureFragment futureFragment;
    private HistoryFragment historyFragment;
    private NoteFragment noteFragment;
    private QueryNoteFragment queryNoteFragment;
    private SettingFragment settingFragment;
    private CheckVersionFragment checkFragment;

    private int year, month, day;
    private DbUtils db;

    private VersionInfoModel versionInfoModel;
    private SoftUpdateFragment fragment;
    private FileProgressFragment progressFragment;

    //判断是否在下载apk文件
    private boolean isDowning = false;
    private static final int REQUECT_CODE_SDCARD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //检查权限
        checkPermission();
        //初始化数据
        initData();
        //初始化布局
        initView();
        //绑定事件
        initEvent();

    }

    void checkPermission() {

        if (!MPermissions.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUECT_CODE_SDCARD)) {
            MPermissions.requestPermissions(MainActivity.this, REQUECT_CODE_SDCARD, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    ;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        MPermissions.onRequestPermissionsResult(MainActivity.this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @ShowRequestPermissionRationale(REQUECT_CODE_SDCARD)
    public void whyNeedSdCard() {
        AppUtils.showToast(this, "APP存储数据到手机内存中，需要手机内存读写权限!");
        MPermissions.requestPermissions(MainActivity.this, REQUECT_CODE_SDCARD, Manifest.permission.WRITE_EXTERNAL_STORAGE);

    }

    @PermissionGrant(REQUECT_CODE_SDCARD)
    public void requestSdcardSuccess() {
        //Toast.makeText(this, "GRANT ACCESS SDCARD!", Toast.LENGTH_SHORT).show();
    }

    @PermissionDenied(REQUECT_CODE_SDCARD)
    public void requestSdcardFailed() {
        AppUtils.showToast(this, "APP存储数据到手机内存中，需要手机内存读写权限!");
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

            ExitFragment fragment=ExitFragment.newInstance();
            fragment.show(getFragmentManager(),"tag");


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
            mHandler.sendEmptyMessageDelayed(7, 250);
        } else if (id == R.id.nav_future) {
            //消除卡顿时，drawer仍展开
            mHandler.sendEmptyMessageDelayed(8, 250);

        } else if (id == R.id.nav_history) {
            mHandler.sendEmptyMessageDelayed(9, 250);

        } else if (id == R.id.nav_note) {
            if (noteFragment == null) {
                noteFragment = NoteFragment.newInstance(db);
            }
            replaceFragment(item.getTitle().toString(), noteFragment);
        } else if (id == R.id.nav_note_query) {
            mHandler.sendEmptyMessageDelayed(10, 250);

        } else if (id == R.id.nav_setting) {
            PersonModel model = db.findFirst(Selector.from(PersonModel.class).where("C_CID", "=", "personId"));
            String cycle = "28", last = "5";
            if (model != null) {
                cycle = model.getCycle() + "";
                last = model.getLast() + "";
            }
            settingFragment = SettingFragment.newInstance(cycle, last, db);
            replaceFragment(item.getTitle().toString(), settingFragment);


        } else if (id == R.id.nav_update) {
            mHandler.sendEmptyMessage(11);

        } else if (id == R.id.nav_share) {

            AppUtils.shareMsg(this,"分享好友","分享","内容。。。。。。。。。。。",null);


        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_exit) {

            AppUtils.exitAPP(MainActivity.this);


        }
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


    //切换fragment
    private void replaceFragment(String title, Fragment fragment) {
        toolbar.setTitle(title);
//        drawer.closeDrawers();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();


    }

    //检查是否有新版本 userCheck true:用户主动检测

    private void checkVersionInfo(boolean userCheck) {
        //判断网络是否连接
        if (!AppUtils.isNetworkAvailable(MainActivity.this)) {
            if (userCheck) {//用户主动检测的，弹出toast，否则不提示
                AppUtils.showToast(MainActivity.this, "版本检测需要联网，请联网后再试!");
            }
            return;
        }
        //版本更新 检查更新规定为1小时 1小时之内不下载新的更新信息
        List<VersionModel> modelList;
        modelList = db.findAll(Selector.from(VersionModel.class));

        if (modelList == null || modelList.size() < 1 || !AppUtils.checkFileState(AppUtils.APP_XML_NAME)) {
            checkFragment = CheckVersionFragment.newInstance("版本检查中...", "");
            checkFragment.show(getFragmentManager(), "tag");
            //不存在版本数据、不存在xml文件，必定下载
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    //下载xml文件(版本升级信息)
                    DownFileUtil util = new DownFileUtil(mHandler);
                    util.getFile(AppUtils.APP_UPDATE_URL, AppUtils.APP_DIR, true);

                    return null;
                }
            }.execute();
        } else {
            //存在版本数据，比较当前时间与版本数据时间，是否间隔1小时
            if (AppUtils.checkUpdateTime(modelList.get(0).getTime())) {
                checkFragment = CheckVersionFragment.newInstance("版本检查中...", "");
                checkFragment.show(getFragmentManager(), "tag");
                //大于一小时，下载xml文件
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        //下载xml文件(版本升级信息)
                        DownFileUtil util = new DownFileUtil(mHandler);
                        util.getFile(AppUtils.APP_UPDATE_URL, AppUtils.APP_DIR, true);

                        return null;
                    }
                }.execute();

            } else {
                //无需下载xml文件，直接解析已存在的xml文件
                mHandler.sendEmptyMessage(1);
            }

        }
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case 1:
                    //解析版本更新信息xml文件
                    final String fileName = AppUtils.APP_XML_NAME;
                    try {
                        //防止xml解析时异常
                        InputStream inputStream = AppUtils.getInputStreamFromSDcard(fileName);
                        versionInfoModel = ParseXMLUtils.Parse(inputStream);
                    } catch (Exception e) {
                    } finally {
                        if (checkFragment != null) {
                            checkFragment.dismiss();
                        }
                        if (versionInfoModel != null) {
                            //版本信息下载完成后，入库
                            List<VersionModel> modelList;
                            modelList = db.findAll(Selector.from(VersionModel.class));
                            if (modelList == null || modelList.size() < 1) {
                                //不存在版本数据，添加一条数据
                                VersionModel model = new VersionModel();
                                model.setCode(versionInfoModel.getCode());
                                model.setTime(System.currentTimeMillis() + "");
                                db.save(model);
                            } else {
                                //存在版本数据，更新该条数据
                                modelList.get(0).setCode(versionInfoModel.getCode());
                                modelList.get(0).setTime(System.currentTimeMillis() + "");
                                db.update(modelList.get(0));
                            }

                            String old_code = AppUtils.getVersion(MainActivity.this);
                            //当前版本低于网络版本
                            if (AppUtils.compareVersion(old_code, versionInfoModel.getCode())) {
                                versionInfoModel.setCode_old(old_code);
                                fragment = SoftUpdateFragment.newInstance(versionInfoModel);
                                fragment.show(getFragmentManager(), "tag");
                            } else {
                                AppUtils.showToast(MainActivity.this, "当前版本已是最新版本，无需更新!");
                            }

                        }
                    }

                    break;
                case 2:
                    if (checkFragment != null) {
                        checkFragment.dismiss();
                    }
                    AppUtils.showToast(MainActivity.this, "网络异常，版本检测失败，请稍后再试!");
                    break;
                case 3:
                    //修改下载状态
                    isDowning = true;
                    //弹出下载进度提示栏
                    progressFragment = FileProgressFragment.newInstance("版本升级中", "后台下载");
                    progressFragment.show(getFragmentManager(), "tag");
                    break;
                case 4:
                    if (progressFragment != null) {
                        //更新apk下载进度
                        progressFragment.setProgress(msg.arg1);
                    }
                    break;
                case 5:
                    //apk下载完成
                    if (progressFragment != null) {
                        progressFragment.dismiss();
                    }
                    AppUtils.installApk(MainActivity.this);
                    //修改下载状态
                    isDowning = false;
                    break;
                case 6:
                    if (progressFragment != null) {
                        progressFragment.dismiss();
                    }
                    AppUtils.showToast(MainActivity.this, "网络异常，版本检测失败，请稍后再试!");
                    break;
                case 7:
                    if (currentFragment == null) {
                        currentFragment = CurrentFragment.newInstance(db, year, month, day);
                    }
                    replaceFragment("本月信息", currentFragment);
                    break;
                case 8:
                    if (futureFragment == null) {
                        futureFragment = FutureFragment.newInstance(db, year, month);
                    }
                    replaceFragment("下月预测", futureFragment);
                    break;
                case 9:
                    List<DateModel> list = AppUtils.getMenstrualList(db);
                    historyFragment = HistoryFragment.newInstance(list);
                    replaceFragment("历史数据", historyFragment);
                    break;
                case 10:
                    List<NoteModel> list1 = db.findAll(Selector.from(NoteModel.class).orderBy("C_Time"));
                    queryNoteFragment = QueryNoteFragment.newInstance(list1);
                    replaceFragment("笔记查询", queryNoteFragment);
                    break;
                case 11:
                    checkVersionInfo(true);
                    break;
                case 12:
                    break;


            }

        }
    };


    @Override
    public void SettingNoteComplete(String subject, String start, String end) {
        //笔记查询条件设置完成
        List<NoteModel> list;
        if (subject.equals("")) {
            if (start.equals("")) {
                if (end.equals("")) {
                    //主题为空，开始为空，结束为空
                    list = db.findAll(Selector.from(NoteModel.class).orderBy("C_Time"));

                } else {
                    //主题为空，开始为空，结束不为空
                    list = db.findAll(Selector.from(NoteModel.class).where("C_Time", "<=", end).orderBy("C_Time"));
                }

            } else {
                if (end.equals("")) {
                    //主题为空，开始不为空，结束为空
                    list = db.findAll(Selector.from(NoteModel.class).where("C_Time", ">=", start).orderBy("C_Time"));

                } else {
                    //主题为空，开始不为空，结束不为空
                    list = db.findAll(Selector.from(NoteModel.class).where("C_Time", ">=", start).and("C_Time", "<=", end).orderBy("C_Time"));
                }

            }
        } else {
            if (start.equals("")) {
                if (end.equals("")) {
                    //主题不为空，开始为空，结束为空
                    list = db.findAll(Selector.from(NoteModel.class).where("C_Subject", "=", subject).orderBy("C_Time"));

                } else {
                    //主题不为空，开始为空，结束不为空
                    list = db.findAll(Selector.from(NoteModel.class).where("C_Time", "<=", end).and("C_Subject", "=", subject).orderBy("C_Time"));
                }

            } else {
                if (end.equals("")) {
                    //主题不为空，开始不为空，结束为空
                    list = db.findAll(Selector.from(NoteModel.class).where("C_Time", ">=", start).and("C_Subject", "=", subject).orderBy("C_Time"));

                } else {
                    //主题不为空，开始不为空，结束不为空
                    list = db.findAll(Selector.from(NoteModel.class).where("C_Time", ">=", start).and("C_Time", "<=", end).and("C_Subject", "=", subject).orderBy("C_Time"));
                }

            }
        }

        if (queryNoteFragment != null) {
            queryNoteFragment.SettingNoteComplete(subject, start, end, list);
        }

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

    @Override
    public void onSoftUpdate(final String link) {
        if (isDowning){
            AppUtils.showToast(this,"后台正在努力下载新版本，请稍后...");
            return;
        }

        //若apk存在且apk文件的版本号大于或等于xml文件中的版本号，不再重新下载，直接安装
        if(AppUtils.checkFileState(AppUtils.APP_DOWNFILE_NAME)){
            String apkCode=AppUtils.apkCode(this);
            List<VersionModel> modelList;
            modelList = db.findAll(Selector.from(VersionModel.class));
            String xmlCode=modelList.get(0).getCode();
            //若apk文件的版本号等于xml文件版本号  或apk文件版本号高于xml文件版本号
            if (apkCode.equals(xmlCode)||AppUtils.compareVersion(xmlCode,apkCode)){
                //直接安装该apk
                mHandler.sendEmptyMessage(5);
                return;
            }
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                mHandler.sendEmptyMessage(3);
                //下载apk文件(版本升级信息)
                DownFileUtil util=new DownFileUtil(mHandler);
                util.getFile(link, AppUtils.APP_DIR, false);
                return null;
            }
        }.execute();
    }
}

package com.alex.myapp.brownsugar.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alex.myapp.brownsugar.R;
import com.alex.myapp.brownsugar.adapter.HomeAdapter;
import com.alex.myapp.brownsugar.adapter.WeekAdapter;
import com.alex.myapp.brownsugar.model.DateModel;
import com.alex.myapp.brownsugar.util.AppUtils;
import com.alex.myapp.brownsugar.util.ChinaDateUtil;
import com.zeone.framework.db.sqlite.DbUtils;
import com.zeone.framework.db.sqlite.Selector;
import com.zeone.framework.db.sqlite.WhereBuilder;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link CurrentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CurrentFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private static final String YEAR="year";
    private static final String MONTH="month";
    private static final String DAY="day";

    private RecyclerView rv, rv_week;
    private HomeAdapter mAdapter;
    private TextView tv_title, tv_cur, tv_ch;
    private String title, strDate;
    private int year, month, day;
    private static DbUtils db;
    private List<DateModel> list;

    private View view;


    public CurrentFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CurrentFragment newInstance(DbUtils mdb,int year,int month,int day) {
        CurrentFragment fragment = new CurrentFragment();
        Bundle args = new Bundle();
        args.putInt(YEAR,year);
        args.putInt(MONTH,month);
        args.putInt(DAY,day);
        db=mdb;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            year=getArguments().getInt(YEAR);
            month=getArguments().getInt(MONTH);
            day=getArguments().getInt(DAY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_current, container, false);

        //初始化数据
        initData();
        //初始化布局
        initView();
        //绑定事件
        initAdapterEvent();

        return view;
    }

    private void initData() {
        strDate = AppUtils.getDate();
        title = year + "年" + month + "月";
    }

    private void initView() {

        rv = (RecyclerView)view.findViewById(R.id.recyclerView);
        rv_week= (RecyclerView) view.findViewById(R.id.rv_week);
        tv_title = (TextView)view.findViewById(R.id.home_title);
        tv_cur = (TextView)view.findViewById(R.id.home_tv_cur);
        tv_ch = (TextView)view.findViewById(R.id.home_tv_ch);
        //绘制星期
        String[] array = getResources().getStringArray(R.array.array_weeks);
        WeekAdapter adapter = new WeekAdapter(getActivity(), array);
        rv_week.setLayoutManager(new StaggeredGridLayoutManager(7,
                StaggeredGridLayoutManager.VERTICAL));
        rv_week.setAdapter(adapter);

        tv_title.setText(title);
        tv_cur.setText("今日:" + strDate);
        tv_ch.setText("农历:" + ChinaDateUtil.oneDay(year, month, day));
        //绘制日历
        //2016-10-27
        getMarkedView(-1,0);
        mAdapter = new HomeAdapter(getActivity(), list);
        rv.setLayoutManager(new StaggeredGridLayoutManager(7,
                StaggeredGridLayoutManager.VERTICAL));
        rv.setAdapter(mAdapter);


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
                    MarkDateFragment fragment = MarkDateFragment.newInstance(getActivity(), model, pos);
                    fragment.show(getActivity().getFragmentManager(), "tag");

                } else {
                    AppUtils.showToast(getActivity(), "只可操作本月数据!");
                }
            }
        });
    };



//    @Override
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
                temp.setColor(AppUtils.getColorByState(getActivity(), 0));
                break;
            case 1:
                //经期开始
                temp.setState(1);
                temp.setColor(AppUtils.getColorByState(getActivity(), 1));
                break;
            case 2:
                //经期结束
                temp.setState(2);
                temp.setColor(AppUtils.getColorByState(getActivity(), 2));
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

        AppUtils.showToast(getActivity(), "日期标记成功!");


    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 1) {

                int pos = msg.arg1;
                int state=msg.arg2;

                getMarkedView(pos,state);

                mAdapter = new HomeAdapter(getActivity(), list);
                rv.setLayoutManager(new StaggeredGridLayoutManager(7,
                        StaggeredGridLayoutManager.VERTICAL));
                rv.setAdapter(mAdapter);
                initAdapterEvent();
            }
        }
    };


    //标记后更新view
    private void getMarkedView(int pos,int state){
        //0、重置list
        list = AppUtils.getDateModelByYM(getActivity(), year,month);
        //1、更新修改的model
        if (pos!=-1){
            list.get(pos).setState(state);
            list.get(pos).setColor(AppUtils.getColorByState(getActivity(),state));
        }
        //2、获取最近的经期记录
        List<DateModel> mList;
        DateModel model=null;
        mList=AppUtils.getMenstrualList(db);
        if (mList!=null&&mList.size()>0){
            model=mList.get(mList.size()-1);
        }
        List<String> risk;
        if (model!=null){
            //得到的预计危险期(倒数第一条记录的危险期预测）
            risk=AppUtils.getRiskData(db,model.getDate(),model.getState());
            for (int i = 0; i < list.size(); i++) {
                //所有在本月的经期记录上色
                for (int j = 0; j < mList.size(); j++) {
                    if (mList.get(j).getDate().equals(list.get(i).getDate())){
                        list.get(i).setState(mList.get(j).getState());
                        list.get(i).setColor(AppUtils.getColorByState(getActivity(),mList.get(j).getState()));
                    }
                }
                //所有在本月预计的危险期上色
                for (int k = 0; k < risk.size(); k++) {
                    if (risk.get(k).equals(list.get(i).getDate())){
                        list.get(i).setState(3);
                        list.get(i).setColor(AppUtils.getColorByState(getActivity(),3));
                    }
                }

            }

        }
        //3、设置今天view
        //最后得到的list，返回之前，今天调色
        int tp=AppUtils.getPos(strDate,list);
        list.get(tp).setColor(getResources().getColor(R.color.date_today));

    }

}

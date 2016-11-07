package com.alex.myapp.brownsugar.fragment;

import android.os.Bundle;
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
import com.zeone.framework.db.sqlite.DbUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * 下月预测
 */
public class FutureFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String YEAR = "param1";
    private static final String MONTH = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView rv, rv_week;
    private HomeAdapter mAdapter;
    private TextView tv_title;
    private String title, strDate;
    private int year, month, f_year,f_month;

    private static DbUtils db;
    private List<DateModel> list,mList;
    private View view;


    public FutureFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FutureFragment newInstance(DbUtils mdb,int year,int month) {
        FutureFragment fragment = new FutureFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        args.putInt(YEAR,year);
        args.putInt(MONTH,month);
        fragment.setArguments(args);
        db = mdb;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            year=getArguments().getInt(YEAR);
            month=getArguments().getInt(MONTH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_future, container, false);

        //初始化数据
        initData();
        //初始化布局
        initView();

        return view;
    }

    private void initData() {
        strDate=AppUtils.getAfterYearAndMonth(year,month);
        f_year=Integer.parseInt(strDate.substring(0,4));
        f_month=Integer.parseInt(strDate.substring(5,strDate.length()));
        title=f_year + "年" + f_month + "月";
        //获得上月有经期记录的list
        mList=AppUtils.getMenstrualListByYM(db,year,month);
        if (mList==null||mList.size()<1){
            AppUtils.showToast(getActivity(),"上月无经期记录，本月无法做出预测!");
        }
    }

    private void initView() {

        rv = (RecyclerView) view.findViewById(R.id.future_rv);
        rv_week = (RecyclerView) view.findViewById(R.id.future_rv_week);
        tv_title = (TextView) view.findViewById(R.id.future_title);
        tv_title.setText(title);
        //绘制星期
        String[] array = getResources().getStringArray(R.array.array_weeks);
        WeekAdapter adapter = new WeekAdapter(getActivity(), array);
        rv_week.setLayoutManager(new StaggeredGridLayoutManager(7,
                StaggeredGridLayoutManager.VERTICAL));
        rv_week.setAdapter(adapter);
//        list = AppUtils.getDateModelByYM(getActivity(), f_year, f_month);
        //绘制日历
        getMarkedView(-1,0);

        mAdapter = new HomeAdapter(getActivity(), list);
        rv.setLayoutManager(new StaggeredGridLayoutManager(7,
                StaggeredGridLayoutManager.VERTICAL));
        rv.setAdapter(mAdapter);
    }

    //标记后更新view
    private void getMarkedView(int pos,int state){
        //0、重置list
        list = AppUtils.getDateModelByYM(getActivity(),f_year, f_month);
        //1、更新修改的model
        if (pos!=-1){
            list.get(pos).setState(state);
            list.get(pos).setColor(AppUtils.getColorByState(getActivity(),state));
        }
        //2、获取上个月最近的经期记录
        List<DateModel> mList;
        DateModel model=null;
        mList=AppUtils.getMenstrualListByYM(db,year,month);
        if (mList!=null&&mList.size()>0){
            model=mList.get(mList.size()-1);
        }
        List<String> risk;
        if (model!=null){
            //得到的预计危险期(倒数第一条记录的危险期预测）
            risk=AppUtils.getRiskData(db,model.getDate(),model.getState());
            //add本月的危险期预测
            String f_data=AppUtils.getDataByCount(model.getDate(),AppUtils.getCycle(db));
            risk.addAll(AppUtils.getRiskData(db,f_data,model.getState()));
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

    }





}

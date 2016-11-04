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
    private DateModel model;


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
        }else {
            model=mList.get(mList.size()-1);
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

        list = AppUtils.getDateModelByYM(getActivity(), f_year, f_month);
        mAdapter = new HomeAdapter(getActivity(), list);
        rv.setLayoutManager(new StaggeredGridLayoutManager(7,
                StaggeredGridLayoutManager.VERTICAL));
        rv.setAdapter(mAdapter);
    }

    private void adjustList(){

        if (model!=null){
            //经期开始记录
//            if (model.getState()==1){
//                //b-a=9
//                int a=AppUtils.getCycle(db)-14-5;//(pos+9)危险期开始
//                int b=AppUtils.getCycle(db)-14+4;//(pos+18)危险期结束
//
//                //相差9天
//                String bd_start=AppUtils.getDataByCount(model.getDate(),a);//上个月推算的危险期开始
//                String bd_end=AppUtils.getDataByCount(model.getDate(),b);//上个月推算的危险期结束
//
//                //相差AppUtils.getLast(db)天
//                String c_start=AppUtils.getDataByCount(model.getDate(),AppUtils.getCycle(db));//本月经期开始
//                String c_end=AppUtils.getDataByCount(model.getDate(),AppUtils.getCycle(db)+AppUtils.getLast(db));//本月经期结束
//
//                //相差9天
//                String cd_start=AppUtils.getDataByCount(c_start,a);//本月推算的危险期开始
//                String cd_end=AppUtils.getDataByCount(c_start,b);//本月推算的危险期结束
//                int bs=-1,be=-1,cs=-1,ce=-1,cds=-1,cde=-1;
//
//                for (int i = 0; i < list.size(); i++) {
//                    if (bd_start.equals(list.get(i).getDate())){
//                        bs=i;
//                    }
//                    if (bd_end.equals(list.get(i).getDate())){
//                        be=i;
//                    }
//                    if (bs!=-1){
//
//                    }
//                    if (c_start.equals(list.get(i).getDate())){
////                        cs=i;
//                        list.get(i).setState(1);
//                        list.get(i).setColor(AppUtils.getColorByState(getActivity(),1));
//                    }
//                    if (c_end.equals(list.get(i).getDate())){
////                        ce=i;
//                        list.get(i).setState(2);
//                        list.get(i).setColor(AppUtils.getColorByState(getActivity(),2));
//                    }
//                    if (cd_start.equals(list.get(i).getDate())){
//                        cds=i;
//                    }
//                    if (cds!=-1){
//
//                    }
//                    if (cd_end.equals(list.get(i).getDate())){
//                        cde=i;
//                    }
//
//                }
//
//                //截取list
//                List<DateModel> result=list;
//
//
//            }
            String strData=AppUtils.getDataByCount(model.getDate(),AppUtils.getCycle(db));
            int pos=AppUtils.getPos(strData,list);


            getForecastView(pos,model.getState());

        }

    }

    //绘制预测日历
    private void getForecastView(int pos,int state){
        switch (state){
            //由经期开始，预测经期结束及危险期
            case 1:
                //危险期预测
                int a=pos+AppUtils.getCycle(db)-14-5;//(pos+9)
                int b=pos+AppUtils.getCycle(db)-14+4;//(pos+18)
                if (a>list.size()-1){
                    return;
                }else {
                    if (b>list.size()-1){
                        for (int i=a;i<list.size();i++){
                            list.get(i).setState(3);
                            list.get(i).setColor(AppUtils.getColorByState(getActivity(),3));
                        }
                    }else{
                        for (int i=a;i<b;i++){
                            list.get(i).setState(3);
                            list.get(i).setColor(AppUtils.getColorByState(getActivity(),3));
                        }
                    }
                }

                break;
            //由经期结束，预测危险期
            case 2:
                int m=pos-AppUtils.getLast(db)+28-14-5;//(pos+4)
                int n=pos-AppUtils.getLast(db)+28-14+4;//(pos+14)
                //危险期预测
                if (m>list.size()-1){
                    return;
                }else {
                    if (n>list.size()-1){
                        for (int i=m;i<list.size();i++){
                            list.get(i).setState(3);
                            list.get(i).setColor(AppUtils.getColorByState(getActivity(),3));
                        }
                    }else{
                        for (int i=m;i<n;i++){
                            list.get(i).setState(3);
                            list.get(i).setColor(AppUtils.getColorByState(getActivity(),3));
                        }
                    }
                }

                break;
        }
        //最后得到的list，回之前，今天调色
//        int tp=AppUtils.getPos(strDate,list);
//        list.get(tp).setColor(getResources().getColor(R.color.date_today));



    }



}

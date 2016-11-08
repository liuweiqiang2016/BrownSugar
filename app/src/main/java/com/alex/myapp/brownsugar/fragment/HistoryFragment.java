package com.alex.myapp.brownsugar.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alex.myapp.brownsugar.R;
import com.alex.myapp.brownsugar.adapter.HistoryAdapter;
import com.alex.myapp.brownsugar.model.DateModel;
import com.alex.myapp.brownsugar.util.AppUtils;
import com.alex.myapp.brownsugar.util.DividerItemDecoration;
import com.zeone.framework.db.sqlite.Selector;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView rv;
    private FloatingActionButton fab;
    private View view;
    private HistoryAdapter adapter;
    private static List<DateModel> mList;
    String TAG="TAG";

    private String his_start="",his_end="";
    private int his_pos=0;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param list Parameter 1.
     * @return A new instance of fragment AboutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(List<DateModel> list) {
        HistoryFragment fragment = new HistoryFragment();
        mList=list;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_history, container, false);

        //初始化数据
        initData();
        //初始化布局
        initView();
        //绑定事件
        initEvent();

        return view;
    }

    private void initData(){}
    private void initView(){

        rv= (RecyclerView) view.findViewById(R.id.rv_his);
        fab= (FloatingActionButton) view.findViewById(R.id.his_fab);

        if (mList==null){
            mList=new ArrayList<>();
        }

//        List<DateModel> list=new ArrayList<>();
//        for (int i=0;i<20;i++){
//            DateModel model=new DateModel();
//            model.setCid("0000000000"+i);
//            model.setDate("2016-11-"+i);
//            model.setState(i%2+1);
//            list.add(model);
//
//        }


        adapter=new HistoryAdapter(mList,getActivity());
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setAdapter(adapter);
        rv.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

    }
    private void  initEvent(){

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                QueryHistoryFragment fragment=QueryHistoryFragment.newInstance(getActivity(),his_start,his_end,his_pos);
                fragment.show(getActivity().getFragmentManager(),"tag");

            }
        });

        adapter.setOnItemClickLitener(new HistoryAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, DateModel model, int pos) {
            }
        });


    }

    public void QueryHistoryComplete(int state, String start, String end,List<DateModel> list){
        his_pos=state;
        his_start=start;
        his_end=end;
        if (list==null||list.size()<1){
            AppUtils.showToast(getActivity(),"所选条件下，无任何数据记录!");
        }
        adapter=new HistoryAdapter(list,getActivity());
        rv.setAdapter(adapter);
    }


}

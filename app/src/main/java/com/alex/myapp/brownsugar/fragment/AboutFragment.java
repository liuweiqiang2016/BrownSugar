package com.alex.myapp.brownsugar.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alex.myapp.brownsugar.R;
import com.alex.myapp.brownsugar.adapter.ManualAdapter;
import com.alex.myapp.brownsugar.adapter.ThankAdapter;
import com.alex.myapp.brownsugar.util.AppUtils;


public class AboutFragment extends Fragment {

    private TextView tv_version;
    private RecyclerView rv,rv_thk;
    private String[] titles,contents,links;
    private String version;
    private ManualAdapter adapter;
    private ThankAdapter thankAdapter;
    private View view;


    public AboutFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AboutFragment newInstance() {
        AboutFragment fragment = new AboutFragment();
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
        view=inflater.inflate(R.layout.fragment_about, container, false);
        //初始化数据
        initData();
        //初始化布局
        initView();
        return view;
    }

    private void initData() {
        titles=getResources().getStringArray(R.array.about_dev);
        contents=getResources().getStringArray(R.array.about_dev_info);
        links=getResources().getStringArray(R.array.about_thank_links);
        version= AppUtils.getVersion(getActivity());
    }
    private void initView() {
        tv_version= (TextView) view.findViewById(R.id.tv_version);
        tv_version.setText("版本:V"+version);
        rv= (RecyclerView) view.findViewById(R.id.rv_dev);
        adapter=new ManualAdapter(titles,contents,getActivity());
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setAdapter(adapter);
        rv_thk=(RecyclerView) view.findViewById(R.id.rv_thk);
        thankAdapter=new ThankAdapter(links,getActivity());
        rv_thk.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_thk.setAdapter(thankAdapter);
    }


}

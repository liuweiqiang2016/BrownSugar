package com.alex.myapp.brownsugar.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.alex.myapp.brownsugar.R;
import com.alex.myapp.brownsugar.model.DateModel;
import com.alex.myapp.brownsugar.util.AppUtils;
import com.alex.myapp.brownsugar.util.DateTimePickDialogUtil;

import java.lang.reflect.Field;


/**
 * Created by liuweiqiang on 2016/9/8.
 */
public class SettingHistoryFragment extends DialogFragment {


    TextView tv_time,tv_ch;
    int pos,state;
    Spinner sp;
    private static DateModel dateModel;
    private static Context mContext;

    private String mStart,mEnd;
    private Spinner spinner;
    private int mPosition;
    private EditText et_start,et_end;
    DateTimePickDialogUtil dialog;


    private static final String START = "param2";
    private static final String END = "param3";
    private static final String POSITION = "param4";
    public static SettingHistoryFragment newInstance(Context context, String mStart, String mEnd, int mPosition) {
        SettingHistoryFragment fragment = new SettingHistoryFragment();
        Bundle args = new Bundle();
        args.putString(START,mStart);
        args.putString(END, mEnd);
        args.putInt(POSITION,mPosition);
        mContext=context;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStart = getArguments().getString(START);
            mEnd = getArguments().getString(END);
            mPosition=getArguments().getInt(POSITION);
        }
    }
    public SettingHistoryFragment() {
    }

    //标记日期完毕后，保存按钮回调处理方法
    public interface QueryHistoryListener
    {
          void QueryHistoryComplete(int state,String start,String end);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //去除最上方标题
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        // Get the layout inflateri
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_query_history_dialog, null);
        et_start = (EditText) view.findViewById(R.id.et_his_start);
        et_end = (EditText) view.findViewById(R.id.et_his_end);
        spinner= (Spinner) view.findViewById(R.id.his_sp);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,getActivity().getResources().getStringArray(R.array.array_state));
        //adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);

        et_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String t1 = "";
                // 若EditText未设定日期，此时显示当前系统日期，否则显示EditText设定的日期
                if (et_start.getText().toString().equals("")) {

                    t1 = AppUtils.getDate();
                } else {
                    t1 = et_start.getText().toString();
                }
                if (dialog==null){
                    dialog=new DateTimePickDialogUtil(getActivity(),false);
                }
                dialog.dateTimePicKDialog(et_start,t1);
            }
        });

        et_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String t2 = "";
                // 若EditText未设定日期，此时显示当前系统日期，否则显示EditText设定的日期
                if (et_end.getText().toString().equals("")) {

                    t2 = AppUtils.getDate();
                } else {
                    t2 = et_end.getText().toString();
                }
                if (dialog==null){
                    dialog=new DateTimePickDialogUtil(getActivity(),false);
                }
                dialog.dateTimePicKDialog(et_end,t2);
            }
        });

        initData();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("查询",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {

                                String start= et_start.getText().toString();
                                String end= et_end.getText().toString();
                                if (AppUtils.compareDate(end,start)){
                                    QueryHistoryListener listener= (QueryHistoryListener) getActivity();
                                    listener.QueryHistoryComplete(spinner.getSelectedItemPosition(),start,end);
                                    try
                                    {
                                        Field field = dialog.getClass()
                                                .getSuperclass().getDeclaredField(
                                                        "mShowing" );
                                        field.setAccessible( true );
                                        // 将mShowing变量设为false，表示对话框已关闭
                                        field.set(dialog, true );
                                        dialog.dismiss();
                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }else {
                                    AppUtils.showToast(getActivity(),"结束时间需晚于开始时间!");
                                    try
                                    {
                                        Field field = dialog.getClass()
                                                .getSuperclass().getDeclaredField(
                                                        "mShowing" );
                                        field.setAccessible( true );
                                        // 将mShowing变量设为false，表示对话框已关闭
                                        field.set(dialog, false );
                                        dialog.dismiss();
                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }

                                }

                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try
                {
                    Field field = dialog.getClass()
                            .getSuperclass().getDeclaredField(
                                    "mShowing" );
                    field.setAccessible( true );
                    // 将mShowing变量设为false，表示对话框已关闭
                    field.set(dialog, true );
                    dialog.dismiss();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        return builder.create();

    }

    //赋值数据
    void initData(){

        et_start.setText(mStart);
        et_end.setText(mEnd);
        spinner.setSelection(mPosition);

    };



}

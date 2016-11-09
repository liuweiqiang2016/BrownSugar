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
public class SettingNoteFragment extends DialogFragment {


    TextView tv_time,tv_ch;
    int pos,state;
    Spinner sp;
    private static DateModel dateModel;
    private static Context mContext;

    private String mStart,mEnd,mSubject;
    private EditText et_start,et_end,et_subject;
    DateTimePickDialogUtil dialog;


    private static final String START = "param2";
    private static final String END = "param3";
    private static final String SUBJECT = "param4";
    public static SettingNoteFragment newInstance(Context context, String mStart, String mEnd, String mSubject) {
        SettingNoteFragment fragment = new SettingNoteFragment();
        Bundle args = new Bundle();
        args.putString(START,mStart);
        args.putString(END, mEnd);
        args.putString(SUBJECT,mSubject);
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
            mSubject = getArguments().getString(SUBJECT);
        }
    }
    public SettingNoteFragment() {
    }

    //标记日期完毕后，保存按钮回调处理方法
    public interface SettingNoteListener
    {
          void SettingNoteComplete(String subject, String start, String end);
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
        View view = inflater.inflate(R.layout.fragment_query_note_dialog, null);
        et_start = (EditText) view.findViewById(R.id.et_his_start);
        et_end = (EditText) view.findViewById(R.id.et_his_end);
        et_subject= (EditText) view.findViewById(R.id.note_subject);

        et_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String t1 = "";
                // 若EditText未设定日期，此时显示当前系统日期，否则显示EditText设定的日期
                if (et_start.getText().toString().equals("")) {

                    t1 = AppUtils.getTime();
                } else {
                    t1 = et_start.getText().toString();
                }
                if (dialog==null){
                    dialog=new DateTimePickDialogUtil(getActivity(),true);
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

                    t2 = AppUtils.getTime();
                } else {
                    t2 = et_end.getText().toString();
                }
                if (dialog==null){
                    dialog=new DateTimePickDialogUtil(getActivity(),true);
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
                                String subject=et_subject.getText().toString().trim();
                                if (AppUtils.compareDate(end,start)){
                                    SettingNoteListener listener= (SettingNoteListener) getActivity();
                                    listener.SettingNoteComplete(subject,start,end);
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
        et_subject.setText(mSubject);

    };



}

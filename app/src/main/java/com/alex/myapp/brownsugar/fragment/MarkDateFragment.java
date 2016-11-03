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
import android.widget.Spinner;
import android.widget.TextView;

import com.alex.myapp.brownsugar.R;
import com.alex.myapp.brownsugar.model.DateModel;
import com.alex.myapp.brownsugar.util.AppUtils;

import java.lang.reflect.Field;


/**
 * Created by liuweiqiang on 2016/9/8.
 */
public class MarkDateFragment extends DialogFragment {


    TextView tv_time,tv_ch;
//    String time,ch;
    int pos,state;
    Spinner sp;
    private static DateModel dateModel;
    private static Context mContext;

    private static final String TIME = "param1";
    private static final String CH = "param2";
    private static final String STATE = "param3";
    private static final String POS = "param4";

    public static MarkDateFragment newInstance(Context context,DateModel model, int pos) {
        MarkDateFragment fragment = new MarkDateFragment();
        Bundle args = new Bundle();
//        args.putString(TIME,model.getDate());
//        args.putString(CH,model.getCh());
//        args.putInt(STATE,model.getState());
        args.putInt(POS,pos);
        dateModel=model;
        mContext=context;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            time = getArguments().getString(TIME);
//            ch = getArguments().getString(CH);
//            state = getArguments().getInt(STATE);
            pos = getArguments().getInt(POS);
        }
    }
    public MarkDateFragment() {
    }

    //标记日期完毕后，保存按钮回调处理方法
    public interface MarkDateListener
    {
//        void onMarkDateComplete(String date,String ch,int state,int pos);
          void onMarkDateComplete(DateModel model,int pos);
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
        View view = inflater.inflate(R.layout.fragment_mark_date_dialog, null);
        tv_time= (TextView) view.findViewById(R.id.mark_tv_date);
        tv_ch= (TextView) view.findViewById(R.id.mark_tv_ch);
        sp= (Spinner) view.findViewById(R.id.mark_sp);
        initView();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {
                                //如果数据改动了：即当前position和进入前不同
                                if (sp.getSelectedItemPosition()!=state){
                                        MarkDateListener listener= (MarkDateListener) getActivity();
//                                    listener.onMarkDateComplete(time,ch,sp.getSelectedItemPosition(),pos);
                                        //修改状态
                                        dateModel.setState(sp.getSelectedItemPosition());
                                        //修改颜色
                                        dateModel.setColor(AppUtils.getColorByState(mContext,sp.getSelectedItemPosition()));
                                        listener.onMarkDateComplete(dateModel,pos);

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
                                }else{
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

                            }
                        }
                )
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
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
    void initView(){

        tv_time.setText("日期:"+dateModel.getDate());
        tv_ch.setText("农历:"+dateModel.getCh());
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.array_op));
        //adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        sp.setAdapter(adapter);
        state=0;
        if (dateModel.getState()==3){
            state=0;
        }else {
            state=dateModel.getState();
        }
        sp.setSelection(state);

    };


}

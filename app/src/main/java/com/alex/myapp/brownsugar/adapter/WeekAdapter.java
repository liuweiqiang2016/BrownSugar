package com.alex.myapp.brownsugar.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alex.myapp.brownsugar.R;
import com.alex.myapp.brownsugar.model.DateModel;
import com.alex.myapp.brownsugar.util.AppUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.MyViewHolder> {

    private LayoutInflater mInflater;
    private Context mContext;

    private String[] mArray;

    public WeekAdapter(Context context,String[] array) {
        mContext = context;
        mArray=array;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mInflater = LayoutInflater.from(mContext);
        MyViewHolder holder = new MyViewHolder(mInflater.inflate(
                R.layout.item_week, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.tv.setText(mArray[position]);
        holder.tv.setTextColor(mContext.getResources().getColor(R.color.black));
        TextPaint tp = holder.tv.getPaint();
        tp.setFakeBoldText(true);

    }

    @Override
    public int getItemCount() {
        return mArray.length;
    }


    class MyViewHolder extends ViewHolder {

        TextView tv;
        RelativeLayout rl;

        public MyViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.id_tv);
            rl= (RelativeLayout) view.findViewById(R.id.home_item);
        }
    }
}
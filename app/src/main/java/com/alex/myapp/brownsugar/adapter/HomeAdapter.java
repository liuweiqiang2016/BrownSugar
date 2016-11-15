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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

    private LayoutInflater mInflater;
    private Context mContext;

    private List<DateModel> mList=new ArrayList<>();

    public interface OnItemClickLitener {

        void onItemClick(View view,DateModel model,int pos);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }


    public HomeAdapter(Context context, List<DateModel> list) {
        mContext = context;
        if (list!=null){
            mList=list;
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mInflater = LayoutInflater.from(mContext);
        MyViewHolder holder = new MyViewHolder(mInflater.inflate(
                R.layout.item_home, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        String strDate=mList.get(position).getDate();
        Date date= AppUtils.formatStringDate(strDate);
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        int day=now.get(Calendar.DAY_OF_MONTH);

        holder.tv.setText(day+"");
        holder.rl.setBackgroundResource(R.drawable.item_bg);
        holder.tv.setTextColor(mList.get(position).getColor());

        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();

                    mOnItemClickLitener.onItemClick(holder.itemView,mList.get(pos),pos);

                }
            });


        }


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void addData(int position) {
        //mDatas.add(position, "Insert One");
        notifyItemInserted(position);
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
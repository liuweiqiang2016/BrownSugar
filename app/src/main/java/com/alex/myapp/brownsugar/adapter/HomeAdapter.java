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

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

    //	private List<String> mDatas;
    private LayoutInflater mInflater;
    private Context mContext;

//    private List<String> mList;
    private List<Integer> mColors;
    private int mStart = -1, mEnd = -1;

    private List<DateModel> mList;

    public interface OnItemClickLitener {
//        void onItemClick(View view, int position,String day,boolean isCurrent,int start,int end,int total);

//        void onItemLongClick(View view, int position);

        void onItemClick(View view,DateModel model,int pos);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }


    public HomeAdapter(Context context, List<DateModel> list) {
        mContext = context;
        mList=list;
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

//        if (position < 7) {
//            holder.tv.setTextColor(mContext.getResources().getColor(R.color.black));
//            TextPaint tp = holder.tv.getPaint();
//            tp.setFakeBoldText(true);
//        } else {
//
//            holder.rl.setBackgroundResource(R.drawable.item_bg);
//
//            if (position > mStart && position < mEnd) {
//                holder.tv.setTextColor(mContext.getResources().getColor(R.color.black));
//                if (position == mPos) {
//                    holder.tv.setTextColor(mContext.getResources().getColor(R.color.date_today));
//                }
//            } else {
//                holder.tv.setTextColor(mContext.getResources().getColor(R.color.darker_gray));
//            }
//        }


//        if (position < 7) {
//            TextPaint tp = holder.tv.getPaint();
//            tp.setFakeBoldText(true);
//        }else{// 如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();

                    mOnItemClickLitener.onItemClick(holder.itemView,mList.get(pos),pos);

//                    boolean isCurrent=false;
//                    if (pos > mStart && pos < mEnd) {
//                        isCurrent=true;
//                    }
//                    mOnItemClickLitener.onItemClick(holder.itemView, pos,mList.get(position),isCurrent,mStart,mEnd,mList.size());
                }
            });
//            holder.rl.setBackgroundResource(R.drawable.item_bg);
//        }
//        holder.tv.setTextColor(mColors.get(position));


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

//    public void upData(int position, int total) {
//        //mDatas.add(position, "Insert One");
//        mTotal = total;
//        notifyItemChanged(position);
//    }
    public void upData(int pos) {
        //mDatas.add(position, "Insert One");
//        mTotal = total;
        notifyItemChanged(pos);
    }

    public void upData(int pos,int count) {
        //mDatas.add(position, "Insert One");
//        mTotal = total;
//        notifyItemChanged(position);
        notifyItemRangeChanged(pos,count);
    }


    public void removeData(int position) {
        //mDatas.remove(position);
        notifyItemRemoved(position);
    }

    class MyViewHolder extends ViewHolder {

        TextView tv, total;
        RelativeLayout rl;

        public MyViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.id_tv);
            rl= (RelativeLayout) view.findViewById(R.id.home_item);
        }
    }
}
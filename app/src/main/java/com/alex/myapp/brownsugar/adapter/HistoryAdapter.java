package com.alex.myapp.brownsugar.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alex.myapp.brownsugar.R;
import com.alex.myapp.brownsugar.model.DateModel;
import com.alex.myapp.brownsugar.util.MaqueeTextView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by uu on 2016/9/7.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    private List<DateModel> mList=new ArrayList<>();
    private Context mContext;

    public interface OnItemClickLitener {

        void onItemClick(View view,DateModel model,int pos);
    }
    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public HistoryAdapter(List<DateModel> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(mContext).inflate(
                R.layout.item_hislist, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder,final int position) {

        //取后十位
        String cid=mList.get(position).getCid();
        holder.id.setText(cid.substring(cid.length()-10,cid.length()));
        holder.date.setText(mList.get(position).getDate());
        if (mList.get(position).getState()==1){
            holder.state.setText("经期开始");
        }
        if (mList.get(position).getState()==2){
            holder.state.setText("经期结束");

        }

        if (mOnItemClickLitener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
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

    class MyViewHolder extends RecyclerView.ViewHolder
    {

        MaqueeTextView id;//项目名称
        TextView date;//项目数量
        TextView state;//项目单价


        public MyViewHolder(View view)
        {
            super(view);
            id = (MaqueeTextView) view.findViewById(R.id.his_id);
            date = (TextView) view.findViewById(R.id.his_date);
            state = (TextView) view.findViewById(R.id.his_state);
        }
    }

}

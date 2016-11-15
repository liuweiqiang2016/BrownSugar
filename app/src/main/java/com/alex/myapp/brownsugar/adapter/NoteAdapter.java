package com.alex.myapp.brownsugar.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alex.myapp.brownsugar.R;
import com.alex.myapp.brownsugar.model.NoteModel;
import com.alex.myapp.brownsugar.util.MaqueeTextView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by uu on 2016/9/7.
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyViewHolder> {

    private List<NoteModel> mList=new ArrayList<>();
    private Context mContext;

    public interface OnItemClickLitener {

        void onItemClick(View view, NoteModel model, int pos);
    }
    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public NoteAdapter(List<NoteModel> mList, Context mContext) {
        if (mList!=null){
            this.mList = mList;
        }
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(mContext).inflate(
                R.layout.item_notelist, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder,final int position) {

        holder.id.setText(mList.get(position).getTime());
        holder.date.setText(mList.get(position).getSubject());


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

    public void changeData(List<NoteModel> list){
        mList=list;
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        if (mList!=null){
            return mList.size();
        }
        return 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {

        MaqueeTextView id,date;//项目数量


        public MyViewHolder(View view)
        {
            super(view);
            id = (MaqueeTextView) view.findViewById(R.id.his_id);
            date = (MaqueeTextView) view.findViewById(R.id.his_date);
        }
    }

}

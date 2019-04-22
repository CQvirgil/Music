package com.virgil.study.music;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    LayoutInflater inflater;
    int type;
    List<String> list;
    OnItemClickListener monItemClickListener;


    public RecyclerAdapter(Context context, int type, List<String> list) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.type = type;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {//用来绑定item的布局
        RecyclerView.ViewHolder holder = null;
        View view = null;

        if(type ==1){
            view = inflater.inflate(R.layout.item,parent,false);
            holder = new itemHolder(view);
        }else if(type >=2 ){
            view = inflater.inflate(R.layout.item,parent,false);
            holder = new itemHolder(view);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {//用来更新item数据
       itemHolder itemholder = (itemHolder) holder;
       itemholder.textView.setText(list.get(position));
       if(monItemClickListener!=null){
           holder.itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   monItemClickListener.onClick(position);
               }
           });
       }
    }

    @Override
    public int getItemCount() {//用来确定列表的长度
        return list.size();
    }

    public class itemHolder extends RecyclerView.ViewHolder{//用来初始化绑定UI组件
        public TextView textView;
        public itemHolder(View itemView) {
            super(itemView);
            textView = (TextView)itemView.findViewById(R.id.music);
        }

        public void setTextView(TextView textView) {
            this.textView = textView;
        }
    }

    public interface OnItemClickListener{
        void onClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.monItemClickListener = onItemClickListener;
    }
}

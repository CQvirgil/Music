package com.virgil.study.music;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<MusicInfo> list;
    LayoutInflater inflater;
    Context context;

    public Adapter(List<MusicInfo> list,Context context) {
        this.list = list;
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public List<MusicInfo> getList() {
        return list;
    }

    public void setList(List<MusicInfo> list) {
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View View = inflater.inflate(R.layout.item,parent,false);
        RecyclerView.ViewHolder musicHolder = new MusicHolder(View);
        return musicHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MusicHolder musicHolder = (MusicHolder)holder;
        musicHolder.textView.setText(list.get(position).getTitle()+"-"+list.get(position).getSinger());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MusicHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public MusicHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.music);
        }

        public void setTextView(TextView textView) {
            this.textView = textView;
        }
    }
}

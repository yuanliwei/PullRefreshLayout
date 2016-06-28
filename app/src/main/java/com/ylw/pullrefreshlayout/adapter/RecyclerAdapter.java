package com.ylw.pullrefreshlayout.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by 袁立位 on 2016/6/28 13:43.
 */
public class RecyclerAdapter extends RecyclerView.Adapter {
    private String[] datas;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), android.R.layout.simple_list_item_1, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder_, int position) {
        ViewHolder holder = (ViewHolder) holder_;
        holder.name.setText(datas[position]);
    }

    @Override
    public int getItemCount() {
        return datas.length;
    }

    public void setDatas(String[] datas) {
        this.datas = datas;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(android.R.id.text1);
        }
    }
}

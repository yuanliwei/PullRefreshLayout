package com.ylw.pullrefreshlayout.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ylw.pullrefreshlayout.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by 袁立位 on 2016/8/5 16:56.
 */
public class ListAdapter extends BaseAdapter {

    public ListAdapter(Context context) {
        this.context = context;
    }

    List<String> datas = Collections.emptyList();
    private Context context;

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int i) {
        return datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ItemHolder holder;
        if (view == null) {
            view = View.inflate(context, R.layout.list_item, null);
            holder = new ItemHolder(view);
            view.setTag(holder);
        } else {
            holder = (ItemHolder) view.getTag();
        }

        String s = datas.get(i);
        holder.name.setText(s);
        return view;
    }

    public List<String> getDatas() {
        return datas;
    }

    public void setDatas(List<String> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    class ItemHolder {
        TextView name;

        public ItemHolder(View view) {
            name = (TextView) view.findViewById(R.id.list_item_name);
        }
    }
}

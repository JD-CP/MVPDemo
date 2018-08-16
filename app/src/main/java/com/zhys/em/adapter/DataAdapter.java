package com.zhys.em.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhys.em.R;
import com.zhys.em.bean.ArticleBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataViewHolder> {

    private LayoutInflater mInflater;
    private List<ArticleBean> dataList = new ArrayList<>();

    public DataAdapter(Context context, List<ArticleBean> list) {
        this.dataList = list;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_article, parent, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataViewHolder holder, int position) {
        holder.tvTitle.setText(dataList.get(position).getTitle());
        holder.tvSource.setText(dataList.get(position).getSource());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setNewData(Collection<ArticleBean> dataList){
        this.dataList.clear();
        this.dataList.addAll(dataList);
        notifyDataSetChanged();
    }

}

package com.zhys.em.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.zhys.em.R;

public class DataViewHolder extends RecyclerView.ViewHolder {

    TextView tvTitle;
    TextView tvSource;

    public DataViewHolder(View itemView) {
        super(itemView);
        tvTitle = itemView.findViewById(R.id.tvTitle);
        tvSource = itemView.findViewById(R.id.tvSource);
    }
}

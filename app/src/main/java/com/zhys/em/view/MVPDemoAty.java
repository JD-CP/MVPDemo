package com.zhys.em.view;

import android.app.ProgressDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zhys.em.adapter.DataAdapter;
import com.zhys.em.R;
import com.zhys.em.base.BaseMVPActivity;
import com.zhys.em.bean.ArticleBean;
import com.zhys.em.presenter.DataPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * View 层，负责 UI 绘制以及与用户的交互
 */
public class MVPDemoAty extends BaseMVPActivity<DataPresenter> implements DataView {

    private static final String APP_KEY = "dbb6893ab0913b02724696504181fe39";

    private Button btnGet;
    private RecyclerView recyclerView;

    private DataAdapter mAdapter;

    private ProgressDialog mDialog;

    @Override
    protected int createView() {
        return R.layout.activity_mvp_demo;
    }

    @Override
    protected DataPresenter createPresenter() {
        return new DataPresenter();
    }

    @Override
    protected void viewCreated() {
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("玩命加载中...");

        btnGet = findViewById(R.id.btnGet);
        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        // linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);


        mAdapter = new DataAdapter(this, new ArrayList<ArticleBean>());
        recyclerView.setAdapter(mAdapter);

        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.show();
                mPresenter.getData(APP_KEY);
            }
        });
    }

    @Override
    public void getDataSuccess(List<ArticleBean> articleList) {
        mDialog.dismiss();
        mAdapter.setNewData(articleList);
    }

    @Override
    public void getDataFail(String failMsg) {
        mDialog.dismiss();
        Toast.makeText(this, failMsg, Toast.LENGTH_SHORT).show();
    }

}

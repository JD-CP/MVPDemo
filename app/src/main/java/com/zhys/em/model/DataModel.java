package com.zhys.em.model;

import com.zhys.em.http.RetrofitHelpter;
import com.zhys.em.bean.BaseResponse;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * model 层：从数据源（网络、数据库）获取数据
 */
public class DataModel {

    private DataApi mApi;

    public DataModel() {
        mApi = RetrofitHelpter.createApi(DataApi.class);
    }

    public void getData(String appKey, Callback<BaseResponse> callback) {
        Call<BaseResponse> responseCall = mApi.getData(appKey);
        // 发起请求
        responseCall.enqueue(callback);
    }

}

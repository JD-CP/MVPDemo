package com.zhys.em.model;

import com.zhys.em.bean.BaseResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DataApi {

    @GET("/weixin/query")
    Call<BaseResponse> getData(@Query("key") String appKey);

}

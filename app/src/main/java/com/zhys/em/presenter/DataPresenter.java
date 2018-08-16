package com.zhys.em.presenter;

import com.zhys.em.view.DataView;
import com.zhys.em.base.BasePresenter;
import com.zhys.em.bean.BaseResponse;
import com.zhys.em.model.DataModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 负责 View 层和 Model 层之间的通信，并对从 Model 层获取的数据进行处理
 */
public class DataPresenter extends BasePresenter<DataView> {

    private DataModel mModel;

    public DataPresenter() {
        this.mModel = new DataModel();
    }

    /**
     * 定义 View 层需要进行的 action
     */
    public void getData(String appKey) {
        mModel.getData(appKey, new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                mView.getDataSuccess(response.body().getResult().getList());
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                mView.getDataFail(t.getMessage());
            }
        });
    }

}

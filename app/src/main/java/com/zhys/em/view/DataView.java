package com.zhys.em.view;

import com.zhys.em.base.IView;
import com.zhys.em.bean.ArticleBean;

import java.util.List;

public interface DataView extends IView {

    void getDataSuccess(List<ArticleBean> articleList);

    void getDataFail(String failMsg);

}

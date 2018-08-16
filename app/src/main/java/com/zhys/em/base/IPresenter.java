package com.zhys.em.base;

public interface IPresenter<V extends IView> {

    void attach(V view);

    void detach();

}

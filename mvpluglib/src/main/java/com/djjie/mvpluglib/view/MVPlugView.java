package com.djjie.mvpluglib.view;

import android.content.Context;

/**
 * Created by shf2 on 2016/12/20.
 */

public interface MVPlugView<T> {

    void setPresenter(T presenter);
    void onRefresh(int tabId);
    void showLoadingView();
    void dismissLoadingView();
    void showToast(String msg);
    void showServerErrorView();
    void showBadInternetView();
    void showEmtyView();
    void setCurrentTabIndex(int tabIndex);
    void setPageFlag(int tabIndex, long pageFlag);
    int getCurrenTabIndex();
    long getPageFlag(int tabIndex);
    Context getCtx();

}

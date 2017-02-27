package com.djjie.mvpluglib.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;
import com.djjie.mvpluglib.model.OnMVPlugViewInit;
import com.djjie.mvpluglib.presenter.MVPlugPresenter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shf2 on 2016/12/20.
 */

public class MVPlugViewImpl<T extends MVPlugPresenter> extends Fragment implements MVPlugView<T> {

    protected IndicatedViewManager indicatedView;
    protected Context context;
    protected T presenter;
    private Activity activity;
    protected int currenTabIndex;
    protected Map<Integer,Long> pageFlags = new HashMap<>();

    @Override
    public void setPresenter(final T presenter) {
        this.presenter = presenter;
        context = getActivity().getApplicationContext();
        indicatedView = new IndicatedViewManager(getActivity(),getView());
        indicatedView.setOnExceptionBtnClicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.startTask(currenTabIndex);
            }
        });
        presenter.startTask(currenTabIndex);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity act = getActivity();
        if (act instanceof OnMVPlugViewInit){
            OnMVPlugViewInit actInter = (OnMVPlugViewInit)act;
            actInter.onMVPlugViewInit(this);
        }else {
            throw new IllegalStateException("the activity must implement the interface of OnMVPlugViewInit !");
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null)presenter.clearTask();
    }

    @Override
    public void setPageFlag(int tabIndex, long pageFlag) {
        pageFlags.put(tabIndex,pageFlag);
    }

    @Override
    public long getPageFlag(int tabIndex) {
        if (!pageFlags.containsKey(tabIndex)) return System.currentTimeMillis();
        return pageFlags.get(tabIndex);
    }

    @Override
    public void setCurrentTabIndex(int tabIndex) {
        this.currenTabIndex = tabIndex;
    }

    @Override
    public int getCurrenTabIndex() {
        return currenTabIndex;
    }

    @Override
    public Context getCtx() {
        return context;
    }

    @Override
    public void showLoadingView() {
        indicatedView.showLoadingLayout();
    }

    @Override
    public void dismissLoadingView() {
        indicatedView.hideAll();
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }

    @Override
    public void showEmtyView() {
        indicatedView.showDataEmptyLayout();
    }

    @Override
    public void showServerErrorView() {
        indicatedView.showExceptionLayout();
    }

    @Override
    public void showBadInternetView() {
        indicatedView.showInternetOffLayout();
    }

    @Override
    public void onRefresh(int tabId) {
        presenter.onRefresh(tabId);
    }
}

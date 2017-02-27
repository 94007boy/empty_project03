package com.djjie.mvpluglib.presenter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.djjie.mvpluglib.model.MVPlugFailReason;
import com.djjie.mvpluglib.view.MVPlugView;
import com.orhanobut.logger.Logger;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by shf2 on 2016/12/20.
 */

public class MVPlugPresenterImpl implements MVPlugPresenter{

    protected CompositeSubscription mSubscriptions;
    private MVPlugView view;

    @Override
    public Context getContext() {
        return view.getCtx();
    }

    public MVPlugPresenterImpl(MVPlugView view){
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void onRefresh(int tabId) {

    }

    @Override
    public void startTask(int tabId) {

    }

    @Override
    public void clearTask() {
        if (mSubscriptions != null && mSubscriptions.hasSubscriptions()){
            mSubscriptions.clear();
        }
        mSubscriptions = null;
    }


    protected void subscribe(boolean isShowLoadingView, Observable observable, Observer observer){
        if(isShowLoadingView){
            view.showLoadingView();
        }
        if (mSubscriptions == null){
            mSubscriptions = new CompositeSubscription();
        }
        mSubscriptions.add(
                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(observer));
    }

    public abstract class ResObserver<T> implements Observer<T> {

        public abstract void onResult(T t);
        public abstract void onResErrorMsg(String resErrorMsg,int code);
        private boolean showExceptionView = true;

        public ResObserver(boolean showExceptionView){
            this.showExceptionView = showExceptionView;
        }

        public ResObserver(){}

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            Logger.e(e.toString());

            if(e instanceof MVPlugFailReason){
                MVPlugFailReason api = (MVPlugFailReason) e;
                onResErrorMsg(api.getMessage(),api.getCode());
            }else if(e instanceof HttpException){
                HttpException exception = (HttpException) e;
                onResErrorMsg(exception.message(),exception.code());
            }
            view.dismissLoadingView();
            if (showExceptionView){
                if (isNetworkAvailable(view.getCtx())){
                    view.showServerErrorView();
                }else {
                    view.showBadInternetView();
                }
            }
        }

        @Override
        public void onNext(T t) {
            view.dismissLoadingView();
            onResult(t);
        }

    }

    protected boolean isNetworkAvailable(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}

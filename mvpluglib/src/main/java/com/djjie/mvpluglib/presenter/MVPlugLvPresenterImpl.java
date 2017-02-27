package com.djjie.mvpluglib.presenter;


import com.djjie.mvpluglib.model.MVPlugFailReason;
import com.djjie.mvpluglib.view.MVPlugLvView;
import com.orhanobut.logger.Logger;

import rx.Observer;

/**
 * Created by shf2 on 2016/12/20.
 */

public class MVPlugLvPresenterImpl extends MVPlugPresenterImpl implements MVPlugLvPresenter {

    private MVPlugLvView view;

    public MVPlugLvPresenterImpl(MVPlugLvView view) {
        super(view);
        this.view = view;
    }

    @Override
    public void onLoadMore(int tabId,long pageFlag) {

    }

    public abstract class ResLvObserver<T> implements Observer<T> {

        private boolean isRefresh;

        public ResLvObserver(boolean isRefresh){
            this.isRefresh = isRefresh;
        }

        public abstract void onResult(T t,boolean isRefresh);
        public abstract void onResErrorMsg(String resErrorMsg);

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            Logger.e(e.toString());
            if(e instanceof MVPlugFailReason){
                MVPlugFailReason api = (MVPlugFailReason) e;
                onResErrorMsg(api.getMessage());
            }
            if (isRefresh){
                if (isNetworkAvailable(getContext())){
                    view.showServerErrorView();
                }else {
                    view.showBadInternetView();
                }
            }else {
                view.onLoadMoreError();
            }
        }

        @Override
        public void onNext(T t) {
            if (isRefresh){
                view.dismissLoadingView();
            }
            onResult(t,isRefresh);
        }
    }
}

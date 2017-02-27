package com.djjie.mvpluglib.presenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.djjie.mvpluglib.MVPlug;
import com.djjie.mvpluglib.MVPlugConfig;
import com.orhanobut.logger.Logger;

/**
 * Created by shf2 on 2016/12/17.
 */

public class MVPlugFooterViewEvent {

    private int currentState = STATE_INITIAL;
    private static final int STATE_INITIAL = 0x1001;//视图状态机 4097
    private static final int STATE_MORE = 0x1002;//4098
    private static final int STATE_NOMORE = 0x1003;//4099
    private static final int STATE_ERROR = 0x1004;//4100
    private static final int STATE_HIDE = 0x1005;//4101

    private MVPlugFooterView footerView;
    private MVPlugAdapter.OnLoadMoreListener onLoadMoreListener;
    private final MVPlugConfig mvPlugConfig;

    public MVPlugFooterViewEvent(Context context, LayoutInflater inflater){
        footerView = new MVPlugFooterView(context);
        mvPlugConfig = MVPlug.getInstance().getConfiguration();
        int loadMoreRes = mvPlugConfig.getFooterLoadMoreLayout();
        int noMoreRes = mvPlugConfig.getFooterNoMoreLayout();
        int errorRes = mvPlugConfig.getFooterErrorLayout();

        if(loadMoreRes != 0){
            FrameLayout container = new FrameLayout(context);
            container.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            footerView.loadmoreView = inflater.inflate(loadMoreRes, container);
        }

        if(noMoreRes != 0){
            FrameLayout container = new FrameLayout(context);
            container.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            footerView.noMoreView = inflater.inflate(noMoreRes, container);
        }

        if(errorRes != 0){
            FrameLayout container = new FrameLayout(context);
            container.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            footerView.errorView = inflater.inflate(errorRes, container);
        }
    }


    public FrameLayout getFooterViewContainer() {
        return footerView.getFooterViewContainer();
    }

    /**
     * 视图滑动到最底部时触发
     * @param hasLoadMore
     */
    public void onFooterViewBinded(boolean hasLoadMore) {
        if (mvPlugConfig.ismIsDebugMode()){
            Logger.d("onFooterViewBinded currentState = "+currentState+" ,hasLoadMore = "+hasLoadMore);
        }
        if (!hasLoadMore)return;
        if (currentState == STATE_MORE){
            onLoadMoreViewVisible();
        }else if(currentState == STATE_ERROR){
            onErrorViewVisible();
        }
    }

    /**
     * 当错误视图又一次可见时，需要重新加载数据了，相当于触发加载更多
     */
    public void onErrorViewVisible() {
        reloadMore();
    }

    /**
     * 当加载更多时，出现网络错误，触发此方法
     */
    public void onLoadMoreError(){
        currentState = STATE_ERROR;
        footerView.showViewByType(currentState);
    }

    /**
     * 重新触发加载更多机制
     */
    private void reloadMore() {
        if (mvPlugConfig.ismIsDebugMode()){
            Logger.d("reloadMore currentState = "+currentState);
        }
        if (currentState == STATE_NOMORE) return;
        currentState = STATE_MORE;
        footerView.showViewByType(currentState);
        onLoadMoreViewVisible();
    }

    public void hideFooterView() {
        currentState = STATE_HIDE;
        footerView.showViewByType(currentState);
    }

    /**
     * 当滑动到最底部，加载更多视图显示出来时
     */
    private void onLoadMoreViewVisible() {
        if (mvPlugConfig.ismIsDebugMode()){
            Logger.d("onLoadMoreViewVisible currentState = "+currentState);
            Logger.d("onLoadMoreViewVisible onLoadMoreListener = "+onLoadMoreListener);
        }
        if (onLoadMoreListener!=null) onLoadMoreListener.onLoadMore();
    }

    public void setOnLoadMoreListener(MVPlugAdapter.OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    private class MVPlugFooterView {

        private View loadmoreView;
        private View noMoreView;
        private View errorView;

        public FrameLayout getFooterViewContainer() {
            return container;
        }

        private FrameLayout container;

        public MVPlugFooterView(Context ctx){
            container = new FrameLayout(ctx);
            container.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        private void showViewByType(int viewType){
            if (mvPlugConfig.ismIsDebugMode()){
                Logger.d("showViewByType viewType = "+viewType);
            }
            View view = null;
            switch (viewType){
                case STATE_MORE:
                    container.setVisibility(View.VISIBLE);
                    view = loadmoreView;
                    break;

                case STATE_NOMORE:
                    container.setVisibility(View.VISIBLE);
                    view = noMoreView;
                    break;

                case STATE_ERROR:
                    container.setVisibility(View.VISIBLE);
                    view = errorView;
                    break;

                case STATE_HIDE:
                    container.setVisibility(View.GONE);
                    return;
            }
            if (view != null){
                if (container.getVisibility() != View.VISIBLE)container.setVisibility(View.VISIBLE);
                if (view.getParent()==null)container.addView(view);

                for (int i = 0; i < container.getChildCount(); i++) {
                    if (container.getChildAt(i) == view)view.setVisibility(View.VISIBLE);
                    else container.getChildAt(i).setVisibility(View.GONE);
                }
            }else {
                container.setVisibility(View.GONE);
            }
        }
    }

    public void changeViewByDatasLength(int length,boolean hasLoadMore){
        if (mvPlugConfig.ismIsDebugMode()){
            Logger.d("changeViewByDatasLength length = "+length);
        }
        if (!hasLoadMore)return;
        updateCurrentState(length);
        footerView.showViewByType(currentState);
    }

    private void updateCurrentState(int length){
        if (length == 0){
            //当添加0个时，认为已结束加载到底
            if (currentState == STATE_INITIAL || currentState == STATE_MORE){
                currentState = STATE_NOMORE;
            }
        }else {
            currentState = STATE_MORE;
        }
    }
}

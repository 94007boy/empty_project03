package com.djjie.mvpluglib.view;

/**
 * Created by shf2 on 2016/12/20.
 */

public interface MVPlugLvView<T,M> extends MVPlugView<T> {

    void onLoadMore(int tabId, long pageFlag);
    void onLoadMoreError();
    void setAdapter(M adapter);
    void setAdapter(int tabId, M adapter);

}

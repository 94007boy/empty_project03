package com.djjie.mvpluglib.model;

/**
 * Created by xiaolv on 16/7/20.
 */
public interface ResponseModel<T> {

    int getResponseCode();
    String getResponseMsg();
    T getResponseData();

}

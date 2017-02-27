package com.djjie.mvpluglib.model;

/**
 * Created by shf2 on 2016/12/19.
 */

public class MVPlugFailReason extends RuntimeException{

    private int code;

    public MVPlugFailReason(String detailMessage) {
        super(detailMessage);
    }


    public MVPlugFailReason(String detailMessage,int code) {
        super(detailMessage);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

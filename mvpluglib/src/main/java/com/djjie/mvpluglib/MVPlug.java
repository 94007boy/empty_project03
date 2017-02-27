package com.djjie.mvpluglib;

/**
 * Created by shf2 on 2016/12/17.
 */

public class MVPlug {

    private static MVPlug sInstance;
    private Object mInitLock = new Object();

    public MVPlugConfig getConfiguration() {
        return configuration;
    }

    private MVPlugConfig configuration;

    private MVPlug(){

    }

    public static MVPlug getInstance() {
        if (sInstance == null) {
            synchronized (MVPlug.class) {
                if (sInstance == null) {
                    sInstance = new MVPlug();
                }
            }
        }
        return sInstance;
    }

    public void init(MVPlugConfig configuration) {
        if (configuration == null) {
            throw new IllegalStateException("requires the field of configuration to be non-null !");
        }
        synchronized (mInitLock) {
            this.configuration = configuration;
        }
    }



}

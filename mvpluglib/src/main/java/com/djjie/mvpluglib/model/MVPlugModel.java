package com.djjie.mvpluglib.model;
import android.text.TextUtils;
import com.djjie.mvpluglib.MVPlug;
import com.djjie.mvpluglib.MVPlugConfig;
import com.orhanobut.logger.Logger;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.functions.Func1;

/**
 * Created by shf2 on 2016/12/16.
 */

public class MVPlugModel {

    private static MVPlugModel INSTANCE;
    public Retrofit retrofit;

    private MVPlugModel(){
        MVPlugConfig plugConfig = MVPlug.getInstance().getConfiguration();

        if (TextUtils.isEmpty(plugConfig.BASE_URL())){
            throw new NullPointerException("BASE_URL == nll!");
        }

        //打印请求Log
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                if (!TextUtils.isEmpty(message) && message.startsWith("{") && message.endsWith("}")){
                    Logger.json(message);
                }else {
                    Logger.d(message);
                }
            }
        });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        //手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(plugConfig.TIMEOUT(), TimeUnit.SECONDS)
                .readTimeout(plugConfig.TIMEOUT(), TimeUnit.SECONDS)
                // 失败重试
                .retryOnConnectionFailure(true);
        if (plugConfig.ismIsDebugMode()){
            //日志Interceptor，可以打印日志
            builder.addInterceptor(interceptor);
        }

        retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(plugConfig.getConverterFactory() == null?plugConfig.defaultCoverterFactory:plugConfig.getConverterFactory())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(plugConfig.BASE_URL())
                .build();
    }

    public static MVPlugModel getInstance(){
        if (INSTANCE == null) {
            synchronized (MVPlugModel.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MVPlugModel();
                }
            }
        }
        return INSTANCE;
    }

    public static class GetPureDataFunc<T> implements Func1<ResponseModel<T>, T> {

        @Override
        public T call(ResponseModel<T> httpResult) {
            Logger.d("GetPureDataFunc");
            return httpResult.getResponseData();
        }
    }
}

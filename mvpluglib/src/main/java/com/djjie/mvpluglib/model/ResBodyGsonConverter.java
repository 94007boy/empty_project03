package com.djjie.mvpluglib.model;

import com.djjie.mvpluglib.MVPlug;
import com.djjie.mvpluglib.MVPlugConfig;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import java.io.IOException;
import java.lang.reflect.Type;
import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by xiaolv on 16/9/8.
 */
public class ResBodyGsonConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final Type type;
    private final MVPlugConfig mvPlugConfig;

    ResBodyGsonConverter(Gson gson, Type type) {
        this.gson = gson;
        this.type = type;
        mvPlugConfig = MVPlug.getInstance().getConfiguration();
    }

    @Override
    public T convert(ResponseBody value){

        if (value == null){
            throw new MVPlugFailReason("the internet is bad !",-1);
        }else {
            String encodeRes = null;
            try{
                encodeRes = value.string();
            }catch (IOException e){
                Logger.e("convert : "+e.toString());
            }

            String decodeRes;
            if (mvPlugConfig.getOnResponseBody() != null){
                //扒第一层皮
                decodeRes = mvPlugConfig.getOnResponseBody().decodeResBody(encodeRes);
            }else {
                decodeRes = encodeRes;
            }
            Logger.d("decodeRes = "+decodeRes);
            Class<? extends ResponseModel> clz = mvPlugConfig.getResModelClass();
            ResponseModel resultResponse = gson.fromJson(decodeRes, clz);

            //扒第二层皮
            if (resultResponse.getResponseCode() == MVPlug.getInstance().getConfiguration().RES_SUCCESS_CODE()){
                //扒第三层皮
                return gson.fromJson(decodeRes, type);
            } else {
                throw new MVPlugFailReason(resultResponse.getResponseMsg(),resultResponse.getResponseCode());
            }
        }
    }
}
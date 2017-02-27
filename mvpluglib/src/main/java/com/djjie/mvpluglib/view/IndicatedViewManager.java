package com.djjie.mvpluglib.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ViewSwitcher;
import com.djjie.mvpluglib.MVPlug;
import com.djjie.mvpluglib.MVPlugConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by shf2 on 2016/12/20.
 */

public class IndicatedViewManager {

    private View mTargetView;
    private View.OnClickListener onExceptionBtnClicked;
    private Context mContext;
    private LayoutInflater mInflater;
    private RelativeLayout mContainer;
    private ArrayList<View> indicatedViews;
    private ViewSwitcher mSwitcher;
    private final String TAG_INTERNET_OFF 	 =  "INTERNET_OFF";
    private final String TAG_LOADING_CONTENT =  "LOADING_CONTENT";
    private final String TAG_RES_ERROR_EXCEPTION =  "RES_ERROR_EXCEPTION";
    private final String TAG_EMPTY_DATA =  "EMPTY_DATA";

    private final String[] mSupportedViews = new String[]{"linearlayout","relativelayout", "framelayout", "scrollview", "recyclerview", "viewgroup"};
    private final MVPlugConfig mvPlugConfig;

    public IndicatedViewManager(Activity activity, View targetView){
        this.mContext 		= activity.getApplicationContext();
        this.mInflater 		= activity.getLayoutInflater();
        this.mTargetView 	= targetView;
        this.mContainer 	= new RelativeLayout(mContext);
        this.indicatedViews = new ArrayList<View>();
        mvPlugConfig = MVPlug.getInstance().getConfiguration();
        Class viewClass = mTargetView.getClass();
        Class superViewClass = viewClass.getSuperclass();
        String viewType = viewClass.getName().substring(viewClass.getName().lastIndexOf('.')+1).toLowerCase(Locale.getDefault());
        String superViewType = superViewClass.getName().substring(superViewClass.getName().lastIndexOf('.')+1).toLowerCase(Locale.getDefault());

        if(Arrays.asList(mSupportedViews).contains(viewType)|| Arrays.asList(mSupportedViews).contains(superViewType))
            initializeViewContainer();
        else
            throw new IllegalArgumentException("TargetView type ["+superViewType+"] is not supported !");
    }

    public void setOnExceptionBtnClicked(View.OnClickListener onExceptionBtnClicked) {
        this.onExceptionBtnClicked = onExceptionBtnClicked;
    }

    private void initializeViewContainer(){
        initViews();
        mSwitcher = new ViewSwitcher(mContext);
        ViewSwitcher.LayoutParams params = new ViewSwitcher.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mSwitcher.setLayoutParams(params);

        ViewGroup group = (ViewGroup)mTargetView.getParent();
        int index = 0;
        Clonner target= new Clonner(mTargetView);
        if(group!=null){
            index = group.indexOfChild(mTargetView);
            group.removeView(mTargetView);

        }
        mSwitcher.addView(mContainer,0);
        mSwitcher.addView(target.getmView(),1);
        mSwitcher.setDisplayedChild(1);

        if(group!=null){
            group.addView(mSwitcher,index);
        }else{
            ((Activity)mContext).setContentView(mSwitcher);
        }
    }

    private void initViews() {
        View mLayoutInternetOff = initView(mvPlugConfig.getBadInternetLayoutRes(),TAG_INTERNET_OFF);
        View mLayoutLoadingContent = initView(mvPlugConfig.getLoadingLayoutRes(),TAG_LOADING_CONTENT);
        View mLayoutResError = initView(mvPlugConfig.getResFailureLayoutRes(), TAG_RES_ERROR_EXCEPTION);
        View mLayoutEmpty = initView(mvPlugConfig.getEmptyDataViewRes(), TAG_EMPTY_DATA);

        indicatedViews.add(0,mLayoutInternetOff);
        indicatedViews.add(1,mLayoutLoadingContent);
        indicatedViews.add(2,mLayoutResError);
        indicatedViews.add(3,mLayoutEmpty);

        // Hide all layouts at first initialization
        mLayoutInternetOff.setVisibility(View.GONE);
        mLayoutLoadingContent.setVisibility(View.GONE);
        mLayoutResError.setVisibility(View.GONE);
        mLayoutEmpty.setVisibility(View.GONE);

        // init Layout params
        RelativeLayout.LayoutParams containerParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        containerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        containerParams.addRule(RelativeLayout.CENTER_VERTICAL);

        // init new RelativeLayout Wrapper
        mContainer.setLayoutParams(containerParams);

        // Add default views
        mContainer.addView(mLayoutLoadingContent);
        mContainer.addView(mLayoutInternetOff);
        mContainer.addView(mLayoutResError);
        mContainer.addView(mLayoutEmpty);
    }

    private View initView(int layout, String tag){
        View view = mInflater.inflate(layout, null,false);

        view.setTag(tag);
        view.setVisibility(View.GONE);

        View buttonView = view.findViewById(mvPlugConfig.getExceptionViewBtnResId());
        if(buttonView!=null)
            buttonView.setOnClickListener(this.onExceptionBtnClicked);

        return view;
    }

    private class Clonner{
        private View mView;

        public Clonner(View view){
            this.setmView(view);
        }

        public View getmView() {
            return mView;
        }

        public void setmView(View mView) {
            this.mView = mView;
        }
    }


    public void showDataEmptyLayout(){
        show(TAG_EMPTY_DATA);
    }

    public void showLoadingLayout(){
        show(TAG_LOADING_CONTENT);
    }

    public void showInternetOffLayout(){
        show(TAG_INTERNET_OFF);
    }

    public void showExceptionLayout(){
        show(TAG_RES_ERROR_EXCEPTION);
    }

    public void showCustomView(String tag){
        show(tag);
    }

    private void show(String tag){
        for(View view : indicatedViews){
            if(view.getTag()!=null && view.getTag().toString().equals(tag)){
                view.setVisibility(View.VISIBLE);
            }else{
                view.setVisibility(View.GONE);
            }
        }
        if(mSwitcher!=null && mSwitcher.getDisplayedChild()!=0){
            mSwitcher.setDisplayedChild(0);
        }
    }

    public void hideAll(){
        for(View view : indicatedViews){
            view.setVisibility(View.GONE);
        }
        if(mSwitcher!=null){
            mSwitcher.setDisplayedChild(1);
        }
    }
}

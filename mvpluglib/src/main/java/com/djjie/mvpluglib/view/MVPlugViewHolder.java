package com.djjie.mvpluglib.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by xiaolv on 16/4/8.
 */
public class MVPlugViewHolder<M> extends RecyclerView.ViewHolder {


//    public MVPlugViewHolder(ViewDataBinding dataBinding) {
//        super(dataBinding.getRoot());
//    }

    public MVPlugViewHolder(View view) {
        super(view);
    }

    public void setData(M data) {
    }

    protected Context getContext(){
        return itemView.getContext();
    }

}

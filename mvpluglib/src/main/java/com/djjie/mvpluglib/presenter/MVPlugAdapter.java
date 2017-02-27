package com.djjie.mvpluglib.presenter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.djjie.mvpluglib.MVPlug;
import com.djjie.mvpluglib.MVPlugConfig;
import com.djjie.mvpluglib.view.MVPlugViewHolder;
import com.orhanobut.logger.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by xiaolv on 16/4/11.
 */
abstract public class MVPlugAdapter<T> extends RecyclerView.Adapter<MVPlugViewHolder>   {

    protected List<T> itemDatas;
    private Context context;
    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;
    private final Object mLock = new Object();
    private boolean mNotifyOnChange = true;
    private boolean isHasHeader = false;
    private View mHeaderView;
    private View mFooterView;
    public static final int VIEW_TYPE_NORMAL = 0X0001;
    public static final int VIEW_TYPE_HEADER = 0X0002;
    public static final int VIEW_TYPE_FOOTER = 0X0003;
    public LayoutInflater inflater;
    private MVPlugFooterViewEvent footerViewEvent;
    private MVPlugConfig plugConfig;
    private boolean hasLoadMore = false;

    public MVPlugAdapter(Context context) {
        init(context,  new ArrayList<T>());
    }

    public MVPlugAdapter(Context context, T[] itemDatas) {
        init(context, Arrays.asList(itemDatas));
    }

    public MVPlugAdapter(Context context, List<T> itemDatas) {
        init(context, itemDatas);
    }

    private void init(Context context , List<T> itemDatas) {
        this.context = context;
        this.itemDatas = itemDatas;
        plugConfig = MVPlug.getInstance().getConfiguration();
        inflater = LayoutInflater.from(context);
        footerViewEvent = new MVPlugFooterViewEvent(context,inflater);
        setFooterView(footerViewEvent.getFooterViewContainer());//自动添加尾部
    }

    public Context getContext() {
        return context;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        hasLoadMore = true;
        if(footerViewEvent != null)footerViewEvent.setOnLoadMoreListener(onLoadMoreListener);
    }

    @Override
    public MVPlugViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (plugConfig.ismIsDebugMode()){
            Logger.d("onCreateViewHolder");
        }
        if(viewType== VIEW_TYPE_FOOTER){
            return new MVPlugViewHolder(mFooterView);
        }

        if(viewType== VIEW_TYPE_HEADER){
            return new MVPlugViewHolder(mHeaderView);
        }
        final MVPlugViewHolder viewHolder = OnCreateViewHolder(parent, viewType);

        if (mItemClickListener!=null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onItemClick(viewHolder.getAdapterPosition() - (isHasHeader?1:0));
                }
            });
        }

        if (mItemLongClickListener!=null){
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return mItemLongClickListener.onItemClick(viewHolder.getAdapterPosition() - (isHasHeader?1:0));
                }
            });
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MVPlugViewHolder holder, final int position) {

        if (plugConfig.ismIsDebugMode()){
            Logger.d("onBindViewHolder");
        }

        if(isHasHeader){//有头
            if(position == 0){//位于头部
                return;
            }
            if(position == itemDatas.size()+1){//位于尾部
                footerViewEvent.onFooterViewBinded(hasLoadMore);//尾部
                return;
            }
            OnBindViewHolder(holder,position-1);//其他时候，修正由于头尾导致的位置错乱
        }

        if(!isHasHeader){//无头
            if(position == itemDatas.size()){//尾部
                footerViewEvent.onFooterViewBinded(hasLoadMore);
                return;
            }
            OnBindViewHolder(holder,position);
        }
    }


    /**
     * 添加头部视图
     * @param header
     */
    public void setHeaderView(View header){
        this.mHeaderView = header;
        isHasHeader = true;
        notifyDataSetChanged();
    }

    /**
     * 添加底部视图,私有，不允许主动添加
     * @param footer
     */
    private void setFooterView(View footer){
        this.mFooterView = footer;
        notifyDataSetChanged();
    }

    public void dismissFooterView(){
        footerViewEvent.hideFooterView();
    }

    @Override
    public int getItemViewType(int position) {//复用机制

        // 根据索引获取当前View的类型，以达到复用的目的

        // 根据位置的索引，获取当前position的类型
        if(isHasHeader && position == 0){
            return VIEW_TYPE_HEADER;
        }
        if(isHasHeader && position == itemDatas.size()+1){
            // 有头部和底部时，最后底部的应该等于size+!
            return VIEW_TYPE_FOOTER;
        }else if(!isHasHeader && position == itemDatas.size()){
            // 没有头部，有底部，底部索引为size
            return VIEW_TYPE_FOOTER;
        }
        return VIEW_TYPE_NORMAL;
    }

    /**
     * 刷新数据
     * @param datas
     */
    public void refreshDatas(List<T> datas){
        this.itemDatas.clear();
        this.itemDatas.addAll(datas);
        footerViewEvent.changeViewByDatasLength(datas == null ? 0 : datas.size(),hasLoadMore);
        notifyDataSetChanged();
        if (plugConfig.ismIsDebugMode()){
            Logger.d("refreshDatas size = "+datas.size());
        }
    }

    /**
     * 添加更多数据
     * @param collection
     */
    public void addMoreDatas(Collection<? extends T> collection) {
        if (plugConfig.ismIsDebugMode()){
            Logger.d("addMoreDatas");
        }
        footerViewEvent.changeViewByDatasLength(collection == null ? 0 : collection.size(),hasLoadMore);
        if (collection!=null && collection.size() != 0){
            synchronized (mLock) {
                itemDatas.addAll(collection);
            }
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * 在指定位置插入元素
     */
    public void insert(T object, int index) {
        synchronized (mLock) {
            itemDatas.add(index, object);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * 删除指定元素
     */
    public void remove(T object) {
        synchronized (mLock) {
            itemDatas.remove(object);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * 删除指定位置的元素
     */
    public void remove(int position) {
        synchronized (mLock) {
            itemDatas.remove(position);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }


    /**
     * 清空全部数据
     */
    public void clear() {
        footerViewEvent.hideFooterView();
        synchronized (mLock) {
            itemDatas.clear();
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * 包含了头部和尾部view的item总个数。
     * @return
     */
    @Override
    public int getItemCount() {
        int size = itemDatas.size();
            size ++;//必有尾部，因此加1
        if(isHasHeader)size++;
        if (plugConfig.ismIsDebugMode()){
            Logger.d("getItemCount size = "+size);
        }
        return size;
    }

    /**
     * 不包含头和尾的item个数
     * @return
     */
    public int getCount(){
        return itemDatas.size();
    }

    public T getItem(int position) {
        if (itemDatas == null || itemDatas.isEmpty() || position < 0)return null;
        return itemDatas.get(position);
    }

    public void OnBindViewHolder(MVPlugViewHolder holder, final int position){
        holder.setData(getItem(position));
    }

    abstract public MVPlugViewHolder OnCreateViewHolder(ViewGroup parent, int viewType);

    public void onLoadMoreError() {
        footerViewEvent.onLoadMoreError();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemClick(int position);
    }

    public interface OnLoadMoreListener{
        void onLoadMore();
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}
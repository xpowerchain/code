package com.lunary.owerwallet.weight;

import android.content.Context;
import android.view.View;

import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;

/**
 * Created by Administrator on 2018/8/11.
 */
public class DefineBAGRefreshWithLoadView extends BGARefreshViewHolder {
    /**
     * @param context
     * @param isLoadingMoreEnabled 上拉加载更多是否可用
     */
    public DefineBAGRefreshWithLoadView(Context context, boolean isLoadingMoreEnabled) {
        super(context, isLoadingMoreEnabled);
    }

    @Override
    public View getRefreshHeaderView() {
        return null;
    }

    @Override
    public void handleScale(float scale, int moveYDistance) {

    }

    @Override
    public void changeToIdle() {

    }

    @Override
    public void changeToPullDown() {

    }

    @Override
    public void changeToReleaseRefresh() {

    }

    @Override
    public void changeToRefreshing() {

    }

    @Override
    public void onEndRefreshing() {

    }
}

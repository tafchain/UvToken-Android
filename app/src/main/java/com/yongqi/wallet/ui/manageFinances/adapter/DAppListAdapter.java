package com.yongqi.wallet.ui.manageFinances.adapter;

import android.view.View;
import android.widget.ImageView;

import com.blankj.utilcode.util.SPUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yongqi.wallet.R;
import com.yongqi.wallet.ui.manageFinances.bean.DAppDataBean;
import com.yongqi.wallet.utils.GlideEngine;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DAppListAdapter extends BaseQuickAdapter<DAppDataBean, BaseViewHolder> {
    public DAppListAdapter(int layoutResId, @Nullable List<DAppDataBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, DAppDataBean dAppDataBean) {
        ImageView iv_dapp = baseViewHolder.getView(R.id.iv_dapp);
        GlideEngine.createGlideEngine().loadImage(getContext(),dAppDataBean.getImg(),iv_dapp);
        if (SPUtils.getInstance().getString("language").equals("English")) {
            baseViewHolder.setText(R.id.tv_dapp_name,dAppDataBean.getName_en());
            baseViewHolder.setText(R.id.tv_dapp_des, dAppDataBean.getLanguage());
        }else {
            baseViewHolder.setText(R.id.tv_dapp_name,dAppDataBean.getName());
            baseViewHolder.setText(R.id.tv_dapp_des, dAppDataBean.getDesc());
        }

    }
}

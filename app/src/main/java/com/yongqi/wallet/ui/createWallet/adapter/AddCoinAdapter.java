package com.yongqi.wallet.ui.createWallet.adapter;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yongqi.wallet.R;
import com.yongqi.wallet.config.CoinConst;
import com.yongqi.wallet.ui.createWallet.bean.AddCoinBean;

import org.jetbrains.annotations.NotNull;

public class AddCoinAdapter extends BaseQuickAdapter<AddCoinBean, BaseViewHolder> {
    public AddCoinAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, AddCoinBean addCoinBean) {
        ImageView ivCoin = baseViewHolder.getView(R.id.ivBtc);
        ImageView ivGifLoad = baseViewHolder.getView(R.id.ivLoadGif);
        Glide.with(getContext()).load(R.drawable.load).into(ivGifLoad);
        //1:创建完成 2：创建中 3：待创建
        switch (addCoinBean.getCreateStatus()) {
            case 1:
                ivCoin.setAlpha(1f);
                ivGifLoad.setVisibility(View.INVISIBLE);
                break;
            case 2:
                ivCoin.setAlpha(0.3f);
                ivGifLoad.setVisibility(View.VISIBLE);
                break;
            case 3:
                ivCoin.setAlpha(0.3f);
                ivGifLoad.setVisibility(View.INVISIBLE);
                break;
        }
        switch (addCoinBean.getCoinName()) {
            case CoinConst.BTC:
                ivCoin.setImageResource(R.mipmap.icon_btc);
                break;
            case CoinConst.ETH:
                ivCoin.setImageResource(R.mipmap.icon_eth);
                break;
            case CoinConst.TRX:
                ivCoin.setImageResource(R.mipmap.ic_trx);
                break;

        }
    }
}

package com.yongqi.wallet.ui.createWallet.adapter;

import android.widget.ImageView;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yongqi.wallet.R;
import com.yongqi.wallet.config.CoinConst;
import com.yongqi.wallet.ui.createWallet.bean.AddTokenBean;

import org.jetbrains.annotations.NotNull;

public class AddTokenAdapter extends BaseQuickAdapter<AddTokenBean, BaseViewHolder> {
    public AddTokenAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, AddTokenBean addTokenBean) {
        ImageView ivToken = baseViewHolder.getView(R.id.iv_token);
        TextView tvTokenName = baseViewHolder.getView(R.id.tv_token_name);
        TextView tvDesTokenName = baseViewHolder.getView(R.id.tv_token_des_name);
        switch (addTokenBean.getTokenName()) {
            case CoinConst.BTC:
                ivToken.setImageResource(R.mipmap.icon_btc);
                tvTokenName.setText(CoinConst.BTC);
                tvDesTokenName.setText("Bitcoin");
                break;
            case CoinConst.ETH:
                ivToken.setImageResource(R.mipmap.icon_eth);
                tvTokenName.setText(CoinConst.ETH);
                tvDesTokenName.setText("Ethereum");
                break;
            case CoinConst.TRX:
                ivToken.setImageResource(R.mipmap.ic_trx);
                tvTokenName.setText(CoinConst.TRX);
                tvDesTokenName.setText("TRON");
                break;
        }
        ImageView ivCheck = baseViewHolder.getView(R.id.iv_check);
        switch (addTokenBean.getStatus()) {
            case 1:
                ivCheck.setAlpha(0.3f);
                ivCheck.setClickable(false);
                ivCheck.setFocusable(false);
                ivCheck.setImageResource(R.mipmap.icon_gx);
                break;
            case 2:
                ivCheck.setAlpha(1f);
                ivCheck.setClickable(true);
                ivCheck.setFocusable(true);
                ivCheck.setImageResource(R.mipmap.icon_wxz_js);
                break;
            case 3:
                ivCheck.setAlpha(1f);
                ivCheck.setClickable(true);
                ivCheck.setFocusable(true);
                ivCheck.setImageResource(R.mipmap.icon_gx);
                break;
        }
    }
}

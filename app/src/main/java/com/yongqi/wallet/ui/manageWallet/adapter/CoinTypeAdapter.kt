package com.yongqi.wallet.ui.manageWallet.adapter

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.yongqi.wallet.App
import com.yongqi.wallet.R
import com.yongqi.wallet.bean.Coin
import com.yongqi.wallet.config.CoinConst

/**
 * author ：SunXiao
 * date : 2021/1/12 15:51
 * package：com.yongqi.wallet.ui.main.adapter
 * description :
 */
class CoinTypeAdapter(layoutResId: Int, walletListData: MutableList<Coin>?) :
    BaseQuickAdapter<Coin, BaseViewHolder>(layoutResId, walletListData) {


    override fun convert(holder: BaseViewHolder, item: Coin) {

        when(item.name){
            "BTC"->{
                var coinIcon = holder.getView<ImageView>(R.id.ivCoinIcon)
                Glide.with(App.context)
                    .load(R.mipmap.icon_btc)
                    .into(coinIcon)
                holder.setText(R.id.tvCoinType,"BTC ${App.context.getString(R.string.private_key)}")
            }

            "AECO"->{
                var coinIcon = holder.getView<ImageView>(R.id.ivCoinIcon)
                Glide.with(App.context)
                    .load(R.mipmap.arther_icon)
                    .into(coinIcon)
                holder.setText(R.id.tvCoinType,"AECO ${App.context.getString(R.string.private_key)}")
            }

            CoinConst.TRX ->{
                val coinIcon = holder.getView<ImageView>(R.id.ivCoinIcon)
                Glide.with(App.context)
                    .load(R.mipmap.ic_trx)
                    .into(coinIcon)
                holder.setText(R.id.tvCoinType,"TRX ${App.context.getString(R.string.private_key)}")
            }

            "TAFT"->{
                var coinIcon = holder.getView<ImageView>(R.id.ivCoinIcon)
                Glide.with(App.context)
                    .load(R.mipmap.icon_taf)
                    .into(coinIcon)
                holder.setText(R.id.tvCoinType,"TAFT ${App.context.getString(R.string.private_key)}")
            }

            else->{
                var coinIcon = holder.getView<ImageView>(R.id.ivCoinIcon)
                Glide.with(App.context)
                    .load(R.mipmap.icon_eth)
                    .into(coinIcon)
                holder.setText(R.id.tvCoinType,"ETH ${App.context.getString(R.string.private_key)}")
            }
        }





    }
}
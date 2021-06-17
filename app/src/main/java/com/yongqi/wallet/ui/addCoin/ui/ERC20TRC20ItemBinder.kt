package com.yongqi.wallet.ui.addCoin.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.binder.BaseItemBinder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.yongqi.wallet.App
import com.yongqi.wallet.R
import com.yongqi.wallet.bean.Coin
import com.yongqi.wallet.config.CoinConst
import com.yongqi.wallet.utils.StringUtils

/**
 * author ：SunXiao
 * date : 2021/6/7 16:25
 * package：com.yongqi.wallet.ui.addCoin.ui
 * description :使用最基础的 BaseItemBinder 创建 Binder
 */
class ERC20TRC20ItemBinder : BaseItemBinder<Coin, BaseViewHolder>() {

    override fun convert(holder: BaseViewHolder, data: Coin) {
        var ivCoinTag = holder.getView<ImageView>(R.id.ivLogo)
        when (data.name) {
            "USDT" -> {
                when(data.coin_tag){
                    CoinConst.ERC20->{
                        ivCoinTag.visibility = View.VISIBLE
                        ivCoinTag.setImageResource(R.mipmap.icon_erc20)
                    }
                    CoinConst.TRC20->{
                        ivCoinTag.visibility = View.VISIBLE
                        ivCoinTag.setImageResource(R.mipmap.icon_trc20)
                    }
                    else->{
                        ivCoinTag.visibility = View.INVISIBLE
                    }
                }
            }
            else -> {
                ivCoinTag.visibility = View.INVISIBLE
            }
        }
        var coinIcon = holder.getView<ImageView>(R.id.ivCoinIcon)
        var ivTag = holder.getView<ImageView>(R.id.ivTag)
        Glide.with(App.context).load(data.image).apply(RequestOptions.bitmapTransform(CircleCrop())).into(coinIcon)
        when (data.coin_tag) {
            CoinConst.ERC20 -> {
                Glide.with(App.context).load(R.mipmap.icon_eth).into(ivTag)
            }
            CoinConst.TRC20 -> {
                Glide.with(App.context).load(R.mipmap.ic_trx).into(ivTag)
            }

            else -> {
                ivTag.visibility = View.GONE
            }
        }
        holder.setText(R.id.tvCoinName, data.name)
        holder.setText(R.id.tvLinks, StringUtils.replaceByX(data.contact_address))
        if (data.isAdd) {
            val ivAdd = holder.getView<ImageView>(R.id.ivAdd)
            ivAdd.setImageResource(R.mipmap.icon_jian)
        } else {
            val ivAdd = holder.getView<ImageView>(R.id.ivAdd)
            ivAdd.setImageResource(R.mipmap.icon_tianjia)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.erc_20_coin_list_item, parent, false)
        return BaseViewHolder(view)
    }
}
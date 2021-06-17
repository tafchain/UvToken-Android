package com.yongqi.wallet.ui.addCoin.adapter

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.yongqi.wallet.App
import com.yongqi.wallet.R
import com.yongqi.wallet.bean.Coin
import com.yongqi.wallet.utils.StringUtils.replaceByX

/**
 * author ：SunXiao
 * date : 2021/1/12 15:51
 * package：com.yongqi.wallet.ui.main.adapter
 * description :
 */
class ERC20CoinAdapter(layoutResId: Int, coinListData: MutableList<Coin>?) :
    BaseQuickAdapter<Coin, BaseViewHolder>(layoutResId, coinListData) {


    override fun convert(holder: BaseViewHolder, item: Coin) {
        when (item.name) {
            "USDT" -> {
                holder.getView<ImageView>(R.id.ivLogo).visibility = View.VISIBLE
            }
            else -> {
                holder.getView<ImageView>(R.id.ivLogo).visibility = View.INVISIBLE
            }
        }
        var coinIcon = holder.getView<ImageView>(R.id.ivCoinIcon)
        Glide.with(App.context).load(item.image).into(coinIcon)
        holder.setText(R.id.tvCoinName, item.name)
        holder.setText(R.id.tvLinks, replaceByX(item.contact_address))
        if (item.isAdd) {
            val ivAdd = holder.getView<ImageView>(R.id.ivAdd)
            ivAdd.setImageResource(R.mipmap.icon_jian)
        } else {
            val ivAdd = holder.getView<ImageView>(R.id.ivAdd)
            ivAdd.setImageResource(R.mipmap.icon_tianjia)
        }

    }
}
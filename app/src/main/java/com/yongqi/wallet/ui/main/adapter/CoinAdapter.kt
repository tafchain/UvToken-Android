package com.yongqi.wallet.ui.main.adapter

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.blankj.utilcode.util.SPUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.yongqi.wallet.App
import com.yongqi.wallet.R
import com.yongqi.wallet.bean.*
import com.yongqi.wallet.config.CoinConst
import com.yongqi.wallet.utils.LoadImageUtils
import java.math.BigDecimal
import java.math.BigDecimal.ROUND_DOWN

/**
 * author ：SunXiao
 * date : 2021/1/12 15:51
 * package：com.yongqi.wallet.ui.main.adapter
 * description :
 */
class CoinAdapter(layoutResId: Int, coinListData: MutableList<Coin>?) :
    BaseQuickAdapter<Coin, BaseViewHolder>(layoutResId, coinListData) {

    private var mIsChecked: Boolean = false

    fun isChecked(isChecked: Boolean = false) {
        mIsChecked = isChecked
    }

    override fun convert(holder: BaseViewHolder, item: Coin) {
        var coinIcon = holder.getView<ImageView>(R.id.ivCoinIcon)
        var ivTag = holder.getView<ImageView>(R.id.ivTag)
        var tvRmb = holder.getView<TextView>(R.id.tvRmb)
        /**
         * 加载图片
         */
        LoadImageUtils.loadImage(item.name, coinIcon,item.image+"")
        var ivCoinTag = holder.getView<ImageView>(R.id.ivCoinTag)
        when (item.name) {
            "USDT" -> {
                when (item.coin_tag) {
                    "ERC20" -> {
                        ivCoinTag.visibility = View.VISIBLE
                        ivCoinTag.setImageResource(R.mipmap.icon_erc20)
                    }
                    "OMNI" -> {
                        ivCoinTag.visibility = View.VISIBLE
                        ivCoinTag.setImageResource(R.mipmap.icon_omni)
                    }
                    CoinConst.TRC20 -> {
                        ivCoinTag.visibility = View.VISIBLE
                        ivCoinTag.setImageResource(R.mipmap.icon_trc20)
                    }
                    else->{
                        ivCoinTag.visibility = View.GONE
                    }
                }

            }
            else -> {
                ivCoinTag.visibility = View.GONE

            }
        }
        when (item.coin_tag) {
            CoinConst.ERC20 -> {
                Glide.with(App.context).load(R.mipmap.icon_eth).into(ivTag)
            }
            CoinConst.TRC20 -> {
                Glide.with(App.context).load(R.mipmap.ic_trx).into(ivTag)
            }
            CoinConst.OMNI -> {
                Glide.with(App.context).load(R.mipmap.icon_btc).into(ivTag)
            }
            else -> {
                Glide.with(App.context).load("").into(ivTag)
            }
        }
        holder.setText(R.id.tvCoinType, item.name)

        var tvCoinMoney = holder.getView<TextView>(R.id.tvCoinMoney)
        var tvRmbMoney = holder.getView<TextView>(R.id.tvRmbMoney)

        if (SPUtils.getInstance().getBoolean("isChecked", true)) {
            var balance = BigDecimal(item.balance)//币资产
            when (SPUtils.getInstance().getString("unit")) {
                "CNY" -> {
                    var money = "0"
                    if (balance > BigDecimal.ZERO) {
                        money = BigDecimal(item.price_cny).multiply(balance).setScale(2,BigDecimal.ROUND_DOWN).toString()
                    }
                    tvCoinMoney.text =  BigDecimal(item.balance).setScale(8,ROUND_DOWN).stripTrailingZeros().toPlainString() //.setScale(8,BigDecimal.ROUND_HALF_UP)
                   if (item.price_cny=="-1"){
                       tvRmbMoney.text = "~"
                       tvRmb.visibility = View.GONE
                   }else{
                       tvRmbMoney.text = money
                       tvRmb.visibility = View.VISIBLE
                   }
                }
                "USD" -> {
                    var money = "0"
                    if (balance > BigDecimal.ZERO) {
                        money = BigDecimal(item.price_usd).multiply(balance).setScale(2,BigDecimal.ROUND_DOWN).toString()
                    }
                    tvCoinMoney.text =  BigDecimal(item.balance).setScale(8,ROUND_DOWN).stripTrailingZeros().toPlainString()//.setScale(8,BigDecimal.ROUND_HALF_UP)

                    if (item.price_usd=="-1"){
                        tvRmbMoney.text = "~"
                        tvRmb.visibility = View.GONE
                    }else{
                        tvRmbMoney.text = money
                        tvRmb.visibility = View.VISIBLE
                    }
                }
                "EUR" -> {
                    var money = "0"
                    if (balance > BigDecimal.ZERO) {
                        money = BigDecimal(item.price_eur).multiply(balance).setScale(2,BigDecimal.ROUND_DOWN).toString()
                    }
                    tvCoinMoney.text =   BigDecimal(item.balance).setScale(8,ROUND_DOWN).stripTrailingZeros().toPlainString()
                    if (item.price_eur=="-1"){
                        tvRmbMoney.text = "~"
                        tvRmb.visibility = View.GONE
                    }else{
                        tvRmbMoney.text = money
                        tvRmb.visibility = View.VISIBLE
                    }
                }
                else -> {
                    var money = "0"
                    if (balance > BigDecimal.ZERO) {
                        money = BigDecimal(item.price_cny).multiply(balance).setScale(2,BigDecimal.ROUND_DOWN).toString()
                    }
                    tvCoinMoney.text =  BigDecimal(item.balance).setScale(8,ROUND_DOWN).stripTrailingZeros().toPlainString()

                    if (item.price_cny=="-1"){
                        tvRmbMoney.text = "~"
                        tvRmb.visibility = View.GONE
                    }else{
                        tvRmbMoney.text = money
                        tvRmb.visibility = View.VISIBLE
                    }
                }
            }

        } else {
            tvCoinMoney.text = "*****"
            tvRmbMoney.text = "*****"
        }

        when (SPUtils.getInstance().getString("unit")) {
            "CNY" -> {
                val drawable: Drawable = context.resources.getDrawable(R.mipmap.icon_rmb_hui)
                drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight);
                tvRmb.setCompoundDrawables(drawable, null, null, null)

            }
            "USD" -> {
                val drawable: Drawable = context.resources.getDrawable(R.mipmap.icon_usd_hui)
                drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight);
                tvRmb.setCompoundDrawables(drawable, null, null, null)
            }
            "EUR" -> {
                val drawable: Drawable = context.resources.getDrawable(R.mipmap.icon_eur_hui)
                drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight);
                tvRmb.setCompoundDrawables(drawable, null, null, null)
            }
            else -> {
                val drawable: Drawable = context.resources.getDrawable(R.mipmap.icon_rmb_hui)
                drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight);
                tvRmb.setCompoundDrawables(drawable, null, null, null)
            }
        }
    }


}
package com.yongqi.wallet.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.yongqi.wallet.App
import com.yongqi.wallet.R

/**
 * author ：SunXiao
 * date : 2021/1/25 16:34
 * package：com.yongqi.wallet.utils
 * description :加载资源图片
 */
object LoadImageUtils {

    fun loadImage(type: String?, iv: ImageView,url:String) {
        if (!url.isNullOrEmpty()){
            Glide.with(App.context).load(url).apply(RequestOptions.bitmapTransform(CircleCrop())).into(iv)
        }else{
            when (type) {
                "BTC" -> {
                    iv?.setImageResource(R.mipmap.icon_btc)
                }

                "ETH" -> {
                    iv?.setImageResource(R.mipmap.icon_eth)

                }

                "TRX" -> {
                    iv.setImageResource(R.mipmap.ic_trx)
                }
                "TAFT" -> {
                    iv?.setImageResource(R.mipmap.icon_taf)

                }

                "USDT" -> {
                    iv?.setImageResource(R.mipmap.icon_usdt)

                }

                "DAI" -> {
                    iv?.setImageResource(R.mipmap.icon_dai)

                }
                "YFI" -> {
                    iv?.setImageResource(R.mipmap.icon_yfi)

                }
                "WBTC" -> {
                    iv?.setImageResource(R.mipmap.icon_wbtc)
                }
                "LINK" -> {
                    iv?.setImageResource(R.mipmap.icon_link)
                }
                "UNI" -> {
                    iv?.setImageResource(R.mipmap.icon_uni)
                }

                "AECO" -> {
                    iv?.setImageResource(R.mipmap.arther_icon)
                }

                "Multi" -> {
                    iv?.setImageResource(R.mipmap.icon_dlqb_26)
                }
                else -> {//TODO 根据首字母生成图片资源

                }
            }
        }



    }
}
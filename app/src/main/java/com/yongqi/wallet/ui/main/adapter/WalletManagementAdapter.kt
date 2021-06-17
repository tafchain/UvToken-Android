package com.yongqi.wallet.ui.main.adapter

import android.graphics.Color
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.blankj.utilcode.util.SPUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.yongqi.wallet.R
import com.yongqi.wallet.bean.Wallet
import com.yongqi.wallet.utils.LoadImageUtils

/**
 * author ：SunXiao
 * date : 2021/1/12 15:51
 * package：com.yongqi.wallet.ui.main.adapter
 * description :
 */
class WalletManagementAdapter(layoutResId: Int, walletListData: MutableList<Wallet>?) :
    BaseQuickAdapter<Wallet, BaseViewHolder>(layoutResId, walletListData) {


    override fun convert(holder: BaseViewHolder, item: Wallet) {
        val walletId = SPUtils.getInstance().getString("walletId")
        var llItem = holder.getView<LinearLayout>(R.id.llItem)
        llItem.isSelected = item.wallet_id == walletId

        var tvWalletType = holder.getView<TextView>(R.id.tvWalletType)
        var tvManager = holder.getView<TextView>(R.id.tvManager)
        var tvWalletName = holder.getView<TextView>(R.id.tvWalletName)
        if (item.wallet_id == walletId) {
            tvWalletType.setTextColor(Color.parseColor("#ffffffff"))
            tvManager.setTextColor(Color.parseColor("#ffffffff"))
            tvWalletName.setTextColor(Color.parseColor("#ffffffff"))

        } else {
            tvWalletType.setTextColor(Color.parseColor("#ff1d223b"))
            tvManager.setTextColor(Color.parseColor("#ff1d223b"))
            tvWalletName.setTextColor(Color.parseColor("#ff1d223b"))

        }
        var coinIcon = holder.getView<ImageView>(R.id.ivCoinIcon)
        when (item.type) {
            "Multi" -> {
//                holder.setText(R.id.tvWalletType, "多链钱包")
                holder.setText(R.id.tvWalletType, context.getString(R.string.multi_chain_wallet))
            }
            else -> {
                holder.setText(
                    R.id.tvWalletType,
                    "${item.type}${context.getString(R.string.title_wallet)}"
                )
            }
        }
        LoadImageUtils.loadImage(item.type, coinIcon,"")

        holder.setText(R.id.tvWalletName, item.name)

    }
}
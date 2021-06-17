package com.yongqi.wallet.ui.createWallet.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.yongqi.wallet.R
import com.yongqi.wallet.bean.MnemonicBean

class ShuffleMnemonicAdapter(layoutResId: Int, mnemonicArray: MutableList<MnemonicBean>?) :
    BaseQuickAdapter<MnemonicBean, BaseViewHolder>(layoutResId, mnemonicArray) {


    override fun convert(holder: BaseViewHolder, item: MnemonicBean) {
        holder.setText(R.id.tvShuffleMnemonic,item.mnemonic)
        holder.getView<TextView>(R.id.tvShuffleMnemonic).isEnabled = item.enabled!!
    }
}
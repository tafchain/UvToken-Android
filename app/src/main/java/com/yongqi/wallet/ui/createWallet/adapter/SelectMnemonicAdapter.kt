package com.yongqi.wallet.ui.createWallet.adapter

import android.view.View
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.yongqi.wallet.R
import com.yongqi.wallet.bean.MnemonicBean

class SelectMnemonicAdapter(layoutResId: Int, mnemonicArray: MutableList<MnemonicBean>?) :
    BaseQuickAdapter<MnemonicBean, BaseViewHolder>(layoutResId, mnemonicArray) {


    override fun convert(holder: BaseViewHolder, item: MnemonicBean) {
        holder.setText(R.id.tvSelectMnemonic,item.mnemonic)
        if (item.isCorrect!!){
            holder.getView<ImageView>(R.id.ivIsCorrect).visibility = View.GONE
        }else{
            holder.getView<ImageView>(R.id.ivIsCorrect).visibility = View.VISIBLE
        }
    }
}
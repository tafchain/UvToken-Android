package com.yongqi.wallet.ui.addCoin.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.binder.BaseItemBinder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.yongqi.wallet.R

/**
 * author ：SunXiao
 * date : 2021/6/7 16:25
 * package：com.yongqi.wallet.ui.addCoin.ui
 * description :使用最基础的 BaseItemBinder 创建 Binder
 */
class TokenClassificationItemBinder : BaseItemBinder<String, BaseViewHolder>() {

    override fun convert(holder: BaseViewHolder, data: String) {
        holder.setText(R.id.tvTokenClassification, data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.token_classification_coin_list_item, parent, false)
        return BaseViewHolder(view)
    }
}
package com.yongqi.wallet.ui.main.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.yongqi.wallet.App
import com.yongqi.wallet.R
import com.yongqi.wallet.bean.Coin
import com.yongqi.wallet.db.wallet.WalletRepository
import com.yongqi.wallet.utils.StringUtils.replaceByX

/**
 * author ：SunXiao
 * date : 2021/1/12 15:51
 * package：com.yongqi.wallet.ui.main.adapter
 * description :
 */
class AECOAdapter(layoutResId: Int, AECOCoinListData: MutableList<Coin>?) :
    BaseQuickAdapter<Coin, BaseViewHolder>(layoutResId, AECOCoinListData) {


    override fun convert(holder: BaseViewHolder, item: Coin) {

        var walletRepository = WalletRepository(App.context)
        var wallet = item.wallet_id?.let { walletRepository?.getWalletById(it) }
        holder.setText(R.id.tvCoinAddress,replaceByX( item.address))
            .setText(R.id.tvWalletName,"AECO")
//            .setText(R.id.tvWalletName,wallet?.name)
    }
}
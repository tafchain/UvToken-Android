package com.yongqi.wallet.ui.addCoin.model

import android.content.Context

/**
 * author ：SunXiao
 * date : 2021/1/28 15:22
 * package：com.yongqi.wallet.ui.addCoin.model
 * description :
 */
interface AddCoinModel {

    fun fuzzySearch(fuzzy: String?, context: Context)


    fun tokenTicker(vararg id: String?, context: Context)


}
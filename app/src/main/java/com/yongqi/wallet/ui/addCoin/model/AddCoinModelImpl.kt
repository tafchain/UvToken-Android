package com.yongqi.wallet.ui.addCoin.model

import android.content.Context
import com.yongqi.wallet.api.NetApi
import com.yongqi.wallet.bean.HttpCoinBean
import com.yongqi.wallet.net.APIClient
import com.yongqi.wallet.net.APIResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * author ：SunXiao
 * date : 2021/1/28 15:27
 * package：com.yongqi.wallet.ui.addCoin.model
 * description :
 */
class AddCoinModelImpl(context: Context?):AddCoinModel {

    override fun fuzzySearch(fuzzy: String?, context: Context) {
//        APIClient.instance.instanceRetrofit(NetApi::class.java)
//            .fuzzySearch(fuzzy)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(object : APIResponse<HttpCoinBean>(context){
//                override fun success(data: HttpCoinBean?) {
//
//                }
//                override fun failure(errorMsg: String?) {
//
//                }
//            })
    }

    override fun tokenTicker(vararg id: String?, context: Context) {
//        APIClient.instance.instanceRetrofit(NetApi::class.java)
//            .tokenTicker(*id)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(object: APIResponse<HttpExchangeRateBean>(context){
//                override fun success(data: HttpExchangeRateBean?) {
//                    TODO("Not yet implemented")
//                }
//
//                override fun failure(errorMsg: String?) {
//                    TODO("Not yet implemented")
//                }
//
//            })
    }
}
package com.yongqi.wallet.ui.vm

import android.content.Context
import android.util.Log
import com.blankj.utilcode.util.SPUtils
import com.yongqi.wallet.api.NetApi
import com.yongqi.wallet.bean.EthFeeRate
import com.yongqi.wallet.bean.HttpExchangeRateBean
import com.yongqi.wallet.bean.VersionUpgradeRequest
import com.yongqi.wallet.bean.VersionUpgradeResponse
import com.yongqi.wallet.config.AppConst
import com.yongqi.wallet.net.APIClient
import com.yongqi.wallet.net.APIResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * author ：SunXiao
 * date : 2021/6/1 11:04
 * package：com.yongqi.wallet.ui.vm
 * description :封装 http请求 多次调用的情况
 */
object HttpApiHelper {

    /**
     * 获取到该币对应美元、人民币和欧元的汇率:1枚=？美元 ; 1枚=？人民币 ;  1枚=？欧元等
     */
    fun tokenTicker(context: Context, queryList: MutableList<String>?, successC:(httpExchangeRateBean: HttpExchangeRateBean?)->Unit={}, failureC:(errorMsg: String?)->Unit={}){
        APIClient.instance.instanceRetrofit(NetApi::class.java)
            .tokenTicker(queryList)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : APIResponse<HttpExchangeRateBean>(context) {
                override fun success(data: HttpExchangeRateBean?) {
//                    Log.e("tokenTicker", "onResponse==${data}")
                    successC(data)
                }

                override fun failure(errorMsg: String?) {
//                    Log.e("tokenTicker", "onFailure==${errorMsg}")
                    failureC(errorMsg)
                }

            })
    }

    /**
     * 版本更新
     */
    fun versionUpgrade(context: Context, successC:(versionUpgradeResponse: VersionUpgradeResponse?)->Unit={}, failureC:(errorMsg: String?)->Unit={}){
        val langType:String = if (SPUtils.getInstance().getString("language") == "简体中文") {
            "cn"
        }else{
            "en"
        }
        APIClient.instance.instanceRetrofit(NetApi::class.java)
            .versionUpgrade(VersionUpgradeRequest(1, AppConst.getVersionName(),langType))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : APIResponse<VersionUpgradeResponse>(context) {
                override fun success(data: VersionUpgradeResponse?) {
                    successC(data)
                }

                override fun failure(errorMsg: String?) {
                    failureC(errorMsg)
                }

            })
    }
    /**
     * 获取ETH的费率
     */
    fun getEthFeeRate(context: Context, successC:(ethFeeRate: EthFeeRate?)->Unit={}, failureC:(errorMsg: String?)->Unit={}){
        APIClient.instance.instanceRetrofit(NetApi::class.java)
            .getEthFeeRate("https://www.gasnow.org/api/v3/gas/price")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : APIResponse<EthFeeRate>(context) {
                override fun success(data: EthFeeRate?) {
                    successC(data)
                }

                override fun failure(errorMsg: String?) {
                    failureC(errorMsg)
                }

            })

    }
}
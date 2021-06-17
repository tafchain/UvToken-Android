package com.yongqi.wallet.utils

import android.util.Log
import api.*
import com.yongqi.wallet.BuildConfig
import io.reactivex.Observable
import io.reactivex.Observable.create
import io.reactivex.ObservableOnSubscribe
import sdk.Sdk

object SdkUtils {

    /**
     * 切换开发和线上环境：
     * regtest表示测试环境
     */
    fun paramConfig(): Observable<String> {
        val paramRequest = ParamRequest()
        if (BuildConfig.FLAVOR == "online"||BuildConfig.FLAVOR == "onlineuvtest") {

            paramRequest.net = "main"
        }
        if (BuildConfig.FLAVOR == "devtest"||BuildConfig.FLAVOR == "devuvtest") {
            paramRequest.net = "regtest"
        }


        return create(ObservableOnSubscribe<String> {

            Sdk.paramConfig(paramRequest, object : ParamCallback {
                override fun failure(p0: Exception?) {
                    Log.e("paramConfig", p0.toString())
                    it.onComplete()
                }

                override fun success(p0: ParamResponse?) {
                    Log.e("paramConfig", p0.toString())
                    p0?.net?.let { it1 -> it.onNext(it1) }
                    it.onComplete()
                }
            })
        })

    }

    /**
     * 查找交易记录
     */
    fun queryTransaction(queryBtcTransactionRequest: QueryBtcTransactionRequest): Observable<Map<String, String>> {
        var observable = create(ObservableOnSubscribe<Map<String, String>> {
            Sdk.queryTransaction(queryBtcTransactionRequest, object : QueryBtcTransactionCallback {
                override fun failure(p0: java.lang.Exception?) {
                    Log.e("queryTransaction", "p0==${p0?.toString()}")
                    it.onError(Throwable(p0?.message))
                    it.onComplete()
                }

                override fun success(p0: QueryBtcTransactionResponse?) {
                    Log.e("queryTransaction", "p0==${p0?.toString()}")

                    var txId = p0?.txId
                    var bip125Replaceable = p0?.bip125Replaceable
                    var blockHash = p0?.blockHash
                    var timeReceived = p0?.timeReceived
                    var blockIndex = p0?.blockIndex
                    var blockTime = p0?.blockTime
                    var hex = p0?.hex
                    var time = p0?.time
                    var dataMap = mutableMapOf<String, String>()

                    txId?.let { it1 -> dataMap.put("txId", it1) }
                    bip125Replaceable?.let { it1 -> dataMap.put("bip125Replaceable", it1) }
                    blockHash?.let { it1 -> dataMap.put("blockHash", it1) }
                    hex?.let { it1 -> dataMap.put("hex", it1) }
                    timeReceived?.let { it1 -> dataMap.put("timeReceived", it1.toString()) }
                    blockIndex?.let { it1 -> dataMap.put("blockIndex", it1.toString()) }
                    blockTime?.let { it1 -> dataMap.put("blockTime", it1.toString()) }
                    time?.let { it1 -> dataMap.put("time", it1.toString()) }

                    it.onNext(dataMap)
                    it.onComplete()
                }
            })
        })
        return observable
    }

    /**
     * 交易
     */
    fun transfer(transferRequest: TransferRequest): Observable<Map<String, String>> {
        var observable = create(ObservableOnSubscribe<Map<String, String>> {
            Sdk.transfer(transferRequest, object : TransferCallback {
                override fun failure(p0: Exception?) {
                    Log.e("transfer", "p0==${p0?.toString()}")
                    it.onError(Throwable(p0?.message))
                    it.onComplete()
                }

                override fun success(p0: TransferResponse?) {
//                    Log.e("transfer", "p0==${p0?.toString()}")
                    var txId = p0?.txId
                    var keyId = p0?.keyId
                    var coinType = p0?.coinType
                    var dataMap = mutableMapOf<String, String>()
                    txId?.let { it1 -> dataMap.put("txId", it1) }
                    keyId?.let { it1 -> dataMap.put("keyId", it1) }
                    coinType?.let { it1 -> dataMap.put("coinType", it1) }
                    it.onNext(dataMap)
                    it.onComplete()
                }
            })

        })
        return observable
    }

    /**
     * 给H5的数据添加签名
     */
    fun sign(aecoSignRequest: AecoSignRequest): Observable<Map<String, String>> {
        var observable = create(ObservableOnSubscribe<Map<String, String>> {
            Sdk.sign(aecoSignRequest, object : AecoSignCallback {
                override fun failure(p0: java.lang.Exception?) {
                    Log.e("sign", "p0==${p0.toString()}")
                    it.onError(Throwable(p0?.message))
                    it.onComplete()
                }

                override fun success(p0: AecoSignResponse?) {
//                    Log.e("sign", "p0==${p0.toString()}")
                    var address = p0?.address
                    var data = p0?.data
                    var signedData = p0?.signedData
                    var dataMap = mutableMapOf<String, String>()
                    dataMap["address"] = address ?: ""
                    dataMap["data"] = data ?: ""
                    dataMap["signedData"] = signedData ?: ""
                    it.onNext(dataMap)
                    it.onComplete()
                }

            })
        })
        return observable
    }


}
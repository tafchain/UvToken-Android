package com.yongqi.wallet.db.trade

import android.content.Context
import androidx.lifecycle.LiveData
import com.yongqi.wallet.bean.Trade
import com.yongqi.wallet.db.AppDatabase


/**
 * author ：SunXiao
 * date : 2021/1/20 11:31
 * package：com.yongqi.wallet.db.wallet
 * description :
 */
class TradeRepository {

    private var liveDataAllTrade: LiveData<List<Trade>>? = null
    private var tradeDao: TradeDao? = null

    constructor(context: Context) {
        val database: AppDatabase = AppDatabase.getInstance(context)
        tradeDao = database.tradeDao()

        if (liveDataAllTrade == null) liveDataAllTrade = tradeDao?.getAllLiveDataTrade()
    }


    //提供一些API给viewmodel使用
    fun insert(vararg trade: Trade?) {
        tradeDao?.insert(*trade)
    }

    fun delete(trade: Trade?) {
        tradeDao?.delete(trade)
    }

    //根据条件删除地址
    fun deleteById(uid: Int?) {
        tradeDao?.deleteById(uid)
    }

    fun update(trade: Trade?) {
        tradeDao?.update(trade)
    }

//    fun getAddressByType(type: String?): List<Trade?>? {
//        return tradeDao?.getAddressByType(type)
//    }

    fun getAddressById(uid: Int?):Trade? {
        return tradeDao?.getTradeById(uid)
    }

    fun getAll(): List<Trade>? {
        return tradeDao?.getAll()
    }

    fun getAllLiveDataTrade(): LiveData<List<Trade>>? {
        return tradeDao?.getAllLiveDataTrade()
    }

    fun getTradesByAddress(address: String?): List<Trade> ? {
        return tradeDao?.getTradesByAddress(address)
    }
}
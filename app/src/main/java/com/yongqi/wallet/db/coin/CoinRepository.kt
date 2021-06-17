package com.yongqi.wallet.db.coin

import android.content.Context
import androidx.lifecycle.LiveData
import com.yongqi.wallet.bean.Coin
import com.yongqi.wallet.db.AppDatabase

/**
 * author ：SunXiao
 * date : 2021/1/20 11:31
 * package：com.yongqi.wallet.db.wallet
 * description :
 */
class CoinRepository {

    private var liveDataAllCoin: LiveData<List<Coin>>? = null
    private var coinDao: CoinDao? = null

    constructor(context: Context) {
        val database: AppDatabase = AppDatabase.getInstance(context)
        coinDao = database.coinDao()

        if (liveDataAllCoin == null) liveDataAllCoin = coinDao?.getAllLiveDataCoins()
    }


    //提供一些API给viewmodel使用
    fun insert(vararg coins: Coin?) {
        coinDao?.insert(*coins)
    }

    fun delete(coin: Coin?) {
        coinDao?.delete(coin)
    }

//    //根据 wallet_id 和 coin_tag 和name删除币
//    @Query("DELETE FROM Coin WHERE wallet_id = (:wallet_id) and name=(:name) and coin_tag=(:coin_tag)")
//    fun delete(wallet_id: String?, name: String?, coin_tag: String?)

    fun delete(wallet_id: String?, name: String?, coin_tag: String?) {
        coinDao?.delete(wallet_id, name, coin_tag)
    }

    fun delete(wallet_id: String?) {
        coinDao?.deleteById(wallet_id)
    }

    fun update(coin: Coin?) {
        coinDao?.update(coin)
    }

    fun updateCoinIsBackUp(
        is_backup: Boolean?,
        address: String?,
        name: String?,
        coin_tag: String?
    ) {
        coinDao?.updateCoinIsBackUp(is_backup, address, name, coin_tag)
    }


    fun updateCoinBalance(
        balance: String?,
        address: String?,
        name: String?,
        coin_tag: String?) {
        coinDao?.updateCoinBalance(balance, address, name, coin_tag)
    }


    fun getCoinsById(wallet_id: String?): List<Coin>? {
        return coinDao?.getCoinsById(wallet_id)
    }

    fun getCoinsByName(name: String?): List<Coin>? {
        return coinDao?.getCoinsByName(name)
    }

    fun getCoinByWalletIdAndName(wallet_id: String?, name: String?):Coin? {
        return coinDao?.getCoin(wallet_id,name)
    }

    fun getCoinByWalletIdAndNameAndTag(wallet_id: String?, name: String?,coinTag:String?):Coin? {
        return coinDao?.getCoinByTag(wallet_id,name,coinTag)
    }


    fun getCoinsByNameAndAddress(name: String?,address: String?): Coin?{
        return coinDao?.getCoinsByNameAndAddress(name,address)
    }

    fun getAll(): List<Coin>? {
        return coinDao?.getAll()
    }

    fun getAllLiveDataCoin(): LiveData<List<Coin>>? {
        return coinDao?.getAllLiveDataCoins()
    }
}
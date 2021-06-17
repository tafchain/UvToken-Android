package com.yongqi.wallet.db.wallet

import android.content.Context
import androidx.lifecycle.LiveData
import com.yongqi.wallet.bean.Wallet
import com.yongqi.wallet.db.AppDatabase

/**
 * author ：SunXiao
 * date : 2021/1/20 11:31
 * package：com.yongqi.wallet.db.wallet
 * description :
 */
class WalletRepository {

    private var liveDataAllWallet: LiveData<List<Wallet>>? = null
    private var walletDao: WalletDao? = null

    constructor(context: Context) {
        val database: AppDatabase = AppDatabase.getInstance(context)
        walletDao = database.walletDao()
        if (liveDataAllWallet == null) liveDataAllWallet = walletDao?.getAllLiveDataWallet()
    }


    //提供一些API给viewmodel使用
    fun insert(vararg wallets: Wallet?) {
        walletDao?.insert(*wallets)
    }

    fun delete(wallet: Wallet?) {
        walletDao?.delete(wallet)
    }


    fun deleteById(walletId: String?) {
        walletDao?.deleteById(walletId)
    }

    fun update(wallet: Wallet?) {
        walletDao?.update(wallet)
    }

    fun updateWalletPwd(wallet_id: String?, newPwd: String?) {
        walletDao?.updateWalletPwd(wallet_id, newPwd)
    }

    fun updateWalletName(wallet_id: String?, newName: String?) {
        walletDao?.updateWalletName(wallet_id, newName)
    }


    fun getWalletById(wallet_id: String): Wallet? {
        return walletDao?.getWalletById(wallet_id)
    }

    fun getAll(): List<Wallet?>? {
        return walletDao?.getAll()
    }

    fun getAllLiveDataWallet(): LiveData<List<Wallet>>? {
        return walletDao?.getAllLiveDataWallet()
    }
}
package com.yongqi.wallet.db.address

import android.content.Context
import androidx.lifecycle.LiveData
import com.yongqi.wallet.bean.Address
import com.yongqi.wallet.db.AppDatabase


/**
 * author ：SunXiao
 * date : 2021/1/20 11:31
 * package：com.yongqi.wallet.db.wallet
 * description :
 */
class AddressRepository {

    private var liveDataAllAddress: LiveData<List<Address>>? = null
    private var addressDao: AddressDao? = null

    constructor(context: Context) {
        val database: AppDatabase = AppDatabase.getInstance(context)
        addressDao = database.addressDao()

        if (liveDataAllAddress == null) liveDataAllAddress = addressDao?.getAllLiveDataAddresses()
    }


    //提供一些API给viewmodel使用
    fun insert(vararg addresses: Address?) {
        addressDao?.insert(*addresses)
    }

    fun delete(address: Address?) {
        addressDao?.delete(address)
    }

    //根据条件删除地址
    fun deleteById(uid: Int?) {
        addressDao?.deleteById(uid)
    }

    fun update(address: Address?) {
        addressDao?.update(address)
    }

    fun getAddressByType(type: String?): List<Address?>? {
        return addressDao?.getAddressByType(type)
    }

    fun getAddressById(uid: Int?):Address? {
        return addressDao?.getAddressById(uid)
    }

    fun getAll(): List<Address?>? {
        return addressDao?.getAll()
    }

    fun getAllLiveDataAddresses(): LiveData<List<Address>>? {
        return addressDao?.getAllLiveDataAddresses()
    }
}
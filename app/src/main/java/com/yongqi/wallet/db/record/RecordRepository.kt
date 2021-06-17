package com.yongqi.wallet.db.record

import android.content.Context
import androidx.lifecycle.LiveData
import com.yongqi.wallet.bean.Record
import com.yongqi.wallet.db.AppDatabase


/**
 * author ：SunXiao
 * date : 2021/1/20 11:31
 * package：com.yongqi.wallet.db.wallet
 * description :
 */
class RecordRepository {

    private var liveDataAllRecords: LiveData<List<Record>>? = null
    private var recordDao: RecordDao? = null

    constructor(context: Context) {
        val database: AppDatabase = AppDatabase.getInstance(context)
        recordDao = database.recordDao()
        if (liveDataAllRecords == null) liveDataAllRecords = recordDao?.getAllLiveDataRecords()
    }

    fun delete(record: Record?) {
        recordDao?.delete(record)
    }

    fun deleteAll() {
        recordDao?.deleteAll()
    }

    //根据条件删除地址
    fun deleteById(uid: Int?) {
        recordDao?.deleteById(uid)
    }

//    fun updateByTxId(
//        status: String?,
//        from_: String?,
//        to_: String?,
//        value_: String?,
//        timestamp: Long?,
//        txId: String?
//    ) {
//
//        recordDao?.updateRecordByTxId(status, from_, to_, value_,timestamp, txId)
//    }

    fun getRecordsById(uid: Int?): Record? {
        return recordDao?.getRecordById(uid)
    }

    fun getRecordByTxIdAddressAndCoinType(
        txId: String?,
        address: String?,
        coinType: String?
    ): Record? {
        return recordDao?.getRecordByTxIdAddressAndCoinType(txId, address, coinType)
    }

    fun getRecordsTxIdAndCoinTag(txId: String?, coinTag: String?): Record? {
        return recordDao?.getRecordByTxId(txId, coinTag)
    }

    fun getAll(): List<Record?>? {
        return recordDao?.getAll()
    }

    fun getAllLiveDataRecords(): LiveData<List<Record>>? {
        return recordDao?.getAllLiveDataRecords()
    }

    //TODO 提供一些API给viewmodel使用
    fun insert(vararg records: Record?) {
        recordDao?.insert(*records)
    }

    //TODO
    fun update(record: Record?) {
        recordDao?.update(record)
    }

    //TODO
    fun getRecordListsTxId(txId: String?): List<Record>? {
        return recordDao?.getRecordListsTxId(txId)
    }

    //TODO
    fun getRecordsByAddress(address: String?): List<Record?>? {
        return recordDao?.getRecordsByAddress(address)
    }

    //TODO
    fun getRecordsByAddressAndCointype(address: String?, coinType: String?): List<Record?>? {
        return recordDao?.getRecordsByAddress(address, coinType)
    }


}
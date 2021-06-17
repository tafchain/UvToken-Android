package com.yongqi.wallet.db.record

import androidx.lifecycle.LiveData
import androidx.room.*
import com.yongqi.wallet.bean.Record

/**
 * author ：SunXiao
 * date : 2021/1/18 21:34
 * package：com.yongqi.wallet.db
 * description :
 */
@Dao
interface RecordDao {

    //插入地址
    /*
        onConflict：默认值是OnConflictStrategy.ABORT，表示当插入有冲突的时候的处理策略。
                    OnConflictStrategy封装了Room解决冲突的相关策略：
       1. OnConflictStrategy.REPLACE：冲突策略是取代旧数据同时继续事务。
       2. OnConflictStrategy.ROLLBACK：冲突策略是回滚事务。
       3. OnConflictStrategy.ABORT：冲突策略是终止事务。
       4. OnConflictStrategy.FAIL：冲突策略是事务失败。
       5. OnConflictStrategy.IGNORE：冲突策略是忽略冲突。
     */
    @Insert
    fun insert(vararg record: Record?)

    //删除交易记录
    @Delete
    fun delete(record: Record?)

    //删除交易记录
    @Query("delete from Record")
    fun deleteAll()

    //根据条件删除地址
    @Query("delete from Record where uid = :uid")
    fun deleteById(uid: Int?)

    //更新交易记录
    @Update
    fun update(record: Record?)


//    //根据TxId更新交易记录
//    @Query( "update Record set type = :status and address =:from_ and to_address = :to_ and amount = :value_ and time = :timestamp  where tx_id =:txId" )
//    fun updateRecordByTxId(status: String?,from_: String?,to_: String?,value_: String?,timestamp: Long?,txId: String?)
//
//
//    //根据TxId更新交易记录
//    @Query( "update Record set  Record= :status  where tx_id =:txId" )
//    fun updateRecordByTxId(status: Record?,txId: String?)

    //修改数据库中币表中是否订阅字段is_backup
    @Query("update  Coin  set is_backup = :is_backup where address = :address and name = :name and coin_tag = :coin_tag " )
    fun updateCoinIsBackUp(is_backup: Boolean? = false,address :String?,name :String?,coin_tag :String?="")

    //根据uid查询交易记录
    @Query("select * from Record where uid = :uid")
    fun getRecordById(uid: Int?): Record?

    //根据txId查询交易记录
    @Query("select * from Record where tx_id = :txId and address = :address and name = :coinType")
    fun getRecordByTxIdAddressAndCoinType(txId: String?,address: String?,coinType :String?): Record?


    //根据txId查询交易记录
    @Query("select * from Record where tx_id = :txId")
    fun getRecordListsTxId(txId: String?): List<Record>?


    //根据txId和coinTag查询交易记录
    @Query("select * from Record where tx_id = :txId and coin_tag = :coinTag")
    fun getRecordByTxId(txId: String?,coinTag: String?): Record?

    //根据address查询交易记录
    @Query("select * from Record where address = :address")
    fun getRecordsByAddress(address: String?):List<Record?>?


    //根据address和coinType查询交易记录
    @Query("select * from Record where address = :address and name = :coinType order by time DESC")
    fun getRecordsByAddress(address: String?,coinType:String?):List<Record?>?

    //查询所有交易记录
    @Query("select * from Record")
    fun getAll(): List<Record?>?

//    //只查N个字段
//    @Query("select name,pwd from Student")
//    fun getRecord(): List<StudentTuple?>?

    //查询所有地址
    @Query("select * from Record order by uid")
    fun getAllLiveDataRecords(): LiveData<List<Record>>?


}
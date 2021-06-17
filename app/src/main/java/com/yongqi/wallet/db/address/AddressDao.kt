package com.yongqi.wallet.db.address

import androidx.lifecycle.LiveData
import androidx.room.*
import com.yongqi.wallet.bean.Address

/**
 * author ：SunXiao
 * date : 2021/1/18 21:34
 * package：com.yongqi.wallet.db
 * description :
 */
@Dao
interface AddressDao {

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
    fun insert(vararg addresses: Address?)

    //删除地址
    @Delete
    fun delete(address: Address?)

    //根据条件删除地址
    @Query("delete from Address where uid = :uid")
    fun deleteById(uid: Int?)

    @Update
    fun update(address: Address?)

    //更新地址
    @Update
    fun updateAddress(addresses: Address)


    //根据uid查询地址
    @Query("select * from Address where uid = :uid")
    fun getAddressById(uid: Int?): Address

    //根据type查询地址
    @Query("select * from Address where type = :type")
    fun getAddressByType(type: String?):List<Address?>?

    //查询所有地址
    @Query("select * from Address")
    fun getAll(): List<Address?>?

//    //只查N个字段
//    @Query("select name,pwd from Student")
//    fun getRecord(): List<StudentTuple?>?

    //查询所有地址
    @Query("select * from Address order by uid")
    fun getAllLiveDataAddresses(): LiveData<List<Address>>?





}
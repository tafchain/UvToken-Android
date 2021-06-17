package com.yongqi.wallet.db.wallet

import androidx.lifecycle.LiveData
import androidx.room.*
import com.yongqi.wallet.bean.Wallet

/**
 * author ：SunXiao
 * date : 2021/1/18 21:34
 * package：com.yongqi.wallet.db
 * description :
 */
@Dao
interface WalletDao {



    //插入钱包
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
    fun insert(vararg wallets: Wallet?)

    //删除钱包
    @Delete
    fun delete(wallet: Wallet?)

    //根据条件删除钱包
    @Query("delete from Wallet where wallet_id = :wallet_id")
    fun deleteById(wallet_id: String?)

    @Update
    fun update(wallet: Wallet?)

    //更新钱包
    @Update
    fun updateWallet(wallets:Wallet)


    //修改钱包密码
    @Query("update  Wallet set password = :newPwd where wallet_id = :wallet_id" )
    fun updateWalletPwd(wallet_id: String?,newPwd :String?)

    //修改钱包名称
    @Query("update  Wallet set name = :newName where wallet_id = :wallet_id" )
    fun updateWalletName(wallet_id: String?,newName :String?)

    //根据ID查询钱包
    @Query("select * from Wallet where wallet_id = :wallet_id")
    fun getWalletById(wallet_id: String?): Wallet

    //查询所有钱包
    @Query("select * from Wallet")
    fun getAll(): List<Wallet?>?

//    //只查N个字段
//    @Query("select name,pwd from Student")
//    fun getRecord(): List<StudentTuple?>?

    //查询所有钱包
    @Query("select * from Wallet order by wallet_id")
    fun getAllLiveDataWallet(): LiveData<List<Wallet>>?





}
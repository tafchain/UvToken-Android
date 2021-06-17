package com.yongqi.wallet.db.coin

import androidx.lifecycle.LiveData
import androidx.room.*
import com.yongqi.wallet.bean.Coin

/**
 * author ：SunXiao
 * date : 2021/1/18 21:34
 * package：com.yongqi.wallet.db
 * description :
 */
@Dao
interface CoinDao {


    //插入币
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
    fun insert(vararg coins: Coin?)

    //删除币
    @Delete
    fun delete(coin: Coin?)

    //根据 wallet_id 和 coin_tag 和name删除币
    @Query("DELETE FROM Coin WHERE wallet_id = (:wallet_id) and name=(:name) and coin_tag=(:coin_tag)")
    fun delete(wallet_id: String?, name: String?, coin_tag: String?)

    //根据条件删除币
    @Query("delete from Coin where wallet_id = :wallet_id")
    fun deleteById(wallet_id: String?)

    //更新币
    @Update
    fun update(coin: Coin?)

    //修改数据库中币表中是否订阅字段is_backup
    @Query("update  Coin  set is_backup = :is_backup where address = :address and name = :name and coin_tag = :coin_tag " )
    fun updateCoinIsBackUp(is_backup: Boolean? = false,address :String?,name :String?,coin_tag :String?="")


    //修改数据库中币表中币的资产
    @Query("update  Coin  set balance = :balance where address = :address and name = :name and coin_tag = :coin_tag " )
    fun updateCoinBalance(balance: String?= "0",address :String?,name :String?,coin_tag :String?="")


    //根据币id查询币种
    @Query("select * from Coin where wallet_id = :wallet_id")
    fun getCoinsById(wallet_id: String?): List<Coin>?

    //根据钱包id和代币名称查询对应的代币
    @Query("select * from Coin where wallet_id = :wallet_id and name = :name")
    fun getCoin(wallet_id: String?,name: String?): Coin?

    //根据钱包id和代币名称查询对应的代币
    @Query("select * from Coin where wallet_id = :wallet_id and name = :name and coin_tag = :coinTag")
    fun getCoinByTag(wallet_id: String?,name: String?,coinTag:String?): Coin?

    //根据币name查询币种
    @Query("select * from Coin where name = :name")
    fun getCoinsByName(name: String?): List<Coin>?

    //根据币name和address查询币种
    @Query("select * from Coin where name = :name and address=:address")
    fun getCoinsByNameAndAddress(name: String?,address: String?): Coin?

    //查询所有币
    @Query("select * from Coin")
    fun getAll(): List<Coin>?

//    //只查N个字段
//    @Query("select name,pwd from Student")
//    fun getRecord(): List<StudentTuple?>?

    //查询所有币
    @Query("select * from Coin order by wallet_id")
    fun getAllLiveDataCoins(): LiveData<List<Coin>>?


}
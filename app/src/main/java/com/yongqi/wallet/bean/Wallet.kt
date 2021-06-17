package com.yongqi.wallet.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * author ：SunXiao
 * date : 2021/1/18 20:18
 * package：com.yongqi.wallet.bean
 * description :钱包的数据表
 */
@Entity(tableName = "Wallet")
//,foreignKeys = [ForeignKey(entity = Coin::class,parentColumns = arrayOf("wallet_id"),childColumns = arrayOf("uid")
//    ,onDelete = ForeignKey.CASCADE)])
data class Wallet(
    @ColumnInfo
    var is_backup: Boolean? = false,
    @ColumnInfo
    var name: String?,
    @ColumnInfo
    var password: String?,
    @ColumnInfo//TODO Multi ETH BTC TAF
    var type: String?,
    @ColumnInfo
    var wallet_id: String?
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    var uid: Int = 0
}
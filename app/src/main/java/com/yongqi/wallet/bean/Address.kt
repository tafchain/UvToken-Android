package com.yongqi.wallet.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * author ：SunXiao
 * date : 2021/1/18 17:26
 * package：com.yongqi.wallet.bean
 * description :地址簿的数据表
 */
@Entity(tableName = "Address")
data class Address(

    @ColumnInfo
    var type: String?,
    @ColumnInfo
    var name: String?,
    @ColumnInfo
    var address: String?,
    @ColumnInfo
    var des: String?
){
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    var uid: Int = 0
}
package com.yongqi.wallet.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


/**
 * author ：SunXiao
 * date : 2021/1/18 20:18
 * package：com.yongqi.wallet.bean
 * description :币的数据表
 */
@Entity(tableName = "Coin")
data class Coin(
    @ColumnInfo
    var address: String?,
    @ColumnInfo
    var name: String?,
    @ColumnInfo
    var wallet_id: String?,
    @ColumnInfo
    var coin: Long = 0x80000000,
    @ColumnInfo
    var account: Long = 0x80000000,
    @ColumnInfo
    var change: Long = 0,
    @ColumnInfo
    var index: Long = 0,
    @ColumnInfo
    var key_id: String?,
    @ColumnInfo
    var coin_tag: String? = "",//判断该币是否是OMNI,ERC20 和 ""
    @ColumnInfo//是否订阅
    var is_backup: Boolean? = false,
    @ColumnInfo//币资产
    var balance: String? = "0",
    @ColumnInfo //代币合约地址
    var contact_address: String? = "",
    @ColumnInfo
    var image: String? = "",
    @ColumnInfo
    var decimals: Int? = 0
) {


    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    var uid: Int = 0

    /**
     * 判断是否添加该ERC20代币
     */
    @Ignore
    var isAdd: Boolean = false

    /**
     * 网络图片
     */
    @Ignore
    var logo_png: String? = ""


    /**
     * 有多少个币
     */
    @Ignore
    var coinAmount: String? = "0"

    /**
     * 1币对应的美元数
     */
    @Ignore
    var price_usd: String? = "-1"

    /**
     * 1币对应的人民币
     */
    @Ignore
    var price_cny: String? = "-1"

    /**
     * 1币对应的欧元
     */
    @Ignore
    var price_eur: String? = "-1"

    /**
     * 该币的总价值
     */
    @Ignore
    var coinTotalMoney: String? = "0"


//    /**
//     * 搜索到的币id
//     */
//    @Ignore
//    var id: String? = ""
//    @Ignore
//    var symbol: String? = ""
//    @Ignore
//    var address: String? = ""
//    @Ignore
//    var image: String? = ""
//    @Ignore
//    var name: String? = ""


}
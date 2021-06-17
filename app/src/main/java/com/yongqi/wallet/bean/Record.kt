package com.yongqi.wallet.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * author ：SunXiao
 * date : 2021/1/18 17:26
 * package：com.yongqi.wallet.bean
 * description :转账记录的数据表
 */
@Entity(tableName = "Record")
data class Record(
    @ColumnInfo
    var tx_id: String?="",//交易id
    @ColumnInfo
    var address: String?="",//交易自身地址
    @ColumnInfo
    var amount: String?="0",//交易金额
    @ColumnInfo
    var result: String?="",//交易是否成功(成功、失败)
    @ColumnInfo
    var time: Long = 0,//交易时间
    @ColumnInfo
    var type: String?="",//收款、转账、挖矿奖励
    @ColumnInfo
    var name: String?="",//币名称(BTC、ETH、TAF)
    @ColumnInfo
    var coin_tag: String?="",//币标识(ERC20、OMNI)
    @ColumnInfo
    var to_address: String?="",//交易对方地址
    @ColumnInfo//TODO
    var block_height: Long?= 0,
    @ColumnInfo//矿工费
    var miner_fee: String  = "0",
    @ColumnInfo//备注 TODO 新增
    var memo: String?  = "",
    @ColumnInfo//发起转账时间 TODO 新增
    var startTime: Long = 0,
    @ColumnInfo//USDT转账是否成功 TODO 新增
    var valid: String? = "",
    @ColumnInfo// TODO 新增字段,ETH及其代币查询详情;如果该值小于等于6,则该笔交易在待确认中的状态中(需要重新查详情);否则(大于6)则该笔交易成功或失败,不需要重新查详情;
    var confirmations: String?= "1",
    @ColumnInfo//TODO 新增gas price 费用
    var gasPrice: String? = "-1",
    @ColumnInfo//TODO 新增交易nonce字段
    var nonce: String? = ""
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    var uid: Int = 0
}
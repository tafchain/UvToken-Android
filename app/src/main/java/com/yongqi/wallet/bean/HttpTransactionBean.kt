package com.yongqi.wallet.bean

/**
 * author ：SunXiao
 * date : 2021/2/2 13:17
 * package：com.yongqi.wallet.bean
 * description :交易模块bean
 */

data class Subscription(val coins: MutableList<SubCoinBean>?)

data class SubCoinBean(val coinType: String? = "", val address: String?)

//data class Hashs(val txhashs:MutableList<HashBean>?)
//data class HashBean(val hash:String?)

data class TxDetails(val list: TxHashs?)


data class TxHashs(val txhashs: MutableList<TxHashBean>?)


data class TxHashBean(
    val hash: String?,
    val blockHash: String? = "",
    val blockNumber: Long?,
    val from: String?,
    val to: String?,
    val from_: String?,
    val gas: String?,
    val gasUsed: String? = "0",
    val gasPrice: String? = "0",
    val fee: String?,
    val input: String?,
    val inputDecode: InputDecodeBean?,
    val logs: MutableList<InputDecodeBean?>? = mutableListOf() ,//新增
    val nonce: String?,
    val to_: String?,
    val transactionIndex: String?,
    val value_: String?,
    val timestamp: Long?,
    val status: String?,
    val hex: String? = "",
    val txid: String? = "",
    val size: Long? = 0,
    val vsize: Long? = 0,
    val version: Long? = 0,
    val locktime: Long?,
    val time: Long? = 0,
    val blocktime: Long? = 0,
    val blockheight: Long? = 0,
    val from_addresses: MutableList<ToAddress>? = mutableListOf(),
    val to_addresses: MutableList<ToAddress>? = mutableListOf(),
    var valid: String? = "",
   var  confirmations:String?="1"


)

data class ToAddress(
    val address: String? = "",
    val amount: String? = ""
)

data class InputDecodeBean(
    val _address: String?,
    val _from: String?,
    val _to: String?,
    val _value: String?,
    val _logIndex: String?
)


data class EthFeeRate(
    val rapid: String? = "0",
    val fast: String? = "0",
    val standard: String? = "0",
    val slow: String? = "0",
    val timestamp: Long? = 0
)

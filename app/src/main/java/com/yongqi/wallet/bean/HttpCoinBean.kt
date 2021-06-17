package com.yongqi.wallet.bean

/**
 * author ：SunXiao
 * date : 2021/1/28 14:44
 * package：com.yongqi.wallet.bean
 * description :
 */
data class HttpCoinBean(val list: List<CoinBean>? ) {
    /**
     * {
     * "code":0,
     * "data":{"list":[{"id":"wrapped-bitcoin","name":"Wrapped Bitcoin","symbol":"wbtc","address":"0x2260fac5e5542a773aa44fbcfedf7c193bc2c599","image":"https://assets.coingecko.com/coins/images/1/large/bitcoin.png?1547033579"}]},
     * "msg":"获取成功"}
     */


}

data class CoinBean(
                val id: String?,
                val name: String? ,
                val symbol: String? ,
                val address: String?,
                val type: String?,
                val decimals: Int? = 0,
                val logoURI: String? )


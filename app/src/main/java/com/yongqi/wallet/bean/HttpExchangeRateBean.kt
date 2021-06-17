package com.yongqi.wallet.bean

/**
 * author ：SunXiao
 * date : 2021/1/28 14:49
 * package：com.yongqi.wallet.bean
 * description :
 */
data class HttpExchangeRateBean(val list: List<ExchangeRateBean>?) {

    /**
     * {
     * "code":0,
     * "data":{"list":[{"id":"wrapped-bitcoin","name":"Wrapped Bitcoin","symbol":"wbtc","address":"0x2260fac5e5542a773aa44fbcfedf7c193bc2c599","image":"https://assets.coingecko.com/coins/images/1/large/bitcoin.png?1547033579"}]},
     * "msg":"获取成功"}
     */


}


data class ExchangeRateBean(
    val id: String?,
    val name: String?,
    val symbol: String?,
    val rank: String?,
    val logo: String?,
    val logo_png: String?,
    val price_usd: String?,//每个币对应多少美元
    val price_cny: String?,
    val price_eur: String?,
    val price_btc: String?,
    var isCollect: Boolean,
    val volume_24h_usd: String?,
    val market_cap_usd: String?,
    val available_supply: String?,
    val total_supply: String?,
    val max_supply: String?,
    val percent_change_1h: String?,
    val percent_change_24h: String?,
    val percent_change_7d: String?,
    val last_updated: String?


) {

}
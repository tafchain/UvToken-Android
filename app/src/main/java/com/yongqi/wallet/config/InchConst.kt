package com.yongqi.wallet.config

object InchConst {
    //1Inch 地址
    const val INCH_APPROVE_SPENDER: String = "https://api.1inch.exchange/v3.0/1/approve/spender"
    //获取余额
    const val INCH_BALANCE:String = "https://balances.1inch.exchange/v1.1/1/aggregatedBalancesAndAllowances/"
    //token
    const val INCH_TOKEN:String = "https://api.1inch.exchange/v3.0/1/tokens"
    //coin rate
    const val INCH_COIN_RATE = "https://token-prices.1inch.exchange/v1.1/1"
    //coin quote
    const val INCH_COIN_QUOTE = "https://api.1inch.exchange/v3.0/1/quote"
    //swap
    const val INCH_SWAP = "https://api.1inch.exchange/v3.0/1/swap"
}
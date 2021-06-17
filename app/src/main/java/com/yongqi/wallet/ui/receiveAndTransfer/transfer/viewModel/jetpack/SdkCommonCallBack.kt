package com.yongqi.wallet.ui.receiveAndTransfer.transfer.viewModel.jetpack

/**
 * author ：SunXiao
 * date : 2021/4/7 16:44
 * package：com.yongqi.wallet.ui.receiveAndTransfer.transfer.viewModel.callback
 * description :
 */
interface SdkCommonCallBack<T> {

    fun onSuccess(t: T?)

    fun onFailed(t: T?)

}


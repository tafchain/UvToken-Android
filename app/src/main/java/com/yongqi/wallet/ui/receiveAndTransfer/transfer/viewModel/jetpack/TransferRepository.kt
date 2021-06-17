package com.yongqi.wallet.ui.receiveAndTransfer.transfer.viewModel.jetpack

import api.EstimateTransactionSizeRequest
import api.EstimateTransactionSizeResponse
import com.yongqi.wallet.utils.SdkUtils
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import uv1.Uv1

/**
 * author ：SunXiao
 * date : 2021/4/7 11:19
 * package：com.yongqi.wallet.ui.receiveAndTransfer.transfer.viewModel
 * description :
 */
class TransferRepository {

    private var mTransferRepository: TransferRepository? = null

    fun getInstance(): TransferRepository? {

        if (mTransferRepository == null) {
            synchronized(TransferRepository::class.java) {
                if (mTransferRepository == null) {
                    mTransferRepository =
                        TransferRepository()
                }
            }
        }
        return mTransferRepository
    }

    /**
     * 获取BTC交易的size
     */
    fun getBTCSize(address: String, callBack: SdkCommonCallBack<Long>) {
        val estimateTransactionSizeRequest = EstimateTransactionSizeRequest()
        estimateTransactionSizeRequest.address = address
        val estimateTransactionSizeResponse: EstimateTransactionSizeResponse = Uv1.estimateTransactionSize(estimateTransactionSizeRequest)
        callBack.onSuccess(estimateTransactionSizeResponse.size)
    }


}


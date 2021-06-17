package com.yongqi.wallet.ui.receiveAndTransfer.transfer.viewModel

import android.util.Log
import api.EstimateTransactionSizeRequest
import api.EstimateTransactionSizeResponse
import com.yongqi.wallet.base.BaseViewModel
import com.yongqi.wallet.databinding.ActivityTransferBinding
import com.yongqi.wallet.utils.SdkUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import uv1.Uv1

/**
 * author ：SunXiao
 * date : 2021/1/14 15:25
 * package：com.yongqi.wallet.ui.receiveAndTransfer.transfer.viewModel
 * description :
 */
class TransferViewModel:BaseViewModel<ActivityTransferBinding>() {

    override fun initUi() {

    }

    override fun initNet() {

    }

    var size: Long? = 0
     fun getEstimateTransactionSize(address :String) {
        val estimateTransactionSizeRequest = EstimateTransactionSizeRequest()
        estimateTransactionSizeRequest.address = address
         try {
           val  estimateTransactionSizeResponse: EstimateTransactionSizeResponse = Uv1.estimateTransactionSize(estimateTransactionSizeRequest)
             size = estimateTransactionSizeResponse.size
         }catch (e:Exception) {

         }
    }
}
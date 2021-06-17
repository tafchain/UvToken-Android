package com.yongqi.wallet.ui.receiveAndTransfer.transfer.viewModel.jetpack

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * author ：SunXiao
 * date : 2021/1/14 15:25
 * package：com.yongqi.wallet.ui.receiveAndTransfer.transfer.viewModel
 * description :
 */
class TransferViewModel3 : ViewModel{

    //BTC获取size,用于计算矿工费
    private lateinit var size: MutableLiveData<Long>



    constructor(repository: TransferRepository){
       repository.getBTCSize("", object :SdkCommonCallBack<Long>{
           override fun onSuccess(t: Long?) {
//               size = LiveData<Long>
           }

           override fun onFailed(t: Long?) {

           }

       })
    }

    fun getBtcSize(): LiveData<Long> {

        return size
    }

}
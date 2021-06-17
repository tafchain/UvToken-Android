package com.yongqi.wallet.utils

import android.content.Context
import com.yongqi.wallet.ui.receiveAndTransfer.transfer.viewModel.jetpack.TransferRepository

/**
 * author ：SunXiao
 * date : 2021/4/7 17:14
 * package：com.yongqi.wallet.utils
 * description :  数据获取源,用于注入各种活动和片段所需的类的静态方法

 */
class InjectorUtils {

    // 获取 我的花园 仓库
    private fun getTransferRepository(context: Context): TransferRepository? {
        // 获取 我的花园 增删改查操作-DAO
//        val dao: GardenPlantingDao = AppDatabase.getInstance(context.applicationContext).getGardenPlantingDao()
        // 把 我的花园dao加入进去 并 创建 我的花园 仓库层
        return TransferRepository()
            .getInstance()
    }


}
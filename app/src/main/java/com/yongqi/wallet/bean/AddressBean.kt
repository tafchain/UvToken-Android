package com.yongqi.wallet.bean

import com.chad.library.adapter.base.entity.MultiItemEntity

/**
 * author ：SunXiao
 * date : 2021/1/18 17:26
 * package：com.yongqi.wallet.bean
 * description :
 */
data class AddressBean(
    var uid: Int? = 0,
    var type: String?,
    var name: String?,
    var address: String?,
    var des: String?,
    override var itemType: Int = 1

) : MultiItemEntity {
    companion object {
        const val IMG_TEXT = 1
        const val TEXT = 2
    }

}
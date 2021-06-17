package com.yongqi.wallet.ui.receiveAndTransfer.transfer.adapter

import android.widget.ImageView
import com.blankj.utilcode.util.StringUtils.getString
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.yongqi.wallet.R
import com.yongqi.wallet.bean.AddressBean

/**
 * author ：SunXiao
 * date : 2021/1/18 19:16
 * package：com.yongqi.wallet.ui.receiveAndTransfer.transfer.adapter
 * description :
 */
class AddressMultiItemQuickAdapter(data: MutableList<AddressBean>?) :
    BaseMultiItemQuickAdapter<AddressBean, BaseViewHolder>(data) {

    init {
        // 绑定 layout 对应的 type
        addItemType(AddressBean.IMG_TEXT, R.layout.address_img_text_item)
        addItemType(AddressBean.TEXT, R.layout.address_text_item)
//        addItemType(AddressBean.IMG, R.layout.item_image_view)
    }

    override fun convert(helper: BaseViewHolder, item: AddressBean) {
        // 根据返回的 type 分别设置数据
        when (helper.itemViewType) {
            AddressBean.IMG_TEXT -> {
                val ivCurrency = helper.getView<ImageView>(R.id.ivCurrency)
                helper.setText(R.id.tvCurrencyAddressIm,item.name)
                when(item.type){
                    "BTC"->{
                        ivCurrency.setImageResource(R.mipmap.icon_btc)
                    }
                    "ETH"->{
                        ivCurrency.setImageResource(R.mipmap.icon_eth)
//                        ivCurrency.setImageResource(R.mipmap.icon_eth)
                    }
                    "TRX"->{
                        ivCurrency.setImageResource(R.mipmap.ic_trx)
                    }
                }
            }
            AddressBean.TEXT -> {
                helper.setText(R.id.tvAddressName,item.name)
                    .setText(R.id.tvCurrencyAddress,item.address)
                    .setText(R.id.tvAddressDes,"${getString(R.string.description)}:${item.des}")
            }
        }


    }


}
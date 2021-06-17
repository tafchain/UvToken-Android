package com.yongqi.wallet.ui.receiveAndTransfer.transfer.ui

import android.view.Gravity
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.afollestad.materialdialogs.DialogBehavior
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.ToastUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.yongqi.wallet.R
import com.yongqi.wallet.base.BaseActivity
import com.yongqi.wallet.bean.Address
import com.yongqi.wallet.databinding.ActivityEditAddressBinding
import com.yongqi.wallet.db.address.AddressRepository
import com.yongqi.wallet.ui.receiveAndTransfer.transfer.viewModel.EditAddressViewModel
import kotlinx.android.synthetic.main.activity_edit_address.*
import kotlinx.android.synthetic.main.common_title.*

class EditAddressActivity : BaseActivity<ActivityEditAddressBinding, EditAddressViewModel>() {

    override fun getLayoutResource(): Int = R.layout.activity_edit_address
     var uid = 0

    private val addressRepository by lazy { AddressRepository(this) }

    override fun initData() {

        immersionBar {
//            fitsSystemWindows(true)//只适合纯色状态栏;解决状态栏和布局重叠问题，使用该属性,必须指定状态栏颜色
//            statusBarColor(R.color.transparent)
            statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
            titleBar(iTitle) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
        }
        ivBack.setOnClickListener(onClickListener)
        tvTitle.text = getString(R.string.edit_address)
        tvRight.visibility = View.VISIBLE
        tvRight.text = getString(R.string.save)

        ClickUtils.applySingleDebouncing(tvRight, 3000) {
            var uid = intent.getIntExtra("uid", 0)
            val addressName = etAddressName.text.toString().trim()
            val currencyAddress = etCurrencyAddress.text.toString().trim()
            val des = etDescription.text.toString().trim()
            var address = Address(type, addressName, currencyAddress, des)
            address.uid = uid
            addressRepository.update(address)
            ToastUtils.make().show(R.string.save_success)
            finish()
        }
        rlType.setOnClickListener(onClickListener)
        btnDelete.setOnClickListener(onClickListener)
        uid = intent.getIntExtra("uid", 0)
        var addressData = addressRepository.getAddressById(uid)
        var type1 = addressData?.type
        var name = addressData?.name
        var address = addressData?.address
        var des = addressData?.des
        when (type1) {
            "BTC" -> {
                tvCurrencyName.text = "BTC"
                ivCurrency.setImageResource(R.mipmap.icon_btc_18)
                type = "BTC"
            }
            "ETH" -> {
                tvCurrencyName.text = "ETH"
                ivCurrency.setImageResource(R.mipmap.icon_eth)
                type = "ETH"
            }
            "TRX" -> {
                tvCurrencyName.text = "TRX"
                ivCurrency.setImageResource(R.mipmap.ic_trx)
                type = "TRX"
            }

        }

        etAddressName.setText(name)
        etCurrencyAddress.setText(address)
        etDescription.setText(des)
    }


    private val onClickListener = View.OnClickListener {
        when (it.id) {
            R.id.ivBack -> finish()

            R.id.rlType -> {
                ivMore.setImageResource(R.mipmap.icon_more_on_hui)
                showSelectCurrencyDialog(BottomSheet(LayoutMode.WRAP_CONTENT))
            }
            R.id.btnDelete -> {
                val addressName = etAddressName.text.toString().trim()
                val currencyAddress = etCurrencyAddress.text.toString().trim()
                val des = etDescription.text.toString().trim()
                if (addressName.isNullOrEmpty() || currencyAddress.isNullOrEmpty()) {
                    ToastUtils.make().show(R.string.please_input)
                    return@OnClickListener
                }
                addressRepository.deleteById(uid)
                ToastUtils.make().show(R.string.success_delete)
                finish()
            }

        }
    }


    private fun showSelectCurrencyDialog(dialogBehavior: DialogBehavior = ModalDialog) {

        val dialog = MaterialDialog(this, dialogBehavior).show {
            customView(R.layout.select_currency_dialog, scrollable = false)
            lifecycleOwner(this@EditAddressActivity)

        }
        dialog.cancelOnTouchOutside(false)
        // Setup custom view content
        val customView = dialog.getCustomView()
        val rlBtc = customView.findViewById<RelativeLayout>(R.id.rlBtc)
        val rlETH = customView.findViewById<RelativeLayout>(R.id.rlETH)
        val rlTRX = customView.findViewById<RelativeLayout>(R.id.rlTRX)

        val tvIsBtcSelect = customView.findViewById<TextView>(R.id.tvIsBtcSelect)
        val tvIsETHSelect = customView.findViewById<TextView>(R.id.tvIsETHSelect)
        val tvIsTRXSelect = customView.findViewById<TextView>(R.id.tvIsTRXSelect)
        var currencyName = tvCurrencyName.text
        when (currencyName) {
            "BTC" -> {
                tvIsBtcSelect.isSelected = true
                tvIsETHSelect.isSelected = false
                tvIsTRXSelect.isSelected = false
            }
            "ETH" -> {
                tvIsBtcSelect.isSelected = false
                tvIsETHSelect.isSelected = true
                tvIsTRXSelect.isSelected = false
            }
            "TRX" -> {
                tvIsBtcSelect.isSelected = false
                tvIsETHSelect.isSelected = false
                tvIsTRXSelect.isSelected = true
            }
        }
        ivMore.setImageResource(R.mipmap.icon_more_on_hui)
        rlBtc.setOnClickListener {
            showSelectCurrency(1)
            dialog.dismiss()
        }
        rlETH.setOnClickListener {
            showSelectCurrency(2)
            dialog.dismiss()
        }
        rlTRX.setOnClickListener {
            showSelectCurrency(3)
            dialog.dismiss()
        }
        dialog.show()
    }

    var type = "BTC"
    private fun showSelectCurrency(position: Int) {
        ivMore.setImageResource(R.mipmap.icon_more_off_hui)
        when (position) {
            1 -> {
                tvCurrencyName.text = "BTC"
                ivCurrency.setImageResource(R.mipmap.icon_btc_18)
                type = "BTC"
            }
            2 -> {
                tvCurrencyName.text = "ETH"
                ivCurrency.setImageResource(R.mipmap.icon_eth)
                type = "ETH"
            }
            3 -> {
                tvCurrencyName.text = "TRX"
                ivCurrency.setImageResource(R.mipmap.ic_trx)
                type = "TRX"
            }
        }
    }
}
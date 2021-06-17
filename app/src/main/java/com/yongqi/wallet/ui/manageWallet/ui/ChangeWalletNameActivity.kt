package com.yongqi.wallet.ui.manageWallet.ui

import android.view.Gravity
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.blankj.utilcode.util.ToastUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.yongqi.wallet.R
import com.yongqi.wallet.base.BaseActivity
import com.yongqi.wallet.databinding.ActivityChangeWalletNameBinding
import com.yongqi.wallet.db.wallet.WalletRepository
import com.yongqi.wallet.ui.manageWallet.viewModel.ChangeWalletNameViewModel
import kotlinx.android.synthetic.main.activity_change_wallet_name.*
import kotlinx.android.synthetic.main.common_title.*

class ChangeWalletNameActivity :  BaseActivity<ActivityChangeWalletNameBinding, ChangeWalletNameViewModel>(){

    override fun getLayoutResource(): Int = R.layout.activity_change_wallet_name

    override fun initData() {

        immersionBar {
//            fitsSystemWindows(true)//只适合纯色状态栏;解决状态栏和布局重叠问题，使用该属性,必须指定状态栏颜色
//            statusBarColor(R.color.transparent)
            statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
            titleBar(iTitle) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
        }
        ivBack.setOnClickListener(onClickListener)
        btnConfirm.setOnClickListener(onClickListener)

        tvTitle.text = getString(R.string.modify_wallet_name)
        editTextCheck()
        var walletName = intent.getStringExtra("walletName")
        etNewName.setText(walletName)
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.ivBack -> {
                finish()
            }
            R.id.btnConfirm -> {
                var etNewName = etNewName.text.toString().trim()
                var walletId = intent.getStringExtra("walletId")
                var walletRepository = WalletRepository(this@ChangeWalletNameActivity)
                walletRepository.updateWalletName(walletId,etNewName)
                ToastUtils.make().show(R.string.modified_success)
                finish()
            }
        }
    }

    private fun editTextCheck() {
        etNewName.addTextChangedListener {
            var etNewName = etNewName.text.toString().trim()
            btnConfirm.isEnabled = !etNewName.isNullOrEmpty()
        }
    }


}
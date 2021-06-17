package com.yongqi.wallet.ui.importWallet.ui

import android.content.Intent
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.gyf.immersionbar.ktx.immersionBar
import com.yongqi.wallet.R
import com.yongqi.wallet.base.BaseActivity
import com.yongqi.wallet.config.CoinConst
import com.yongqi.wallet.databinding.ActivityPrivateKeyImportWalletBinding
import com.yongqi.wallet.ui.importWallet.viewModel.PrivateKeyImportWalletViewModel
import kotlinx.android.synthetic.main.activity_private_key_import_wallet.*
import kotlinx.android.synthetic.main.common_title.*

class PrivateKeyImportWalletActivity :
    BaseActivity<ActivityPrivateKeyImportWalletBinding, PrivateKeyImportWalletViewModel>() {

    override fun getLayoutResource(): Int = R.layout.activity_private_key_import_wallet

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.ivBack -> {
                finish()
            }
            R.id.btnImport -> {//跳转到设置密码页面
                val iconType: String? = intent.getStringExtra("iconType")
                var privateKey = etPrivateKey.text.trim().toString()

                startActivity(
                    Intent(this, PrivateKeyImportSetWalletPwdActivity::class.java)
                        .putExtra("iconType", iconType)
                        .putExtra("privateKey", privateKey)

                )
            }
        }
    }

    override fun initData() {
        immersionBar {
//            fitsSystemWindows(true)//只适合纯色状态栏;解决状态栏和布局重叠问题，使用该属性,必须指定状态栏颜色
//            statusBarColor(R.color.transparent)
            statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
            titleBar(iTitle) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
        }
        ivBack.setOnClickListener(onClickListener)
        val iconType: String? = intent.getStringExtra("iconType")
        when(iconType){
            "BTC"->{
                tvTitle.text = getString(R.string.import_btc_title)
                tvPlease_write_private_key.text = getString(R.string.input_btc_private_key_address)
            }
            "ETH"->{
                tvTitle.text = getString(R.string.import_eth_title)
                tvPlease_write_private_key.text = getString(R.string.input_eth_private_key_address)
            }
            CoinConst.TRX->{
                tvTitle.text = getString(R.string.import_trx_title)
                tvPlease_write_private_key.text = getString(R.string.input_trx_private_key_address)
            }

            "AECO"->{
                tvTitle.text = getString(R.string.import_aeco_title)
                tvPlease_write_private_key.text = getString(R.string.input_aeco_private_key_address)
            }
        }

        etPrivateKey.addTextChangedListener {
            btnImport.isEnabled = !it.isNullOrEmpty()
        }

        btnImport.setOnClickListener(onClickListener)
    }
}
package com.yongqi.wallet.ui.scan.ui

import android.view.Gravity
import android.view.View
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.yongqi.wallet.R
import com.yongqi.wallet.base.BaseActivity
import com.yongqi.wallet.bean.Coin
import com.yongqi.wallet.config.CoinConst
import com.yongqi.wallet.config.WalletConfig
import com.yongqi.wallet.databinding.ActivityScanResultBinding
import com.yongqi.wallet.db.coin.CoinRepository
import com.yongqi.wallet.ui.scan.viewModel.ScanResultViewModel
import com.yongqi.wallet.walletconnect.controller.MixProtocolController
import kotlinx.android.synthetic.main.activity_scan_result.*
import kotlinx.android.synthetic.main.common_title.*

class ScanResultActivity : BaseActivity<ActivityScanResultBinding, ScanResultViewModel>() {


    val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.ivBack -> {
                finish()
            }
            R.id.btnCopy -> {
                ClipboardUtils.copyText(tvPrivateKey.text)
                ToastUtils.make().show(R.string.copy_success)//显示吐司
            }
        }
    }

    override fun getLayoutResource(): Int = R.layout.activity_scan_result



    override fun initData() {
        immersionBar {
//            fitsSystemWindows(true)//只适合纯色状态栏;解决状态栏和布局重叠问题，使用该属性,必须指定状态栏颜色
//            statusBarColor(R.color.transparent)
            statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
            titleBar(iTitle) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
        }
        ivBack.setOnClickListener(onClickListener)
        tvTitle.setText(R.string.scan_result)
        btnCopy.setOnClickListener(onClickListener)
        val address = intent.getStringExtra("address")
        tvPrivateKey.text = address

//        if (address!!.startsWith("wc")) {
//            DialogUtils.showCustomDialog(this,supportFragmentManager,object :DialogUtils.DialogResponse {
//                override fun onSuccess(pwd: String) {
//                    queryEthCoinByAddress()
//                    WalletConnectUtil.checkWalletConnect(this@ScanResultActivity,address)
//                }
//
//                override fun onError() {
//                }
//
//            })
//        }else{
//            tvPrivateKey.text = address
//        }
    }

    //根据钱包地址查询币信息
    private fun queryEthCoinByAddress() {
        val coinRepository = CoinRepository(this)
        val coin: Coin? = coinRepository.getCoinByWalletIdAndName(WalletConfig.getInstance().walletId, CoinConst.ETH)
        if (null == coin) {
            MixProtocolController.ETHAddress = ""
            ToastUtils.showShort(R.string.has_no_create_eth)
            return
        }
        MixProtocolController.ETHAddress = coin.address
    }
}
package com.yongqi.wallet.ui.receiveAndTransfer.ui

import android.util.Log
import android.view.View
import android.webkit.WebViewClient
import com.gyf.immersionbar.ktx.immersionBar
import com.yongqi.wallet.R
import com.yongqi.wallet.base.BaseActivity
import com.yongqi.wallet.databinding.ActivityTransferWebBinding
import com.yongqi.wallet.ui.receiveAndTransfer.viewModel.TransferWebViewModel
import com.yongqi.wallet.view.webview.MWebChromeClient
import kotlinx.android.synthetic.main.activity_transfer_web.*
import kotlinx.android.synthetic.main.common_pb.*
import kotlinx.android.synthetic.main.common_title.*

class TransferWebActivity : BaseActivity<ActivityTransferWebBinding, TransferWebViewModel>() {

    override fun getLayoutResource(): Int = R.layout.activity_transfer_web


    override fun initData() {
        immersionBar {
//            fitsSystemWindows(true)//只适合纯色状态栏;解决状态栏和布局重叠问题，使用该属性,必须指定状态栏颜色
//            statusBarColor(R.color.transparent)
            statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
            titleBar(iTitle) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
        }

        ivBack.setOnClickListener(onClickListener)
        var hash = intent.getStringExtra("hash")
        var url = intent.getStringExtra("url")
        //1，开启h5与kotlin的通讯开关
        wvTransferWebDetail.settings.javaScriptEnabled = true
        //2，设置两个webviewclient
        wvTransferWebDetail.webChromeClient = MWebChromeClient(wvProgressBar,tvTitle)

        wvTransferWebDetail.webViewClient = WebViewClient()

        wvTransferWebDetail.settings.domStorageEnabled = true

        wvTransferWebDetail.loadUrl("$url$hash")
        Log.e("transfer_web_url","$url$hash")
    }


    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.ivBack -> {
                finish()
            }

        }
    }
}
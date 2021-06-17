package com.yongqi.wallet.ui.wallet.ui

import android.view.View
import android.webkit.WebViewClient
import com.blankj.utilcode.util.SPUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.yongqi.wallet.R
import com.yongqi.wallet.base.BaseActivity
import com.yongqi.wallet.databinding.ActivityAgreementBinding
import com.yongqi.wallet.ui.launch.viewModel.AgreementViewModel
import com.yongqi.wallet.view.webview.MWebChromeClient
import kotlinx.android.synthetic.main.activity_agreement.*
import kotlinx.android.synthetic.main.common_pb.*
import kotlinx.android.synthetic.main.common_title.*
import com.yongqi.wallet.BuildConfig


class AgreementActivity : BaseActivity<ActivityAgreementBinding, AgreementViewModel>() {

    override fun getLayoutResource(): Int = R.layout.activity_agreement


    override fun initData() {
        immersionBar {
//            fitsSystemWindows(true)//只适合纯色状态栏;解决状态栏和布局重叠问题，使用该属性,必须指定状态栏颜色
//            statusBarColor(R.color.transparent)
            statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
            titleBar(iTitle) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
        }
        //tvTitle.text = getString(R.string.terms_of_use)
        ivBack.setOnClickListener(onClickListener)

        //1，开启h5与kotlin的通讯开关
        wvAgreement.settings.javaScriptEnabled = true
        //2，设置两个webviewclient
        wvAgreement.webChromeClient = MWebChromeClient(wvProgressBar,tvTitle)

        wvAgreement.webViewClient = WebViewClient()

        wvAgreement.settings.domStorageEnabled = true

        when(SPUtils.getInstance().getString("language")){
            "简体中文"->{
                wvAgreement.loadUrl("${BuildConfig.API_URL}wallet/clause/agreement/agreement.html")
            }
            "English"->{
                wvAgreement.loadUrl("${BuildConfig.API_URL}wallet/clause/agreement/agreement-en.html")
            }
            else->{
                wvAgreement.loadUrl("${BuildConfig.API_URL}wallet/clause/agreement/agreement.html")
            }
        }

    }


    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.ivBack -> {
                finish()
            }

        }
    }
}
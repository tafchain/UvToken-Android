package com.yongqi.wallet.ui.manageFinances.ui

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import com.blankj.utilcode.util.ToastUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.just.agentwebX5.AgentWebX5
import com.tencent.smtt.export.external.interfaces.SslError
import com.tencent.smtt.export.external.interfaces.SslErrorHandler
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import com.tencent.smtt.sdk.*
import com.yongqi.wallet.App
import com.yongqi.wallet.R
import com.yongqi.wallet.base.BaseActivity
import com.yongqi.wallet.bean.Coin
import com.yongqi.wallet.config.CoinConst
import com.yongqi.wallet.config.WalletConfig
import com.yongqi.wallet.databinding.ActivityFinancialManagementWebViewBinding
import com.yongqi.wallet.db.coin.CoinRepository
import com.yongqi.wallet.ui.manageFinances.viewmodel.FinancialManagementWebViewViewModel
import com.yongqi.wallet.utils.DialogUtils
import com.yongqi.wallet.view.LollipopFixedWebView
import com.yongqi.wallet.walletconnect.WalletConnectManager
import com.yongqi.wallet.walletconnect.WalletConnectUtil
import com.yongqi.wallet.walletconnect.controller.MixProtocolController
import kotlinx.android.synthetic.main.activity_financial_management_web_view.*
import kotlinx.android.synthetic.main.activity_financial_management_web_view.iTitle
import kotlinx.android.synthetic.main.common_title.*

class FinancialManagementWebViewActivity :
    BaseActivity<ActivityFinancialManagementWebViewBinding, FinancialManagementWebViewViewModel>() {

    private var agentWebX5: AgentWebX5? = null
    override fun getLayoutResource(): Int = R.layout.activity_financial_management_web_view


    override fun initData() {
        immersionBar {
            statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
            titleBar(iTitle) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
        }
        tvTitle.text = intent.getStringExtra("title")
        ivBack.setOnClickListener(onClickListener)

        agentWebX5 = AgentWebX5.with(this)
            .setAgentWebParent(web_id_root, LinearLayout.LayoutParams(-1, -1))
            .useDefaultIndicator()
            .setIndicatorColor(R.color.abled_btn_color)
//            .setWebChromeClient(com.kylin.job.ui.main.WebActivity.CommonWebChromeClient())
            .setWebViewClient(webViewClient)
            .setWebView(LollipopFixedWebView(this))
            .createAgentWeb()
            .ready()
            .go(intent.getStringExtra("url"))

        initWebSetting()

    }

    override fun onDestroy() {
        //清空所有Cookie
        CookieSyncManager.createInstance(App.context)  //Create a singleton CookieSyncManager within a context
        val cookieManager:CookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookie()// Removes all cookies.
        CookieSyncManager.getInstance().sync() // forces sync manager to sync now

        agentWebX5?.webCreator?.get()?.webChromeClient = null
        agentWebX5?.webCreator?.get()?.webViewClient = null
        agentWebX5!!.webCreator.get().settings.javaScriptEnabled = false
        agentWebX5?.webCreator?.get()?.clearCache(true)
        agentWebX5?.clearWebCache()
        agentWebX5!!.webCreator.get().clearFormData()
        agentWebX5 = null
        WebStorage.getInstance().deleteAllData()
        super.onDestroy()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebSetting() {
        val mWebView = agentWebX5!!.webCreator.get()
        val webSettings = mWebView.settings
        webSettings.javaScriptEnabled = true
        webSettings.setSupportZoom(true)
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.domStorageEnabled = true
        webSettings.setAppCacheEnabled(true)
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
    }

    //根据钱包地址查询币信息
    private fun queryEthCoinByAddress() {
        val coinRepository = CoinRepository(this)
        val coin: Coin? = coinRepository.getCoinByWalletIdAndName(WalletConfig.getInstance().walletId, CoinConst.ETH)
        if (null == coin) {
            ToastUtils.showShort(R.string.has_no_create_eth)
            MixProtocolController.ETHAddress = ""
            return
        }
        MixProtocolController.ETHAddress = coin.address
    }

    private val webViewClient: WebViewClient = object : CommonWebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (url.startsWith("wc") && url.contains("bridge") && url.contains("key")) {
                DialogUtils.showCustomDialog(this@FinancialManagementWebViewActivity,supportFragmentManager,object : DialogUtils.DialogResponse {
                    override fun onSuccess(pwd: String) {
                        queryEthCoinByAddress()
                        checkWalletConnect(url)
                    }

                    override fun onError() {

                    }

                })
                return true
            }
            return true
        }

        override fun onReceivedSslError(
            webView: WebView,
            sslErrorHandler: SslErrorHandler,
            sslError: SslError
        ) {
//            super.onReceivedSslError(webView, sslErrorHandler, sslError);
            sslErrorHandler.proceed()
        }
    }

    private fun checkWalletConnect(result: String): Boolean {
        // 没有ETH地址，或者协议不对均不需要start wallet connect
        if (TextUtils.isEmpty(MixProtocolController.ETHAddress) || !WalletConnectUtil.isWalletConnectProtocol(result)) {
            return false
        }
        WalletConnectManager.getInstance().startConnect(this, result)
        return true
    }
    private open class CommonWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            webView: WebView,
            webResourceRequest: WebResourceRequest
        ): Boolean {
            return super.shouldOverrideUrlLoading(webView, webResourceRequest)
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
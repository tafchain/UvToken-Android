package com.yongqi.wallet.view.webview

import android.text.TextUtils
import android.util.Log
import android.webkit.WebResourceResponse
import android.webkit.WebView
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.github.lzyzsd.jsbridge.BridgeWebViewClient
import com.yongqi.wallet.walletconnect.WalletConnectManager
import com.yongqi.wallet.walletconnect.WalletConnectUtil
import com.yongqi.wallet.walletconnect.controller.MixProtocolController

//package com.yongqi.taft.view.webview


class MWebViewClient(webView: BridgeWebView) : BridgeWebViewClient(webView) {


    override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
        if (url.toString().contains("wc")) {
            return null
        }
        return null
//        return super.shouldInterceptRequest(view, url)
    }


//    override fun onReceivedError(
//        view: WebView?,
//        request: WebResourceRequest?,
//        error: WebResourceError?
//    ) {
//        super.onReceivedError(view, request, error)
//    }
//
//    override fun onPageFinished(view: WebView?, url: String?) {
//        // 无参数调用
////                wvBooking.loadUrl("javascript:reloadPage()");
//        // 传递参数调用
////                wvBooking.loadUrl("javascript:selectLanguage('" + "en" + "')");
//
//        super.onPageFinished(view, url)
//
//    }
}
package com.yongqi.wallet.view.webview

import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.ProgressBar
import android.widget.TextView

class MWebChromeClient constructor(progressBar: ProgressBar,title: TextView? = null) : WebChromeClient() {

    private val mProgressBar: ProgressBar = progressBar
    private val mTitle: TextView? = title
    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        return super.onConsoleMessage(consoleMessage)
    }

    override fun onProgressChanged(view: WebView?, newProgress: Int) {//设置加载进度条
        if (newProgress == 100) {
            mProgressBar.visibility = GONE
        } else {
            if (GONE == mProgressBar.visibility) {
                mProgressBar.visibility = VISIBLE
            }
            mProgressBar.progress = newProgress
        }
        super.onProgressChanged(view, newProgress)

    }

    override fun onReceivedTitle(view: WebView?, title: String?) {
        super.onReceivedTitle(view, title)
        mTitle?.text = title
    }
}
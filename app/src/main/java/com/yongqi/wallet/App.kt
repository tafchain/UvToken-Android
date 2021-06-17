package com.yongqi.wallet

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.webkit.WebView
import androidx.multidex.MultiDex
import api.CreateWalletRequest
import com.blankj.utilcode.util.Utils
import com.squareup.moshi.Moshi
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.QbSdk.PreInitCallback
import com.yongqi.wallet.ui.quotes.server.BridgeServer
import com.yongqi.wallet.walletconnect.Session
import com.yongqi.wallet.walletconnect.WalletConnectHelperManager
import com.yongqi.wallet.walletconnect.impls.*
import com.yongqi.wallet.walletconnect.nullOnThrow
import es.dmoral.toasty.Toasty
import okhttp3.OkHttpClient
import org.komputing.khex.extensions.toNoPrefixHexString
import java.io.File
import java.util.*

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this
        CrashReport.initCrashReport(this, "61ea2db6e6", false)
        // 初始化MultiDex 解决65535bug
        MultiDex.install(this)
        WebView(this).destroy()
        Utils.init(this)
        showNetworkDialog = false
        initClient()
        initMoshi()
        initBridge()
        initX5WebView()
        initSessionStorage()
        WalletConnectHelperManager.getInstance().initWalletConnect(applicationContext)
    }

    private fun initClient() {
        client = OkHttpClient.Builder().build()
    }

    private fun initMoshi() {
        moshi = Moshi.Builder().build()
    }

    private fun initBridge() {
        bridge = BridgeServer(moshi)
        bridge.start()
    }

    private fun initSessionStorage() {
        storage = FileWCSessionStore(
            File(cacheDir, "session_store.json").apply { createNewFile() },
            moshi
        )
    }


    /**
     * 派生對象，單例模式
     */
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        private lateinit var client: OkHttpClient
        private lateinit var moshi: Moshi
        private lateinit var bridge: BridgeServer
        private lateinit var storage: WCSessionStore
        lateinit var config: Session.Config
//        lateinit var session: Session

        var showNetworkDialog = false
        val createWalletRequest = CreateWalletRequest()

//        fun resetSession(uri: String) {
//            nullOnThrow { session }?.clearCallbacks()
//            val key = ByteArray(32).also { Random().nextBytes(it) }.toNoPrefixHexString()
//
//            val moshi = Moshi.Builder().build()
//            val sessionDir = File("build/tmp/").apply { mkdirs() }
////            val sessionStore = FileWCSessionStore(File(sessionDir, "test_store.json").apply { createNewFile() }, moshi)
////            val uri =
////                "wc:eada8fc9-0e54-428b-8837-ad2bb2b60c21@1?bridge=https%3A%2F%2Fbridge.walletconnect.org&key=1f3a572ffc36dcbac1f3ad813e215b0871097800ef425665ef96a8f1f2b352d0"
////            val config = Session.Config.fromWCUri(uri)
////            val uri =
////                "wc:ffd70e47-8634-4eba-95e9-81d7d1ee3bc3@1?bridge=https%3A%2F%2Fbridge.walletconnect.org&key=10d842ec755f67ed37de894811d2b641e1e752f3a91cec05d64ed4b7735cb8c3"
//
//            val config = Session.Config.fromWCUri(uri)
////            config = Session.Config(
////                UUID.randomUUID().toString(),
////                "http://localhost:${BridgeServer.PORT}",
////                key
////            )
//            val session = WCSession(
//                config,
//                MoshiPayloadAdapter(moshi),
//                storage,
//                OkHttpTransport.Builder(client, moshi),
//                Session.PeerMeta(name = "Example App")
//            )
////            session = WCSession(
////                config,
////                MoshiPayloadAdapter(moshi),
////                storage,
////                OkHttpTransport.Builder(client, moshi),
////                Session.PeerMeta(name = "Example App")
////            )
//            session.init()
//            Thread.sleep(2000)
//            session.approve(listOf("0x00000000000000000000000000000000DeaDBEAF"), 1)
//        }
    }

    //腾讯X5webView
    private fun initX5WebView() {
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        val cb: PreInitCallback = object : PreInitCallback {
            override fun onViewInitFinished(arg0: Boolean) {}
            override fun onCoreInitFinished() {}
        }
        //x5内核初始化接口
        QbSdk.initX5Environment(applicationContext, cb)
    }


}
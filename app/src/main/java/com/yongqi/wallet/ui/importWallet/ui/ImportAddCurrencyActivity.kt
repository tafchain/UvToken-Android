package com.yongqi.wallet.ui.importWallet.ui

import android.content.Intent
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import api.*
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.gyf.immersionbar.ktx.immersionBar
import com.shehuan.nicedialog.BaseNiceDialog
import com.shehuan.nicedialog.NiceDialog
import com.shehuan.nicedialog.ViewConvertListener
import com.shehuan.nicedialog.ViewHolder
import com.yongqi.wallet.BuildConfig
import com.yongqi.wallet.R
import com.yongqi.wallet.base.BaseActivity
import com.yongqi.wallet.bean.Coin
import com.yongqi.wallet.bean.Wallet
import com.yongqi.wallet.config.CoinConst
import com.yongqi.wallet.databinding.ActivityImportAddCurrencyBinding
import com.yongqi.wallet.db.coin.CoinRepository
import com.yongqi.wallet.db.wallet.WalletRepository
import com.yongqi.wallet.ui.createWallet.adapter.AddCoinAdapter
import com.yongqi.wallet.ui.createWallet.bean.AddCoinBean
import com.yongqi.wallet.ui.importWallet.viewModel.ImportAddCurrencyViewModel
import com.yongqi.wallet.ui.main.ui.HomePageActivity
import com.yongqi.wallet.utils.ActivityCollector
import com.yongqi.wallet.utils.DirUtils
import com.yongqi.wallet.utils.RSAUtils
import com.yongqi.wallet.utils.Uv1Helper
import com.yongqi.wallet.view.LoadingDialog
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_import_add_currency.*
import kotlinx.android.synthetic.main.common_title.*
import sdk.Sdk
import uv1.Uv1
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

/**
 * 助记词导入钱包添加币种
 */
class ImportAddCurrencyActivity :
    BaseActivity<ActivityImportAddCurrencyBinding, ImportAddCurrencyViewModel>() {

    override fun getLayoutResource(): Int = R.layout.activity_import_add_currency

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.ivBack -> {
                finish()
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
        tvTitle.text = getString(R.string.add_currency)
//        btnNext.setOnClickListener(onClickListener)
        //设置防抖动
        ClickUtils.applySingleDebouncing(btnNext, 5000) {
            showCreateAnimationDialog()
        }
        if (BuildConfig.FLAVOR == "online" || BuildConfig.FLAVOR == "devtest") {
            rlAECO.visibility = View.GONE
        } else {//if ( BuildConfig.FLAVOR == "onlineuvtest"|| BuildConfig.FLAVOR == "devuvtest") {
            rlAECO.visibility = View.VISIBLE
        }
    }

//    /**
//     * 创建钱包动画弹窗
//     */
//    private lateinit var ivBtc: ImageView
//    private lateinit var ivLoadGif1: ImageView
//    private lateinit var ivETH: ImageView
//    private lateinit var ivLoadGif2: ImageView
//    private lateinit var ivAECO: ImageView
//    private lateinit var ivLoadGif3: ImageView
//    private fun showCreateAnimationDialog() {
//        val coinTypes = if (BuildConfig.FLAVOR == "online" || BuildConfig.FLAVOR == "devtest") {
//            "BTC,ETH,TRX"
//        } else {//if ( BuildConfig.FLAVOR == "onlineuvtest"|| BuildConfig.FLAVOR == "devuvtest") {
//            "BTC,ETH,AECO,TRX"
//        }
//        val dialog = MaterialDialog(this)
//            .customView(R.layout.create_wallet_dialog, scrollable = true)
//        dialog.cancelOnTouchOutside(false)
//        val customView = dialog.getCustomView()
//        val rlBtc = customView.findViewById<RelativeLayout>(R.id.rlBtc)
//        val ivArrow1 = customView.findViewById<ImageView>(R.id.ivArrow1)
//        val rlETH = customView.findViewById<RelativeLayout>(R.id.rlETH)
//        val ivArrow2 = customView.findViewById<ImageView>(R.id.ivArrow2)
//        val rlAECO = customView.findViewById<RelativeLayout>(R.id.rlAECO)
//
//        // Use the view instance, e.g. to set values or setup listeners
//        ivBtc = customView.findViewById<ImageView>(R.id.ivBtc)
//        ivLoadGif1 = customView.findViewById<ImageView>(R.id.ivLoadGif1)
//        ivETH = customView.findViewById<ImageView>(R.id.ivETH)
//        ivLoadGif2 = customView.findViewById<ImageView>(R.id.ivLoadGif2)
//        ivAECO = customView.findViewById<ImageView>(R.id.ivAECO)
//        ivLoadGif3 = customView.findViewById<ImageView>(R.id.ivLoadGif3)
//
//
//        if (coinTypes.contains("BTC")) {
//            rlBtc.visibility = View.VISIBLE
//            ivArrow1.visibility = View.VISIBLE
//        } else {
//            rlBtc.visibility = View.GONE
//            ivArrow1.visibility = View.GONE
//        }
//        if (coinTypes.contains("ETH")) {
//            rlETH.visibility = View.VISIBLE
//        } else {
//            rlETH.visibility = View.GONE
//        }
//        if (coinTypes.contains("AECO")) {
//            rlAECO.visibility = View.VISIBLE
//            ivArrow2.visibility = View.VISIBLE
//        } else {
//            rlAECO.visibility = View.GONE
//            ivArrow2.visibility = View.GONE
//        }
//
//        ivLoadGif1.visibility = View.VISIBLE
//        ivBtc.alpha = 0.3f
//        ivETH.alpha = 0.3f
//        ivAECO.alpha = 0.3f
//        Glide.with(this@ImportAddCurrencyActivity).load(R.drawable.load).into(ivLoadGif1)
//        dialog.show()
//        observableOperate(dialog, coinTypes)
//    }

    var addCoinAdapter = AddCoinAdapter(R.layout.item_dialog_add_coin)

    private fun showCreateAnimationDialog() {
        val coinTypes = if (BuildConfig.FLAVOR == "online" || BuildConfig.FLAVOR == "devtest") {
            "BTC,ETH,TRX"
        } else {//if ( BuildConfig.FLAVOR == "onlineuvtest"|| BuildConfig.FLAVOR == "devuvtest") {
            "BTC,ETH,AECO,TRX"
        }
        NiceDialog.init()
            .setLayoutId(R.layout.dialog_add_coin)     //设置dialog布局文件
            .setConvertListener(object : ViewConvertListener() {
                override fun convertView(holder: ViewHolder?, dialog: BaseNiceDialog?) {
                    val rvCoin = holder?.getView<RecyclerView>(R.id.rv_coin)
                    rvCoin?.layoutManager = GridLayoutManager(this@ImportAddCurrencyActivity,3)
                    rvCoin?.adapter = addCoinAdapter
                    val coinStrList = coinTypes.split(",")
                    val coinList = mutableListOf<AddCoinBean>()
                    coinStrList.forEachIndexed { index, bean ->
                        val addCoinBean = AddCoinBean()
                        if  (index == 0) {
                            addCoinBean.createStatus = 2
                        }else{
                            addCoinBean.createStatus = 3
                        }
                        addCoinBean.coinName = bean
                        coinList.add(addCoinBean)
                    }
                    addCoinAdapter.setList(coinList)
                    //创建钱包
                    observableOperate(dialog, coinTypes)

                }
            })
            .setDimAmount(0.3f)     //调节灰色背景透明度[0-1]，默认0.5f
            .setMargin(38)     //dialog左右两边到屏幕边缘的距离（单位：dp），默认0dp
            .setOutCancel(true)     //点击dialog外是否可取消，默认true
            .show(supportFragmentManager)     //显示dialog
    }

    var index: Int = 1
    lateinit var importWalletFromMnemonic: ImportWalletFromMnemonicResponse
    var keyId = ""
    var walletId = ""
    var coinType = ""
    var address = ""

    /**
     * 创建钱包
     * 在子线程中执行createWallet的请求,在ui线程中执行onProgress和success
     */
    private fun observableOperate(dialog: BaseNiceDialog?, coinTypes: String?) {
        val walletName = intent.getStringExtra("walletName")
        val pwd = intent.getStringExtra("pwd")
        val mnemonics = intent.getStringExtra("mnemonics")

        val importWalletFromMnemonicRequest = ImportWalletFromMnemonicRequest()
        importWalletFromMnemonicRequest.mnemonics = mnemonics
        importWalletFromMnemonicRequest.coinTypes = coinTypes
        importWalletFromMnemonicRequest.keystoreDir = DirUtils.createKeyStoreDir()
        importWalletFromMnemonicRequest.keystorePassword = pwd
        Uv1Helper.importWalletFromMnemonic(this,importWalletFromMnemonicRequest,object :Uv1Helper.ResponseDataCallback<ImportWalletFromMnemonicResponse> {
            override fun onSuccess(data: ImportWalletFromMnemonicResponse) {
                SPUtils.getInstance().put("walletId", data.walletId)
                walletId = data.walletId
                //钱包密码RSA加密保存
                val walletRepository = WalletRepository(this@ImportAddCurrencyActivity)
                walletRepository.insert(
                    Wallet(
                        wallet_id = walletId,
                        is_backup = true,
                        name = walletName,
                        type = "Multi",
                        password = ""
                    )
                )
                //添加币种
                val addCoinTypes = ArrayList<String>()
                addCoinTypes.add(CoinConst.BTC)
                addCoinTypes.add(CoinConst.ETH)
                addCoinTypes.add(CoinConst.TRX)
                if (BuildConfig.FLAVOR == "onlineuvtest" || BuildConfig.FLAVOR == "devuvtest") {
                    addCoinTypes.add(CoinConst.AECO)
                }
                for (i in addCoinTypes.indices) {
                    val addCoinTypeRequest = AddCoinTypeRequest()
                    addCoinTypeRequest.walletId = walletId
                    addCoinTypeRequest.coinType = addCoinTypes[i]
                    addCoinTypeRequest.keystoreDir = DirUtils.createKeyStoreDir()
                    addCoinTypeRequest.keystorePassword = pwd
//                    addCoinAdapter.data[i].createStatus = 1
//                    addCoinAdapter.notifyItemChanged(i)

                    Uv1Helper.addCoinType(this@ImportAddCurrencyActivity,addCoinTypeRequest,object :Uv1Helper.ResponseDataCallback<AddCoinTypeResponse> {
                        override fun onSuccess(data: AddCoinTypeResponse) {
                            if (null == data) {
                                return
                            }
                            insertCoinDataToDatabase(addCoinTypes[i], data)
                            if (i == addCoinTypes.size - 1) {
                                var position = 0
                                val timer = Timer()
                                val timerTask = object : TimerTask() {
                                    override fun run() {
                                        runOnUiThread {
                                            addCoinAdapter.data[position].createStatus = 1
                                            addCoinAdapter.notifyItemChanged(position)
                                            position += 1
                                            if (position<= addCoinTypes.size - 1){
                                                addCoinAdapter.data[position].createStatus = 2
                                                addCoinAdapter.notifyItemChanged(position)
                                            }
                                        }
                                        if (position == addCoinTypes.size-1) {
                                            Thread.sleep(1000L)
                                            timer?.cancel()
                                            runOnUiThread {
                                                dialog?.dismiss()
                                                ToastUtils.make().show(R.string.import_success)
                                                ActivityCollector.finishAll()

                                                startActivity(Intent(this@ImportAddCurrencyActivity, HomePageActivity::class.java))
                                                finish()
                                            }
                                        }
                                    }
                                }
                                timer.schedule(timerTask, 1500L, 1500L)
                            }

//                            else{
//                                addCoinAdapter.data[i + 1].createStatus = 2
//                                addCoinAdapter.notifyItemChanged(i + 1)
//                            }
                        }

                        override fun onError(e: Exception) {
                            Log.e("addCoinTypes", e.localizedMessage!!)
                            dialog?.dismiss()
                            ActivityCollector.finishAll()
                            startActivity(
                                Intent(this@ImportAddCurrencyActivity, RepeatImportActivity::class.java)
                            )
                        }
                    })
                }
            }

            override fun onError(e: Exception) {
                LoadingDialog.cancel()
                dialog?.dismiss()
                Log.e("createWallet", e.localizedMessage!!)
                startActivity(Intent(this@ImportAddCurrencyActivity, RepeatImportActivity::class.java))
            }
        })
    }

    /**
     * 插入币数据
     */
    private fun insertCoinDataToDatabase(
        coinType: String,
        addCoinTypeResponse: AddCoinTypeResponse
    ) {
        val coinRepository = CoinRepository(this@ImportAddCurrencyActivity)
        coinRepository.insert(
            Coin(
                address = addCoinTypeResponse.address,
                name = coinType,
                wallet_id = walletId,
                coin = addCoinTypeResponse.coin,
                account = addCoinTypeResponse.account,
                change = addCoinTypeResponse.change,
                index = addCoinTypeResponse.index,
                key_id = keyId,
                is_backup = false,
                coin_tag = ""
            )
        )
        if (coinType == "BTC") {
            coinRepository.insert(
                Coin(
                    address = addCoinTypeResponse.address,
                    name = "USDT",
                    wallet_id = walletId,
                    coin = addCoinTypeResponse.coin,
                    account = addCoinTypeResponse.account,
                    change = addCoinTypeResponse.change,
                    index = addCoinTypeResponse.index,
                    key_id = keyId,
                    is_backup = false,
                    coin_tag = "OMNI"
                )
            )
        }
    }


    //    /**
//     * 创建钱包动画弹窗
//     */
//    private fun showCreateAnimationDialog() {
//        val dialog = MaterialDialog(this)
//            .customView(R.layout.create_wallet_dialog, scrollable = true)
//        dialog.cancelOnTouchOutside(false)
//        val customView = dialog.getCustomView()
//        // Use the view instance, e.g. to set values or setup listeners
//        val ivBtc = customView.findViewById<ImageView>(R.id.ivBtc)
//        val ivLoadGif1 = customView.findViewById<ImageView>(R.id.ivLoadGif1)
//        val ivETH = customView.findViewById<ImageView>(R.id.ivETH)
//        val ivLoadGif2 = customView.findViewById<ImageView>(R.id.ivLoadGif2)
//        val ivTAF = customView.findViewById<ImageView>(R.id.ivTAF)
//        val ivLoadGif3 = customView.findViewById<ImageView>(R.id.ivLoadGif3)
//        val alphaAnimation1 = AlphaAnimation(0.3f, 1.0f)
//        alphaAnimation1.duration = 2000
//
//        val alphaAnimation2 = AlphaAnimation(0.3f, 1.0f)
//        alphaAnimation2.duration = 2000
//
//        val alphaAnimation3 = AlphaAnimation(0.3f, 1.0f)
//        alphaAnimation3.duration = 2000
//        alphaAnimation3.setAnimationListener(object : Animation.AnimationListener {
//            override fun onAnimationRepeat(animation: Animation?) {
//
//            }
//
//            override fun onAnimationEnd(animation: Animation?) {
//                ivLoadGif3.visibility = View.INVISIBLE
//                ivTAF.alpha = 1.0f
//                intervalRangeOperate(1, dialog)
//
//            }
//
//            override fun onAnimationStart(animation: Animation?) {
//                ivLoadGif3.visibility = View.VISIBLE
//                Glide.with(this@ImportAddCurrencyActivity).load(R.drawable.load).into(ivLoadGif3)
//
//
//
//            }
//
//        })
//        alphaAnimation2.setAnimationListener(object : Animation.AnimationListener {
//            override fun onAnimationRepeat(animation: Animation?) {
//
//            }
//
//            override fun onAnimationEnd(animation: Animation?) {
//                ivLoadGif2.visibility = View.INVISIBLE
//                ivTAF.startAnimation(alphaAnimation3)
//                ivETH.alpha = 1.0f
//            }
//
//            override fun onAnimationStart(animation: Animation?) {
//                ivLoadGif2.visibility = View.VISIBLE
//                Glide.with(this@ImportAddCurrencyActivity).load(R.drawable.load).into(ivLoadGif2)
//            }
//
//        })
//
//
//
//        alphaAnimation1.setAnimationListener(object : Animation.AnimationListener {
//            override fun onAnimationRepeat(animation: Animation?) {
//
//            }
//
//            override fun onAnimationEnd(animation: Animation?) {
//                ivLoadGif1.visibility = View.INVISIBLE
//                //                ivETH.animation = alphaAnimation2
//                ivETH.startAnimation(alphaAnimation2)
//                ivBtc.alpha = 1.0f
//            }
//
//            override fun onAnimationStart(animation: Animation?) {
//                ivLoadGif1.visibility = View.VISIBLE
//                ivBtc.alpha = 0.3f
//                ivETH.alpha = 0.3f
//                ivTAF.alpha = 0.3f
//                Glide.with(this@ImportAddCurrencyActivity).load(R.drawable.load).into(ivLoadGif1)
//            }
//
//        })
//        //        ivBtc.animation = alphaAnimation1
//        ivBtc.startAnimation(alphaAnimation1)
//        dialog.show()
//    }
//
//
//    /**
//     * intervalRange()
//     * 有什么用？
//     * 每隔一段时间就会发送一个事件，这个事件是从0开始，不断增1的数字。
//     * 可以指定发送事件的开始值和数量，其他与 interval() 的功能一样。
//     */
//    private fun intervalRangeOperate(countTime: Long, dialog: MaterialDialog) {
//        if (countTime <= 0) return
//        Observable.interval(0, 1, TimeUnit.SECONDS) //0延迟  每隔1秒触发
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())//操作UI主要在UI线程
//            .take(countTime + 1) //设置循环次数
//            .map { aLong ->
//                countTime - aLong
//            } //从60-1
//            .doOnSubscribe {
//
//            } //执行过程中按键为不可点击状态
//            .subscribe(object : Observer<Long> {
//                override fun onSubscribe(d: Disposable) {
////                        Log.e(tag, "onSubscribe")
//                }
//
//                override fun onNext(along: Long) {
//                    Log.e(tag(), "aLong:$along")
//
//                }
//
//                override fun onError(e: Throwable) {
//                    Log.e(tag(), "onError")
//                }
//
//                override fun onComplete() {
//                    dialog.dismiss()
//                    importWallet()
////                    startActivity(Intent(this@ImportAddCurrencyActivity, RepeatImportActivity::class.java)
////                        .putExtra("iconType",iconType))
//                }
//            })
//    }
//
//
//    fun importWallet(){
//        val walletName = intent.getStringExtra("walletName")
//        val pwd = intent.getStringExtra("pwd")
//        var mnemonics = intent.getStringExtra("mnemonics")

//        var importWalletFromMnemonicRequest = ImportWalletFromMnemonicRequest()
//        importWalletFromMnemonicRequest.mnemonics = mnemonics
////        interfaces.set(1,eth)
////        interfaces.set(2,taf)
//        importWalletFromMnemonicRequest.coinTypes = "BTC"
//        importWalletFromMnemonicRequest.keystoreDir = DirUtils.createKeyStoreDir()
//        importWalletFromMnemonicRequest.keystorePassword = pwd
//        Sdk.importWalletFromMnemonic(importWalletFromMnemonicRequest,
//            object : ImportWalletFromMnemonicCallback {
//                override fun onProgress(p0: String?, p1: String?, p2: String?) {
//                    //p0=keyId;p1=WalletId;p2=coinTypes
//                    Log.e(tag(), "p0==$p0;p1=$p1;p2==$p2")
//                }
//
//                override fun failure(p0: Exception?) {
//                    Log.e(tag(), p0.toString())
//                    startActivity(Intent(this@ImportAddCurrencyActivity, RepeatImportActivity::class.java))
//                }
//
//                override fun success(p0: ImportWalletFromMnemonicResponse?) {
//                    var walletId = p0?.walletId
//                    startActivity(Intent(this@ImportAddCurrencyActivity, HomePageActivity::class.java))
//                    Log.e(tag(), p0.toString())
//                }
//            }
//        )
//    }
}
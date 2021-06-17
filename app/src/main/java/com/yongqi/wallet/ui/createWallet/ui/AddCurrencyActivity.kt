package com.yongqi.wallet.ui.createWallet.ui

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import api.*
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ThreadUtils
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
import com.yongqi.wallet.databinding.ActivityAddCurrencyBinding
import com.yongqi.wallet.db.coin.CoinRepository
import com.yongqi.wallet.db.wallet.WalletRepository
import com.yongqi.wallet.ui.createWallet.adapter.AddCoinAdapter
import com.yongqi.wallet.ui.createWallet.bean.AddCoinBean
import com.yongqi.wallet.ui.createWallet.viewModel.AddCurrencyViewModel
import com.yongqi.wallet.utils.DirUtils
import com.yongqi.wallet.utils.Uv1Helper
import kotlinx.android.synthetic.main.activity_add_currency.*
import kotlinx.android.synthetic.main.common_title.*
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


class AddCurrencyActivity : BaseActivity<ActivityAddCurrencyBinding, AddCurrencyViewModel>() {

    override fun getLayoutResource(): Int = R.layout.activity_add_currency

    override fun initData() {
        immersionBar {
            statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
            titleBar(iTitle) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
        }
        ivBack.setOnClickListener(onClickListener)
        tvTitle.text = getString(R.string.add_currency)

        if (BuildConfig.FLAVOR == "online" || BuildConfig.FLAVOR == "devtest") {
            rlAECO.visibility = View.GONE

        } else {//if ( BuildConfig.FLAVOR == "onlineuvtest"|| BuildConfig.FLAVOR == "devuvtest") {
            rlAECO.visibility = View.VISIBLE
        }

        ClickUtils.applySingleDebouncing(btnCreate, 5000) {
            showCreateAnimationDialog()
        }
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.ivBack -> {
                finish()
            }
        }
    }


    var addCoinAdapter = AddCoinAdapter(R.layout.item_dialog_add_coin)

    private fun showCreateAnimationDialog() {
        val coinTypes = if (BuildConfig.FLAVOR == "online" || BuildConfig.FLAVOR == "devtest") {
            "BTC,ETH,TRX"
        } else {//if ( BuildConfig.FLAVOR == "onlineuvtest"|| BuildConfig.FLAVOR == "devuvtest") {
            "BTC,ETH,AECO"
        }
        NiceDialog.init()
            .setLayoutId(R.layout.dialog_add_coin)     //设置dialog布局文件
            .setConvertListener(object : ViewConvertListener() {
                override fun convertView(holder: ViewHolder?, dialog: BaseNiceDialog?) {
                    val rvCoin = holder?.getView<RecyclerView>(R.id.rv_coin)
                    rvCoin?.layoutManager = GridLayoutManager(this@AddCurrencyActivity, 3)
                    rvCoin?.adapter = addCoinAdapter
                    val coinStrList = coinTypes.split(",")
                    val coinList = mutableListOf<AddCoinBean>()
                    coinStrList.forEachIndexed { index, bean ->
                        val addCoinBean = AddCoinBean()
                        if (index == 0) {
                            addCoinBean.createStatus = 2
                        } else {
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
    var keyId = ""
    var walletId = ""
    var coinType = ""
    var address = ""

    /**
     * 创建钱包
     */
    private fun observableOperate(dialog: BaseNiceDialog?, coinTypes: String?) {
        val walletName = intent.getStringExtra("walletName")
        val pwd = intent.getStringExtra("pwd")

        val createWalletRequest = CreateWalletRequest()
        createWalletRequest.keystorePassword = pwd
        createWalletRequest.coinTypes = coinTypes//,ETH,TAFTb
        createWalletRequest.keystoreDir = DirUtils.createKeyStoreDir()
        Uv1Helper.createWallet(this, createWalletRequest,
            object : Uv1Helper.ResponseDataCallback<CreateWalletResponse> {
                override fun onSuccess(data: CreateWalletResponse?) {
                    if (null == data) {
                        return
                    }
                    SPUtils.getInstance().put("walletId", data.walletId)
                    walletId = data.walletId
                    //添加币种
                    val addCoinTypes = ArrayList<String>()
                    addCoinTypes.add("BTC")
                    addCoinTypes.add("ETH")
                    addCoinTypes.add("TRX")

                    if (BuildConfig.FLAVOR == "onlineuvtest" || BuildConfig.FLAVOR == "devuvtest") {
                        addCoinTypes.add("AECO")
                    }

                    for (i in addCoinTypes.indices) {
                        val addCoinTypeRequest = AddCoinTypeRequest()
                        addCoinTypeRequest.walletId = walletId
                        addCoinTypeRequest.coinType = addCoinTypes[i]
                        addCoinTypeRequest.keystoreDir = DirUtils.createKeyStoreDir()
                        addCoinTypeRequest.keystorePassword = pwd
                        Uv1Helper.addCoinType(this@AddCurrencyActivity, addCoinTypeRequest,
                            object : Uv1Helper.ResponseDataCallback<AddCoinTypeResponse> {
                                override fun onSuccess(data: AddCoinTypeResponse?) {
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
                                                        //钱包密码RSA加密保存
                                                        val walletRepository = WalletRepository(this@AddCurrencyActivity)
                                                        walletRepository.insert(Wallet(
                                                            wallet_id = walletId,
                                                            is_backup = false,
                                                            name = walletName,
                                                            type = "Multi",
                                                            password = ""
                                                        ))
                                                        dialog?.dismiss()
                                                        startActivity(Intent(this@AddCurrencyActivity, CreateWalletActivity::class.java)
                                                            .putExtra("walletId", walletId)
                                                            .putExtra("pwd", pwd))
                                                        finish()
                                                    }
                                                }
                                            }
                                        }
                                        timer.schedule(timerTask, 1500L, 1500L)

                                    }
                                }

                                override fun onError(e: java.lang.Exception?) {
                                    Log.e("addCoinTypes", e?.localizedMessage!!)
                                }

                            })
                    }

                }

                override fun onError(e: java.lang.Exception) {
                    Log.e("createWallet", e.localizedMessage!!)
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
        val coinRepository = CoinRepository(this@AddCurrencyActivity)
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


//   fun executeBySingleAtFixRate(){
//        asyncTest(10, new TestRunnable<String>() {
//            @Override
//            public void run(final int index, CountDownLatch latch) {
//                final TestScheduledTask<String> task = new TestScheduledTask<String>(latch, 3) {
//                    @Override
//                    public String doInBackground() throws Throwable {
//                        Thread.sleep(100 + index * 10);
//                        if (index < 4) {
//                            return Thread.currentThread() + " :" + index;
//                        } else if (index < 7) {
//                            cancel();
//                            return null;
//                        } else {
//                            throw new NullPointerException(String.valueOf(index));
//                        }
//                    }
//
//                    @Override
//                    void onTestSuccess(String result) {
//                        System.out.println(result);
//                    }
//                };
//                ThreadUtils.executeBySingleAtFixRate(task, 2000 + index * 10, TimeUnit.MILLISECONDS);
//            }
//        });
}
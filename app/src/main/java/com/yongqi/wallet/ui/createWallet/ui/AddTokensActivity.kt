package com.yongqi.wallet.ui.createWallet.ui

import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import api.AddCoinTypeRequest
import api.AddCoinTypeResponse
import api.CreateWalletRequest
import api.CreateWalletResponse
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SPUtils
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
import com.yongqi.wallet.databinding.ActivityAddTokensBinding
import com.yongqi.wallet.db.coin.CoinRepository
import com.yongqi.wallet.db.wallet.WalletRepository
import com.yongqi.wallet.ui.createWallet.adapter.AddCoinAdapter
import com.yongqi.wallet.ui.createWallet.adapter.AddTokenAdapter
import com.yongqi.wallet.ui.createWallet.bean.AddCoinBean
import com.yongqi.wallet.ui.createWallet.bean.AddTokenBean
import com.yongqi.wallet.ui.createWallet.viewModel.AddTokensViewModel
import com.yongqi.wallet.ui.main.ui.HomePageActivity
import com.yongqi.wallet.utils.ActivityCollector
import com.yongqi.wallet.utils.DialogUtils
import com.yongqi.wallet.utils.DirUtils
import com.yongqi.wallet.utils.Uv1Helper
import kotlinx.android.synthetic.main.activity_add_currency.*
import kotlinx.android.synthetic.main.activity_add_currency.iTitle
import kotlinx.android.synthetic.main.activity_add_tokens.*
import kotlinx.android.synthetic.main.common_title.*
import java.util.*

/**
 * 添加主链
 */
class AddTokensActivity : BaseActivity<ActivityAddTokensBinding, AddTokensViewModel>() {
    var keyId = ""
    private val addTokenAdapter = AddTokenAdapter(R.layout.item_add_token)

    private val coinRepository by lazy {
        CoinRepository(this@AddTokensActivity)
    }
    private val walletId by lazy {
        SPUtils.getInstance().getString("walletId")
    }

    private val btcCoin by lazy {
        coinRepository.getCoinByWalletIdAndName(walletId, CoinConst.BTC)
    }
    private val ethCoin by lazy {
        coinRepository.getCoinByWalletIdAndName(walletId, CoinConst.ETH)
    }
    private val trxCoin by lazy {
        coinRepository.getCoinByWalletIdAndName(walletId, CoinConst.TRX)
    }

    override fun getLayoutResource(): Int {
        return R.layout.activity_add_tokens
    }

    override fun initData() {
        initTitle()
        val tokens: MutableList<AddTokenBean> = mutableListOf()
        //status 1:已添加(不可取消) 2：未添加 3：添加（可取消）
        val btcAddTokenBean = AddTokenBean()
        btcAddTokenBean.tokenName = CoinConst.BTC
        if (null != btcCoin) {
            btcAddTokenBean.status = 1
        } else {
            btcAddTokenBean.status = 2
        }

        val ethAddTokenBean = AddTokenBean()
        ethAddTokenBean.tokenName = CoinConst.ETH
        if (null != ethCoin) {
            ethAddTokenBean.status = 1
        } else {
            ethAddTokenBean.status = 2
        }

        val trxAddTokenBean = AddTokenBean()
        trxAddTokenBean.tokenName = CoinConst.TRX
        if (null != trxCoin) {
            trxAddTokenBean.status = 1
        } else {
            trxAddTokenBean.status = 2
        }
        tokens.add(btcAddTokenBean)
        tokens.add(ethAddTokenBean)
        tokens.add(trxAddTokenBean)
        checkCreateBtnEnable()

        rv_token.layoutManager = LinearLayoutManager(this)
        rv_token.adapter = addTokenAdapter
        addTokenAdapter.setList(tokens)
        addTokenAdapter.setOnItemClickListener { _, _, position ->
            if (addTokenAdapter.data[position].status == 1) {
                return@setOnItemClickListener
            }
            if (addTokenAdapter.data[position].status == 2) {
                addTokenAdapter.data[position].status = 3
            } else {
                addTokenAdapter.data[position].status = 2
            }
            addTokenAdapter.notifyItemChanged(position)
            checkCreateBtnEnable()
        }

        ClickUtils.applySingleDebouncing(btn_create, 5000) {
            var isAddToken = false
            for (item in addTokenAdapter.data) {
                if (item.status == 3) {
                    isAddToken = true
                }
            }
            if (isAddToken) {
                DialogUtils.showCheckPwdDialog(this, supportFragmentManager, object :
                    DialogUtils.DialogResponse {
                    override fun onSuccess(pwd: String) {
                        showCreateAnimationDialog(pwd)
                    }

                    override fun onError() {

                    }

                })
            }
        }
    }

    private fun checkCreateBtnEnable() {
        var isAddToken = false
        for (item in addTokenAdapter.data) {
            if (item.status == 3) {
                isAddToken = true
            }
        }

        if (isAddToken) {
            btn_create.background = ContextCompat.getDrawable(this, R.drawable.common_btn_select)
            btn_create.isClickable = true
        } else {
            btn_create.background = ContextCompat.getDrawable(this, R.drawable.common_btn_pressed)
            btn_create.isClickable = false
        }
    }

    var addCoinAdapter = AddCoinAdapter(R.layout.item_dialog_add_coin)
    private fun showCreateAnimationDialog(pwd: String) {
        var coinTypes = ""
        for (item in addTokenAdapter.data) {
            if (item.status == 3) {
                coinTypes += "${item.tokenName},"
            }
        }
        coinTypes = coinTypes.substring(0,coinTypes.length-1)
        NiceDialog.init()
            .setLayoutId(R.layout.dialog_add_coin)     //设置dialog布局文件
            .setConvertListener(object : ViewConvertListener() {
                override fun convertView(holder: ViewHolder?, dialog: BaseNiceDialog?) {
                    val rvCoin = holder?.getView<RecyclerView>(R.id.rv_coin)
                    rvCoin?.layoutManager = GridLayoutManager(this@AddTokensActivity, 3)
                    rvCoin?.adapter = addCoinAdapter
                    val coinStrList = coinTypes.split(",")
                    val coinList = mutableListOf<AddCoinBean>()
                    coinStrList.forEachIndexed { index, bean ->
                        val addCoinBean = AddCoinBean()
                        if (index==0){
                            addCoinBean.createStatus = 2
                        }else{
                            addCoinBean.createStatus = 3
                        }
                        addCoinBean.coinName = bean
                        coinList.add(addCoinBean)
                    }
                    addCoinAdapter.setList(coinList)
                    //创建钱包
                    observableOperate(dialog, coinList, pwd)

                }
            })
            .setDimAmount(0.3f)     //调节灰色背景透明度[0-1]，默认0.5f
            .setMargin(38)     //dialog左右两边到屏幕边缘的距离（单位：dp），默认0dp
            .setOutCancel(true)     //点击dialog外是否可取消，默认true
            .show(supportFragmentManager)     //显示dialog
    }

    /**
     * 创建钱包
     */
    private fun observableOperate(dialog: BaseNiceDialog?, coinTypes:MutableList<AddCoinBean>, pwd: String) {
        //添加币种
        val addCoinTypes = ArrayList<String>()

        for (item in addTokenAdapter.data) {
            if (item.status == 3) {
                addCoinTypes.add(item.tokenName)
            }
        }
        for (i in addCoinTypes.indices) {
            val addCoinTypeRequest = AddCoinTypeRequest()
            addCoinTypeRequest.walletId = walletId
            addCoinTypeRequest.coinType = addCoinTypes[i]
            addCoinTypeRequest.keystoreDir = DirUtils.createKeyStoreDir()
            addCoinTypeRequest.keystorePassword = pwd
//            addCoinAdapter.data[0].createStatus = 1
//            addCoinAdapter.notifyItemChanged(0)

            Uv1Helper.addCoinType(this@AddTokensActivity, addCoinTypeRequest,
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
                                            dialog?.dismiss()
                                            ActivityCollector.finishAll()
                                            startActivity(Intent(this@AddTokensActivity, HomePageActivity::class.java))
                                        }
                                    }
                                }
                            }
                            timer.schedule(timerTask, 1500L, 1500L)

                        }

//                        else{
//                            addCoinAdapter.data[i + 1].createStatus = 2
//                            addCoinAdapter.notifyItemChanged(i + 1)
//                        }
                    }

                    override fun onError(e: java.lang.Exception?) {
                        Log.e("addCoinTypes", e?.localizedMessage!!)
                    }

                })
        }

    }

    private fun initTitle() {
        immersionBar {
            statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
            titleBar(iTitle) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
        }
        ivBack.setOnClickListener {
            finish()
        }
        tvTitle.text = getString(R.string.add_currency)
    }

    /**
     * 插入币数据
     */
    private fun insertCoinDataToDatabase(
        coinType: String,
        addCoinTypeResponse: AddCoinTypeResponse
    ) {
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
}
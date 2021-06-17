package com.yongqi.wallet.ui.main.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.View
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import api.GetAddressBalanceRequest
import api.GetAddressBalanceResponse
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.gyf.immersionbar.ktx.immersionBar
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.listener.OnMultiListener
import com.yongqi.wallet.App
import com.yongqi.wallet.R
import com.yongqi.wallet.base.BaseFragment
import com.yongqi.wallet.bean.Coin
import com.yongqi.wallet.bean.SwitchWalletEvent
import com.yongqi.wallet.bean.Wallet
import com.yongqi.wallet.config.AppConst.ETH_TO_WEI
import com.yongqi.wallet.config.CoinConst
import com.yongqi.wallet.config.WalletConfig
import com.yongqi.wallet.databinding.FragmentWalletBinding
import com.yongqi.wallet.db.coin.CoinRepository
import com.yongqi.wallet.db.wallet.WalletRepository
import com.yongqi.wallet.ui.addCoin.ui.AddCoinActivity
import com.yongqi.wallet.ui.createWallet.ui.BackupMnemonicActivity
import com.yongqi.wallet.ui.main.adapter.CoinAdapter
import com.yongqi.wallet.ui.receiveAndTransfer.ui.ReceiveAndTransferActivity
import com.yongqi.wallet.ui.vm.HttpApiHelper
import com.yongqi.wallet.utils.DialogUtils
import com.yongqi.wallet.utils.PermissionUtils.requestCameraPermissions
import com.yongqi.wallet.utils.UtilString
import com.yongqi.wallet.utils.Uv1Helper
import com.yongqi.wallet.walletconnect.WalletConnectUtil
import com.yongqi.wallet.walletconnect.controller.MixProtocolController
import kotlinx.android.synthetic.main.fragment_wallet.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.StringBuilder
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.util.*


/**
 * 首页钱包
 */
class WalletFragment : BaseFragment<FragmentWalletBinding>() {
    private var mContext: HomePageActivity? = null
    private var coinAdapter: CoinAdapter? = null
    private var mWalletId: String = ""
    override fun getLayoutResource(): Int = R.layout.fragment_wallet

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = activity as HomePageActivity?
    }

    override fun initImmersionBar() {
        immersionBar {
            titleBar(llStatusBar)
        }
    }

    override fun initView() {
        mWalletId = WalletConfig.getInstance().walletId
        //查询默认值
        queryCoinsDefaultValue()
        initReceiver()
        tvWalletName.setOnClickListener(onClickListener)
        ivQrScan.setOnClickListener(onClickListener)
        cbTotalAssetsEye.setOnClickListener(onClickListener)
        ivClose.setOnClickListener(onClickListener)
        tvBackupImmediately.setOnClickListener(onClickListener)
        ivAddCoin.setOnClickListener(onClickListener)
    }

    override fun loadData() {}

    override fun onResume() {
        super.onResume()
        val wcContent = SPUtils.getInstance().getString("wcContent")
        if (UtilString.isNotEmpty(wcContent)) {
            DialogUtils.showCustomDialog(
                context,
                childFragmentManager,
                object : DialogUtils.DialogResponse {
                    override fun onSuccess(pwd: String) {
                        queryEthCoinByAddress()
                        WalletConnectUtil.checkWalletConnect(context, wcContent)
                    }

                    override fun onError() {
                    }

                })
            SPUtils.getInstance().put("wcContent", "")
        }

        val isChecked = SPUtils.getInstance().getBoolean("isChecked", true)
        cbTotalAssetsEye.isChecked = isChecked
        when (SPUtils.getInstance().getString("unit")) {
            "CNY" -> {
                tvTotalAssets.text =
                    StringBuilder().append(getString(R.string.total_assets)).append(" (CNY)")
            }
            "USD" -> {
                tvTotalAssets.text =
                    StringBuilder().append(getString(R.string.total_assets)).append(" (USD)")
            }
            "EUR" -> {
                tvTotalAssets.text =
                    StringBuilder().append(getString(R.string.total_assets)).append(" (EUR)")
            }
            else -> {
                tvTotalAssets.text =
                    StringBuilder().append(getString(R.string.total_assets)).append(" (CNY)")
            }
        }
        if (!NetworkUtils.isConnected()) {
            tvOffLine.visibility = View.VISIBLE
        } else {
            tvOffLine.visibility = View.GONE
        }

        coinAdapter = CoinAdapter(R.layout.rv_coin_item, coins)
        val layoutManager = LinearLayoutManager(mContext)
        rvCoin.layoutManager = layoutManager

        rvCoin.adapter = coinAdapter
        coinAdapter?.setOnItemClickListener { adapter, _, position ->
            val coin = adapter.data[position] as Coin
            startActivity(
                Intent(mContext, ReceiveAndTransferActivity::class.java)
                    .putExtra("address", coin.address)//交易地址
                    .putExtra("coinAmount", coin.balance)//币资产
                    .putExtra("coinType", coin.name)//币类型
                    .putExtra("keyId", coin.key_id)//币id
                    .putExtra("coinTag", coin.coin_tag)//币id
                    .putExtra("image", coin.image)//币id
                    .putExtra("contactAddress", coin.contact_address)//币id
                    .putExtra("decimals", coin.decimals)//币精度
            )
        }


        refreshLayout?.setRefreshHeader(ClassicsHeader(mContext))
//        refreshLayout.setRefreshFooter(ClassicsFooter(mContext))
        setRefreshMultiListener()
        refreshLayout?.setOnRefreshListener { //下拉刷新,清空数据,pageNo从1开始
            refresh()
        }
        refresh()
    }


    val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.tvWalletName -> {
                mContext?.drawerLayout?.openDrawer(GravityCompat.START)
            }
            R.id.ivQrScan -> {
                //初始化权限
                mContext?.let { requestCameraPermissions(it) }

            }

            R.id.cbTotalAssetsEye -> {
                SPUtils.getInstance().put("isChecked", cbTotalAssetsEye?.isChecked!!)
                refreshListAndTotalMoney()

            }
            R.id.ivClose -> {
                SPUtils.getInstance().put("isShowBackup", false)
                llBackUpPrompt?.visibility = View.GONE

            }
            R.id.tvBackupImmediately -> {
                val walletId = SPUtils.getInstance().getString("walletId")
                walletId?.let {
                    fragmentManager?.let { fm ->
                        DialogUtils.showCommonVerifyPasswordDialog(activity, fm, it,
                            block = { pwd ->
                                startActivity(
                                    Intent(context, BackupMnemonicActivity::class.java)
                                        .putExtra("pwd", pwd)
                                        .putExtra("walletId", walletId)
                                )
                            })
                    }
                }
            }
            R.id.ivAddCoin -> {
                val walletId = SPUtils.getInstance().getString("walletId")
                startActivity(Intent(mContext, AddCoinActivity::class.java)
                        .putExtra("walletId", walletId))
            }

        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun reloadData(switchWalletEvent: SwitchWalletEvent) {
        SPUtils.getInstance().put("isShowBackup", true)
        //写入接收广播后的操作
        if (!NetworkUtils.isConnected() && !App.showNetworkDialog) {//无网络提示
            DialogUtils.showNoNetworkDialog(activity, this.requireFragmentManager())
        }
        if (!NetworkUtils.isConnected()) {
            tvOffLine.visibility = View.VISIBLE
        } else {
            tvOffLine.visibility = View.GONE
        }
        refresh()
    }


    private var coins: MutableList<Coin>? = mutableListOf()//要显示的币数据列表

    /**
     * 刷新数据,刷新页面
     * */
    private fun refresh() {
        val walletId = SPUtils.getInstance().getString("walletId")
        Log.e("walletId", walletId)
        val walletRepository = mContext?.let { WalletRepository(it) }
        val wallet: Wallet? = walletRepository?.getWalletById(walletId)
        tvWalletName.text = wallet?.name

        val isShowBackup = SPUtils.getInstance().getBoolean("isShowBackup")
        if (wallet?.type == "Multi" && !wallet.is_backup!! && isShowBackup) {
            llBackUpPrompt.visibility = View.VISIBLE
        } else {
            llBackUpPrompt.visibility = View.GONE
        }

        val coinRepository = mContext?.let { CoinRepository(it) }
        coins = coinRepository?.getCoinsById(walletId) as MutableList<Coin>?
        coinAdapter?.data = this!!.coins!!
        coinAdapter!!.notifyDataSetChanged()

        coins?.forEachIndexed { index, coin ->
            if (coin.name == "ETH"||coin.name==CoinConst.TRX) {//TODO
                ivAddCoin.visibility = View.VISIBLE
            }
        }

        val iterator = coins?.iterator()
        while (iterator?.hasNext()!!) {//从数据中删除AECO币
            val next = iterator.next()
            if (next.name == "AECO") {
                iterator.remove()
            }
        }
        //TODO网络请求,获取到汇率,每个币的资产相加得到总资产
        getRate()
    }


    /**
     * 获取到费率,获取到币资产
     */
    private fun getRate() {
        if (coins.isNullOrEmpty()) return
        val queryList = mutableListOf<String>()
        coins?.forEachIndexed { index, coin ->
            coin.name?.let { queryList.add(index, it) }
        }

        activity?.let {
            HttpApiHelper.tokenTicker(it, queryList,
                successC = { httpExchangeRateBean->
                    if (refreshLayout == null) return@tokenTicker
                    if (refreshLayout.isRefreshing) {
                        refreshLayout.finishRefresh()//结束刷新
                    }

                    if (refreshLayout.isLoading) {
                        refreshLayout.finishLoadMore()//结束加载
                    }
                    val list = httpExchangeRateBean?.list
                    if (list.isNullOrEmpty()) return@tokenTicker
                    coins?.forEachIndexed { _, coin ->
                        list.forEachIndexed { _, exchangeRateBean ->
                            if (coin.name == (exchangeRateBean.symbol?.toUpperCase(Locale.ROOT))) {
                                coin.price_usd = exchangeRateBean.price_usd
                                coin.price_cny = exchangeRateBean.price_cny
                                coin.price_eur = exchangeRateBean.price_eur
                                coin.logo_png = exchangeRateBean.logo_png
                            }
                        }
                    }
                    coins?.forEachIndexed { _, coin ->
                        val getAddressBalanceRequest = GetAddressBalanceRequest()
                        //如果是主币？1、就传主币的名称;2、如果是代币就传主币的名称和tokenType代币的名称
                        when (coin.coin_tag) {
                            "OMNI" -> {
                                getAddressBalanceRequest.address = coin.address
                                getAddressBalanceRequest.coinType = "BTC"
                                getAddressBalanceRequest.tokenType = coin.name

                            }
                            "ERC20" -> {
                                getAddressBalanceRequest.address = coin.address
                                getAddressBalanceRequest.coinType = "ETH"
                                getAddressBalanceRequest.tokenType = coin.name
                                getAddressBalanceRequest.tokenAddress = coin.contact_address
                            }
                            "TRC20"-> {
                                getAddressBalanceRequest.address = coin.address
                                getAddressBalanceRequest.coinType = "TRX"
                                getAddressBalanceRequest.tokenType = coin.name
                                getAddressBalanceRequest.tokenAddress = coin.contact_address
                            }
                            else -> {
                                getAddressBalanceRequest.address = coin.address
                                getAddressBalanceRequest.coinType = coin.name
//                                    getAddressBalanceRequest.tokenType = coin.name
                            }
                        }

                        Uv1Helper.getAddressBalance(
                            activity,
                            getAddressBalanceRequest,
                            object : Uv1Helper.ResponseDataCallback<GetAddressBalanceResponse> {
                                override fun onSuccess(data: GetAddressBalanceResponse?) {
                                    Log.e(
                                        "getAddressBalance",
                                        getAddressBalanceRequest.address + "==" + getAddressBalanceRequest.coinType
                                                + "==" + data?.balanceAmount + "==" + data?.tokenType + "==" + data?.address + "==" + data?.coinType
                                    )
                                    activity?.runOnUiThread {
                                        val balanceAmount: String? = data?.balanceAmount

                                        var coinBalance = "0"
                                        if (coin.name == "ETH") {// ||coin.coin_tag == "ERC20") {
                                            val ethBalance = BigDecimal(balanceAmount)
                                            if (ethBalance > ZERO) {
//                                                coinBalance = ethBalance.divide(
//                                                    BigDecimal(ETH_TO_WEI),
//                                                    8,
//                                                    BigDecimal.ROUND_DOWN
//                                                ).stripTrailingZeros().toPlainString()

                                                coinBalance =
                                                    ethBalance.divide(BigDecimal(ETH_TO_WEI))
                                                        .stripTrailingZeros().toPlainString()

                                                coin.balance = coinBalance
                                            } else {
                                                coin.balance = "0"
                                                coinBalance = "0"
                                            }
                                        } else if (coin.coin_tag == "ERC20") {//TODO 新增
                                            var ethBalance = BigDecimal(balanceAmount)
                                            if (ethBalance > ZERO) {
                                                for (i in 0 until coin?.decimals!!) {
                                                    ethBalance =
                                                        ethBalance.divide(BigDecimal(10))
//                                                    ethBalance =  ethBalance.divide(BigDecimal(10), 8, BigDecimal.ROUND_DOWN)
                                                }
                                                coinBalance = ethBalance.stripTrailingZeros()
                                                    .toPlainString()
                                                coin.balance = coinBalance

                                            } else {
                                                coin.balance = "0"
                                                coinBalance = "0"
                                            }

                                        } else {
                                            val btcBalance = BigDecimal(balanceAmount)
                                            if (btcBalance > ZERO) {
                                                coinBalance = btcBalance.stripTrailingZeros()
                                                    .toPlainString()
                                                coin.balance = coinBalance
                                            } else {
                                                coin.balance = "0"
                                                coinBalance = "0"
                                            }
                                        }
                                        //更新数据库中的币资产
                                        val coinRepository =
                                            mContext?.let { CoinRepository(it) }
                                        coinRepository?.updateCoinBalance(
                                            coinBalance,
                                            coin.address,
                                            coin.name,
                                            coin.coin_tag
                                        )

                                        //数据准备就绪
                                        refreshListAndTotalMoney()
                                    }
                                }

                                override fun onError(e: java.lang.Exception?) {
                                    Log.e(
                                        "getAddressBalance",
                                        getAddressBalanceRequest.address + "==" + getAddressBalanceRequest.coinType
                                    )
                                }

                            })
                    }
                },
                failureC = { errorMsg->
                    refreshListAndTotalMoney()
                    if (refreshLayout == null) return@tokenTicker
                    if (refreshLayout.isRefreshing) {
                        refreshLayout.finishRefresh()//结束刷新

                    }
                    if (refreshLayout.isLoading) {
                        refreshLayout.finishLoadMore()//结束加载
                    }
                })
        }
    }

    private fun refreshListAndTotalMoney() {
        val isChecked = SPUtils.getInstance().getBoolean("isChecked", true)
        coinAdapter?.data = this.coins!!
        coinAdapter?.isChecked(isChecked)
        coinAdapter?.notifyDataSetChanged()
        if (cbTotalAssetsEye == null) return

        if (isChecked) {
            var totalMoney = BigDecimal(0)
            coins?.forEachIndexed { _, coin ->
                when (SPUtils.getInstance().getString("unit")) {
                    "CNY" -> {
                        if (coin.price_cny == "-1") {

                        } else {
                            totalMoney =
                                totalMoney.add(BigDecimal(coin.balance).multiply(BigDecimal(coin.price_cny)))
                        }

                    }
                    "USD" -> {
                        if (coin.price_usd == "-1") {

                        } else {
                            totalMoney =
                                totalMoney.add(BigDecimal(coin.balance).multiply(BigDecimal(coin.price_usd)))
                        }
                    }
                    "EUR" -> {
                        if (coin.price_eur == "-1") {

                        } else {
                            totalMoney =
                                totalMoney.add(BigDecimal(coin.balance).multiply(BigDecimal(coin.price_eur)))
                        }
                    }
                    else -> {
                        if (coin.price_cny == "-1") {

                        } else {
                            totalMoney =
                                totalMoney.add(BigDecimal(coin.balance).multiply(BigDecimal(coin.price_cny)))
                        }

                    }
                }
            }
            tvTotalMoney.text = totalMoney.setScale(2, BigDecimal.ROUND_HALF_UP).toString()
        } else {
            tvTotalMoney.text = "*****"
        }
    }


    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }


    var netBroadcastReceiver: NetBroadcastReceiver? = null

    /**
     * 注册网络监听的广播
     */
    private fun initReceiver() {
        val timeFilter = IntentFilter()
        timeFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        netBroadcastReceiver = NetBroadcastReceiver()
        context?.registerReceiver(netBroadcastReceiver, timeFilter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (netBroadcastReceiver != null) {
            context?.unregisterReceiver(netBroadcastReceiver)
            netBroadcastReceiver = null
        }

    }

    // 继承BroadcastReceivre基类
    class NetBroadcastReceiver : BroadcastReceiver() {
        // 复写onReceive()方法，接收到广播后，则自动调用该方法
        override fun onReceive(context: Context, intent: Intent) {
            EventBus.getDefault().post(SwitchWalletEvent())//网络变化,刷新数据

        }
    }

    /**
     * 根据滑动偏移量显示或隐藏头部控件
     */
    private fun setRefreshMultiListener() {
        refreshLayout.setOnMultiListener(object : OnMultiListener {
            override fun onFooterMoving(
                footer: RefreshFooter?,
                isDragging: Boolean,
                percent: Float,
                offset: Int,
                footerHeight: Int,
                maxDragHeight: Int
            ) {
            }

            override fun onHeaderStartAnimator(
                header: RefreshHeader?,
                headerHeight: Int,
                maxDragHeight: Int
            ) {

            }

            override fun onFooterReleased(
                footer: RefreshFooter?,
                footerHeight: Int,
                maxDragHeight: Int
            ) {
            }

            override fun onStateChanged(
                refreshLayout: RefreshLayout,
                oldState: RefreshState,
                newState: RefreshState
            ) {

            }

            override fun onHeaderMoving(
                header: RefreshHeader?,
                isDragging: Boolean,
                percent: Float,
                offset: Int,
                headerHeight: Int,
                maxDragHeight: Int
            ) {
                if (offset == 0) {
                    llWalletHead.visibility = View.VISIBLE
                } else {
                    llWalletHead.visibility = View.INVISIBLE
                }
            }

            override fun onFooterFinish(footer: RefreshFooter?, success: Boolean) {
            }

            override fun onFooterStartAnimator(
                footer: RefreshFooter?,
                footerHeight: Int,
                maxDragHeight: Int
            ) {
            }

            override fun onHeaderReleased(
                header: RefreshHeader?,
                headerHeight: Int,
                maxDragHeight: Int
            ) {
            }

            override fun onLoadMore(refreshLayout: RefreshLayout) {
            }

            override fun onRefresh(refreshLayout: RefreshLayout) {

            }

            override fun onHeaderFinish(header: RefreshHeader?, success: Boolean) {

            }

        })
    }

    //根据钱包地址查询币信息
    private fun queryEthCoinByAddress() {
        val coinRepository = CoinRepository(activity!!)
        val coin: Coin? = coinRepository.getCoinByWalletIdAndName(WalletConfig.getInstance().walletId, CoinConst.ETH)
        if (null == coin) {
            MixProtocolController.ETHAddress = ""
            ToastUtils.showShort(R.string.has_no_create_eth)
            return
        }
        MixProtocolController.ETHAddress = coin.address
    }

    //查询代币默认值
    private fun queryCoinsDefaultValue() {
        val coinRepository = CoinRepository(activity!!)
        val coins: MutableList<Coin>? = coinRepository.getAll() as MutableList<Coin>?
        coins?.forEachIndexed { _, coin ->
            Log.e("queryCoinsDefaultValue", Gson().toJson(coin))
            if (coin.name == CoinConst.ETH) {
                if (coin.coin == 0x80000000) {
                    coin.coin = 0x8000003c
                    coin.account = 0x80000000
                    coin.index = 0
                    coin.change = 0
                    coinRepository.update(coin)
                }
            }

            if (coin.name == CoinConst.AECO) {
                if (coin.coin == 0x80000000) {
                    coin.coin = 0x80000001
                    coin.account = 0x80000000
                    coin.index = 0
                    coin.change = 0
                    coinRepository.update(coin)
                }
            }

            if (coin.coin_tag == "ERC20") {
                if (coin.coin == 0x80000000) {
                    coin.coin = 0x8000003c
                    coin.account = 0x80000000
                    coin.index = 0
                    coin.change = 0
                    coinRepository.update(coin)
                }
            }

        }

    }
}
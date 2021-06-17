package com.yongqi.wallet.ui.main.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.webkit.WebView
import android.widget.*
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import com.blankj.utilcode.util.CollectionUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.smallraw.chain.ethereum.PrivateKey
import com.smallraw.chain.ethereum.extensions.toHexPrefix
import com.smallraw.chain.ethereum.network.MainNet
import com.smallraw.chain.ethereum.transaction.Transaction
import com.smallraw.chain.ethereum.transaction.serializers.TransactionSerializer
import com.yongqi.wallet.App
import com.yongqi.wallet.BuildConfig
import com.yongqi.wallet.R
import com.yongqi.wallet.api.NetApi
import com.yongqi.wallet.base.BaseActivity
import com.yongqi.wallet.bean.*
import com.yongqi.wallet.config.CoinConst
import com.yongqi.wallet.config.WalletConfig
import com.yongqi.wallet.databinding.ActivityHomePageBinding
import com.yongqi.wallet.db.coin.CoinRepository
import com.yongqi.wallet.db.wallet.WalletRepository
import com.yongqi.wallet.net.APIClient
import com.yongqi.wallet.net.APIResponse
import com.yongqi.wallet.ui.createWallet.dialog.CustomTipDialog
import com.yongqi.wallet.ui.launch.ui.LaunchActivity
import com.yongqi.wallet.ui.main.viewModel.HomePageViewModel
import com.yongqi.wallet.ui.manageFinances.ui.DappFragment
import com.yongqi.wallet.ui.manageFinances.ui.FinancialManagementFragment
import com.yongqi.wallet.ui.mine.ui.MineFragment
import com.yongqi.wallet.ui.quotes.ui.QuotesFragment
import com.yongqi.wallet.ui.transaction.ui.TransactionFragment
import com.yongqi.wallet.utils.DialogUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_home_page.*
import kotlinx.android.synthetic.main.fragment_wallet.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.math.BigInteger


/**
 * 首页
 */
class HomePageActivity : BaseActivity<ActivityHomePageBinding, HomePageViewModel>(),
    RadioGroup.OnCheckedChangeListener {
    var drawerLayout: DrawerLayout? = null
    private var mWallFragment: WalletFragment? = null
    private var mTransactionFragment: TransactionFragment? = null
    private var mQuotesFragment: QuotesFragment? = null
    private var mDappFragment: DappFragment? = null
    private var mMineFragment: MineFragment? = null
    private var waitTime: Long = 2000
    private var touchTime: Long = 0
    override fun getLayoutResource(): Int = R.layout.activity_home_page

    override fun onCreate(savedInstanceState: Bundle?) {
        SPUtils.getInstance().put("currentVersion",BuildConfig.VERSION_NAME)
        super.onCreate(savedInstanceState)
        if (null != savedInstanceState) {
            val intent = Intent(this, LaunchActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
         WebView(this).destroy()
    }

    override fun initData() {
        val privateKey =
            PrivateKey.ofHex("89afc24b157548633b6e54e4e7e6f00096cfb0e750854914472fc6571306e849")
        val publicKey = privateKey.getPublicKey()
        val address = publicKey.getAddress()

        val transaction = Transaction(
            nonce = 0,
            gasPrice = 1,
            gasLimit = 1,
            to = address,
            value = BigInteger.valueOf(1000000000000000000)
        )

        val sha256 = TransactionSerializer.hashForSignature(transaction, MainNet())
        val sign = privateKey.sign(sha256)
        transaction.signature = sign


        SPUtils.getInstance().put("isNeedOpen",false)
        drawerLayout = findViewById(R.id.drawerLayout)
        if (BuildConfig.FLAVOR == "online" || BuildConfig.FLAVOR == "devtest") {
            main_tip_button_transaction.visibility = View.GONE

            main_tip_button_quotation.visibility = View.VISIBLE
            main_tip_financial_management.visibility = View.VISIBLE

        } else if ( BuildConfig.FLAVOR == "onlineuvtest"|| BuildConfig.FLAVOR == "devuvtest") {
            main_tip_button_transaction.visibility = View.VISIBLE
            main_tip_button_quotation.visibility = View.GONE
            main_tip_financial_management.visibility = View.GONE
        }
        initNavigationBottom()

    }

    private fun initNavigationBottom() {
        main_radio_group.setOnCheckedChangeListener(this)
        main_tip_button_wallet.isChecked = true
        onCheckedChanged(main_radio_group, R.id.main_tip_button_wallet)
    }

    override fun onResume() {
        super.onResume()
        setSubscription()
        if (SPUtils.getInstance().getBoolean("isNeedOpen")) {
            EventBus.getDefault().post(OpenDrawLayoutEvent())
            SPUtils.getInstance().put("isNeedOpen",false)
        }
    }

    /**
     * 设置订阅
     * 创建coins参数数组
     * 1、查询数据库中Coin表中所有的数据;
     * 2、判断是否订阅is_backup true:订阅,false:未订阅;筛选出所有未订阅的数据;
     * 3、去除掉所有name为"AECO"的数据;
     * 4、如果name为USDT,coin_tag为OMNI,在coins里添加一条coinType为USDT_OMNI,address为address的数据;
     * 5、如果name为USDT,coin_tag为ERC20,在coins里添加一条coinType为USDT_ERC20,address为address的数据;
     * 6、否则,在coins里添加一条coinType为coin.name,address为address的数据;
     * 7、如果coins的size小于1,则直接返回，不调用订阅接口;
     * 8、否则,调用setSubscription接口;
     * 9、成功:循环数据库中所有的币,将包含coins数据的is_backup设置为true
     */
    private fun setSubscription() {
        val coins: MutableList<SubCoinBean> = mutableListOf()
        val coinRepository = CoinRepository(this)
        val allCoins = coinRepository.getAll()

        allCoins?.forEachIndexed { _, coin ->
            if (!coin.is_backup!!) {//去除已订阅的
                if (!coin.name.equals("AECO")) {//去除亚瑟币
                    if (coin.name == "USDT" && coin.coin_tag == "OMNI") {
                        coins.add(SubCoinBean(coinType = "USDT_OMNI", address = coin.address))
                    } else if (coin.name == "ETH" || coin.coin_tag == "ERC20") {
                        coins.add(SubCoinBean(coinType = "ETH", address = coin.address))
                    } else {
                        coins.add(SubCoinBean(coinType = coin.name, address = coin.address))
                    }
                }
            }
        }
        if (coins.size < 1) {
            return
        }
        APIClient.instance.instanceRetrofit(NetApi::class.java)
            .setSubscription(Subscription(coins))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : APIResponse<SubCoinBean>(this) {
                override fun success(data: SubCoinBean?) {
                    Log.e(tag(), "onResponse==${data.toString()}")
                    //订阅完成  更新本地数据库

                    allCoins?.forEachIndexed { _, coin ->
                        if (!coin.is_backup!!) {//去除已订阅的
                            if (!coin.name.equals("AECO")) {//去除亚瑟币
                                coinRepository.updateCoinIsBackUp(
                                    is_backup = true,
                                    address = coin.address,
                                    name = coin.name,
                                    coin_tag = coin.coin_tag
                                )
                            }
                        }
                    }
                }

                override fun failure(errorMsg: String?) {
                    Log.e(tag(), "onFailure==${errorMsg}")
                }

            })
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        val fragmentManager =
            supportFragmentManager //获取Fragment管理器
        val fragmentTransaction = fragmentManager.beginTransaction() //开启Fragment事务
        hideFragment(fragmentTransaction)
        when (checkedId) {
            R.id.main_tip_button_wallet -> {
                ImmersionBar.with(this).statusBarDarkFont(false).init()
                if (null == mWallFragment) {
                    mWallFragment = WalletFragment()
                    fragmentTransaction.add(R.id.nav_host_fragment, mWallFragment!!)
                    fragmentTransaction.show(mWallFragment!!)
                } else {
                    fragmentTransaction.show(mWallFragment!!)
                }
            }
            R.id.main_tip_button_transaction -> {
                checkEthEnable()
                ImmersionBar.with(this).statusBarDarkFont(true, 0.2f).init()
                if (null == mTransactionFragment) {
                    mTransactionFragment = TransactionFragment()
                    fragmentTransaction.add(R.id.nav_host_fragment, mTransactionFragment!!)
                    fragmentTransaction.show(mTransactionFragment!!)
                } else {
                    fragmentTransaction.show(mTransactionFragment!!)
                }
            }
            R.id.main_tip_button_quotation -> {
                checkEthEnable()
                ImmersionBar.with(this).statusBarDarkFont(true, 0.2f).init()
                if (null == mQuotesFragment) {
                    mQuotesFragment = QuotesFragment()
                    fragmentTransaction.add(R.id.nav_host_fragment, mQuotesFragment!!)
                    fragmentTransaction.show(mQuotesFragment!!)
                } else {
                    fragmentTransaction.show(mQuotesFragment!!)
                }
            }

            R.id.main_tip_financial_management -> {
                ImmersionBar.with(this).statusBarDarkFont(true, 0.2f).init()
                if (null == mDappFragment) {
                    mDappFragment = DappFragment()
                    fragmentTransaction.add(R.id.nav_host_fragment, mDappFragment!!)
                    fragmentTransaction.show(mDappFragment!!)
                } else {
                    fragmentTransaction.show(mDappFragment!!)
                }
            }
            R.id.main_tip_button_mine -> {
                ImmersionBar.with(this).statusBarDarkFont(true, 0.2f).init()
                if (null == mMineFragment) {
                    mMineFragment = MineFragment()
                    fragmentTransaction.add(R.id.nav_host_fragment, mMineFragment!!)
                    fragmentTransaction.show(mMineFragment!!)
                } else {
                    fragmentTransaction.show(mMineFragment!!)
                }
            }
            else -> {
            }
        }
        fragmentTransaction.commitAllowingStateLoss()
    }

    /**
     * 自定义隐藏未选中的Fragment的方法
     * 当Fragment不为空的时候,都将其隐藏,只有用户点击到了相应的页面的时候,才将其显示出来
     */
    private fun hideFragment(fragmentTransaction: FragmentTransaction) {
        if (mWallFragment != null) {
            fragmentTransaction.hide(mWallFragment!!)
        }
        if (mTransactionFragment != null) {
            fragmentTransaction.hide(mTransactionFragment!!)
        }
        if (mQuotesFragment != null) {
            fragmentTransaction.hide(mQuotesFragment!!)
        }
        if (mDappFragment != null) {
            fragmentTransaction.hide(mDappFragment!!)
        }
        if (mMineFragment != null) {
            fragmentTransaction.hide(mMineFragment!!)
        }
    }

    override fun onBackPressed() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - touchTime >= waitTime) {
            //让Toast的显示时间和等待时间相同
            Toast.makeText(this, R.string.press_again_to_exit, waitTime.toInt()).show()
            touchTime = currentTime
        } else {
            super.onBackPressed()
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



    @Subscribe(threadMode = ThreadMode.MAIN)
    fun openDrawLayout(openDrawLayoutEvent: OpenDrawLayoutEvent) {
        main_tip_button_wallet.isChecked = true
        onCheckedChanged(main_radio_group, R.id.main_tip_button_wallet)
        drawerLayout?.openDrawer(GravityCompat.START)
    }



    //校验是否eth可用
    private fun checkEthEnable() {
        //判断当前的钱包是否包含eth
        val coinRepository = CoinRepository(this@HomePageActivity)
        val coin:Coin? = coinRepository.getCoinByWalletIdAndName(WalletConfig.getInstance().walletId,CoinConst.ETH)
        if (null == coin) {
            //遍历所有的钱包是否有eth地址的钱包
            val walletRepository = WalletRepository(this@HomePageActivity)
            val wallets:List<Wallet?>? = walletRepository.getAll()

            val customTipDialog = CustomTipDialog()
            customTipDialog.setNeedCancel(false)
            customTipDialog.setConfirmText(R.string.ok_0)
            customTipDialog.setMargin(38)
            customTipDialog.setOutCancel(false)
            customTipDialog.setOnClickResultListener(object :CustomTipDialog.ResponseDataCallback{
                override fun onConfirm() {
                    EventBus.getDefault().post(OpenDrawLayoutEvent())
                }

                override fun onCancel() {

                }
            })
            if (CollectionUtils.isEmpty(wallets)) {
                customTipDialog.setContent(R.string.eth_empty_tip)
            }else{
                var isHaveEthAddress = false
                for (item in wallets!!) {
                    val otherWalletCoin:Coin? = coinRepository.getCoinByWalletIdAndName(item?.wallet_id,CoinConst.ETH)
                    if (null != otherWalletCoin) {
                        isHaveEthAddress = true
                    }
                }
                if (isHaveEthAddress) {
                    customTipDialog.setContent(R.string.eth_select_tip)
                }else{
                    customTipDialog.setContent(R.string.eth_empty_tip)
                }
            }
            customTipDialog.show(supportFragmentManager)
            return
        }

    }

}
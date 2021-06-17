package com.yongqi.wallet.ui.main.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ScreenUtils
import com.yongqi.wallet.BuildConfig
import com.yongqi.wallet.R
import com.yongqi.wallet.base.BaseFragment
import com.yongqi.wallet.bean.Coin
import com.yongqi.wallet.bean.SwitchWalletEvent
import com.yongqi.wallet.bean.Wallet
import com.yongqi.wallet.databinding.FragmentWalletManagementBinding
import com.yongqi.wallet.db.coin.CoinRepository
import com.yongqi.wallet.db.wallet.WalletRepository
import com.yongqi.wallet.ui.createWallet.ui.SetWalletPwdActivity
import com.yongqi.wallet.ui.importWallet.ui.ImportWalletActivity
import com.yongqi.wallet.ui.main.adapter.AECOAdapter
import com.yongqi.wallet.ui.main.adapter.WalletManagementAdapter
import com.yongqi.wallet.ui.manageWallet.ui.ManageWalletActivity
import kotlinx.android.synthetic.main.fragment_wallet_management.*
import org.greenrobot.eventbus.EventBus


/**
 * A simple [Fragment] subclass.
 * Use the [WalletManagementFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WalletManagementFragment :  BaseFragment<FragmentWalletManagementBinding>()  {


    override fun getLayoutResource(): Int = R.layout.fragment_wallet_management

    override fun initImmersionBar() {
//        immersionBar {
//            fitsSystemWindows(true)//只适合纯色状态栏;解决状态栏和布局重叠问题，使用该属性,必须指定状态栏颜色
//            statusBarColor(R.color.transparent)
//            statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
//            titleBar(llHead) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
//        }
    }

    var mContext: HomePageActivity? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = activity as HomePageActivity?
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root: View? = inflater.inflate(R.layout.fragment_wallet_management, container, false)
        val screenWidth = ScreenUtils.getScreenWidth()
        val screenHeight = ScreenUtils.getScreenHeight()
        root?.layoutParams = LinearLayout.LayoutParams(
            (0.827 * screenWidth).toInt(),
            screenHeight
        )
        return root ?:   /* root null  就用后面的 */
        super.onCreateView(
            inflater,
            container,
            savedInstanceState
        )
    }



    val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.tvClose -> {//TODO
                mContext?.drawerLayout?.closeDrawers()
            }

            R.id.tvCreateWallet -> {//创建钱包
                startActivity(Intent(mContext, SetWalletPwdActivity::class.java))
            }
            R.id.tvImportWallet -> {//导入钱包
                startActivity(Intent(mContext, ImportWalletActivity::class.java))
            }
        }
    }

    override fun initView() {
        tvClose.setOnClickListener(onClickListener)
        tvCreateWallet.setOnClickListener(onClickListener)
        tvImportWallet.setOnClickListener(onClickListener)
       if ( BuildConfig.FLAVOR == "onlineuvtest"|| BuildConfig.FLAVOR == "devuvtest") {
           llManageAccount.visibility =View.VISIBLE
           rvAECO.visibility =View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        var walletRepository = mContext?.let { WalletRepository(it) }
        var wallets : MutableList<Wallet>?= walletRepository?.getAll() as MutableList<Wallet>?
        val adapter = WalletManagementAdapter(R.layout.rv_wallet_item, wallets )
        val layoutManager = LinearLayoutManager(mContext)
        rvWallet.layoutManager = layoutManager
        rvWallet.adapter = adapter
        /**
         * Item 点击事件
         */
        adapter.setOnItemClickListener { adapter, view, position ->
            mContext?.drawerLayout?.closeDrawers()
            var walletBean = adapter.data[position] as Wallet
            SPUtils.getInstance().put("walletId", walletBean.wallet_id)
            adapter.notifyDataSetChanged()
            EventBus.getDefault().post(SwitchWalletEvent())//切换钱包,刷新数据

        }
        // 先注册需要点击的子控件id（注意，请不要写在convert方法里）

        /**
         * Item 内子View的点击事件：
         *  先注册需要点击的子控件id（注意，请不要写在convert方法里）
         */
        adapter.addChildClickViewIds(R.id.tvManager, R.id.tvCreateWallet, R.id.tvImportWallet)
        adapter.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.tvManager -> {
                    //Todo drawerLayout.closeDrawers()
                    mContext?.drawerLayout?.closeDrawers()
                    var wallet = adapter.data[position] as Wallet
                    startActivity(
                        Intent(mContext, ManageWalletActivity::class.java)
                            .putExtra("walletId",wallet.wallet_id)
                            .putExtra("name",wallet.name)
                            .putExtra("type",wallet.type)
                    )
                }
            }
        }

        var coinRepository = mContext?.let { CoinRepository(it) }
        var coins = coinRepository?.getCoinsByName("AECO") as MutableList<Coin>?
        if (coins.isNullOrEmpty()||coins.size<1){
            llManageAccount.visibility = View.GONE
            rvAECO.visibility = View.GONE
        }else{
            llManageAccount.visibility = View.VISIBLE
            rvAECO.visibility = View.VISIBLE
        }
        val aecoAdapter = AECOAdapter(R.layout.rv_aeco_item, coins )
        val aecolayoutManager = LinearLayoutManager(mContext)
        rvAECO.layoutManager = aecolayoutManager
        rvAECO.adapter = aecoAdapter
        aecoAdapter.setOnItemClickListener { adapter, view, position ->
            mContext?.drawerLayout?.closeDrawers()
            var coin = aecoAdapter.data[position] as Coin
            var wallet = coin.wallet_id?.let { walletRepository?.getWalletById(it) }
            startActivity(
                Intent(mContext, ManageWalletActivity::class.java)
                    .putExtra("walletId",wallet?.wallet_id)
                    .putExtra("name",wallet?.name)
                    .putExtra("type","AECO")
                    .putExtra("address",coin.address)
            )
        }


    }

    override fun loadData() {

    }




}
package com.yongqi.wallet.ui.manageFinances.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.blankj.utilcode.util.CollectionUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.shehuan.nicedialog.BaseNiceDialog
import com.shehuan.nicedialog.NiceDialog
import com.shehuan.nicedialog.ViewConvertListener
import com.shehuan.nicedialog.ViewHolder
import com.yongqi.wallet.BuildConfig
import com.yongqi.wallet.R
import com.yongqi.wallet.api.NetApi
import com.yongqi.wallet.base.BaseFragment
import com.yongqi.wallet.bean.Coin
import com.yongqi.wallet.bean.OpenDrawLayoutEvent
import com.yongqi.wallet.bean.Wallet
import com.yongqi.wallet.config.CoinConst
import com.yongqi.wallet.config.WalletConfig
import com.yongqi.wallet.databinding.FragmentDappBinding
import com.yongqi.wallet.db.coin.CoinRepository
import com.yongqi.wallet.db.wallet.WalletRepository
import com.yongqi.wallet.net.APIClient
import com.yongqi.wallet.net.APIResponse
import com.yongqi.wallet.ui.createWallet.dialog.CustomTipDialog
import com.yongqi.wallet.ui.manageFinances.DAppScanActivity
import com.yongqi.wallet.ui.manageFinances.adapter.DAppBannerAdapter
import com.yongqi.wallet.ui.manageFinances.adapter.DAppHotAdapter
import com.yongqi.wallet.ui.manageFinances.bean.DAppAssetJsonBean
import com.yongqi.wallet.ui.manageFinances.bean.DAppBean
import com.yongqi.wallet.ui.manageFinances.bean.DAppDataBean
import com.yongqi.wallet.ui.quotes.adapter.CustomPageAdapter
import com.yongqi.wallet.utils.ViewPageIndicatorUtil
import com.youth.banner.indicator.CircleIndicator
import com.youth.banner.listener.OnBannerListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_dapp.*
import org.greenrobot.eventbus.EventBus
import java.io.InputStream
import java.util.*


class DappFragment : BaseFragment<FragmentDappBinding>() {
    private val mFragments: MutableList<Fragment> = mutableListOf()
    private var mBenefitAdapter: CustomPageAdapter? = null
    private var titles = listOf(R.string.dapp_swap,R.string.dapp_layer2,R.string.dapp_financing,R.string.dapp_nft,R.string.dapp_game,R.string.dapp_other)
    override fun initView() {
        immersionBar {
            statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
            titleBar(topView) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
        }
        layout_search.setOnClickListener {
            startActivity(Intent(context,DAppSearchActivity::class.java))
        }
        iv_scan.setOnClickListener {
            //初始化权限
            context?.let { requestCameraPermissions(it) }
        }
        getAssetJson()
        getDAppData()
    }

    //获取本地json文件
    private fun getAssetJson() {
        try {
            var s = ""
            try {
                val inputSteam:InputStream? = context?.assets?.open("dapp.json")
                val scanner: Scanner = Scanner(inputSteam, "UTF-8").useDelimiter("\\A")
                if (scanner.hasNext()) {
                    s = scanner.next()
                }
                inputSteam?.close()
                val json:DAppAssetJsonBean = GsonUtils.fromJson(s,DAppAssetJsonBean::class.java)
                val dataBean:DAppBean = json.data
                initData(dataBean)
            }
            finally {

            }
        }catch (e:RuntimeException) {
            throw RuntimeException(e)
        }
    }

    override fun loadData() {

    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_dapp
    }

    override fun initImmersionBar() {

    }

    private fun getDAppData() {
        context?.let {
            APIClient.instance.instanceRetrofit(NetApi::class.java)
                .getDAppData(BuildConfig.DAPP_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : APIResponse<DAppBean>(it) {
                    override fun success(data: DAppBean?) {
                        initData(data)
                    }

                    override fun failure(errorMsg: String?) {
                    }

                })
        }
    }

    fun initData(data:DAppBean?) {
        if (CollectionUtils.isEmpty(data?.banner)) {
            banner.visibility = View.GONE
        } else {
            banner.visibility = View.VISIBLE
            banner.adapter = (DAppBannerAdapter(context,data?.banner))
            banner.indicator = CircleIndicator(context)
            banner.setOnBannerListener(object :OnBannerListener<DAppDataBean> {
                override fun OnBannerClick(data: DAppDataBean, position: Int) {
                    showTipsDialog(data.url,
                        if (SPUtils.getInstance().getString("language") == "English") {
                            data.name_en
                        } else {
                            data.name
                        })
                }
            })
        }
        rv_hot.layoutManager = GridLayoutManager(context,5)
        val hotAdapter = DAppHotAdapter(R.layout.item_dapp_hot,data?.hot)
        rv_hot.adapter = hotAdapter
        hotAdapter.setOnItemClickListener { _, _, position ->
            showTipsDialog(
                hotAdapter.data[position].url,
                if (SPUtils.getInstance().getString("language") == "English") {
                    hotAdapter.data[position].name_en
                } else {
                    hotAdapter.data[position].name
                }
            )
        }

        mFragments.clear()
        mFragments.add(DAppListFragment.newInstance(GsonUtils.toJson(data?.swap)))
        mFragments.add(DAppListFragment.newInstance(GsonUtils.toJson(data?.layer2)))
        mFragments.add(DAppListFragment.newInstance(GsonUtils.toJson(data?.licai)))
        mFragments.add(DAppListFragment.newInstance(GsonUtils.toJson(data?.nft)))
        mFragments.add(DAppListFragment.newInstance(GsonUtils.toJson(data?.game)))
        mFragments.add(DAppListFragment.newInstance(GsonUtils.toJson(data?.other)))
        mBenefitAdapter = CustomPageAdapter(childFragmentManager, mFragments, titles)
        view_pager.adapter = mBenefitAdapter
        view_pager.offscreenPageLimit = (mFragments as ArrayList<Fragment>).size - 1
        ViewPageIndicatorUtil.initDAppListIndicator(context, magic_indicator, view_pager, titles)
    }

    /**
     * 弹窗提示
     */
    private fun showTipsDialog(url:String,title:String) {
        val isEthEnable = checkEthEnable()
        if (!isEthEnable) {
            return
        }
        NiceDialog.init()
            .setLayoutId(R.layout.financial_tips_dialog)     //设置dialog布局文件
//                    .setTheme(R.style.MyDialog) // 设置dialog主题，默认主题继承自Theme.AppCompat.Light.Dialog
            .setConvertListener(object : ViewConvertListener() {
                override fun convertView(holder: ViewHolder?, dialog: BaseNiceDialog?) {
                    val tvAgree = holder?.getView<TextView>(R.id.tvAgree)
                    val tvCancel = holder?.getView<TextView>(R.id.tvCancel)
                    tvAgree?.setOnClickListener {
                        startActivity(Intent(activity,FinancialManagementWebViewActivity::class.java).putExtra("url",url).putExtra("title",title))
                        dialog?.dismiss()

                    }
                    tvCancel?.setOnClickListener {
                        dialog?.dismiss()
                    }
                }
            })
            .setDimAmount(0.3f)     //调节灰色背景透明度[0-1]，默认0.5f
//                    .setGravity(Gravity.CENTER)     //可选，设置dialog的位置，默认居中，可通过系统Gravity的类的常量修改，例如Gravity.BOTTOM（底部），Gravity.Right（右边），Gravity.BOTTOM|Gravity.Right（右下）
            .setMargin(38)     //dialog左右两边到屏幕边缘的距离（单位：dp），默认0dp
//                    .setWidth(270)     //dialog宽度（单位：dp），默认为屏幕宽度，-1代表WRAP_CONTENT
//            .setHeight(159)     //dialog高度（单位：dp），默认为WRAP_CONTENT
            .setOutCancel(false)     //点击dialog外是否可取消，默认true
            //.setAnimStyle(R.style.EnterExitAnimation)     //设置dialog进入、退出的自定义动画；根据设置的Gravity，默认提供了左、上、右、下位置进入退出的动画
            .show(fragmentManager)     //显示dialog

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    /**
     * 请求相机权限
     */
    fun requestCameraPermissions(context: Context) {
        if (com.blankj.utilcode.util.PermissionUtils.isGranted(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {//判断权限是否被授予
            activity?.startActivityForResult(Intent(context, DAppScanActivity::class.java),10)
        } else {
            com.blankj.utilcode.util.PermissionUtils
                .permission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA)//设置请求权限
//                .permissionGroup(PermissionConstants.CALENDAR)//设置请求权限组
//                .explain { activity, denied, shouldRequest ->//设置权限请求前的解释
//                }
                .callback { isAllGranted, granted, deniedForever, denied ->
                    LogUtils.d(granted, deniedForever, denied)

                    if (isAllGranted) {
                        activity?.startActivityForResult(Intent(context, DAppScanActivity::class.java),10)
                        return@callback
                    }
                    if (deniedForever.isNotEmpty()) {
                        val dialog = context.let { MaterialDialog(it) }
                        dialog.title(R.string.title_permission)
                        dialog.message(R.string.permission_rationale_camera_message)
                        dialog.cancelOnTouchOutside(false)
                        dialog.negativeButton(R.string.cancel_0) {
                            dialog.dismiss()
                        }
                        dialog.positiveButton(R.string.ok_0) {
                            com.blankj.utilcode.util.PermissionUtils.launchAppDetailsSettings()//打开应用具体设置
                        }
//                        dialog?.lifecycleOwner(this@WalletFragment)
                        dialog.show()
                    }
                }
                .request()//开始请求
        }
    }

    //校验是否eth可用
    private fun checkEthEnable():Boolean {
        //判断当前的钱包是否包含eth
        val coinRepository = context?.let { CoinRepository(it) }
        val coin: Coin? = coinRepository?.getCoinByWalletIdAndName(
            WalletConfig.getInstance().walletId,
            CoinConst.ETH)
        if (null == coin) {
            //遍历所有的钱包是否有eth地址的钱包
            val walletRepository = context?.let { WalletRepository(it) }
            val wallets:List<Wallet?>? = walletRepository?.getAll()

            val customTipDialog = CustomTipDialog()
            customTipDialog.setNeedCancel(false)
            customTipDialog.setConfirmText(R.string.ok_0)
            customTipDialog.setMargin(38)
            customTipDialog.setOutCancel(false)
            customTipDialog.setOnClickResultListener(object : CustomTipDialog.ResponseDataCallback{
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
                    val otherWalletCoin: Coin? = coinRepository?.getCoinByWalletIdAndName(item?.wallet_id,
                        CoinConst.ETH)
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
            customTipDialog.show(childFragmentManager)
            return false
        }
        return true
    }
}
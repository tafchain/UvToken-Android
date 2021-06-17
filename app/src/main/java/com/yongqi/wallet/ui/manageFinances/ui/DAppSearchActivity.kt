package com.yongqi.wallet.ui.manageFinances.ui

import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.CollectionUtils
import com.blankj.utilcode.util.SPUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.gyf.immersionbar.ktx.immersionBar
import com.shehuan.nicedialog.BaseNiceDialog
import com.shehuan.nicedialog.NiceDialog
import com.shehuan.nicedialog.ViewConvertListener
import com.shehuan.nicedialog.ViewHolder
import com.yongqi.wallet.App.Companion.context
import com.yongqi.wallet.BuildConfig
import com.yongqi.wallet.R
import com.yongqi.wallet.api.NetApi
import com.yongqi.wallet.base.BaseActivity
import com.yongqi.wallet.bean.Coin
import com.yongqi.wallet.bean.OpenDrawLayoutEvent
import com.yongqi.wallet.bean.ResponseWrapper
import com.yongqi.wallet.bean.Wallet
import com.yongqi.wallet.config.CoinConst
import com.yongqi.wallet.config.WalletConfig
import com.yongqi.wallet.databinding.ActivityDappSearchBinding
import com.yongqi.wallet.db.coin.CoinRepository
import com.yongqi.wallet.db.wallet.WalletRepository
import com.yongqi.wallet.net.APIClient
import com.yongqi.wallet.net.APIResponse
import com.yongqi.wallet.ui.createWallet.dialog.CustomTipDialog
import com.yongqi.wallet.ui.manageFinances.adapter.DAppSearchAdapter
import com.yongqi.wallet.ui.manageFinances.bean.DAppBean
import com.yongqi.wallet.ui.manageFinances.bean.DAppDataBean
import com.yongqi.wallet.ui.manageFinances.request.DAppSearchRequest
import com.yongqi.wallet.ui.manageFinances.viewmodel.DAppSearchViewModel
import com.yongqi.wallet.utils.EditTextUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_dapp_search.*
import kotlinx.android.synthetic.main.activity_scan_qr.*
import org.greenrobot.eventbus.EventBus

class DAppSearchActivity : BaseActivity<ActivityDappSearchBinding,DAppSearchViewModel>() {

    override fun getLayoutResource(): Int {
        return R.layout.activity_dapp_search
    }

    override fun initData() {
        immersionBar {
            statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
            titleBar(topView) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
        }
        edit_search.setOnEditorActionListener(object :TextView.OnEditorActionListener{
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                when(actionId) {
                    EditorInfo.IME_ACTION_SEARCH ->{
                        searchDApp(edit_search.text.toString())
                        EditTextUtils.hideKeyboard(this@DAppSearchActivity)
                        return true
                    }
                }
                return true
            }

        })
        tv_cancel.setOnClickListener{
            finish()
        }
    }

    private fun searchDApp(dAppName:String) {
        APIClient.instance.instanceRetrofit(NetApi::class.java)
            .searchDApp(BuildConfig.DAPP_URL,DAppSearchRequest(dAppName))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object :APIResponse<Map<String,List<DAppDataBean>>>(context = this) {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun success(data: Map<String,List<DAppDataBean>>?) {
                    val list:MutableList<DAppDataBean> = mutableListOf()
                    val map = mutableMapOf<String,DAppDataBean>()
                    data?.forEach { (t, u) ->
                        for (item in u) {
                            //map[item.name] = item
                            if (SPUtils.getInstance().getString("language") == "English") {
                                map[item.name_en] = item
                            } else {
                                map[item.name] = item
                            }
                        }
                    }
                    map.forEach{(t,u) ->
                        list.add(u)
                    }
                    if (list.size == 0) {
                        layout_empty.visibility = View.VISIBLE
                        rv_dapp_list.visibility =  View.GONE
                    }else{
                        layout_empty.visibility = View.GONE
                        rv_dapp_list.visibility =  View.VISIBLE
                        rv_dapp_list.layoutManager = LinearLayoutManager(context)
                        val adapter = DAppSearchAdapter(R.layout.item_dapp,list)
                        rv_dapp_list.adapter = adapter
                        adapter.setOnItemClickListener(object :OnItemClickListener{
                            override fun onItemClick(
                                adapter: BaseQuickAdapter<*, *>,
                                view: View,
                                position: Int
                            ) {
                                showTipsDialog(list[position].url,
                                    if (SPUtils.getInstance().getString("language") == "English") {
                                        list[position].name_en
                                    } else {
                                        list[position].name
                                    })
                            }

                        })
                    }

                }

                override fun failure(errorMsg: String?) {

                }


            })


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
                        startActivity(Intent(this@DAppSearchActivity,FinancialManagementWebViewActivity::class.java).putExtra("url",url).putExtra("title",title))
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
            .show(supportFragmentManager)     //显示dialog

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
                    finish()
                    SPUtils.getInstance().put("isNeedOpen",true)
//                    EventBus.getDefault().post(OpenDrawLayoutEvent())
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
            customTipDialog.show(supportFragmentManager)
            return false
        }
        return true
    }
}
package com.yongqi.wallet.ui.manageFinances.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.CollectionUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.StringUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.google.gson.Gson
import com.shehuan.nicedialog.BaseNiceDialog
import com.shehuan.nicedialog.NiceDialog
import com.shehuan.nicedialog.ViewConvertListener
import com.shehuan.nicedialog.ViewHolder
import com.yongqi.wallet.R
import com.yongqi.wallet.base.BaseFragment
import com.yongqi.wallet.bean.Coin
import com.yongqi.wallet.bean.OpenDrawLayoutEvent
import com.yongqi.wallet.bean.Wallet
import com.yongqi.wallet.config.CoinConst
import com.yongqi.wallet.config.WalletConfig
import com.yongqi.wallet.databinding.FragmentDAppListBinding
import com.yongqi.wallet.db.coin.CoinRepository
import com.yongqi.wallet.db.wallet.WalletRepository
import com.yongqi.wallet.ui.createWallet.dialog.CustomTipDialog
import com.yongqi.wallet.ui.manageFinances.adapter.DAppListAdapter
import com.yongqi.wallet.ui.manageFinances.bean.DAppDataBean
import kotlinx.android.synthetic.main.fragment_d_app_list.*
import org.greenrobot.eventbus.EventBus
import java.util.*


class DAppListFragment : BaseFragment<FragmentDAppListBinding>() {

    private var dataStr: String? = null

    override fun initImmersionBar() {

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            DAppListFragment().apply {
                arguments = Bundle().apply {
                    putString(DATA_STR, param1)
                }
            }

        private const val DATA_STR: String = ""
    }

    override fun initView() {
        arguments?.let {
            dataStr = it.getString(DATA_STR)
            if (StringUtils.isEmpty(dataStr)) {
                return
            }
            dataStr?.let { it1 ->
                val list = getListFromJson(it1, Array<DAppDataBean>::class.java)
                rv_dapp_list.layoutManager = LinearLayoutManager(context)
                val dAppAdapter = DAppListAdapter(R.layout.item_dapp, list)
                rv_dapp_list.adapter = dAppAdapter
                dAppAdapter.setOnItemClickListener(object : OnItemClickListener {
                    override fun onItemClick(
                        adapter: BaseQuickAdapter<*, *>,
                        view: View,
                        position: Int
                    ) {
                        showTipsDialog(
                            dAppAdapter.data[position].url,
                            if (SPUtils.getInstance().getString("language") == "English") {
                                dAppAdapter.data[position].name_en
                            } else {
                                dAppAdapter.data[position].name
                            }
                        )
                    }

                })
            }
        }
    }

    override fun loadData() {
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_d_app_list
    }

    private fun <T> getListFromJson(json: String?, clazz: Class<Array<T>>): List<T>? {
        try {
            val arr: Array<T> = Gson().fromJson(json, clazz)
            return listOf(*arr)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * ????????????
     */
    private fun showTipsDialog(url: String, title: String) {
        val isEthEnable = checkEthEnable()
        if (!isEthEnable) {
            return
        }
        NiceDialog.init()
            .setLayoutId(R.layout.financial_tips_dialog)     //??????dialog????????????
//                    .setTheme(R.style.MyDialog) // ??????dialog??????????????????????????????Theme.AppCompat.Light.Dialog
            .setConvertListener(object : ViewConvertListener() {
                override fun convertView(holder: ViewHolder?, dialog: BaseNiceDialog?) {
                    val tvAgree = holder?.getView<TextView>(R.id.tvAgree)
                    val tvCancel = holder?.getView<TextView>(R.id.tvCancel)
                    tvAgree?.setOnClickListener {
                        startActivity(
                            Intent(
                                activity,
                                FinancialManagementWebViewActivity::class.java
                            ).putExtra("url", url).putExtra("title", title)
                        )
                        dialog?.dismiss()

                    }
                    tvCancel?.setOnClickListener {
                        dialog?.dismiss()
                    }
                }
            })
            .setDimAmount(0.3f)     //???????????????????????????[0-1]?????????0.5f
//                    .setGravity(Gravity.CENTER)     //???????????????dialog??????????????????????????????????????????Gravity??????????????????????????????Gravity.BOTTOM???????????????Gravity.Right???????????????Gravity.BOTTOM|Gravity.Right????????????
            .setMargin(38)     //dialog????????????????????????????????????????????????dp????????????0dp
//                    .setWidth(270)     //dialog??????????????????dp??????????????????????????????-1??????WRAP_CONTENT
//            .setHeight(159)     //dialog??????????????????dp???????????????WRAP_CONTENT
            .setOutCancel(false)     //??????dialog???????????????????????????true
            //.setAnimStyle(R.style.EnterExitAnimation)     //??????dialog???????????????????????????????????????????????????Gravity??????????????????????????????????????????????????????????????????
            .show(fragmentManager)     //??????dialog
    }

    //????????????eth??????
    private fun checkEthEnable():Boolean {
        //?????????????????????????????????eth
        val coinRepository = context?.let { CoinRepository(it) }
        val coin: Coin? = coinRepository?.getCoinByWalletIdAndName(
            WalletConfig.getInstance().walletId,
            CoinConst.ETH)
        if (null == coin) {
            //??????????????????????????????eth???????????????
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
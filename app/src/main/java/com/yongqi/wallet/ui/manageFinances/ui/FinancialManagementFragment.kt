package com.yongqi.wallet.ui.manageFinances.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.blankj.utilcode.util.ToastUtils
import com.shehuan.nicedialog.BaseNiceDialog
import com.shehuan.nicedialog.NiceDialog
import com.shehuan.nicedialog.ViewConvertListener
import com.shehuan.nicedialog.ViewHolder
import com.yongqi.wallet.R
import com.yongqi.wallet.base.BaseFragment
import com.yongqi.wallet.config.AppConst
import com.yongqi.wallet.config.AppConst.AAVE
import com.yongqi.wallet.config.AppConst.COMPOUND
import com.yongqi.wallet.config.AppConst.CURVE
import com.yongqi.wallet.config.AppConst.DFORCE
import com.yongqi.wallet.config.AppConst.DYDX
import com.yongqi.wallet.config.AppConst.ONE_INCH
import com.yongqi.wallet.config.AppConst.SUSHI
import com.yongqi.wallet.config.AppConst.UNISWAP
import com.yongqi.wallet.databinding.FragmentFinancialManagementBinding
import com.yongqi.wallet.utils.DialogUtils
import kotlinx.android.synthetic.main.common_title.*
import kotlinx.android.synthetic.main.fragment_financial_management.*


/**
 * A simple [Fragment] subclass.
 * Use the [FinancialManagementFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FinancialManagementFragment : BaseFragment<FragmentFinancialManagementBinding>() {//, EasyPermissions.PermissionCallbacks


    override fun getLayoutResource(): Int = R.layout.fragment_financial_management


    override fun initView() {
        ivBack.visibility = View.GONE
        tvTitle.text = getString(R.string.decentralized_financial_management)
        rlAAVE.setOnClickListener(onClickListener)
        rlCompound.setOnClickListener(onClickListener)
        rlCurve.setOnClickListener(onClickListener)
        rlDeforce.setOnClickListener(onClickListener)
        rl_1inch.setOnClickListener(onClickListener)
        rl_uniswap.setOnClickListener(onClickListener)
        rl_sushiSwap.setOnClickListener(onClickListener)
        rl_dydx.setOnClickListener(onClickListener)
    }

    override fun loadData() {
    }

    val onClickListener = View.OnClickListener {
        when(it.id){
            R.id.rlAAVE->{
                showTipsDialog(AAVE,"AAVE")
            }
            R.id.rlCompound->{
                showTipsDialog(COMPOUND,"Compound")
            }
            R.id.rlCurve->{
                showTipsDialog(CURVE,"Curve")
            }
            R.id.rlDeforce->{
                showTipsDialog(DFORCE,"dForce")
            }
            R.id.rl_1inch->{
                showTipsDialog(ONE_INCH,"1inch")
            }
            R.id.rl_uniswap->{
                showTipsDialog(UNISWAP,"uniswap")
            }
            R.id.rl_sushiSwap->{
                showTipsDialog(SUSHI,"SushiSwap")
            }
            R.id.rl_dydx->{
                showTipsDialog(DYDX,"dYdX")
            }
        }
    }

    override fun initImmersionBar() {
    }

    /**
     * 弹窗提示
     */
    private fun showTipsDialog(url:String,title:String) {
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

}
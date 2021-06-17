package com.yongqi.wallet.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.InputType
import android.text.Selection
import android.view.Gravity
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import api.VerifyWalletPasswordRequest
import api.VerifyWalletPasswordResponse
import com.blankj.utilcode.util.*
import com.shehuan.nicedialog.BaseNiceDialog
import com.shehuan.nicedialog.NiceDialog
import com.shehuan.nicedialog.ViewConvertListener
import com.shehuan.nicedialog.ViewHolder
import com.yongqi.wallet.App
import com.yongqi.wallet.R
import com.yongqi.wallet.bean.Wallet
import com.yongqi.wallet.db.wallet.WalletRepository
import com.yongqi.wallet.ui.createWallet.ui.BackupMnemonicActivity
import com.yongqi.wallet.ui.manageFinances.ui.FinancialManagementWebViewActivity
import com.yongqi.wallet.view.LoadingDialog
import uv1.Uv1

/**
 * author ：SunXiao
 * date : 2021/1/29 17:56
 * package：com.yongqi.wallet.utils
 * description :
 */
object DialogUtils {
    interface DialogResponse{
        fun onSuccess(pwd:String)
        fun onError()
    }

    fun showCustomTopTip(context: Context?, manager: FragmentManager,content:String) {
        NiceDialog.init()
            .setLayoutId(R.layout.layout_custom_tip)     //设置dialog布局文件
            .setConvertListener(object : ViewConvertListener() {
                override fun convertView(holder: ViewHolder?, dialog: BaseNiceDialog?) {
                    val tvAgree = holder?.getView<TextView>(R.id.tvAgree)
                    val tvCancel = holder?.getView<TextView>(R.id.tvCancel)
                    tvAgree?.setOnClickListener {
                        dialog?.dismiss()
                    }
                    tvCancel?.setOnClickListener {
                        dialog?.dismiss()
                    }
                }
            })
            .setDimAmount(0f)     //调节灰色背景透明度[0-1]，默认0.5f
            .setMargin(38)     //dialog左右两边到屏幕边缘的距离（单位：dp），默认0dp
            .setOutCancel(false)     //点击dialog外是否可取消，默认true
            .setGravity(Gravity.TOP)
            .setWidth(10000)
            .setAnimStyle(R.style.TopAnimation)
            .show(manager)     //显示dialog
    }

    fun showCustomDialog(context: Context?, manager: FragmentManager,dialogResponse: DialogResponse) {
        NiceDialog.init()
            .setLayoutId(R.layout.financial_tips_dialog)     //设置dialog布局文件
            .setConvertListener(object : ViewConvertListener() {
                override fun convertView(holder: ViewHolder?, dialog: BaseNiceDialog?) {
                    val tvAgree = holder?.getView<TextView>(R.id.tvAgree)
                    val tvCancel = holder?.getView<TextView>(R.id.tvCancel)
                    tvAgree?.setOnClickListener {
                        dialogResponse.onSuccess("")
                        dialog?.dismiss()

                    }
                    tvCancel?.setOnClickListener {
                        dialog?.dismiss()
                    }
                }
            })
            .setDimAmount(0.3f)     //调节灰色背景透明度[0-1]，默认0.5f
            .setMargin(38)     //dialog左右两边到屏幕边缘的距离（单位：dp），默认0dp
            .setOutCancel(false)     //点击dialog外是否可取消，默认true
            .show(manager)     //显示dialog
    }

    fun showCustomTipDialog(manager: FragmentManager,string: String,dialogResponse: DialogResponse) {
        NiceDialog.init()
            .setLayoutId(R.layout.layout_tip)     //设置dialog布局文件
            .setConvertListener(object : ViewConvertListener() {
                override fun convertView(holder: ViewHolder?, dialog: BaseNiceDialog?) {
                    val tvAgree = holder?.getView<TextView>(R.id.tvAgree)
                    val tvCancel = holder?.getView<TextView>(R.id.tvCancel)
                    tvAgree?.setOnClickListener {
                        dialogResponse.onSuccess("")
                        dialog?.dismiss()

                    }
                    tvCancel?.setOnClickListener {
                        dialog?.dismiss()
                    }
                }
            })
            .setDimAmount(0.3f)     //调节灰色背景透明度[0-1]，默认0.5f
            .setMargin(38)     //dialog左右两边到屏幕边缘的距离（单位：dp），默认0dp
            .setOutCancel(false)     //点击dialog外是否可取消，默认true
            .show(manager)     //显示dialog
    }




    /**
     * 无网络弹窗
     */
    fun showNoNetworkDialog(context: Context?, manager: FragmentManager) {
        NiceDialog.init()
            .setLayoutId(R.layout.offline_dialog)     //设置dialog布局文件
//                    .setTheme(R.style.MyDialog) // 设置dialog主题，默认主题继承自Theme.AppCompat.Light.Dialog
            .setConvertListener(object : ViewConvertListener() {
                override fun convertView(holder: ViewHolder?, dialog: BaseNiceDialog?) {
                    val tvGotIt = holder?.getView<TextView>(R.id.tvGotIt)
                    tvGotIt?.setOnClickListener {
                        dialog?.dismiss()
                        App.showNetworkDialog = true
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
            .show(manager)     //显示dialog
    }

    /**
     * 校验密码
     */
    fun showCommonVerifyPasswordDialog(context: Context?, manager: FragmentManager,walletId:String,block: (pwd:String) -> Unit = {}, error: () -> Unit = {}) {
        NiceDialog.init()
            .setLayoutId(R.layout.input_pwd_dialog)     //设置dialog布局文件
            .setConvertListener(object : ViewConvertListener() {
                override fun convertView(holder: ViewHolder?, dialog: BaseNiceDialog?) {
                    val ivClose = holder?.getView<ImageView>(R.id.ivClose)
                    val pwd = holder?.getView<EditText>(R.id.etPwd)
                    val ok = holder?.getView<TextView>(R.id.tvOk)
                    ivClose?.setOnClickListener {
                        dialog?.dismiss()
                    }
                    val cbEye = holder?.getView<CheckBox>(R.id.cbEye)
                    cbEye?.setOnClickListener {
                        if (cbEye.isChecked) {
                            pwd?.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        } else {
                            pwd?.inputType =
                                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        }
                        //键盘光标移到EditText末尾
                        pwd?.text?.length?.let { it1 -> Selection.setSelection(pwd.text, it1) }
                    }

                    pwd?.addTextChangedListener {
                        val password = pwd?.text?.trim().toString()
                        cbEye?.isEnabled = password.isNotEmpty()
                        if (password.isNotEmpty()) {
                            ok?.isEnabled = StringUtils.checkString(password) && password.length > 7
                        } else {
                            ok?.isEnabled = false
                        }
                    }
                    ok?.setOnClickListener {
                        if (KeyboardUtils.isSoftInputVisible(context as Activity)) {//隐藏系统软键盘
                            pwd?.let { it1 -> KeyboardUtils.hideSoftInput(it1) }
                        }
                        LoadingDialog.show(context)
                        val pwd = pwd?.text?.trim().toString()
                        //校验密码
                        val verifyWalletPasswordRequest = VerifyWalletPasswordRequest()
                        verifyWalletPasswordRequest.password = pwd
                        verifyWalletPasswordRequest.walletId =walletId
                        verifyWalletPasswordRequest.keystoreDir = DirUtils.createKeyStoreDir()
                        try {
                            val verifyWalletPassword: VerifyWalletPasswordResponse = Uv1.verifyWalletPassword(verifyWalletPasswordRequest)
                            if (!verifyWalletPassword.valid) {
                                LoadingDialog.cancel()
                                ToastUtils.make().show(R.string.wrong_password)
                                error()
                                return@setOnClickListener
                            }
                        } catch (e: Exception) {
                            LoadingDialog.cancel()
                            ToastUtils.make().show(R.string.wrong_password)
                            error()
                            return@setOnClickListener
                        }
                        LoadingDialog.cancel()
                        block(pwd)
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
            .show(manager)     //显示dialog
    }

    //校验密码弹窗
    fun showCheckPwdDialog(context: Context?, manager: FragmentManager,dialogResponse: DialogResponse) {
        NiceDialog.init()
            .setLayoutId(R.layout.input_pwd_dialog)     //设置dialog布局文件
            .setConvertListener(object : ViewConvertListener() {
                override fun convertView(holder: ViewHolder?, dialog: BaseNiceDialog?) {
                    val tvBackup = holder?.getView<TextView>(R.id.tvBackup)
                    val ivClose = holder?.getView<ImageView>(R.id.ivClose)
                    val pwd = holder?.getView<EditText>(R.id.etPwd)
                    val ok = holder?.getView<TextView>(R.id.tvOk)
                    ivClose?.setOnClickListener {
                        dialog?.dismiss()
                    }
                    val cbEye = holder?.getView<CheckBox>(R.id.cbEye)
                    cbEye?.setOnClickListener {
                        if (cbEye.isChecked) {
                            pwd?.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        } else {
                            pwd?.inputType =
                                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        }
                        //键盘光标移到EditText末尾
                        pwd?.text?.length?.let { it1 -> Selection.setSelection(pwd.text, it1) }
                    }
                    pwd?.addTextChangedListener {
                        val password = pwd.text.trim().toString()
                        cbEye?.isEnabled = password.isNotEmpty()
                        if (password.isNotEmpty()) {
                            ok?.isEnabled = StringUtils.checkString(password) && password.length > 7
                        } else {
                            ok?.isEnabled = false
                        }
                    }
                    ok?.setOnClickListener {
                        if (KeyboardUtils.isSoftInputVisible(context as Activity)) {//隐藏系统软键盘
                            pwd?.let { it1 -> KeyboardUtils.hideSoftInput(it1) }
                        }

                        val pwd = pwd?.text?.trim().toString()
                        val walletId = SPUtils.getInstance().getString("walletId")
                        val walletRepository = WalletRepository(context)
                        val walletById: Wallet? =
                            walletId?.let { it1 -> walletRepository.getWalletById(it1) }
                        val password = walletById?.password?.trim()

                        //校验密码
                        val verifyWalletPasswordRequest = VerifyWalletPasswordRequest()
                        verifyWalletPasswordRequest.password = pwd
                        verifyWalletPasswordRequest.walletId =
                            SPUtils.getInstance().getString("walletId")
                        verifyWalletPasswordRequest.keystoreDir = DirUtils.createKeyStoreDir()
                        LoadingDialog.show(context)
                        Uv1Helper.verifyWalletPassword(context,verifyWalletPasswordRequest,object :Uv1Helper.ResponseDataCallback<VerifyWalletPasswordResponse> {
                            override fun onSuccess(data: VerifyWalletPasswordResponse) {
                                LoadingDialog.cancel()
                                if (data.valid) {
                                    dialogResponse.onSuccess(pwd)
                                }else{
                                    ToastUtils.make().show(R.string.wrong_password)
                                    dialogResponse.onError()
                                }
                            }

                            override fun onError(e: java.lang.Exception?) {
                                LoadingDialog.cancel()
                                ToastUtils.make().show(R.string.wrong_password)
                                dialogResponse.onError()
                            }

                        })
                        dialog?.dismiss()
                    }

                }
            })
            .setDimAmount(0.3f)     //调节灰色背景透明度[0-1]，默认0.5f
            .setMargin(38)     //dialog左右两边到屏幕边缘的距离（单位：dp），默认0dp
            .setOutCancel(false)     //点击dialog外是否可取消，默认true
            .show(manager)     //显示dialog
    }

    /**
     * 校验密码
     */
    fun showCommonDialog(context: Context?, manager: FragmentManager) {
        NiceDialog.init()
            .setLayoutId(R.layout.input_pwd_dialog)     //设置dialog布局文件
            .setConvertListener(object : ViewConvertListener() {
                override fun convertView(holder: ViewHolder?, dialog: BaseNiceDialog?) {
                    val tvBackup = holder?.getView<TextView>(R.id.tvBackup)
                    val ivClose = holder?.getView<ImageView>(R.id.ivClose)
                    val pwd = holder?.getView<EditText>(R.id.etPwd)
                    val ok = holder?.getView<TextView>(R.id.tvOk)
                    ivClose?.setOnClickListener {
                        dialog?.dismiss()
                    }
                    val cbEye = holder?.getView<CheckBox>(R.id.cbEye)
                    cbEye?.setOnClickListener {
                        if (cbEye.isChecked) {
                            pwd?.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        } else {
                            pwd?.inputType =
                                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        }
                        //键盘光标移到EditText末尾
                        pwd?.text?.length?.let { it1 -> Selection.setSelection(pwd.text, it1) }
                    }
                    pwd?.addTextChangedListener {
                        val password = pwd.text.trim().toString()
                        cbEye?.isEnabled = password.isNotEmpty()
                        if (password.isNotEmpty()) {
                            ok?.isEnabled = StringUtils.checkString(password) && password.length > 7
                        } else {
                            ok?.isEnabled = false
                        }
                    }
                    ok?.setOnClickListener {
                        if (KeyboardUtils.isSoftInputVisible(context as Activity)) {//隐藏系统软键盘
                            pwd?.let { it1 -> KeyboardUtils.hideSoftInput(it1) }
                        }

                        val pwd = pwd?.text?.trim().toString()
                        val walletId = SPUtils.getInstance().getString("walletId")
                        val walletRepository = WalletRepository(context)
                        val walletById: Wallet? =
                            walletId?.let { it1 -> walletRepository.getWalletById(it1) }
                        val password = walletById?.password?.trim()

                        //校验密码
                        val verifyWalletPasswordRequest = VerifyWalletPasswordRequest()
                        verifyWalletPasswordRequest.password = pwd
                        verifyWalletPasswordRequest.walletId =
                            SPUtils.getInstance().getString("walletId")
                        verifyWalletPasswordRequest.keystoreDir = DirUtils.createKeyStoreDir()
                        try {
                            val verifyWalletPassword: VerifyWalletPasswordResponse =
                                Uv1.verifyWalletPassword(
                                    verifyWalletPasswordRequest
                                )
                            if (!verifyWalletPassword.valid) {
                                ToastUtils.make().show(R.string.wrong_password)
                                return@setOnClickListener
                            }
                        } catch (e: Exception) {
                            ToastUtils.make().show(R.string.wrong_password)
                            return@setOnClickListener
                        }
                        context.startActivity(
                            Intent(context, BackupMnemonicActivity::class.java)
                                .putExtra("pwd", pwd)
                                .putExtra("walletId", walletId)
                        )

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
            .show(manager)     //显示dialog

    }

}
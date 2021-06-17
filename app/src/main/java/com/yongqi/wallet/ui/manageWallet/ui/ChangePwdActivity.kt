package com.yongqi.wallet.ui.manageWallet.ui

import android.text.InputType
import android.text.Selection
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.core.widget.addTextChangedListener
import api.ModifyPasswordRequest
import api.ModifyPasswordResponse
import com.blankj.utilcode.util.ToastUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.yongqi.wallet.R
import com.yongqi.wallet.base.BaseActivity
import com.yongqi.wallet.databinding.ActivityChangePwdBinding
import com.yongqi.wallet.db.coin.CoinRepository
import com.yongqi.wallet.ui.manageWallet.viewModel.ChangePwdViewModel
import com.yongqi.wallet.utils.DirUtils
import com.yongqi.wallet.utils.StringUtils
import com.yongqi.wallet.utils.Uv1Helper
import com.yongqi.wallet.view.LoadingDialog
import kotlinx.android.synthetic.main.activity_change_pwd.*
import kotlinx.android.synthetic.main.common_title.*
import uv1.Uv1

class ChangePwdActivity : BaseActivity<ActivityChangePwdBinding, ChangePwdViewModel>() {

    override fun getLayoutResource(): Int = R.layout.activity_change_pwd


    override fun initData() {

        immersionBar {
            statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
            titleBar(iTitle) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
        }
        ivBack.setOnClickListener(onClickListener)
        btnConfirm.setOnClickListener(onClickListener)
        cbEye.setOnClickListener(onClickListener)
        cbEye1.setOnClickListener(onClickListener)
        cbEye2.setOnClickListener(onClickListener)
        tvTitle.text = getString(R.string.modify_wallet_pwd)
        editTextCheck()

    }

    private val onClickListener = View.OnClickListener { view ->
        var originalPwd = etOriginalPwd.text.toString().trim()
        val pwd = etPwd.text.toString().trim()
        val repeatPwdPrompt = etRepeatPwd.text.toString().trim()
        when (view.id) {


            R.id.ivBack -> {
                finish()
            }

            R.id.cbEye -> {
                if (cbEye.isChecked) {
                    etOriginalPwd.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                } else {
                    etOriginalPwd.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                }
                //键盘光标移到EditText末尾
                etOriginalPwd?.text?.length?.let { it1 ->
                    Selection.setSelection(
                        etOriginalPwd?.text,
                        it1
                    )
                }
            }

            R.id.cbEye1 -> {
                if (cbEye1.isChecked) {
                    etPwd.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                } else {
                    etPwd.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                }
                //键盘光标移到EditText末尾
                etPwd?.text?.length?.let { it1 -> Selection.setSelection(etPwd?.text, it1) }
            }

            R.id.cbEye2 -> {
                if (cbEye2.isChecked) {
                    etRepeatPwd.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                } else {
                    etRepeatPwd.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                }

                //键盘光标移到EditText末尾
                etRepeatPwd?.text?.length?.let { it1 ->
                    Selection.setSelection(
                        etRepeatPwd?.text,
                        it1
                    )
                }
            }
            R.id.btnConfirm -> {
                var originalPwd = etOriginalPwd.text.toString().trim()
                val pwd = etPwd.text.toString().trim()
                val repeatPwdPrompt = etRepeatPwd.text.toString().trim()
                if (pwd == repeatPwdPrompt) {
                    mModifyPassword()
                } else {
                    ToastUtils.make().show(R.string.password_input_is_inconsistent)
                }
            }
        }
    }

    private fun editTextCheck() {

        etOriginalPwd.addTextChangedListener {
            var originalPwd = etOriginalPwd.text.toString().trim()
            val pwd = etPwd.text.toString().trim()
            val repeatPwdPrompt = etRepeatPwd.text.toString().trim()
            val isMatch1 = StringUtils.checkString(originalPwd)
            val isMatch2 = StringUtils.checkString(pwd)
            val isMatch3 = StringUtils.checkString(repeatPwdPrompt)
            if (isMatch1&&originalPwd.length>7){
                tvPwdPrompt1.setTextColor(resources.getColor(R.color.color_9))
            }else{
                tvPwdPrompt1.setTextColor(resources.getColor(R.color.color_14))
            }
            cbEye.isEnabled = originalPwd.isNotEmpty()
//            if (isMatch2){
//                tvPwdPrompt2.setTextColor(resources.getColor(R.color.color_9))
//            }else{
//                tvPwdPrompt2.setTextColor(resources.getColor(R.color.color_14))
//            }
            btnConfirm.isEnabled = isMatch1 && isMatch2 && isMatch3 && originalPwd.length > 7 && pwd.length > 7 && repeatPwdPrompt.length > 7
        }

        etPwd.addTextChangedListener {
            val originalPwd = etOriginalPwd.text.toString().trim()
            val pwd = etPwd.text.toString().trim()
            val repeatPwdPrompt = etRepeatPwd.text.toString().trim()
            val isMatch1 = StringUtils.checkString(originalPwd)
            val isMatch2 = StringUtils.checkString(pwd)
            val isMatch3 = StringUtils.checkString(repeatPwdPrompt)
            if (isMatch2&&pwd.length>7){
                tvPwdPrompt2.setTextColor(resources.getColor(R.color.color_9))
            }else{
                tvPwdPrompt2.setTextColor(resources.getColor(R.color.color_14))
            }
            cbEye1.isEnabled = pwd.isNotEmpty()
            btnConfirm.isEnabled =
                isMatch1 && isMatch2 && isMatch3 && originalPwd.length > 7 && pwd.length > 7 && repeatPwdPrompt.length > 7
        }
        etRepeatPwd.addTextChangedListener {
            val originalPwd = etOriginalPwd.text.toString().trim()
            val pwd = etPwd.text.toString().trim()
            val repeatPwdPrompt = etRepeatPwd.text.toString().trim()
            val isMatch1 = StringUtils.checkString(originalPwd)
            val isMatch2 = StringUtils.checkString(pwd)
            val isMatch3 = StringUtils.checkString(repeatPwdPrompt)
            cbEye2.isEnabled = repeatPwdPrompt.isNotEmpty()
            btnConfirm.isEnabled = isMatch1 && isMatch2 && isMatch3 && originalPwd.length > 7 && pwd.length > 7 && repeatPwdPrompt.length > 7
        }

    }


    private fun mModifyPassword() {
        val originalPwd = etOriginalPwd.text.toString().trim()
        val pwd = etPwd.text.toString().trim()

        val walletId = intent.getStringExtra("walletId")
        val coinRepository = CoinRepository(this)
        val coinsById = coinRepository.getCoinsById(walletId)
        var keyIds = ""
        coinsById?.forEachIndexed { index, coin ->
            if (coin.coin_tag.isNullOrEmpty()) {
                keyIds += "${coin.key_id},"
            }
        }
        keyIds = keyIds.trim().substring(0, keyIds.length - 1)
        val modifyPasswordRequest = ModifyPasswordRequest()
        modifyPasswordRequest.keyIds = keyIds
        modifyPasswordRequest.keystoreDir = DirUtils.createKeyStoreDir()
        modifyPasswordRequest.prevPassword = originalPwd
        modifyPasswordRequest.newPassword = pwd
        modifyPasswordRequest.walletId = walletId

        LoadingDialog.show(this)
        Uv1Helper.modifyPassword(this,modifyPasswordRequest,object :Uv1Helper.ResponseDataCallback<ModifyPasswordResponse> {
            override fun onSuccess(data: ModifyPasswordResponse?) {
                LoadingDialog.cancel()
                ToastUtils.make().show(R.string.modified_success)
                finish()
            }

            override fun onError(e: java.lang.Exception?) {
                LoadingDialog.cancel()
                ToastUtils.make().show(R.string.modified_failed)
            }
        })
    }

}
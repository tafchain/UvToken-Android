package com.yongqi.wallet.ui.importWallet.ui

import android.content.Intent
import android.text.InputType
import android.text.Selection
import android.view.Gravity
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.blankj.utilcode.util.ToastUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.yongqi.wallet.R
import com.yongqi.wallet.base.BaseActivity
import com.yongqi.wallet.databinding.ActivityMnemonicImportSetWalletPwdBinding
import com.yongqi.wallet.ui.importWallet.viewModel.MnemonicImportSetWalletPwdViewModel
import com.yongqi.wallet.ui.wallet.ui.AgreementActivity
import com.yongqi.wallet.utils.StringUtils
import kotlinx.android.synthetic.main.activity_mnemonic_import_set_wallet_pwd.*
import kotlinx.android.synthetic.main.activity_mnemonic_import_set_wallet_pwd.cbAgreement
import kotlinx.android.synthetic.main.activity_mnemonic_import_set_wallet_pwd.cbEye1
import kotlinx.android.synthetic.main.activity_mnemonic_import_set_wallet_pwd.cbEye2
import kotlinx.android.synthetic.main.activity_mnemonic_import_set_wallet_pwd.etPwd
import kotlinx.android.synthetic.main.activity_mnemonic_import_set_wallet_pwd.etRepeatPwd
import kotlinx.android.synthetic.main.activity_mnemonic_import_set_wallet_pwd.etWalletName
import kotlinx.android.synthetic.main.activity_mnemonic_import_set_wallet_pwd.iTitle
import kotlinx.android.synthetic.main.activity_mnemonic_import_set_wallet_pwd.ivClear
import kotlinx.android.synthetic.main.activity_mnemonic_import_set_wallet_pwd.tvAgreement
import kotlinx.android.synthetic.main.activity_mnemonic_import_set_wallet_pwd.tvPwdPrompt
import kotlinx.android.synthetic.main.activity_mnemonic_import_set_wallet_pwd.tvWalletNamePrompt
import kotlinx.android.synthetic.main.common_title.*

/**
 * 助记词导入钱包
 */
class MnemonicImportSetWalletPwdActivity :
    BaseActivity<ActivityMnemonicImportSetWalletPwdBinding, MnemonicImportSetWalletPwdViewModel>() {

    override fun getLayoutResource(): Int = R.layout.activity_mnemonic_import_set_wallet_pwd

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {

            R.id.ivBack -> {
                finish()
            }
            R.id.cbEye1 -> {
                if (cbEye1.isChecked) {
                    etPwd.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
                } else {
                    etPwd.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                }
                //键盘光标移到EditText末尾
                etPwd?.text?.length?.let { it1 -> Selection.setSelection(etPwd?.text, it1) }

            }
            R.id.cbEye2 -> {
                if (cbEye2.isChecked) {
                    etRepeatPwd.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
                } else {
                    etRepeatPwd.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                }

                //键盘光标移到EditText末尾
                etRepeatPwd?.text?.length?.let { it1 -> Selection.setSelection(etRepeatPwd?.text, it1) }
            }

            R.id.ivClear -> {
                etWalletName.setText("")
            }
            R.id.tvAgreement -> {
                startActivity(
                    Intent(
                        this,
                        AgreementActivity::class.java
                    )
                )
            }
            R.id.btnComplete -> {//todo
                val walletName = etWalletName.text.trim().toString()
                val pwd = etPwd.text.trim().toString()
                val repeatPwd = etRepeatPwd.text.trim().toString()
                if (pwd != repeatPwd) {
                    ToastUtils.make().show(R.string.password_input_is_inconsistent)
                    return@OnClickListener
                }
                startActivity(
                    Intent(this, ImportAddCurrencyActivity::class.java)
                        .putExtra("walletName", walletName)
                        .putExtra("pwd", pwd)
                        .putExtra("mnemonics", intent.getStringExtra("mnemonics"))
                )
            }
        }
    }


    override fun initData() {
        immersionBar {
            statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
            titleBar(iTitle) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
        }
        tvTitle.text = getString(R.string.import_wallet)
        ivBack.setOnClickListener(onClickListener)
        cbEye1.setOnClickListener(onClickListener)
        cbEye2.setOnClickListener(onClickListener)
        ivClear.setOnClickListener(onClickListener)
        tvAgreement.setOnClickListener(onClickListener)
        btnComplete.setOnClickListener(onClickListener)
        editTextCheck()
    }


    private fun editTextCheck() {

        etWalletName.addTextChangedListener {
            val walletName = etWalletName.text.toString().trim()
            val walletNamePrompt = tvWalletNamePrompt.text.toString().trim()
            val pwd = etPwd.text.toString().trim()
            val pwdPrompt = tvPwdPrompt.text.toString().trim()
            val repeatPwdPrompt = etRepeatPwd.text.toString().trim()
            if (!walletName.isNullOrEmpty()) {
                ivClear.visibility = View.VISIBLE
            } else {
                ivClear.visibility = View.GONE
            }
            cbEye1.isEnabled = !pwd.isNullOrEmpty()
            cbEye2.isEnabled = !repeatPwdPrompt.isNullOrEmpty()
            val isMatch = StringUtils.checkString(pwd)
//            if (isMatch && pwd.length > 7) {
//                tvPwdPrompt.setTextColor(resources.getColor(R.color.color_9))
//            } else {
//                tvPwdPrompt.setTextColor(resources.getColor(R.color.color_14))
//            }
            btnComplete.isEnabled =
                walletName.isNotEmpty() && isMatch && pwd.length > 7 && repeatPwdPrompt.isNotEmpty() && repeatPwdPrompt.length > 7 && cbAgreement.isChecked
        }

        etPwd.addTextChangedListener {
            val walletName = etWalletName.text.toString().trim()
            val walletNamePrompt = tvWalletNamePrompt.text.toString().trim()
            val pwd = etPwd.text.toString().trim()
            val pwdPrompt = tvPwdPrompt.text.toString().trim()
            val repeatPwdPrompt = etRepeatPwd.text.toString().trim()
            if (!walletName.isNullOrEmpty()) {
                ivClear.visibility = View.VISIBLE
            } else {
                ivClear.visibility = View.GONE
            }
            cbEye1.isEnabled = !pwd.isNullOrEmpty()
            cbEye2.isEnabled = !repeatPwdPrompt.isNullOrEmpty()
            val isMatch = StringUtils.checkString(pwd)
            if (isMatch && pwd.length > 7) {
                tvPwdPrompt.setTextColor(resources.getColor(R.color.color_9))
            } else {
                tvPwdPrompt.setTextColor(resources.getColor(R.color.color_14))
            }
            btnComplete.isEnabled =
                walletName.isNotEmpty() && isMatch && pwd.length > 7 && repeatPwdPrompt.isNotEmpty() && repeatPwdPrompt.length > 7 && cbAgreement.isChecked
        }
        etRepeatPwd.addTextChangedListener {
            val walletName = etWalletName.text.toString().trim()
            val walletNamePrompt = tvWalletNamePrompt.text.toString().trim()
            val pwd = etPwd.text.toString().trim()
            val pwdPrompt = tvPwdPrompt.text.toString().trim()
            val repeatPwdPrompt = etRepeatPwd.text.toString().trim()
            if (!walletName.isNullOrEmpty()) {
                ivClear.visibility = View.VISIBLE
            } else {
                ivClear.visibility = View.GONE
            }
            cbEye1.isEnabled = !pwd.isNullOrEmpty()
            cbEye2.isEnabled = !repeatPwdPrompt.isNullOrEmpty()
            val isMatch = StringUtils.checkString(pwd)
            if (isMatch && pwd.length >= 8) {
                tvPwdPrompt.setTextColor(resources.getColor(R.color.color_9))
            } else {
                tvPwdPrompt.setTextColor(resources.getColor(R.color.color_14))
            }
            btnComplete.isEnabled =
                walletName.isNotEmpty() && isMatch && pwd.length > 7 && repeatPwdPrompt.isNotEmpty() && repeatPwdPrompt.length > 7 && cbAgreement.isChecked
        }

        cbAgreement.setOnCheckedChangeListener { _, _ ->
            val walletName = etWalletName.text.toString().trim()
            val walletNamePrompt = tvWalletNamePrompt.text.toString().trim()
            val pwd = etPwd.text.toString().trim()
            val pwdPrompt = tvPwdPrompt.text.toString().trim()
            val repeatPwdPrompt = etRepeatPwd.text.toString().trim()
            if (!walletName.isNullOrEmpty()) {
                ivClear.visibility = View.VISIBLE
            } else {
                ivClear.visibility = View.GONE
            }
            cbEye1.isEnabled = !pwd.isNullOrEmpty()
            cbEye2.isEnabled = !repeatPwdPrompt.isNullOrEmpty()
            val isMatch = StringUtils.checkString(pwd)
            if (isMatch && pwd.length > 7) {
                tvPwdPrompt.setTextColor(resources.getColor(R.color.color_9))
            } else {
                tvPwdPrompt.setTextColor(resources.getColor(R.color.color_14))
            }
            btnComplete.isEnabled =
                walletName.isNotEmpty() && isMatch && pwd.length > 7 && repeatPwdPrompt.isNotEmpty() && repeatPwdPrompt.length > 7 && cbAgreement.isChecked
        }
    }


}
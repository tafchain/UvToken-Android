package com.yongqi.wallet.ui.createWallet.ui

import android.content.Intent
import android.view.Gravity
import android.view.View
import android.widget.TextView
import api.BackupMnemonicRequest
import api.BackupMnemonicResponse
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.yongqi.wallet.BuildConfig
import com.yongqi.wallet.R
import com.yongqi.wallet.base.BaseActivity
import com.yongqi.wallet.databinding.ActivityBackupMnemonicBinding
import com.yongqi.wallet.ui.createWallet.viewModel.BackupMnemonicViewModel
import com.yongqi.wallet.utils.DirUtils
import com.yongqi.wallet.utils.Uv1Helper
import kotlinx.android.synthetic.main.activity_backup_mnemonic.*
import kotlinx.android.synthetic.main.common_title.*
import java.lang.Exception

/**
 * 备份助记词
 */
class BackupMnemonicActivity : BaseActivity<ActivityBackupMnemonicBinding, BackupMnemonicViewModel>(),
    View.OnClickListener {

    var mnemonics = ""
    override fun getLayoutResource(): Int = R.layout.activity_backup_mnemonic

    override fun initData() {
        immersionBar {
            statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
            titleBar(iTitle) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
        }
        tvTitle.text = getString(R.string.mnemonic_backup_title)

        ivBack.setOnClickListener(this)
        btnBackupCompleted.setOnClickListener(this)
        if (BuildConfig.FLAVOR == "online"|| BuildConfig.FLAVOR == "onlineuvtest") {
            btnCopy.visibility = View.GONE
        }
        if (BuildConfig.FLAVOR == "devtest"|| BuildConfig.FLAVOR == "devuvtest") {
            btnCopy.visibility = View.VISIBLE
        }
        btnCopy.setOnClickListener(this)
        showTipDialog()
        //获取助记词
        getMnemonics()
    }

    private fun showTipDialog() {
        val dialog = MaterialDialog(this)
            .customView(R.layout.backup_prompt_dialog, scrollable = true)
        dialog.cancelOnTouchOutside(false)
        val customView = dialog.getCustomView()
        val tvUnderstood = customView.findViewById<TextView>(R.id.tvUnderstood)
        tvUnderstood.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun getMnemonics() {
        val walletId = intent.getStringExtra("walletId")
        val pwd = intent.getStringExtra("pwd")
        val backupMnemonicRequest = BackupMnemonicRequest()
        backupMnemonicRequest.walletId = walletId
        backupMnemonicRequest.keystoreDir = DirUtils.createKeyStoreDir()
        backupMnemonicRequest.keystorePassword = pwd

        Uv1Helper.backupMnemonic(this,backupMnemonicRequest,object :Uv1Helper.ResponseDataCallback<BackupMnemonicResponse> {
            override fun onSuccess(data: BackupMnemonicResponse?) {
                mnemonics = data?.mnemonics!!
                val mnes = mnemonics.split(" ")
                tvMnemonic1.text = mnes[0]
                tvMnemonic2.text = mnes[1]
                tvMnemonic3.text = mnes[2]
                tvMnemonic4.text = mnes[3]
                tvMnemonic5.text = mnes[4]
                tvMnemonic6.text = mnes[5]
                tvMnemonic7.text = mnes[6]
                tvMnemonic8.text = mnes[7]
                tvMnemonic9.text = mnes[8]
                tvMnemonic10.text = mnes[9]
                tvMnemonic11.text = mnes[10]
                tvMnemonic12.text = mnes[11]
            }

            override fun onError(e: Exception?) {

            }
        })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivBack -> {
                finish()
            }
            R.id.btnCopy -> {
                ClipboardUtils.copyText(mnemonics)
                ToastUtils.make().show(getString(R.string.copy_success))
            }
            R.id.btnBackupCompleted -> {
                startActivity(
                    Intent(this, ConfirmMnemonicActivity::class.java)
                        .putExtra("mnemonics", mnemonics)
                        .putExtra("walletId", intent.getStringExtra("walletId"))
                )
            }
        }
    }
}
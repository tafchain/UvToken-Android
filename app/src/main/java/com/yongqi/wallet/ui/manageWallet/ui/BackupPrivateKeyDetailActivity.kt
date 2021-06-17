package com.yongqi.wallet.ui.manageWallet.ui

import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import api.*
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.yongqi.wallet.R
import com.yongqi.wallet.base.BaseActivity
import com.yongqi.wallet.bean.Coin
import com.yongqi.wallet.databinding.ActivityBackupPrivateKeyDetailBinding
import com.yongqi.wallet.db.coin.CoinRepository
import com.yongqi.wallet.ui.manageWallet.viewModel.BackupPrivateKeyDetailViewModel
import com.yongqi.wallet.utils.DirUtils
import com.yongqi.wallet.utils.Uv1Helper
import kotlinx.android.synthetic.main.activity_backup_private_key_detail.*
import kotlinx.android.synthetic.main.common_title.*
import java.lang.StringBuilder

class BackupPrivateKeyDetailActivity :
    BaseActivity<ActivityBackupPrivateKeyDetailBinding, BackupPrivateKeyDetailViewModel>(), View.OnClickListener {

    var mWalletType:String = ""

    override fun getLayoutResource(): Int = R.layout.activity_backup_private_key_detail



    override fun initData() {
        immersionBar {
            statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
            titleBar(iTitle) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
        }
        showTipDialog()
        initListener()

        mWalletType = intent.getStringExtra("walletType").toString()
        val iconType = intent.getStringExtra("iconType")
        tvTitle.text = StringBuilder().append(iconType).append(" ").append(getString(R.string.private_key))

        if (mWalletType == "Multi") {
            //导出多链钱包
            val coinRepository = CoinRepository(this)
            val coin:Coin? = coinRepository.getCoinByWalletIdAndName(intent.getStringExtra("walletId"),iconType)
            val  exportPrivateKeyFromMasterRequest = ExportPrivateKeyFromMasterRequest()
            exportPrivateKeyFromMasterRequest.keystoreDir = DirUtils.createKeyStoreDir()
            exportPrivateKeyFromMasterRequest.passphrase = intent.getStringExtra("pwd")
            exportPrivateKeyFromMasterRequest.walletId = intent.getStringExtra("walletId")
            exportPrivateKeyFromMasterRequest.account = coin?.account!!
            exportPrivateKeyFromMasterRequest.change = coin.change!!
            exportPrivateKeyFromMasterRequest.coin = coin.coin!!
            exportPrivateKeyFromMasterRequest.index = coin.index!!
            Uv1Helper.exportPrivateKeyFromMaster(this,exportPrivateKeyFromMasterRequest,object :Uv1Helper.ResponseDataCallback<ExportPrivateKeyFromMasterResponse> {
                override fun onSuccess(data: ExportPrivateKeyFromMasterResponse?) {
                    runOnUiThread{
                        tvPrivateKey.text = data?.privateKey
                    }
                }

                override fun onError(e: Exception?) {
                    Log.e("sdk_error_log",e?.localizedMessage!!)
                }

            })
        }else{
            //导出单链钱包
            val exportPrivateKeyRequest = ExportPrivateKeyRequest()
            exportPrivateKeyRequest.coinType = iconType
            exportPrivateKeyRequest.keyId = intent.getStringExtra("walletId")
            exportPrivateKeyRequest.keystoreDir = DirUtils.createKeyStoreDir()
            exportPrivateKeyRequest.passphrase = intent.getStringExtra("pwd")
            Uv1Helper.exportPrivateKey(this,exportPrivateKeyRequest,object :Uv1Helper.ResponseDataCallback<ExportPrivateKeyResponse> {
                override fun onSuccess(data: ExportPrivateKeyResponse?) {
                    runOnUiThread{
                        tvPrivateKey.text = data?.privateKey
                    }
                }

                override fun onError(e: java.lang.Exception?) {
                    Log.e("sdk_error_log",e?.localizedMessage!!)
                }

            })
        }
    }

    private fun initListener() {
        ivBack.setOnClickListener(this)
        btnCopy.setOnClickListener(this)
    }

    private fun showTipDialog() {
        val dialog = MaterialDialog(this)
            .customView(R.layout.manage_backup_prompt_dialog, scrollable = true)
        dialog.cancelOnTouchOutside(false)
        val customView = dialog.getCustomView()
        val tvUnderstood = customView.findViewById<TextView>(R.id.tvUnderstood)
        tvUnderstood.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                finish()
            }
            R.id.btnCopy -> {
                ClipboardUtils.copyText(tvPrivateKey.text)

                ToastUtils.showShort(getString(R.string.copy_success))
            }
        }
    }

}
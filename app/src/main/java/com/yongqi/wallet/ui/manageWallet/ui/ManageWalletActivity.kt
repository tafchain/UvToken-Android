package com.yongqi.wallet.ui.manageWallet.ui

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.Gravity
import android.view.View
import api.*
import com.blankj.utilcode.util.*
import com.gyf.immersionbar.ktx.immersionBar
import com.yongqi.wallet.R
import com.yongqi.wallet.base.BaseActivity
import com.yongqi.wallet.databinding.ActivityManageWalletBinding
import com.yongqi.wallet.db.coin.CoinRepository
import com.yongqi.wallet.db.wallet.WalletRepository
import com.yongqi.wallet.ui.createWallet.ui.BackupMnemonicActivity
import com.yongqi.wallet.ui.manageWallet.viewModel.ManageWalletViewModel
import com.yongqi.wallet.utils.*
import com.yongqi.wallet.utils.StringUtils
import com.yongqi.wallet.view.LoadingDialog
import kotlinx.android.synthetic.main.activity_manage_wallet.*
import kotlinx.android.synthetic.main.activity_manage_wallet.iTitle
import kotlinx.android.synthetic.main.common_title.*
import uv1.Uv1
import java.lang.Exception

/**
 * 钱包管理
 */
class ManageWalletActivity : BaseActivity<ActivityManageWalletBinding, ManageWalletViewModel>() {


    override fun getLayoutResource(): Int = R.layout.activity_manage_wallet

    var walletId = ""
    lateinit var mContext:Context
    override fun initData() {
        mContext = this
        immersionBar {
            statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
            titleBar(iTitle) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
        }
        ivBack.setOnClickListener(onClickListener)
        tvTitle.text = getString(R.string.wallet_manage)
//        llWalletName.setOnClickListener(onClickListener)
        llChangePwd.setOnClickListener(onClickListener)
        llMnemonicBackup.setOnClickListener(onClickListener)
        llBackupPrivateKey.setOnClickListener(onClickListener)
        tvDeleteWallet.setOnClickListener(onClickListener)

        val type = intent.getStringExtra("type")
        var name = intent.getStringExtra("name")
        walletId = intent.getStringExtra("walletId").toString()
        when (type) {
            "Multi" -> {
                llWalletName.setOnClickListener(onClickListener)
                llMnemonicBackup.visibility = View.VISIBLE
                v_3.visibility = View.VISIBLE
            }
            "AECO" -> {
                var address = intent.getStringExtra("address")
                tvNameOrAddress.text = getString(R.string.wallet_address)
                tvName.text = StringUtils.replaceByX(address)
                ivChangeName.visibility = View.GONE
                llChangePwd.visibility = View.GONE
                v_2.visibility = View.GONE
                llMnemonicBackup.visibility = View.GONE
                tvDeleteWallet.visibility = View.GONE
                llWalletName.setOnClickListener {
                    ClipboardUtils.copyText(address)
                    ToastUtils.make().show("${getString(R.string.copy_success)}")
                }
            }
            else -> {
                llWalletName.setOnClickListener(onClickListener)
                llMnemonicBackup.visibility = View.GONE
                v_3.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        var type = intent.getStringExtra("type")
        when (type) {
            "AECO" -> {}
            else -> {
                val walletRepository = WalletRepository(this)
                var walletById = walletId?.let { walletRepository.getWalletById(it) }
                tvName.text = walletById?.name
            }
        }
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.ivBack -> {
                finish()
            }
            R.id.llWalletName -> {//修改钱包名称
                startActivity(
                    Intent(this, ChangeWalletNameActivity::class.java)
//                        .putExtra("pwd", pwd)
                        .putExtra("walletId", intent.getStringExtra("walletId"))
                        .putExtra("walletName", tvName.text.toString().trim())
                )
            }
            R.id.llChangePwd -> {
//                showCommonDialog(1)
                startActivity(
                    Intent(this, ChangePwdActivity::class.java)
//                        .putExtra("pwd", pwd)
                        .putExtra("walletId", intent.getStringExtra("walletId"))
                )
            }
            R.id.llMnemonicBackup -> {
                showPwdDialog(2)
            }
            R.id.llBackupPrivateKey -> {
                showPwdDialog(3)

            }
            R.id.tvDeleteWallet -> {//TODO 删除钱包

                val walletId = intent.getStringExtra("walletId")
                val walletIdIntent = SPUtils.getInstance().getString("walletId")
                if (walletIdIntent != walletId) {
                    showPwdDialog(4)
                } else {
                    ToastUtils.make().show(R.string.can_not_delete)
                }
            }
        }
    }


    private fun deleteWallet(walletId: String?, pwd: String?) {
        val walletRepository = WalletRepository(this)
        val walletById = walletId?.let { walletRepository.getWalletById(it) }
        val coinRepository = CoinRepository(this)

        val coinsById = coinRepository.getCoinsById(walletId)

        var dbKeyIds = ""
        coinsById?.forEachIndexed { index, coin ->
            if (coin.coin_tag.isNullOrEmpty()) {
                dbKeyIds += "${coin.key_id},"
            }
        }
        dbKeyIds = dbKeyIds.trim().substring(0, dbKeyIds.length - 1)
        if (walletById?.type == "Multi") {//删除多链钱包
            val removeWalletRequest = RemoveWalletRequest()
            removeWalletRequest.keyIds = dbKeyIds
            removeWalletRequest.keystoreDir = DirUtils.createKeyStoreDir()
            removeWalletRequest.password = pwd
            removeWalletRequest.walletId = walletId
            Log.e(tag(), "keyIds==:${dbKeyIds},keystoreDir==:${DirUtils.createKeyStoreDir()},password==:${pwd},walletId==:${walletId},")
            Uv1Helper.removeMultiWallet(this,removeWalletRequest,object :Uv1Helper.ResponseDataCallback<RemoveWalletResponse> {
                override fun onSuccess(data: RemoveWalletResponse) {
                    LoadingDialog.cancel()
                    if (walletId == data.walletId) {
                        coinRepository.delete(walletId)
                        walletRepository.deleteById(walletId)
                        ToastUtils.make().show(R.string.success_delete)
                        finish()
                    }
                }

                override fun onError(e: Exception) {
                    LoadingDialog.cancel()
                    Log.e("removeWallet",e.localizedMessage!!)
                }

            })
        } else {
            //删除单链钱包
            val mRunnable = Runnable {
                run {
                    val removeKeyRequest = RemoveKeyRequest()
                    removeKeyRequest.keyIds = dbKeyIds
                    removeKeyRequest.keystoreDir = DirUtils.createKeyStoreDir()
                    removeKeyRequest.password = pwd
                    Log.e(tag(), "keyIds==:${dbKeyIds},keystoreDir==:${DirUtils.createKeyStoreDir()},password==:${pwd},walletId==:${walletId},")
                    try {
                        val removeKeyResponse: RemoveKeyResponse = Uv1.removeKey(removeKeyRequest)
                        if (walletId == removeKeyResponse.keyIds) {
                            coinRepository.delete(walletId)
                            walletRepository.deleteById(walletId)
                            ToastUtils.make().show(R.string.success_delete)
                            LoadingDialog.cancel()
                            finish()
                        }else{
                            Log.e("removeKeyResponse","删除失败")
                        }
                    } catch (e: Exception) {
                        Log.e("removeKeyResponse", e.localizedMessage!!)
                    }
                }
            }
            Thread(mRunnable).start()
        }
    }



    /**
     * 密码弹窗
     */
    private fun showPwdDialog(position: Int) {
        val walletId = intent.getStringExtra("walletId")
        walletId?.let {
            DialogUtils.showCommonVerifyPasswordDialog(this,supportFragmentManager, it,
                block = { pwd ->
                    when (position) {
                        1 -> {
                        }
                        2 -> {
                            startActivity(
                                Intent(mContext, BackupMnemonicActivity::class.java)
                                    .putExtra("pwd", pwd)
                                    .putExtra("walletId", intent.getStringExtra("walletId"))
                            )
                        }
                        3 -> {
                            startActivity(
                                Intent(mContext, BackupPrivateKeyActivity::class.java)
                                    .putExtra("pwd", pwd)
                                    .putExtra("walletId", intent.getStringExtra("walletId"))
                                    .putExtra("type", intent.getStringExtra("type"))
                            )
                        }
                        4 -> {//删除钱包
                            val walletIdIntent = SPUtils.getInstance().getString("walletId")
                            if (walletIdIntent != walletId) {
                                deleteWallet(walletId, pwd)
                            } else {
                                ToastUtils.make().show(R.string.can_not_delete)
                            }
                        }
                    }
                })
        }
    }

}
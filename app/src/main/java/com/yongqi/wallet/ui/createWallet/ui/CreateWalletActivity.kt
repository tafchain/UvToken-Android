package com.yongqi.wallet.ui.createWallet.ui

import android.content.Intent
import android.view.KeyEvent
import android.view.View
import com.gyf.immersionbar.ktx.immersionBar
import com.yongqi.wallet.R
import com.yongqi.wallet.base.BaseActivity
import com.yongqi.wallet.databinding.ActivityCreateWalletBinding
import com.yongqi.wallet.ui.createWallet.viewModel.CreateWalletViewModel
import com.yongqi.wallet.ui.main.ui.HomePageActivity
import com.yongqi.wallet.utils.ActivityCollector
import kotlinx.android.synthetic.main.activity_create_wallet.*
import kotlinx.android.synthetic.main.common_title.*

class CreateWalletActivity : BaseActivity<ActivityCreateWalletBinding, CreateWalletViewModel>() {

    override fun getLayoutResource(): Int = R.layout.activity_create_wallet

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.btnBackupImmediately -> {
                startActivity(
                    Intent(this, BackupMnemonicActivity::class.java)
                        .putExtra("walletId", intent.getStringExtra("walletId"))
                        .putExtra("pwd", intent.getStringExtra("pwd")))
            }
            R.id.btnBackupLater -> {//稍后备份
                ActivityCollector.finishAll()
                startActivity(Intent(this, HomePageActivity::class.java))
            }

        }

    }


    override fun initData() {
        immersionBar {
            statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
            titleBar(iTitle) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
        }
        ivBack.visibility = View.GONE
        tvTitle.text = getString(R.string.create_wallet)
        btnBackupImmediately.setOnClickListener(onClickListener)
        btnBackupLater.setOnClickListener(onClickListener)
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event?.keyCode == KeyEvent.KEYCODE_BACK){//禁用系统返回键
            return true//分配结束
        }
        return super.dispatchKeyEvent(event)
    }
}
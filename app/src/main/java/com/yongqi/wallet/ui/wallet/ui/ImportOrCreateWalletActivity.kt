package com.yongqi.wallet.ui.wallet.ui

import android.content.Intent
import android.view.View
import com.blankj.utilcode.util.ScreenUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.yongqi.wallet.R
import com.yongqi.wallet.base.BaseActivity
import com.yongqi.wallet.databinding.ActivityImportOrCreateWalletBinding
import com.yongqi.wallet.ui.launch.viewModel.ImportOrCreateWalletViewModel
import com.yongqi.wallet.ui.createWallet.ui.SetWalletPwdActivity
import com.yongqi.wallet.ui.importWallet.ui.ImportWalletActivity
import com.yongqi.wallet.utils.GlideEngine
import kotlinx.android.synthetic.main.activity_import_or_create_wallet.*

class ImportOrCreateWalletActivity : BaseActivity<ActivityImportOrCreateWalletBinding, ImportOrCreateWalletViewModel>() {

    override fun getLayoutResource(): Int = R.layout.activity_import_or_create_wallet


    private val onClickLister = View.OnClickListener { view ->
        when (view.id) {
            R.id.rlCreateWallet -> {
                startActivity(
                    Intent(this, SetWalletPwdActivity::class.java)
                )
            }
            R.id.rlImportWallet -> {
                startActivity(Intent(this, ImportWalletActivity::class.java))
            }

        }
    }


    override fun initData() {
        immersionBar {
//            fitsSystemWindows(true)//只适合纯色状态栏;解决状态栏和布局重叠问题，使用该属性,必须指定状态栏颜色
//            statusBarColor(R.color.transparent)
//            titleBar(llBg) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
        }
        GlideEngine.loadImageWrapHeight(this,R.mipmap.pic_home_top,ivTop, ScreenUtils.getScreenWidth())
        GlideEngine.loadImageWrapHeight(this,R.mipmap.pic_home_dow,ivCoin, ScreenUtils.getScreenWidth())
        rlCreateWallet.setOnClickListener(onClickLister)
        rlImportWallet.setOnClickListener(onClickLister)

    }
}
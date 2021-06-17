package com.yongqi.wallet.ui.importWallet.ui

import android.content.Intent
import android.view.View
import com.gyf.immersionbar.ktx.immersionBar
import com.yongqi.wallet.R
import com.yongqi.wallet.base.BaseActivity
import com.yongqi.wallet.databinding.ActivityRepeatImportBinding
import com.yongqi.wallet.ui.importWallet.viewModel.RepeatImportViewModel
import kotlinx.android.synthetic.main.activity_repeat_import.*
import kotlinx.android.synthetic.main.common_title.*

class RepeatImportActivity : BaseActivity<ActivityRepeatImportBinding, RepeatImportViewModel>() {


    val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.ivBack -> {
                finish()
            }
            R.id.btnReImport -> {
                startActivity(
                    Intent(
                        this,
                        ImportWalletActivity::class.java
                    )
                )
            }
        }
    }

    override fun getLayoutResource(): Int = R.layout.activity_repeat_import

    override fun initData() {

        immersionBar {
//            fitsSystemWindows(true)//只适合纯色状态栏;解决状态栏和布局重叠问题，使用该属性,必须指定状态栏颜色
//            statusBarColor(R.color.transparent)
            statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
            titleBar(iTitle) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
        }
        val iconType: String? = intent.getStringExtra("iconType")
        if (iconType.isNullOrEmpty()){
            tvTitle.text = "${getString(R.string.import_wallet)}"
        }else{
            tvTitle.text = "${getString(R.string.import_wallet)} $iconType ${getString(R.string.title_wallet)}"
        }

        ivBack.setOnClickListener(onClickListener)
        btnReImport.setOnClickListener(onClickListener)
    }
}
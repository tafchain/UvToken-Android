package com.yongqi.wallet.ui.importWallet.ui

import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.core.widget.addTextChangedListener
import api.*
import com.gyf.immersionbar.ktx.immersionBar
import com.yongqi.wallet.BuildConfig
import com.yongqi.wallet.R
import com.yongqi.wallet.base.BaseActivity
import com.yongqi.wallet.config.CoinConst
import com.yongqi.wallet.databinding.ActivityImportWalletBinding
import com.yongqi.wallet.ui.createWallet.viewModel.ImportWalletViewModel
import com.yongqi.wallet.utils.EditTextUtils
import com.yongqi.wallet.utils.Uv1Helper
import kotlinx.android.synthetic.main.activity_import_wallet.*
import kotlinx.android.synthetic.main.common_select_coin_type.*
import kotlinx.android.synthetic.main.common_title.*

class ImportWalletActivity : BaseActivity<ActivityImportWalletBinding, ImportWalletViewModel>() {
    var mnemonicOrPrivateKey = 0

    override fun getLayoutResource(): Int = R.layout.activity_import_wallet

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.ivBack -> {
                finish()
            }
            R.id.tvMnemonicImport -> {//助记词导入   TODO 做成可以左右滑动
                mnemonicOrPrivateKey = 0
                checkButton(mnemonicOrPrivateKey)
            }
            R.id.tvPrivateKeyImport -> {//私钥导入
                mnemonicOrPrivateKey = 1
                checkButton(mnemonicOrPrivateKey)
            }
            R.id.btnImport -> {
                val mnemonics = etMnemonic.text.toString()
                isMnemonicCorrect(mnemonics,true)
            }

            R.id.rlBTC -> {
                startActivity(
                    Intent(this, PrivateKeyImportWalletActivity::class.java)
                        .putExtra("iconType", "BTC")
                )
            }
            R.id.rlETH -> {
                startActivity(
                    Intent(this, PrivateKeyImportWalletActivity::class.java)
                        .putExtra("iconType", "ETH")
                )
            }
            R.id.rlAECO -> {
                startActivity(
                    Intent(this, PrivateKeyImportWalletActivity::class.java)
                        .putExtra("iconType", "AECO")
                )
            }
            R.id.rlTRX -> {
                startActivity(
                    Intent(this, PrivateKeyImportWalletActivity::class.java)
                        .putExtra("iconType", CoinConst.TRX)
                )
            }
            R.id.layout_import_wallet -> {
                EditTextUtils.hideKeyboard(this)
            }
        }
    }

    private fun checkButton(position: Int) {
        when (position) {
            0 -> {
                tvMnemonicImport.isSelected = true
                tvPrivateKeyImport.isSelected = false
                tvPrompt.setText(R.string.input_prompt)
                etMnemonic.visibility = View.VISIBLE
                rlBTC.visibility = View.GONE
                rlETH.visibility = View.GONE
                rlAECO.visibility = View.GONE
                rlTRX.visibility = View.GONE
                btnImport.visibility = View.VISIBLE
                setTransparentStatusBar()
                tvMnemonicSpelledCorrectly.visibility = View.GONE

                v_1.visibility = View.VISIBLE
                v_2.visibility = View.INVISIBLE
            }
            1 -> {
                tvMnemonicImport.isSelected = false
                tvPrivateKeyImport.isSelected = true
                tvPrompt.setText(R.string.select_chain)
                etMnemonic.visibility = View.GONE
                rlBTC.visibility = View.VISIBLE
                rlETH.visibility = View.VISIBLE
                rlTRX.visibility = View.VISIBLE
                if (BuildConfig.FLAVOR == "online" || BuildConfig.FLAVOR == "devtest") {
                    rlAECO.visibility = View.GONE
                } else {//if ( BuildConfig.FLAVOR == "onlineuvtest"|| BuildConfig.FLAVOR == "devuvtest") {
                    rlAECO.visibility = View.VISIBLE
                }
                btnImport.visibility = View.GONE
                setTransparentStatusBar()
                tvMnemonicSpelledCorrectly.visibility = View.GONE
                v_1.visibility = View.INVISIBLE
                v_2.visibility = View.VISIBLE
            }
        }
    }


    override fun initData() {
        immersionBar {
            statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
            titleBar(v) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
            transparentStatusBar()//透明状态栏，不写默认透明色
        }
        ivBack.setOnClickListener(onClickListener)
        tvTitle.text = getString(R.string.import_wallet)
        checkButton(mnemonicOrPrivateKey)
        tvMnemonicImport.setOnClickListener(onClickListener)
        tvPrivateKeyImport.setOnClickListener(onClickListener)
        btnImport.setOnClickListener(onClickListener)
        layout_import_wallet.setOnClickListener(onClickListener)

        rlBTC.setOnClickListener(onClickListener)
        rlETH.setOnClickListener(onClickListener)
        rlTRX.setOnClickListener(onClickListener)
        rlAECO.setOnClickListener(onClickListener)
        //禁止输入回车符号
        etMnemonic.setOnEditorActionListener { v, actionId, event -> event!=null&&(event.keyCode == KeyEvent.KEYCODE_ENTER) }


        etMnemonic.addTextChangedListener {
            val mnemonics = etMnemonic.text.trim().toString()
            if (mnemonics.isNotEmpty()) {
                val mnemonicsSubstr = etMnemonic.text.toString().substring(mnemonics.length - 1)
                val mnes = mnemonics.split(" ")
                if (mnemonicsSubstr.contains(" ")) {
                    isMnemonicCorrect(mnemonics)
                }
                btnImport.isEnabled = mnes.size>11
            }else{
                setTransparentStatusBar()
                tvMnemonicSpelledCorrectly.visibility = View.GONE
                btnImport.isEnabled = false
            }
        }


    }

     private fun isMnemonicCorrect(mnemonics:String,isJump:Boolean = false){
         val mnemonicList = if (isJump){
             mnemonics.trim().split(" ")
         }else{
             mnemonics.split(" ")
         }
         val verifyMultiMnemonicRequest = VerifyMultiMnemonicRequest()
         verifyMultiMnemonicRequest.word = mnemonics
         Uv1Helper.verifyMultiMnemonic(this,verifyMultiMnemonicRequest,object :Uv1Helper.ResponseDataCallback<VerifyMultiMnemonicResponse> {
             override fun onSuccess(data: VerifyMultiMnemonicResponse?) {
                 runOnUiThread{
                     val invalidWord = data?.invalidWord
                     if (invalidWord.isNullOrEmpty()) {
                         setTransparentStatusBar()
                         tvMnemonicSpelledCorrectly.visibility = View.GONE
                         btnImport.isEnabled = mnemonicList.size == 12
                         if (isJump&&mnemonicList.size == 12){
                             startActivity(Intent(this@ImportWalletActivity, MnemonicImportSetWalletPwdActivity::class.java)
                                 .putExtra("mnemonics", etMnemonic.text.toString())
                             )
                         }
                     } else {
                         btnImport.isEnabled = false
                         setStatusBarColor()
                         tvMnemonicSpelledCorrectly.visibility = View.VISIBLE
                     }
                 }
             }

             override fun onError(e: java.lang.Exception?) {
                 btnImport.isEnabled = false

             }

         })
    }


    private fun setTransparentStatusBar() {
        immersionBar {
            transparentStatusBar()
        }
    }

    private fun setStatusBarColor() {
        immersionBar {
            statusBarColor(R.color.color_14)
        }
    }
}

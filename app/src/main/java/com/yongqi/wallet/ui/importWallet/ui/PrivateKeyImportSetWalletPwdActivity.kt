package com.yongqi.wallet.ui.importWallet.ui

import android.content.Intent
import android.text.InputType
import android.text.Selection
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import api.*
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.yongqi.wallet.R
import com.yongqi.wallet.base.BaseActivity
import com.yongqi.wallet.bean.Coin
import com.yongqi.wallet.bean.Wallet
import com.yongqi.wallet.config.CoinConst
import com.yongqi.wallet.config.WalletConfig
import com.yongqi.wallet.databinding.ActivityPrivateKeyImportSetWalletPwdBinding
import com.yongqi.wallet.db.coin.CoinRepository
import com.yongqi.wallet.db.wallet.WalletRepository
import com.yongqi.wallet.ui.importWallet.viewModel.PrivateKeyImportSetWalletViewModel
import com.yongqi.wallet.ui.main.ui.HomePageActivity
import com.yongqi.wallet.ui.wallet.ui.AgreementActivity
import com.yongqi.wallet.utils.*
import com.yongqi.wallet.view.LoadingDialog
import kotlinx.android.synthetic.main.activity_private_key_import_set_wallet_pwd.*
import kotlinx.android.synthetic.main.activity_private_key_import_set_wallet_pwd.cbAgreement
import kotlinx.android.synthetic.main.activity_private_key_import_set_wallet_pwd.cbEye1
import kotlinx.android.synthetic.main.activity_private_key_import_set_wallet_pwd.cbEye2
import kotlinx.android.synthetic.main.activity_private_key_import_set_wallet_pwd.etPwd
import kotlinx.android.synthetic.main.activity_private_key_import_set_wallet_pwd.etRepeatPwd
import kotlinx.android.synthetic.main.activity_private_key_import_set_wallet_pwd.etWalletName
import kotlinx.android.synthetic.main.activity_private_key_import_set_wallet_pwd.iTitle
import kotlinx.android.synthetic.main.activity_private_key_import_set_wallet_pwd.ivClear
import kotlinx.android.synthetic.main.activity_private_key_import_set_wallet_pwd.tvAgreement
import kotlinx.android.synthetic.main.activity_private_key_import_set_wallet_pwd.tvPwdPrompt
import kotlinx.android.synthetic.main.common_title.*
import uv1.Uv1

/**
 * 私钥导入
 */
class PrivateKeyImportSetWalletPwdActivity : BaseActivity<ActivityPrivateKeyImportSetWalletPwdBinding, PrivateKeyImportSetWalletViewModel>(), View.OnClickListener {

//    private var mWalletId: String? = ""
    private var mCoinType: String? = ""
    private val coinRepository by lazy { CoinRepository(this@PrivateKeyImportSetWalletPwdActivity) }

    override fun getLayoutResource(): Int = R.layout.activity_private_key_import_set_wallet_pwd

    override fun initData() {
        immersionBar {
            statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
            titleBar(iTitle) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
        }
        mCoinType = intent.getStringExtra("iconType")
        initListener()
        initTitle(mCoinType)
    }

    private fun initListener() {
        ivBack.setOnClickListener(this)
        cbEye1.setOnClickListener(this)
        cbEye2.setOnClickListener(this)
        ivClear.setOnClickListener(this)
        tvAgreement.setOnClickListener(this)
        //监听输入框
        editTextCheck()
        //导入私钥
        ClickUtils.applySingleDebouncing(btnImport, 5000, View.OnClickListener {
            val pwd = etPwd.text.trim().toString()
            val repeatPwd = etRepeatPwd.text.trim().toString()
            if (pwd != repeatPwd) {
                ToastUtils.make()
                    .show(R.string.password_input_is_inconsistent)
                return@OnClickListener
            }
            importWallet(pwd)
        })
    }

    private fun initTitle(coinType:String?) {
        when (coinType) {
            CoinConst.BTC -> {
                tvTitle.text = getString(R.string.import_btc_title)
            }
            CoinConst.ETH -> {
                tvTitle.text = getString(R.string.import_eth_title)
            }
            CoinConst.AECO -> {
                tvTitle.text = getString(R.string.import_aeco_title)
            }
        }
    }


    private fun editTextCheck() {
        etWalletName.addTextChangedListener {
            val walletName = etWalletName.text.toString().trim()
            val pwd = etPwd.text.toString().trim()
            val repeatPwdPrompt = etRepeatPwd.text.toString().trim()
            if (walletName.isNotEmpty()) {
                ivClear.visibility = View.VISIBLE
            } else {
                ivClear.visibility = View.GONE
            }
            cbEye1.isEnabled = pwd.isNotEmpty()
            cbEye2.isEnabled = repeatPwdPrompt.isNotEmpty()
            val isMatch = StringUtils.checkString(pwd)
            btnImport.isEnabled =
                walletName.isNotEmpty() && isMatch && pwd.length > 7 && repeatPwdPrompt.isNotEmpty() && repeatPwdPrompt.length > 7 && cbAgreement.isChecked
        }
        etPwd.addTextChangedListener {
            val walletName = etWalletName.text.toString().trim()
            val pwd = etPwd.text.toString().trim()
            val repeatPwdPrompt = etRepeatPwd.text.toString().trim()
            if (walletName.isNotEmpty()) {
                ivClear.visibility = View.VISIBLE
            } else {
                ivClear.visibility = View.GONE
            }
            cbEye1.isEnabled = pwd.isNotEmpty()
            cbEye2.isEnabled = repeatPwdPrompt.isNotEmpty()
            val isMatch = StringUtils.checkString(pwd)
            if (isMatch && pwd.length > 7) {
                tvPwdPrompt.setTextColor(ContextCompat.getColor(this, R.color.color_9))
            } else {
                tvPwdPrompt.setTextColor(ContextCompat.getColor(this, R.color.color_14))
            }
            btnImport.isEnabled =
                walletName.isNotEmpty() && isMatch && pwd.length > 7 && repeatPwdPrompt.isNotEmpty() && repeatPwdPrompt.length > 7 && cbAgreement.isChecked
        }

        etRepeatPwd.addTextChangedListener {
            val walletName = etWalletName.text.toString().trim()
            val pwd = etPwd.text.toString().trim()
            val repeatPwdPrompt = etRepeatPwd.text.toString().trim()
            if (walletName.isNotEmpty()) {
                ivClear.visibility = View.VISIBLE
            } else {
                ivClear.visibility = View.GONE
            }
            cbEye1.isEnabled = pwd.isNotEmpty()
            cbEye2.isEnabled = repeatPwdPrompt.isNotEmpty()
            val isMatch = StringUtils.checkString(pwd)
            if (isMatch && pwd.length >= 8) {
                tvPwdPrompt.setTextColor(ContextCompat.getColor(this, R.color.color_9))
            } else {
                tvPwdPrompt.setTextColor(ContextCompat.getColor(this, R.color.color_14))
            }
            btnImport.isEnabled =
                walletName.isNotEmpty() && isMatch && pwd.length > 7 && repeatPwdPrompt.isNotEmpty() && repeatPwdPrompt.length > 7 && cbAgreement.isChecked
        }


        cbAgreement.setOnCheckedChangeListener { _, _ ->
            val walletName = etWalletName.text.toString().trim()
            val pwd = etPwd.text.toString().trim()
            val repeatPwdPrompt = etRepeatPwd.text.toString().trim()
            if (walletName.isNotEmpty()) {
                ivClear.visibility = View.VISIBLE
            } else {
                ivClear.visibility = View.GONE
            }
            cbEye1.isEnabled = pwd.isNotEmpty()
            cbEye2.isEnabled = repeatPwdPrompt.isNotEmpty()
            val isMatch = StringUtils.checkString(pwd)
            if (isMatch && pwd.length > 7) {
                tvPwdPrompt.setTextColor(ContextCompat.getColor(this, R.color.color_9))
            } else {
                tvPwdPrompt.setTextColor(ContextCompat.getColor(this, R.color.color_14))
            }
            btnImport.isEnabled =
                walletName.isNotEmpty() && isMatch && pwd.length > 7 && repeatPwdPrompt.isNotEmpty() && repeatPwdPrompt.length > 7 && cbAgreement.isChecked
        }
    }

    //导入钱包
    fun importWallet(pwd: String) {
        val iconType: String? = intent.getStringExtra("iconType")
        val privateKey: String? = intent.getStringExtra("privateKey")
        val importPrivateKeyRequest = ImportPrivateKeyRequest()
        importPrivateKeyRequest.coinType = iconType
        importPrivateKeyRequest.keystoreDir = DirUtils.createKeyStoreDir()
        importPrivateKeyRequest.passphrase = etPwd.text.trim().toString()
        importPrivateKeyRequest.privateKey = privateKey

        LoadingDialog.show(this)
        //导入私钥
        Uv1Helper.importPrivateKey(this,importPrivateKeyRequest,object :Uv1Helper.ResponseDataCallback<ImportPrivateKeyResponse> {
            override fun onSuccess(importPrivateKeyResponse: ImportPrivateKeyResponse) {
               var mWalletId = importPrivateKeyResponse.keyId
                 val coin = coinRepository?.getCoinsByNameAndAddress(iconType, mWalletId)
                if (coin!=null){
                    LoadingDialog.cancel()
                    val filePath = "${DirUtils.createKeyStoreDir()}/$mWalletId"
                    if (FileUtils.isFileExists(filePath)){
                        FileUtils.delete(filePath)
                    }
                    startActivity(Intent(this@PrivateKeyImportSetWalletPwdActivity, RepeatImportActivity::class.java).putExtra("iconType", iconType))
                    return@onSuccess
                }
                SPUtils.getInstance().put("walletId", mWalletId)
                val walletRepository = WalletRepository(this@PrivateKeyImportSetWalletPwdActivity)
                walletRepository.insert(mWalletId?.let {
                    Wallet(
                        wallet_id = mWalletId,
                        is_backup = false,
                        name = etWalletName.text.toString().trim(),
                        type = mCoinType,
                        password = ""
                    )
                })
                //添加币种
                val addCoinTypeRequest = AddCoinTypeRequest()
                addCoinTypeRequest.walletId = mWalletId
                addCoinTypeRequest.coinType = mCoinType
                addCoinTypeRequest.keystoreDir = DirUtils.createKeyStoreDir()
                addCoinTypeRequest.keystorePassword = pwd
                //添加币种
                Uv1Helper.addCoinType(this@PrivateKeyImportSetWalletPwdActivity,addCoinTypeRequest,object :Uv1Helper.ResponseDataCallback<AddCoinTypeResponse> {
                    override fun onSuccess(data: AddCoinTypeResponse?) {
                        ToastUtils.make().show(R.string.import_success)
                        LoadingDialog.cancel()
                        //插入数据库
                        insertCoinDataToDatabase(importPrivateKeyResponse.address,importPrivateKeyResponse.keyId,mCoinType,data)

                        ActivityCollector.finishAll()
                        startActivity(Intent(this@PrivateKeyImportSetWalletPwdActivity, HomePageActivity::class.java))
                    }

                    override fun onError(e:Exception) {
                        LoadingDialog.cancel()

                    }
                })
            }

            override fun onError(e: java.lang.Exception?) {
                LoadingDialog.cancel()
                if (e?.message == "address already exists") {
                    startActivity(Intent(this@PrivateKeyImportSetWalletPwdActivity, RepeatImportActivity::class.java).putExtra("iconType", iconType))
                } else {
                    ToastUtils.make().show(R.string.private_key_wrong)
                }
            }
        })
    }


    /**
     * 插入币数据
     */
    private fun insertCoinDataToDatabase(
        address: String,
        keyId: String,
        coinType: String?,
        addCoinTypeResponse: AddCoinTypeResponse?
    ) {
        val coinRepository = CoinRepository(this@PrivateKeyImportSetWalletPwdActivity)
        coinRepository.insert(
            Coin(
                address = address,
                name = coinType,
                wallet_id = keyId,
                coin = addCoinTypeResponse!!.coin,
                account = addCoinTypeResponse.account,
                change = addCoinTypeResponse.change,
                index = addCoinTypeResponse.index,
                key_id = keyId,
                is_backup = false,
                coin_tag = ""
            )
        )
        if (coinType == "BTC") {
            coinRepository.insert(
                Coin(
                    address = address,
                    name = "USDT",
                    wallet_id = keyId,
                    coin = addCoinTypeResponse.coin,
                    account = addCoinTypeResponse.account,
                    change = addCoinTypeResponse.change,
                    index = addCoinTypeResponse.index,
                    key_id = keyId,
                    is_backup = false,
                    coin_tag = "OMNI"
                )
            )
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.ivBack -> {
                finish()
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
        }
    }

}
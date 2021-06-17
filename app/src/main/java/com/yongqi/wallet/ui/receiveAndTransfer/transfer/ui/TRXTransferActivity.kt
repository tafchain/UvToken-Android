package com.yongqi.wallet.ui.receiveAndTransfer.transfer.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import api.*
import com.afollestad.materialdialogs.MaterialDialog
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.shehuan.nicedialog.BaseNiceDialog
import com.shehuan.nicedialog.NiceDialog
import com.shehuan.nicedialog.ViewConvertListener
import com.shehuan.nicedialog.ViewHolder
import com.yongqi.wallet.R
import com.yongqi.wallet.base.BaseActivity
import com.yongqi.wallet.bean.Coin
import com.yongqi.wallet.bean.Record
import com.yongqi.wallet.config.AppConst
import com.yongqi.wallet.config.CoinConst
import com.yongqi.wallet.config.WalletConst
import com.yongqi.wallet.databinding.ActivityTrxtransferBinding
import com.yongqi.wallet.db.coin.CoinRepository
import com.yongqi.wallet.db.record.RecordRepository
import com.yongqi.wallet.db.wallet.WalletRepository
import com.yongqi.wallet.ui.createWallet.dialog.CustomTipDialog
import com.yongqi.wallet.ui.receiveAndTransfer.transfer.viewModel.TRXTransferViewModel
import com.yongqi.wallet.ui.scan.ui.ScanQrActivity
import com.yongqi.wallet.utils.*
import com.yongqi.wallet.view.LoadingDialog
import kotlinx.android.synthetic.main.activity_transfer.*
import kotlinx.android.synthetic.main.activity_transfer.btnCommit
import kotlinx.android.synthetic.main.activity_transfer.etTransferAddress
import kotlinx.android.synthetic.main.activity_transfer.etTransferAmount
import kotlinx.android.synthetic.main.activity_transfer.iTitle
import kotlinx.android.synthetic.main.activity_transfer.ivTransferAddress
import kotlinx.android.synthetic.main.activity_transfer.tvAll
import kotlinx.android.synthetic.main.activity_transfer.tvAllAmount
import kotlinx.android.synthetic.main.activity_trxtransfer.*
import kotlinx.android.synthetic.main.common_title.*
import java.math.BigDecimal

/**
 * trx转账
 */
class TRXTransferActivity : BaseActivity<ActivityTrxtransferBinding, TRXTransferViewModel>() {

    var address = ""//币的地址
    var coinAmount = ""//币资产
    var coinType = ""//币名字
    var keyId = ""//币的keyId
    var coinTag = ""////判断该币是否是OMNI或ERC20的代币
    var contactAddress = ""//币的合约地址
    var decimals = 0//ERC20币的精度
    var kMoney: BigDecimal = BigDecimal("0")
    var mProgress = 50
    var toAddress: String? = ""

    override fun getLayoutResource(): Int {
        return R.layout.activity_trxtransfer
    }

    companion object {
        const val REQUEST_CODE = 1
        const val pageType = 1
    }

    override fun initData() {

        initImmersionBar()
        address = intent.getStringExtra("address").toString()
        coinAmount = intent.getStringExtra("coinAmount").toString()
        coinType = intent.getStringExtra("coinType").toString()
        keyId = intent.getStringExtra("keyId").toString()
        coinTag = intent.getStringExtra("coinTag").toString()
        contactAddress = intent.getStringExtra("contactAddress").toString()//币的合约地址

        decimals = intent.getIntExtra("decimals", 0)//币的精度

        //设置转账金额小数点限制
        setAmountEditTextLength(decimals)

        ivBack.setOnClickListener(onClickListener)
        tvTitle.text = "$coinType ${getString(R.string.transfer)}"

        ivRightIcon.visibility = View.VISIBLE
        ivRightIcon.setImageResource(R.mipmap.icon_sys_h)
        ivRightIcon.setOnClickListener(onClickListener)
        ivTransferAddress.setOnClickListener(onClickListener)
        tvAll.setOnClickListener(onClickListener)
        btnCommit.setOnClickListener(onClickListener)
        tvAllAmount.text = "${getString(R.string.assets)} $coinAmount $coinType"

        etTransferAmount.addTextChangedListener {
            val transferAmount = etTransferAmount.text.trim().toString()
            val transferAddress = etTransferAddress.text.trim().toString()
            btnCommit.isEnabled =
                transferAmount.isNotEmpty() && transferAddress.isNotEmpty()
        }

        etTransferAddress.addTextChangedListener {
            val transferAmount = etTransferAmount.text.trim().toString()
            val transferAddress = etTransferAddress.text.trim().toString()
            (transferAmount.isNotEmpty() && transferAddress.isNotEmpty()).also { btnCommit.isEnabled = it }
            if (transferAddress.isNotEmpty()) {
                toAddress = transferAddress
            }
        }

        iv_fee.setOnClickListener{
            val customTipDialog = CustomTipDialog()
            customTipDialog.setContent(R.string.trx_tip)
            customTipDialog.setNeedCancel(false)
            customTipDialog.setConfirmText(R.string.ok_0)
            customTipDialog.setMargin(38)
            customTipDialog.setOutCancel(false)
            customTipDialog.show(supportFragmentManager)
        }
    }

    private fun setAmountEditTextLength(decimals:Int) {
        if (decimals == 0) {
            EditTextUtils.setPoint(etTransferAmount, 6)
        }else{
            EditTextUtils.setPoint(etTransferAmount, decimals)
        }
    }

    private val onClickListener = View.OnClickListener {
        when (it.id) {
            R.id.ivBack -> finish()
            R.id.ivRightIcon -> {
                requestCameraPermissions(this)
            }
            R.id.ivTransferAddress -> {
                startActivityForResult(
                    Intent(this, AddressBookActivity::class.java)
                        .putExtra("pageType", TransferActivity.pageType)
                        .putExtra("coinType", coinType)
                        .putExtra("coinTag", coinTag), TransferActivity.REQUEST_CODE
                )
            }

            R.id.tvAll -> {
                etTransferAmount.setText(coinAmount)
            }


            R.id.btnCommit -> {
                val transferAmount = etTransferAmount.text.toString().trim()
                if (BigDecimal(transferAmount).compareTo(BigDecimal.ZERO) < 0 || BigDecimal(transferAmount).compareTo(BigDecimal.ZERO) == 0){
                    if (coinTag != "TRC20") {
                        ToastUtils.make().show(R.string.trx_amount_tip)
                        return@OnClickListener
                    }
                }

                if (coinType == "BTC" || coinType == "ETH") {
                    val transferMoney = BigDecimal(transferAmount).add(kMoney)
                    if (transferMoney.subtract(BigDecimal(coinAmount)) > BigDecimal.ZERO) {
                        ToastUtils.make().show(R.string.insufficient_current_balance)
                        return@OnClickListener
                    }
                } else {
                    if (BigDecimal(transferAmount).subtract(BigDecimal(coinAmount)) > BigDecimal.ZERO) {
                        ToastUtils.make().show(R.string.insufficient_current_balance)
                        return@OnClickListener
                    }
                }


                if (address == toAddress) {
                    ToastUtils.showShort(getString(R.string.receiving_address_same_as_transfer_address))
//                    ToastUtils.make().setGravity(Gravity.TOP, 0, 100)
//                        .show(R.string.receiving_address_same_as_transfer_address)
                    return@OnClickListener
                }
                toAddress?.let { it1 -> isAddressIllegal(it1) }
            }
        }
    }

    /**
     * 校验输入的地址是否合法
     */
    private fun isAddressIllegal(toAddress: String) {
        val validateAddressRequest = ValidateAddressRequest()
        validateAddressRequest.address = toAddress
        validateAddressRequest.coinType = "TRX"
        LoadingDialog.show(this)
        Uv1Helper.validateAddress(this, validateAddressRequest,
            object : Uv1Helper.ResponseDataCallback<ValidateAddressResponse> {
                override fun onSuccess(data: ValidateAddressResponse?) {
                    LoadingDialog.cancel()
                    if (data?.valid!!) {
                        transferInfo()
                    } else {
                        ToastUtils.make().setGravity(Gravity.TOP, 0, 100)
                            .show(R.string.address_illegal)
                    }
                }

                override fun onError(e: java.lang.Exception?) {
                    LoadingDialog.cancel()
                    ToastUtils.make().setGravity(Gravity.TOP, 0, 100).show(R.string.address_illegal)
                }

            })
    }

    private fun transferInfo() {
        val transferAmount = etTransferAmount.text.trim().toString()

        NiceDialog.init()
            .setLayoutId(R.layout.layout_dialog_transfer_detail)
            .setConvertListener(object : ViewConvertListener() {
                override fun convertView(holder: ViewHolder?, dialog: BaseNiceDialog?) {
                    val ivClose = holder?.getView<ImageView>(R.id.iv_close)
                    val tvCoinMoney = holder?.getView<TextView>(R.id.tvCoinMoney)
                    val tvCoinType = holder?.getView<TextView>(R.id.tvCoinType)
                    val tvTransferAddress = holder?.getView<TextView>(R.id.tvTransferAddress)
                    val tvKFee = holder?.getView<TextView>(R.id.tvKFee)
                    val tvMemo = holder?.getView<TextView>(R.id.tvMemo)
                    tvCoinMoney?.text = transferAmount
                    tvCoinType?.text = coinType
                    tvTransferAddress?.text = toAddress

                    tvKFee?.text = "0 TRX"
                    tvMemo?.text = ""
                    ivClose?.setOnClickListener { dialog?.dismiss() }
                    val tvConfirm = holder?.getView<TextView>(R.id.tv_confirm)
                    tvConfirm?.setOnClickListener {
                        showPwdDialog()
                        dialog?.dismiss()
                    }


                }
            })
            .setDimAmount(0.3f)
            .setGravity(Gravity.BOTTOM)
            .setOutCancel(true)
            .setAnimStyle(R.style.BottomAnimation)
            .show(supportFragmentManager)
    }

    var pwdStr = ""
    private fun showPwdDialog() {
        val walletId = SPUtils.getInstance().getString("walletId")
        walletId?.let {
            DialogUtils.showCommonVerifyPasswordDialog(this,supportFragmentManager, it,
                block = { pwd ->
                    pwdStr = pwd
                    transfer()
                })
        }
    }

    /**
     * 转账
     */
    private fun transfer() {//TODO 自定义
        val transferAmount = etTransferAmount.text.trim().toString()
        val transferRequest = TransferRequest()
        transferRequest.toAddress = toAddress
        transferRequest.feeRate = "0"
        transferRequest.keyId = SPUtils.getInstance().getString("walletId")
        transferRequest.keystoreDir = DirUtils.createKeyStoreDir()
        transferRequest.passphrase = pwdStr
        var dbTrasferAmount = "0"
        when (coinTag) {
            "TRC20"-> {
                transferRequest.coinType = "TRX"
                transferRequest.tokenType = coinType
                transferRequest.tokenAddress = contactAddress
                transferRequest.amount = if (BigDecimal(transferAmount) == BigDecimal.ZERO) {
                    "0"
                } else {//循环除以10的精度次方
                    transferAmount
                }
                dbTrasferAmount = transferAmount
            }
            else -> {
                if (coinType == CoinConst.TRX) {//ETH转账用wei做单位
                    transferRequest.amount =
                        BigDecimal(transferAmount).multiply(BigDecimal(AppConst.TRX_TO_SUN))
                            .stripTrailingZeros().toPlainString()
                    dbTrasferAmount = BigDecimal(transferAmount).stripTrailingZeros()
                        .toPlainString()////.multiply(BigDecimal(ETH_TO_WEI))
                } else {
                    transferRequest.amount = transferAmount
                    dbTrasferAmount = transferAmount
                }
                transferRequest.coinType = coinType
            }
        }
        Log.e(
            tag(),
            "toAddress==$toAddress;transferAmount==$transferAmount;coinType==$coinType;keyId==$keyId;dir==${DirUtils.createKeyStoreDir()};pwdStr==$pwdStr"
        )
        val walletRepository = WalletRepository(this)
        val walletType = WalletUtils.getWalletType(
            walletRepository,
            SPUtils.getInstance().getString("walletId")
        )
        if (walletType == WalletConst.WALLET_TYPE_MULTI) {
            //多链钱包交易
            val coinRepository = CoinRepository(this)
//            val coin: Coin? = coinRepository.getCoin(SPUtils.getInstance().getString("walletId"), coinType)
            val coin: Coin? = coinRepository.getCoinsByNameAndAddress(coinType, address)
            val transferFromHdWalletRequest = TransferFromHdWalletRequest()
            transferFromHdWalletRequest.account = coin?.account!!
            transferFromHdWalletRequest.change = coin.change!!
            transferFromHdWalletRequest.coin = coin.coin!!
            transferFromHdWalletRequest.index = coin.index!!
            transferFromHdWalletRequest.transferRequest = transferRequest
            Uv1Helper.transferFromHdWallet(this, transferFromHdWalletRequest,
                object : Uv1Helper.ResponseDataCallback<TransferFromHdWalletResponse> {
                    override fun onSuccess(data: TransferFromHdWalletResponse?) {
                        LoadingDialog.cancel()
                        val txId = data?.transferResponse!!.txId
                        val keyId = data.transferResponse.keyId
                        val nonce = data?.transferResponse.nonce
                        val recordRepository = RecordRepository(this@TRXTransferActivity)
                        recordRepository.insert(
                            Record(
                                txId,
                                address,
                                dbTrasferAmount,
                                "",
                                0,
                                "转账",
                                coinType,
                                coinTag,
                                toAddress,
                                memo = "",
                                startTime = System.currentTimeMillis(),
                                gasPrice = "-1", //新增
                                nonce = nonce//新增
                            )
                        )
                        finish()
                        ToastUtils.make().setGravity(Gravity.TOP, 0, 100)
                            .setDurationIsLong(true)
                            .show(R.string.submitted_seccessfully)
                    }

                    override fun onError(e: Exception?) {
                        LoadingDialog.cancel()
                        if (e?.localizedMessage == "insufficient funds available to construct transaction") {
                            ToastUtils.make().setGravity(Gravity.TOP, 0, 100)
                                .show(R.string.balance_is_not_enough)
                        } else if(e?.localizedMessage!!.contains("Amount must be greater")) {
                            ToastUtils.make().setGravity(Gravity.TOP, 0, 100)
                                .show(e?.localizedMessage)
                        } else{
                            ToastUtils.make().setGravity(Gravity.TOP, 0, 100)
                                .setDurationIsLong(true)
                                .show(R.string.failed_ti_submit)
                        }
                    }

                })
        } else {
            Uv1Helper.transferFromKey(
                this,
                transferRequest,
                object : Uv1Helper.ResponseDataCallback<TransferResponse> {
                    override fun onSuccess(data: TransferResponse?) {
                        LoadingDialog.cancel()
                        val txId = data?.txId
                        val keyId = data?.keyId
                        val nonce = data?.nonce
                        val recordRepository = RecordRepository(this@TRXTransferActivity)
                        recordRepository.insert(
                            Record(
                                txId,
                                address,
                                dbTrasferAmount,
                                "",
                                0,
                                "转账",
                                coinType,
                                coinTag,
                                toAddress,
                                memo = "",
                                startTime = System.currentTimeMillis(),
                                gasPrice = "-1",//新增
                                nonce = nonce//新增
                            )
                        )
                        finish()
                        ToastUtils.make().setGravity(Gravity.TOP, 0, 100).setDurationIsLong(true)
                            .show(R.string.submitted_seccessfully)
                    }

                    override fun onError(e: java.lang.Exception?) {
                        LoadingDialog.cancel()
                        if (e?.localizedMessage == "insufficient funds available to construct transaction") {
                            ToastUtils.make().setGravity(Gravity.TOP, 0, 100)
                                .show(R.string.balance_is_not_enough)
                        } else {
                            ToastUtils.make().setGravity(Gravity.TOP, 0, 100)
                                .setDurationIsLong(true)
                                .show(R.string.failed_ti_submit)
                        }
                    }

                })
        }
    }

    private fun requestCameraPermissions(context: Context) {
        if (com.blankj.utilcode.util.PermissionUtils.isGranted(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        ) {//判断权限是否被授予
            val bundle = Bundle();
            bundle.putString("intent_type", "intent_type_transfer");
            bundle.putString("coinType", coinType)
            bundle.putString("coinTag", coinTag)
            val intent = Intent(context, ScanQrActivity::class.java)
            intent.putExtras(bundle)
            startActivityForResult(intent, 2)
        } else {
            com.blankj.utilcode.util.PermissionUtils
                .permission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ).callback { isAllGranted, granted, deniedForever, denied ->
                    LogUtils.d(granted, deniedForever, denied)
                    if (isAllGranted) {
                        val bundle = Bundle()
                        bundle.putString("intent_type", "intent_type_transfer")
                        bundle.putString("coinType", coinType)
                        bundle.putString("coinTag", coinTag)
                        val intent = Intent(context, ScanQrActivity::class.java)
                        intent.putExtras(bundle)
                        startActivityForResult(intent, 2)
                        return@callback
                    }
                    if (deniedForever.isNotEmpty()) {
                        val dialog = MaterialDialog(context)
                        dialog.title(R.string.title_permission)
                        dialog.message(R.string.permission_rationale_camera_message)
                        dialog.cancelOnTouchOutside(false)
                        dialog.negativeButton(R.string.cancel_0) {
                            dialog.dismiss()
                        }
                        dialog.positiveButton(R.string.ok_0) {
                            com.blankj.utilcode.util.PermissionUtils.launchAppDetailsSettings()//打开应用具体设置
                        }
                        dialog.show()
                    }
                }
                .request()//开始请求
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TransferActivity.REQUEST_CODE && resultCode == 3) {
            toAddress = data?.getStringExtra("address")
            etTransferAddress.setText(toAddress)
        }

        if (requestCode == 2 && resultCode == 1) {
            toAddress = data?.getStringExtra("address")
            etTransferAddress.setText(toAddress)
        }
    }

    private fun initImmersionBar() {
        immersionBar {
            keyboardEnable(true)
            statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
            titleBar(iTitle) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
            setOnKeyboardListener { isPopup, _ ->
                //防止系统键盘将布局顶上去
                if (isPopup) {
                    btnCommit.visibility = View.GONE
                } else {
                    btnCommit.visibility = View.VISIBLE
                }
            }
        }
    }
}
package com.yongqi.wallet.ui.receiveAndTransfer.transfer.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import api.*
import com.afollestad.materialdialogs.MaterialDialog
import com.blankj.utilcode.constant.TimeConstants
import com.blankj.utilcode.util.*
import com.gyf.immersionbar.OnKeyboardListener
import com.gyf.immersionbar.ktx.immersionBar
import com.shehuan.nicedialog.BaseNiceDialog
import com.shehuan.nicedialog.NiceDialog
import com.shehuan.nicedialog.ViewConvertListener
import com.shehuan.nicedialog.ViewHolder
import com.xw.repo.BubbleSeekBar
import com.yongqi.wallet.api.NetApi
import com.yongqi.wallet.R
import com.yongqi.wallet.base.BaseActivity
import com.yongqi.wallet.bean.*
import com.yongqi.wallet.config.AppConst
import com.yongqi.wallet.config.AppConst.ETH_TO_WEI
import com.yongqi.wallet.config.AppConst.GWEI_ETH
import com.yongqi.wallet.config.AppConst.SAT_BTC
import com.yongqi.wallet.config.CoinConst
import com.yongqi.wallet.config.WalletConst
import com.yongqi.wallet.databinding.ActivityTransferBinding
import com.yongqi.wallet.db.coin.CoinRepository
import com.yongqi.wallet.db.record.RecordRepository
import com.yongqi.wallet.db.wallet.WalletRepository
import com.yongqi.wallet.net.APIClient
import com.yongqi.wallet.net.APIResponse
import com.yongqi.wallet.ui.receiveAndTransfer.transfer.viewModel.TransferViewModel
import com.yongqi.wallet.ui.scan.ui.ScanQrActivity
import com.yongqi.wallet.ui.vm.HttpApiHelper
import com.yongqi.wallet.utils.*
import com.yongqi.wallet.utils.StringUtils
import com.yongqi.wallet.view.LoadingDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_transfer.*
import kotlinx.android.synthetic.main.activity_transfer.btnCommit
import kotlinx.android.synthetic.main.activity_transfer.etAmount
import kotlinx.android.synthetic.main.activity_transfer.etContent
import kotlinx.android.synthetic.main.activity_transfer.etTransferAddress
import kotlinx.android.synthetic.main.activity_transfer.etTransferAmount
import kotlinx.android.synthetic.main.activity_transfer.iTitle
import kotlinx.android.synthetic.main.activity_transfer.ivMore
import kotlinx.android.synthetic.main.activity_transfer.ivTransferAddress
import kotlinx.android.synthetic.main.activity_transfer.rlGas
import kotlinx.android.synthetic.main.activity_transfer.rlGasPrice
import kotlinx.android.synthetic.main.activity_transfer.sbProgress
import kotlinx.android.synthetic.main.activity_transfer.tvAbsenteeismCurrency
import kotlinx.android.synthetic.main.activity_transfer.tvAbsenteeismRmb
import kotlinx.android.synthetic.main.activity_transfer.tvAll
import kotlinx.android.synthetic.main.activity_transfer.tvAllAmount
import kotlinx.android.synthetic.main.activity_transfer.tvFast
import kotlinx.android.synthetic.main.activity_transfer.tvGarPrice
import kotlinx.android.synthetic.main.activity_transfer.tvGasFeePrompt
import kotlinx.android.synthetic.main.activity_transfer.tvGasPriceUnit
import kotlinx.android.synthetic.main.activity_transfer.tvMore
import kotlinx.android.synthetic.main.activity_transfer.tvRecommend
import kotlinx.android.synthetic.main.activity_transfer.tvSlower
import kotlinx.android.synthetic.main.common_title.*
import java.lang.StringBuilder
import java.math.BigDecimal

/**
 * 考虑用MVVM架构来修改该页面:
 * https://mp.weixin.qq.com/s/t1VBFZSOrzfxjxbfELF2Ng
 *
 */

class TransferActivity : BaseActivity<ActivityTransferBinding, TransferViewModel>() {

    override fun getLayoutResource(): Int = R.layout.activity_transfer

    companion object {
        const val REQUEST_CODE = 1
        const val pageType = 1
    }

    var address = ""//币的地址
    var coinAmount = ""//币资产
    var coinType = ""//币名字
    var keyId = ""//币的keyId
    var coinTag = ""////判断该币是否是OMNI或ERC20的代币
    var contactAddress = ""//币的合约地址
    var decimals = 0//ERC20币的精度
    var kMoney: BigDecimal = BigDecimal("0")
    var mProgress = 50
    override fun initData() {
        immersionBar {
            keyboardEnable(true)
            statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
            titleBar(iTitle) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
            setOnKeyboardListener(object : OnKeyboardListener {
                //防止系统键盘将布局顶上去
                override fun onKeyboardChange(isPopup: Boolean, keyboardHeight: Int) {
                    if (isPopup) {
                        btnCommit.visibility = GONE
                    } else {
                        btnCommit.visibility = VISIBLE
                    }
                }
            })
        }

        address = intent.getStringExtra("address").toString()
        coinAmount = intent.getStringExtra("coinAmount").toString()
        coinType = intent.getStringExtra("coinType").toString()
        keyId = intent.getStringExtra("keyId").toString()
        coinTag = intent.getStringExtra("coinTag").toString()
        contactAddress = intent.getStringExtra("contactAddress").toString()//币的合约地址

        decimals = intent.getIntExtra("decimals", 0)//币的精度
        ivBack.setOnClickListener(onClickListener)

        //设置转账金额小数点限制
        setAmountEditTextLength()

        if (coinTag.isEmpty()) {
            tvTitle.text = "$coinType ${getString(R.string.transfer)}"
        } else {
            tvTitle.text = "$coinType ($coinTag) ${getString(R.string.transfer)}"
        }

        ivRightIcon.visibility = View.VISIBLE
        ivRightIcon.setImageResource(R.mipmap.icon_sys_h)
        ivRightIcon.setOnClickListener(onClickListener)
        ivTransferAddress.setOnClickListener(onClickListener)
        tvAll.setOnClickListener(onClickListener)
        ivMore.setOnClickListener(onClickListener)
        tvMore.setOnClickListener(onClickListener)
        btnCommit.setOnClickListener(onClickListener)
        tvAllAmount.text = "${getString(R.string.assets)} $coinAmount $coinType"

        etTransferAmount.addTextChangedListener {
            var transferAmount = etTransferAmount.text.trim().toString()
            var transferAddress = etTransferAddress.text.trim().toString()
            btnCommit.isEnabled =
                !transferAmount.isNullOrEmpty() && !transferAddress.isNullOrEmpty()
        }

        etTransferAddress.addTextChangedListener {
            var transferAmount = etTransferAmount.text.trim().toString()
            var transferAddress = etTransferAddress.text.trim().toString()
            btnCommit.isEnabled =
                !transferAmount.isNullOrEmpty() && !transferAddress.isNullOrEmpty()
            if (!transferAddress.isNullOrEmpty()) {
                toAddress = transferAddress
            }
        }
        /**
         * BTC费率和矿工费计算
         */
        if (coinType == "BTC" || coinTag == "OMNI") {//ETH没有size,BTC有size
            noData("BTC")
            getEstimateTransactionSize()
            getBTCTransactionFeeRate()
        }

        /**
         *  ETH费率和矿工费计算
         */
        if (coinType == "ETH" || coinTag == "ERC20") {
            noData("ETH")
            getEthFeeRate()

        }

        /**
         * seekBar滑动后:BTC、ETH矿工费计算
         */
        sbProgress.onProgressChangedListener =
            object : BubbleSeekBar.OnProgressChangedListenerAdapter() {
                override fun getProgressOnFinally(
                    bubbleSeekBar: BubbleSeekBar?,
                    progress: Int,
                    progressFloat: Float,
                    fromUser: Boolean
                ) {

                    hideCustomize()
                    mProgress = progress
                    if (coinType == "BTC" || coinTag == "OMNI") {//ETH没有size,BTC有size
                        showSeekBarSize(mProgress)
                    }

                    if (coinType == "ETH" || coinTag == "ERC20") {
                        getEthFeeRate()
                    }
                }
            }

        etAmount.addTextChangedListener {//TODO 自定义
            val amount = etAmount.text.toString().trim()
            if (amount.isNullOrEmpty() || amount.startsWith(".")) {//|| BigDecimal(amount) <= BigDecimal.ZERO
                tvGasFeePrompt.visibility = INVISIBLE
                return@addTextChangedListener
            }
            if (coinType == "BTC" || coinTag == "OMNI") {
                kMoney = if (size == 0L) {
                    BigDecimal(0)
                } else {
                    BigDecimal(amount).multiply(BigDecimal(size.toString()))
                        .divide(BigDecimal(SAT_BTC), 8, BigDecimal.ROUND_HALF_UP)
                }
                if (BigDecimal(amount) == BigDecimal.ZERO || amount == "0.") {
                    kMoney = BigDecimal(0)
                }

                //矿工费

                tvAbsenteeismCurrency.text =
                    StringBuilder().append(kMoney.stripTrailingZeros().toPlainString())
                        .append(" BTC")
                getRate("BTC")
                transferFeeRate = BigDecimal(amount).toString()

                when {
                    BigDecimal(hourFee).subtract(BigDecimal(amount)) > BigDecimal.ZERO -> {
                        tvGasFeePrompt.visibility = VISIBLE
                        tvGasFeePrompt.text = getString(R.string.miner_fee_rate_is_too_low)
                    }
                    BigDecimal(amount).subtract(BigDecimal(fastestFee)) > BigDecimal.ZERO -> {
                        tvGasFeePrompt.visibility = VISIBLE
                        tvGasFeePrompt.text = getString(R.string.miner_fee_rate_is_too_high)
                    }
                    else -> {
                        tvGasFeePrompt.visibility = INVISIBLE
                    }
                }
            }
            if (coinType == "ETH" || coinTag == "ERC20") {
                kMoney = BigDecimal(amount).multiply(BigDecimal(21000))
                    .divide(BigDecimal(GWEI_ETH), 8, BigDecimal.ROUND_HALF_UP)

                if (BigDecimal(amount) == BigDecimal.ZERO || amount == "0.") {
                    kMoney = BigDecimal(0)
                }
                //矿工费
                tvAbsenteeismCurrency.text =
                    StringBuilder().append(kMoney.stripTrailingZeros().toPlainString())
                        .append(" ETH")
                getRate("ETH")
                transferFeeRate = BigDecimal(amount).multiply(BigDecimal(GWEI_ETH)).toString()

                when {
                    BigDecimal(ethHourFee).subtract(BigDecimal(amount)) > BigDecimal.ZERO -> {
                        tvGasFeePrompt.visibility = VISIBLE
                        tvGasFeePrompt.text = getString(R.string.miner_fee_rate_is_too_low)
                    }
                    BigDecimal(amount).subtract(BigDecimal(ethFastestFee)) > BigDecimal.ZERO -> {
                        tvGasFeePrompt.visibility = VISIBLE
                        tvGasFeePrompt.text = getString(R.string.miner_fee_rate_is_too_high)
                    }
                    else -> {
                        tvGasFeePrompt.visibility = INVISIBLE
                    }
                }

            }
        }
    }

    /**
     * 限制小数点位数
     */
    private fun setAmountEditTextLength() {
        when (coinTag) {
            "ERC20" -> {
                EditTextUtils.setPoint(etTransferAmount, decimals)
            }
            else -> {
                EditTextUtils.setPoint(etTransferAmount, 8)

            }
        }
        EditTextUtils.setPoint(etAmount, 2)

    }


    /**
     * 根据滑块显示矿工费
     */
    private fun showSeekBarSize(mProgress: Int) {
        when (mProgress) {
            0 -> {
                if (coinType == "BTC" || coinTag == "OMNI") {//ETH没有size,BTC有size
                    if (hourFee == "0") return
                    transferFeeRate = hourFee
                    kMoney = if (size == 0L) {
                        BigDecimal(0)
                    } else {
                        BigDecimal(hourFee.toString()).multiply(BigDecimal(size.toString()))
                            .divide(BigDecimal(SAT_BTC), 8, BigDecimal.ROUND_HALF_UP)
                    }
                }

//                if (coinType == "ETH" || coinTag == "ERC20") {
//                    if (ethHourFee == "0") return
//                    transferFeeRate =
//                        BigDecimal(ethHourFee.toString()).multiply(BigDecimal(GWEI_ETH)).toString()
//                    kMoney = BigDecimal(ethHourFee.toString()).multiply(BigDecimal(21000))
//                        .divide(BigDecimal(GWEI_ETH), 8, BigDecimal.ROUND_HALF_UP)
//                }

            }
            50 -> {
                if (coinType == "BTC" || coinTag == "OMNI") {//ETH没有size,BTC有size
                    if (halfHourFee == "0") return
                    transferFeeRate = halfHourFee
                    kMoney = if (size == 0L) {
                        BigDecimal(0)
                    } else {
                        BigDecimal(halfHourFee.toString()).multiply(BigDecimal(size.toString()))
                            .divide(BigDecimal(SAT_BTC), 8, BigDecimal.ROUND_HALF_UP)
                    }
                }
//                if (coinType == "ETH" || coinTag == "ERC20") {
//                    if (ethHalfHourFee == "0") return
//                    transferFeeRate = BigDecimal(ethHalfHourFee.toString()).multiply(
//                        BigDecimal(GWEI_ETH)
//                    ).toString()
//                    kMoney =
//                        BigDecimal(ethHalfHourFee.toString()).multiply(BigDecimal(21000))
//                            .divide(BigDecimal(GWEI_ETH), 8, BigDecimal.ROUND_HALF_UP)
//                }

            }
            100 -> {
                if (coinType == "BTC" || coinTag == "OMNI") {//ETH没有size,BTC有size
                    if (fastestFee == "0") return
                    transferFeeRate = fastestFee
                    kMoney = if (size == 0L) {
                        BigDecimal(0)
                    } else {
                        BigDecimal(fastestFee.toString()).multiply(BigDecimal(size.toString()))
                            .divide(BigDecimal(SAT_BTC), 8, BigDecimal.ROUND_HALF_UP)
                    }
                }

//                if (coinType == "ETH" || coinTag == "ERC20") {
//                    if (ethFastestFee == "0") return
//                    transferFeeRate =
//                        BigDecimal(ethFastestFee.toString()).multiply(BigDecimal(GWEI_ETH))
//                            .toString()
//                    kMoney = BigDecimal(ethFastestFee.toString()).multiply(BigDecimal(21000))
//                        .divide(BigDecimal(GWEI_ETH), 8, BigDecimal.ROUND_HALF_UP)
//                }


            }
        }
        if (coinType == "BTC" || coinTag == "OMNI") {//ETH没有size,BTC有size
            //矿工费


            tvAbsenteeismCurrency.text =
                StringBuilder().append(kMoney.stripTrailingZeros().toPlainString()).append(" BTC")
            getRate("BTC")
        }
//        if (coinType == "ETH" || coinTag == "ERC20") {
//            //矿工费
//            tvAbsenteeismCurrency.text = "$kMoney ETH"
//            getRate(coinType)
//        }

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
                )//设置请求权限
//                .permissionGroup(PermissionConstants.CALENDAR)//设置请求权限组
//                .explain { activity, denied, shouldRequest ->//设置权限请求前的解释
//                }

                .callback { isAllGranted, granted, deniedForever, denied ->
                    LogUtils.d(granted, deniedForever, denied)

                    if (isAllGranted) {
                        val bundle = Bundle();
                        bundle.putString("intent_type", "intent_type_transfer")
                        bundle.putString("coinType", coinType)
                        bundle.putString("coinTag", coinTag)
                        val intent = Intent(context, ScanQrActivity::class.java)
                        intent.putExtras(bundle)
                        startActivityForResult(intent, 2)
                        return@callback
                    }
                    if (deniedForever.isNotEmpty()) {
                        val dialog = context?.let { MaterialDialog(it) }
                        dialog?.title(R.string.title_permission)
                        dialog?.message(R.string.permission_rationale_camera_message)
                        dialog?.cancelOnTouchOutside(false)
                        dialog?.negativeButton(R.string.cancel_0) {
                            dialog?.dismiss()
                        }
                        dialog?.positiveButton(R.string.ok_0) {
                            com.blankj.utilcode.util.PermissionUtils.launchAppDetailsSettings()//打开应用具体设置
                        }
//                        dialog?.lifecycleOwner(this@WalletFragment)
                        dialog?.show()
                    }
                }
                .request()//开始请求
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
                        .putExtra("pageType", pageType)
                        .putExtra("coinType", coinType)
                        .putExtra("coinTag", coinTag), REQUEST_CODE
                )
            }

            R.id.tvAll -> {
                etTransferAmount.setText(coinAmount)
            }
            R.id.ivMore -> {
                if (rlGasPrice.isVisible) {
                    hideCustomize()
                    if (coinType == "BTC" || coinTag == "OMNI") {//ETH没有size,BTC有size
                        showSeekBarSize(mProgress)
                    }

                    if (coinType == "ETH" || coinTag == "ERC20") {
                        getEthFeeRate()
                    }

                } else {
                    showCustomize()
                }
            }
            R.id.tvMore -> {
                if (rlGasPrice.isVisible) {
                    hideCustomize()
                    if (coinType == "BTC" || coinTag == "OMNI") {//ETH没有size,BTC有size
                        showSeekBarSize(mProgress)
                    }

                    if (coinType == "ETH" || coinTag == "ERC20") {
                        getEthFeeRate()
                    }
                } else {
                    showCustomize()
                }
            }
            R.id.btnCommit -> {
                var transferAmount = etTransferAmount.text.toString().trim()
                if (BigDecimal(transferAmount).compareTo(BigDecimal.ZERO) < 0 || BigDecimal(
                        transferAmount
                    ).compareTo(BigDecimal.ZERO) == 0
                ) {//TODO 小数点不生效
                    ToastUtils.make().show(R.string.server_error102604)
                    return@OnClickListener
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

                if (coinTag == "OMNI") {
                    var coinRepository = CoinRepository(this)
                    var coin = coinRepository?.getCoinsByNameAndAddress("BTC", address)
                    if (BigDecimal(coin?.balance).subtract(kMoney) < BigDecimal.ZERO) {
                        ToastUtils.make().show(R.string.balance_is_not_enough)
                        return@OnClickListener
                    }
                }

                if (coinType == "BTC" || coinTag == "OMNI") {
                    if (BigDecimal(transferFeeRate) < BigDecimal.ONE) {
                        ToastUtils.make().show(R.string.fee_rate_cannot_be_lower_than_1)
                        return@OnClickListener
                    }

                }

                if (coinTag == "ERC20") {
                    var coinRepository = CoinRepository(this)
                    var coin = coinRepository?.getCoinsByNameAndAddress("ETH", address)
                    if (BigDecimal(coin?.balance).subtract(kMoney) < BigDecimal.ZERO) {
                        ToastUtils.make().show(R.string.balance_is_not_enough)
                        return@OnClickListener
                    }
                }

                if (address == toAddress) {
                    ToastUtils.make().show(R.string.receiving_address_same_as_transfer_address)
                    return@OnClickListener
                }
                if (coinType == "BTC" || coinTag == "OMNI") {//TODO ETH去掉此限制
                    var hasUnconfirmedTransaction = false
                    var recordRepository = RecordRepository(this@TransferActivity)
                    var recordsByAddress = recordRepository.getRecordsByAddressAndCointype(
                        address,
                        coinType
                    ) as MutableList<Record>?
                    recordsByAddress?.forEachIndexed { index, record ->
                        var timeMins = TimeUtils.getTimeSpan(
                            System.currentTimeMillis(),
                            record?.startTime!!,
                            TimeConstants.MIN
                        )
                        if (record?.address == address && record.to_address == toAddress && record.time == 0L && BigDecimal(
                                timeMins
                            ) < BigDecimal.TEN
                        ) {
                            hasUnconfirmedTransaction = true
                        }
                    }
                    if (hasUnconfirmedTransaction) {
                        ToastUtils.make().show(R.string.you_hava_an_unconfirmed_transaction)
                        return@OnClickListener
                    }
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
        if (coinType == "BTC" || coinTag == "OMNI") {
            validateAddressRequest.coinType = "BTC"
        }
        if (coinType == "ETH" || coinTag == "ERC20") {
            validateAddressRequest.coinType = "ETH"
        }

        Uv1Helper.validateAddress(this, validateAddressRequest,
            object : Uv1Helper.ResponseDataCallback<ValidateAddressResponse> {
                override fun onSuccess(data: ValidateAddressResponse?) {
                    if (data?.valid!!) {
                        transferInfo()
                    } else {
                        ToastUtils.make().show(R.string.address_illegal)
                    }
                }

                override fun onError(e: java.lang.Exception?) {
                    ToastUtils.make().show(R.string.address_illegal)
                }

            })
    }

    private fun showCustomize() {
        if (coinType == "BTC" || coinTag == "OMNI") {
            rlGas.visibility = GONE
            tvGasPriceUnit.text = "sat/b"
        }
        ivMore.setImageResource(R.mipmap.icon_more_on)
        rlGasPrice.visibility = VISIBLE
        tvGarPrice.text = "Gas Price"
        tvGasFeePrompt.visibility = VISIBLE
        if (coinType == "ETH" || coinTag == "ERC20") {
            rlGas.visibility = VISIBLE
            tvGasPriceUnit.text = "GWEI"
        }
    }

    private fun hideCustomize() {
        etAmount.setText("")
        ivMore.setImageResource(R.mipmap.icon_more_off)
        rlGasPrice.visibility = GONE
        rlGas.visibility = GONE
        tvGasFeePrompt.visibility = INVISIBLE

    }


    var toAddress: String? = ""
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == 3) {
            toAddress = data?.getStringExtra("address")
            etTransferAddress.setText(toAddress)
        }

        if (requestCode == 2 && resultCode == 1) {
            toAddress = data?.getStringExtra("address")
            etTransferAddress.setText(toAddress)
        }
    }

    /**
     * 考虑封装到dialogUtil中去
     */
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
        val memo = etContent.text.trim().toString()
        val transferAmount = etTransferAmount.text.trim().toString()
        val transferRequest = TransferRequest()
        transferRequest.toAddress = toAddress
        val transferFeeRateData = BigDecimal(transferFeeRate).longValueExact().toString()
        transferRequest.feeRate = transferFeeRateData
        transferRequest.keyId = SPUtils.getInstance().getString("walletId")
        transferRequest.keystoreDir = DirUtils.createKeyStoreDir()
        transferRequest.passphrase = pwdStr
        var dbTrasferAmount = "0"
        when (coinTag) {
            "OMNI" -> {
                transferRequest.coinType = "BTC"
                transferRequest.tokenType = coinType
                transferRequest.amount = transferAmount
                dbTrasferAmount = transferAmount
            }
            "ERC20" -> {
                transferRequest.coinType = "ETH"
                transferRequest.tokenType = coinType
                transferRequest.tokenAddress = contactAddress
                var ERC20Money = if (BigDecimal(transferAmount) == BigDecimal.ZERO) {
                    "0"
                } else {//循环除以10的精度次方
                    var money = transferAmount
                    for (i in 0 until decimals) {
                        money = BigDecimal(money).multiply(BigDecimal(10)).stripTrailingZeros()
                            .toPlainString()
                    }
                    money
                }
                transferRequest.amount = ERC20Money
                dbTrasferAmount = transferAmount
//                dbTrasferAmount = ERC20Money
//                transferRequest.amount = BigDecimal(transferAmount).multiply(BigDecimal(ETH_TO_WEI)).stripTrailingZeros().toPlainString()
//                dbTrasferAmount = BigDecimal(transferAmount).multiply(BigDecimal(ETH_TO_WEI)).stripTrailingZeros().toPlainString()

            }
            else -> {
                if (coinType == "ETH") {//ETH转账用wei做单位
                    transferRequest.amount =
                        BigDecimal(transferAmount).multiply(BigDecimal(ETH_TO_WEI))
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
            "toAddress==$toAddress;transferAmount==$transferAmount;coinType==$coinType;halfHourFee==$halfHourFee;keyId==$keyId;dir==${DirUtils.createKeyStoreDir()};pwdStr==$pwdStr"
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
                        val recordRepository = RecordRepository(this@TransferActivity)
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
                                memo = memo,
                                startTime = System.currentTimeMillis(),
                                gasPrice = transferFeeRateData, //新增
                                nonce = nonce//新增
                            )
                        )
                        finish()
                        ToastUtils.make().setDurationIsLong(true).show(R.string.submitted_seccessfully)
                    }

                    override fun onError(e: Exception?) {
                        LoadingDialog.cancel()
                        if (e?.localizedMessage == "insufficient funds available to construct transaction") {
                            ToastUtils.make().show(R.string.balance_is_not_enough)
                        } else {
                            ToastUtils.make().setDurationIsLong(true).show(R.string.failed_ti_submit)
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
                        val recordRepository = RecordRepository(this@TransferActivity)
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
                                memo = memo,
                                startTime = System.currentTimeMillis(),
                                gasPrice = transferFeeRateData,//新增
                                nonce = nonce//新增
                            )
                        )
                        finish()
                        ToastUtils.make().setDurationIsLong(true).show(R.string.submitted_seccessfully)
                    }

                    override fun onError(e: java.lang.Exception?) {
                        LoadingDialog.cancel()
                        if (e?.localizedMessage == "insufficient funds available to construct transaction") {
                            ToastUtils.make().show(R.string.balance_is_not_enough)
                        } else {
                            ToastUtils.make().setDurationIsLong(true).show(R.string.failed_ti_submit)
                        }
                    }

                })
        }
    }

    /**
     * 获取ETH的费率
     */
    private fun getEthFeeRate() {
        LoadingDialog.show(this)
        HttpApiHelper.getEthFeeRate(this,
        successC = { ethFeeRate->
            Log.e(tag(), "onResponse==${ethFeeRate.toString()}")
            var slow = ethFeeRate?.slow
            var standard = ethFeeRate?.standard
            var rapid = ethFeeRate?.rapid

            ethHourFee = BigDecimal(slow)//0.4
                .divide(BigDecimal(GWEI_ETH), 8, BigDecimal.ROUND_HALF_UP)
                .stripTrailingZeros().toPlainString()
            ethHalfHourFee = BigDecimal(standard)//0.6
                .divide(BigDecimal(GWEI_ETH), 8, BigDecimal.ROUND_HALF_UP)
                .stripTrailingZeros().toPlainString()
            ethFastestFee = BigDecimal(rapid)
                .divide(BigDecimal(GWEI_ETH), 8, BigDecimal.ROUND_HALF_UP)
                .stripTrailingZeros().toPlainString()
//                    tvSlower.text = "$ethHourFee GWEI\n${getString(R.string.forecast)}60${getString(R.string.minutes)}"
            tvSlower.text =
                "$ethHourFee GWEI\n${getString(R.string.forecast)}10${getString(R.string.minutes)}"
//                    tvRecommend.text = "$ethHalfHourFee GWEI\n${getString(R.string.forecast)}30${getString(R.string.minutes)}"
            tvRecommend.text =
                "$ethHalfHourFee GWEI\n${getString(R.string.forecast)}3${getString(R.string.minutes)}"
//                    tvFast.text = "$ethFastestFee GWEI\n${getString(R.string.forecast)}10${getString(R.string.minutes)}"
            tvFast.text =
                "$ethFastestFee GWEI\n${getString(R.string.forecast)}0.25${getString(R.string.minutes)}"

            when (mProgress) {
                0 -> {
                    kMoney = BigDecimal(ethHourFee!!).multiply(BigDecimal(21000))
                        .divide(BigDecimal(GWEI_ETH), 8, BigDecimal.ROUND_HALF_UP)
                    //矿工费
                    tvAbsenteeismCurrency.text =
                        StringBuilder().append(kMoney.stripTrailingZeros().toPlainString())
                            .append(" ETH")
//                            getRate(coinType)
                    getRate("ETH")
                    transferFeeRate =
                        BigDecimal(ethHourFee.toString()).multiply(BigDecimal(GWEI_ETH))
                            .stripTrailingZeros().toPlainString()
                }
                50 -> {
                    kMoney = BigDecimal(ethHalfHourFee!!).multiply(BigDecimal(21000))
                        .divide(BigDecimal(GWEI_ETH), 8, BigDecimal.ROUND_HALF_UP)
                    //矿工费
                    tvAbsenteeismCurrency.text =
                        StringBuilder().append(kMoney.stripTrailingZeros().toPlainString())
                            .append(" ETH")
//                            getRate(coinType)
                    getRate("ETH")
                    transferFeeRate =
                        BigDecimal(ethHalfHourFee.toString()).multiply(BigDecimal(GWEI_ETH))
                            .stripTrailingZeros().toPlainString()
                }
                100 -> {
                    kMoney = BigDecimal(ethFastestFee!!).multiply(BigDecimal(21000))
                        .divide(BigDecimal(GWEI_ETH), 8, BigDecimal.ROUND_HALF_UP)
                    //矿工费
                    tvAbsenteeismCurrency.text =
                        StringBuilder().append(kMoney.stripTrailingZeros().toPlainString())
                            .append(" ETH")
                    getRate("ETH")
//                            getRate(coinType)
                    transferFeeRate =
                        BigDecimal(ethFastestFee.toString()).multiply(BigDecimal(GWEI_ETH))
                            .stripTrailingZeros().toPlainString()
                }
            }
        },
        failureC = {errorMsg->
            Log.e(tag(), "onFailure==${errorMsg}")
            getETHTransactionFeeRate()
        })
    }


    var transferFeeRate: String? = "0"//转账需要传的费率
    var ethHourFee: String? = "0"
    var ethHalfHourFee: String? = "0"
    var ethFastestFee: String? = "0"

    /**
     * ETH转账获取费率
     */
    private fun getETHTransactionFeeRate() {
        val estimateEthGasPriceRequest = EstimateEthGasPriceRequest()
        Uv1Helper.estimateEthGasPrice(
            this,
            estimateEthGasPriceRequest,
            object : Uv1Helper.ResponseDataCallback<EstimateEthGasPriceResponse> {
                override fun onSuccess(data: EstimateEthGasPriceResponse?) {
                    runOnUiThread {
                        if (StringUtils.isEmpty(data?.feeRate)) {
                            noData("ETH")
                        } else {
                            val feeRate: String = data?.feeRate!!
                            ethHourFee = BigDecimal(feeRate).multiply(BigDecimal(0.7))//0.4
                                .divide(BigDecimal(AppConst.GWEI_ETH), 8, BigDecimal.ROUND_HALF_UP)
                                .stripTrailingZeros().toPlainString()
                            ethHalfHourFee = BigDecimal(feeRate).multiply(BigDecimal(0.85))//0.6
                                .divide(BigDecimal(AppConst.GWEI_ETH), 8, BigDecimal.ROUND_HALF_UP)
                                .stripTrailingZeros().toPlainString()
                            ethFastestFee = BigDecimal(feeRate).multiply(BigDecimal(1))
                                .divide(BigDecimal(AppConst.GWEI_ETH), 8, BigDecimal.ROUND_HALF_UP)
                                .stripTrailingZeros().toPlainString()

                            tvSlower.text = StringBuilder().append("$ethHourFee GWEI\n")
                                .append("${getString(R.string.forecast)}10${getString(R.string.minutes)}")

                            tvRecommend.text = StringBuilder().append("$ethHalfHourFee GWEI\n")
                                .append("${getString(R.string.forecast)}3${getString(R.string.minutes)}")

                            tvFast.text = StringBuilder().append("$ethFastestFee GWEI\n")
                                .append("${getString(R.string.forecast)}0.25${getString(R.string.minutes)}")

                            kMoney = BigDecimal(ethHalfHourFee!!).multiply(BigDecimal(21000))
                                .divide(BigDecimal(AppConst.GWEI_ETH), 8, BigDecimal.ROUND_HALF_UP)
                            //矿工费
                            tvAbsenteeismCurrency.text =
                                StringBuilder().append(kMoney.stripTrailingZeros().toPlainString())
                                    .append(" ETH")
                            getRate("ETH")
//                            getRate(coinType)
                            transferFeeRate =
                                BigDecimal(ethHalfHourFee.toString()).multiply(BigDecimal(AppConst.GWEI_ETH))
                                    .stripTrailingZeros().toPlainString()
                        }
                    }
                }

                override fun onError(e: Exception?) {
                    LoadingDialog.cancel()
                    noData("ETH")
                }

            })
    }

    /**
     * BTC交易获取到size
     */
    var size: Long? = 0
    private fun getEstimateTransactionSize() {
        val estimateTransactionSizeRequest = EstimateTransactionSizeRequest()
        estimateTransactionSizeRequest.address = address
        Uv1Helper.estimateTransactionSize(this, estimateTransactionSizeRequest,
            object : Uv1Helper.ResponseDataCallback<EstimateTransactionSizeResponse> {
                override fun onSuccess(data: EstimateTransactionSizeResponse?) {
                    size = data?.size
                }

                override fun onError(e: Exception?) {
                }
            })
    }


    var hourFee: String? = "0"
    var halfHourFee: String? = "0"
    var fastestFee: String? = "0"

    /**
     * BTC转账获取费率
     */
    private fun getBTCTransactionFeeRate() {

        val transactionFeeRateRequest = TransactionFeeRateRequest()
//        transactionFeeRateRequest.coinType = coinType //TODO
        if (coinTag == "OMNI") {
            transactionFeeRateRequest.coinType = "BTC"
        } else {
            transactionFeeRateRequest.coinType = coinType
        }
        Uv1Helper.transactionFeeRate(this, transactionFeeRateRequest,
            object : Uv1Helper.ResponseDataCallback<TransactionFeeRateResponse> {
                override fun onSuccess(data: TransactionFeeRateResponse?) {
                    runOnUiThread {
                        hourFee = data?.hourFee.toString()
                        halfHourFee = data?.halfHourFee.toString()
                        fastestFee = data?.fastestFee.toString()
                        tvSlower.text =
                            "$hourFee sat/b\n${getString(R.string.forecast)}60${getString(R.string.minutes)}"
                        tvRecommend.text =
                            "$halfHourFee sat/b\n${getString(R.string.forecast)}30${getString(R.string.minutes)}"
                        tvFast.text =
                            "$fastestFee sat/b\n${getString(R.string.forecast)}10${getString(R.string.minutes)}"
                        kMoney = if (size == 0L) {
                            BigDecimal(0)
                        } else {
                            BigDecimal(halfHourFee).multiply(BigDecimal(size.toString()))
                                .divide(BigDecimal(SAT_BTC), 8, BigDecimal.ROUND_HALF_UP)
                        }
                        //矿工费

                        tvAbsenteeismCurrency.text =
                            StringBuilder().append(kMoney.stripTrailingZeros().toPlainString())
                                .append(" BTC")
                        transferFeeRate = halfHourFee

                    }
                    getRate("BTC")
                }

                override fun onError(e: java.lang.Exception?) {

                    runOnUiThread {
                        noData("BTC")
                    }
                    Log.e("sdk_erroe_info", e?.localizedMessage!!)
                }

            })
    }

    /**
     * 无网络或无数据的情况,显示的页面
     */
    private fun noData(coinType: String) {
        when (coinType) {
            "BTC" -> {
                tvSlower.text =
                    "0 sat/b\n${getString(R.string.forecast)}60${getString(R.string.minutes)}"
                tvRecommend.text =
                    "0 sat/b\n${getString(R.string.forecast)}30${getString(R.string.minutes)}"
                tvFast.text =
                    "0 sat/b\n${getString(R.string.forecast)}10${getString(R.string.minutes)}"
                tvAbsenteeismCurrency.text = "0 BTC"
            }
            "ETH" -> {
                tvSlower.text =
                    "0 GWEI\n${getString(R.string.forecast)}10${getString(R.string.minutes)}"
                tvRecommend.text =
                    "0 GWEI\n${getString(R.string.forecast)}3${getString(R.string.minutes)}"
                tvFast.text =
                    "0 GWEI\n${getString(R.string.forecast)}0.25${getString(R.string.minutes)}"
                tvAbsenteeismCurrency.text = "0 ETH"
            }
            else -> {
                if (coinTag == "ERC20") {
                    tvSlower.text =
                        "0 GWEI\n${getString(R.string.forecast)}10${getString(R.string.minutes)}"
                    tvRecommend.text =
                        "0 GWEI\n${getString(R.string.forecast)}3${getString(R.string.minutes)}"
                    tvFast.text =
                        "0 GWEI\n${getString(R.string.forecast)}0.25${getString(R.string.minutes)}"
                    tvAbsenteeismCurrency.text = "0 ETH"
                } else {
                    tvSlower.text =
                        "0 sat/b\n${getString(R.string.forecast)}60${getString(R.string.minutes)}"
                    tvRecommend.text =
                        "0 sat/b\n${getString(R.string.forecast)}30${getString(R.string.minutes)}"
                    tvFast.text =
                        "0 sat/b\n${getString(R.string.forecast)}10${getString(R.string.minutes)}"
                    tvAbsenteeismCurrency.text = "0 BTC"
                }

            }
        }
    }


    /**
     * 将币资产转换成相应的人民币、美元或者欧元等
     */
    private fun getRate(coinType: String) {
        val queryList = mutableListOf<String>()
        queryList.add(0, coinType)
            HttpApiHelper.tokenTicker(this, queryList,
                successC = { httpExchangeRateBean ->
                    LoadingDialog.cancel()
                    Log.e(tag(), "onResponse==${httpExchangeRateBean}")
                    var list = httpExchangeRateBean?.list
                    if (list.isNullOrEmpty()) return@tokenTicker
                    var exchangeRateBean = list[0]
                    when (SPUtils.getInstance().getString("unit")) {
                        "CNY" -> {
                            var priceCny: String? = exchangeRateBean.price_cny
                            var money = if (kMoney < BigDecimal.ZERO || kMoney == BigDecimal.ZERO) {
                                "0"
                            } else {
                                BigDecimal(priceCny).multiply(kMoney)
                                    .setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros()
                                    .toPlainString()
                            }
                            Log.e("afgasfdg", "money==${money};kMoney==$kMoney")
                            tvAbsenteeismRmb.text = "￥$money"
                        }
                        "USD" -> {
                            var priceUsd: String? = exchangeRateBean.price_usd

                            var money = if (kMoney < BigDecimal.ZERO || kMoney == BigDecimal.ZERO) {
                                "0"
                            } else {
                                BigDecimal(priceUsd).multiply(kMoney)
                                    .setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros()
                                    .toPlainString()
                            }

                            tvAbsenteeismRmb.text = "$$money"
                        }
                        "EUR" -> {
                            var priceEur: String? = exchangeRateBean.price_eur
                            var money = if (kMoney < BigDecimal.ZERO || kMoney == BigDecimal.ZERO) {
                                "0"
                            } else {
                                BigDecimal(priceEur).multiply(kMoney)
                                    .setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros()
                                    .toPlainString()
                            }
                            tvAbsenteeismRmb.text = "€$money"
                        }
                        else -> {
                            var priceCny: String? = exchangeRateBean.price_cny
                            var money = if (kMoney < BigDecimal.ZERO || kMoney == BigDecimal.ZERO) {
                                "0"
                            } else {
                                BigDecimal(priceCny).multiply(kMoney)
                                    .setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros()
                                    .toPlainString()
                            }
                            Log.e("afgasfdg", "money==${money};kMoney==$kMoney")
                            tvAbsenteeismRmb.text = "￥$money"
                        }
                    }
                },
                failureC = { errorMsg ->
                    LoadingDialog.cancel()
                })

    }


    private fun transferInfo() {
        var transferAmount = etTransferAmount.text.trim().toString()
        var memo = etContent.text.trim().toString()

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

                    if (coinType == "BTC" || coinTag == "OMNI") { //矿工费
                        tvKFee?.text = StringBuilder().append(kMoney.stripTrailingZeros().toPlainString()).append(" BTC")
                    }
                    if (coinType == "ETH" || coinTag == "ERC20") {
                        tvKFee?.text = StringBuilder().append(kMoney.stripTrailingZeros().toPlainString()).append(" ETH")
                    }
                    tvMemo?.text = memo
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

    private fun noMoney() {
        tvAllAmount.text = "${getString(R.string.assets)} 0 $coinType"
        tvAbsenteeismCurrency.text = "0$coinType"
        when (SPUtils.getInstance().getString("unit")) {
            "CNY" -> {
                tvAbsenteeismRmb.text = "￥0"
            }
            "USD" -> {
                tvAbsenteeismRmb.text = "$0"
            }
            "EUR" -> {
                tvAbsenteeismRmb.text = "€0"
            }
            else -> {
                tvAbsenteeismRmb.text = "￥0"
            }
        }

        /**
         * BTC费率和矿工费计算
         */
        if (coinType == "BTC" || coinTag == "OMNI") {//ETH没有size,BTC有size
            tvSlower.text =
                "10 sat/b\n${getString(R.string.forecast)}60${getString(R.string.minutes)}"
            tvRecommend.text =
                "20 sat/b\n${getString(R.string.forecast)}30${getString(R.string.minutes)}"
            tvFast.text =
                "30 sat/b\n${getString(R.string.forecast)}10${getString(R.string.minutes)}"
        }

        /**
         *  ETH费率和矿工费计算
         */
        if (coinType == "ETH" || coinTag == "ERC20") {
            tvSlower.text =
                "10 GWEI\n${getString(R.string.forecast)}60${getString(R.string.minutes)}"
            tvRecommend.text =
                "20 GWEI\n${getString(R.string.forecast)}30${getString(R.string.minutes)}"
            tvFast.text = "30 GWEI\n${getString(R.string.forecast)}10${getString(R.string.minutes)}"
        }
    }


}
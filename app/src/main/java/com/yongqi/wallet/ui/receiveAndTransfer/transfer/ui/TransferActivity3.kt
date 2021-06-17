//package com.yongqi.wallet.ui.receiveAndTransfer.transfer.ui
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.content.Context
//import android.content.Intent
//import android.os.Bundle
//import android.text.InputType
//import android.text.Selection
//import android.util.Log
//import android.view.Gravity
//import android.view.View
//import android.widget.CheckBox
//import android.widget.EditText
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.core.view.isVisible
//import androidx.core.widget.addTextChangedListener
//import api.*
//import com.afollestad.materialdialogs.MaterialDialog
//import com.afollestad.materialdialogs.customview.customView
//import com.afollestad.materialdialogs.customview.getCustomView
//import com.blankj.utilcode.constant.TimeConstants
//import com.blankj.utilcode.util.*
//import com.gyf.immersionbar.OnKeyboardListener
//import com.gyf.immersionbar.ktx.immersionBar
//import com.shehuan.nicedialog.BaseNiceDialog
//import com.shehuan.nicedialog.NiceDialog
//import com.shehuan.nicedialog.ViewConvertListener
//import com.shehuan.nicedialog.ViewHolder
//import com.xw.repo.BubbleSeekBar
//import com.yongqi.wallet.R
//import com.yongqi.wallet.api.NetApi
//import com.yongqi.wallet.base.BaseActivity2
//import com.yongqi.wallet.bean.EthFeeRate
//import com.yongqi.wallet.bean.HttpExchangeRateBean
//import com.yongqi.wallet.bean.Record
//import com.yongqi.wallet.bean.Wallet
//import com.yongqi.wallet.config.AppConst
//import com.yongqi.wallet.databinding.ActivityTransfer3Binding
//import com.yongqi.wallet.db.coin.CoinRepository
//import com.yongqi.wallet.db.record.RecordRepository
//import com.yongqi.wallet.db.wallet.WalletRepository
//import com.yongqi.wallet.net.APIClient
//import com.yongqi.wallet.net.APIResponse
//import com.yongqi.wallet.ui.scan.ui.ScanQrActivity
//import com.yongqi.wallet.utils.*
//import com.yongqi.wallet.utils.StringUtils
//import io.reactivex.Observer
//import io.reactivex.android.schedulers.AndroidSchedulers
//import io.reactivex.disposables.Disposable
//import io.reactivex.schedulers.Schedulers
//import kotlinx.android.synthetic.main.activity_transfer.*
//import kotlinx.android.synthetic.main.activity_transfer3.*
//import kotlinx.android.synthetic.main.activity_transfer3.btnCommit
//import kotlinx.android.synthetic.main.activity_transfer3.etAmount
//import kotlinx.android.synthetic.main.activity_transfer3.etContent
//import kotlinx.android.synthetic.main.activity_transfer3.etTransferAddress
//import kotlinx.android.synthetic.main.activity_transfer3.etTransferAmount
//import kotlinx.android.synthetic.main.activity_transfer3.iTitle
//import kotlinx.android.synthetic.main.activity_transfer3.ivMore
//import kotlinx.android.synthetic.main.activity_transfer3.ivTransferAddress
//import kotlinx.android.synthetic.main.activity_transfer3.rlGas
//import kotlinx.android.synthetic.main.activity_transfer3.rlGasPrice
//import kotlinx.android.synthetic.main.activity_transfer3.sbProgress
//import kotlinx.android.synthetic.main.activity_transfer3.tvAbsenteeismCurrency
//import kotlinx.android.synthetic.main.activity_transfer3.tvAbsenteeismRmb
//import kotlinx.android.synthetic.main.activity_transfer3.tvAll
//import kotlinx.android.synthetic.main.activity_transfer3.tvAllAmount
//import kotlinx.android.synthetic.main.activity_transfer3.tvFast
//import kotlinx.android.synthetic.main.activity_transfer3.tvGarPrice
//import kotlinx.android.synthetic.main.activity_transfer3.tvGasFeePrompt
//import kotlinx.android.synthetic.main.activity_transfer3.tvGasPriceUnit
//import kotlinx.android.synthetic.main.activity_transfer3.tvMore
//import kotlinx.android.synthetic.main.activity_transfer3.tvRecommend
//import kotlinx.android.synthetic.main.activity_transfer3.tvSlower
//import kotlinx.android.synthetic.main.common_title.*
//import sdk.Sdk
//import uv1.Uv1
//import java.lang.StringBuilder
//import java.math.BigDecimal
//
///**
// * 考虑用MVVM架构来修改该页面:
// * https://mp.weixin.qq.com/s/t1VBFZSOrzfxjxbfELF2Ng
// *
// */
//class TransferActivity3 : BaseActivity2<ActivityTransfer3Binding>() {
//    override fun getLayoutResource(): Int = R.layout.activity_transfer3
//
//    companion object {
//        const val REQUEST_CODE = 1
//        const val pageType = 1
//    }
//
//    var address = ""//币的地址
//    var coinAmount = ""//币资产
//    var coinType = ""//币名字
//    var keyId = ""//币的keyId
//    var coinTag = ""////判断该币是否是OMNI或ERC20的代币
//
//    var kMoney: BigDecimal = BigDecimal("0")
//    var mProgress = 50
//    override fun initData() {
//        immersionBar {
//            keyboardEnable(true)
//            statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
//            titleBar(iTitle) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
//            setOnKeyboardListener { isPopup, _ ->
//                //防止系统键盘将布局顶上去
//                if (isPopup) {
//                    btnCommit.visibility = View.GONE
//                } else {
//                    btnCommit.visibility = View.VISIBLE
//                }
//            }
//        }
//        //设置小数点最多8位
//        EditTextUtils.setPointWithInteger(etTransferAmount, 8, 20)
//
//        address = intent.getStringExtra("address").toString()
//        coinAmount = intent.getStringExtra("coinAmount").toString()
//        coinType = intent.getStringExtra("coinType").toString()
//        keyId = intent.getStringExtra("keyId").toString()
//        coinTag = intent.getStringExtra("coinTag").toString()
//        ivBack.setOnClickListener(onClickListener)
//
//        if (coinTag.isEmpty()) {
//            tvTitle.text = "$coinType ${getString(R.string.transfer)}"
//        } else {
//            tvTitle.text = "$coinType ($coinTag) ${getString(R.string.transfer)}"
//        }
//
//        ivRightIcon.visibility = View.VISIBLE
//        ivRightIcon.setImageResource(R.mipmap.icon_sys_h)
//        ivRightIcon.setOnClickListener(onClickListener)
//        ivTransferAddress.setOnClickListener(onClickListener)
//        tvAll.setOnClickListener(onClickListener)
//        ivMore.setOnClickListener(onClickListener)
//        tvMore.setOnClickListener(onClickListener)
//        btnCommit.setOnClickListener(onClickListener)
//        tvAllAmount.text = "${getString(R.string.assets)} $coinAmount $coinType"
//
//        etTransferAmount.addTextChangedListener {
//            var transferAmount = etTransferAmount.text.trim().toString()
//            var transferAddress = etTransferAddress.text.trim().toString()
//            btnCommit.isEnabled =
//                !transferAmount.isNullOrEmpty() && !transferAddress.isNullOrEmpty()
//        }
//
//        etTransferAddress.addTextChangedListener {
//            var transferAmount = etTransferAmount.text.trim().toString()
//            var transferAddress = etTransferAddress.text.trim().toString()
//            btnCommit.isEnabled =
//                !transferAmount.isNullOrEmpty() && !transferAddress.isNullOrEmpty()
//            if (!transferAddress.isNullOrEmpty()) {
//                toAddress = transferAddress
//            }
//        }
//        /**
//         * BTC费率和矿工费计算
//         */
//        if (coinType == "BTC" || coinTag == "OMNI") {//ETH没有size,BTC有size
//            noData("BTC")
//            //getEstimateTransactionSize()
//            getBTCTransactionFeeRate()
//        }
//
//        /**
//         *  ETH费率和矿工费计算
//         */
//        if (coinType == "ETH" || coinTag == "ERC20") {
//            noData("ETH")
//            getEthFeeRate()
//
//        }
//
//        /**
//         * seekBar滑动后:BTC、ETH矿工费计算
//         */
//        sbProgress.onProgressChangedListener =
//            object : BubbleSeekBar.OnProgressChangedListenerAdapter() {
//                override fun getProgressOnFinally(
//                    bubbleSeekBar: BubbleSeekBar?,
//                    progress: Int,
//                    progressFloat: Float,
//                    fromUser: Boolean
//                ) {
//
//                    hideCustomize()
//                    mProgress = progress
//                    if (coinType == "BTC" || coinTag == "OMNI") {//ETH没有size,BTC有size
//                        showSeekBarSize(mProgress)
//                    }
//
//                    if (coinType == "ETH" || coinTag == "ERC20") {
//                        getEthFeeRate()
//                    }
//                }
//            }
//
//        etAmount.addTextChangedListener {//TODO 自定义
//            val amount = etAmount.text.toString().trim()
//            if (amount.isNullOrEmpty() || amount.startsWith(".")) {//|| BigDecimal(amount) <= BigDecimal.ZERO
//                tvGasFeePrompt.visibility = View.INVISIBLE
//                return@addTextChangedListener
//            }
//            if (coinType == "BTC" || coinTag == "OMNI") {
//                kMoney = if (size == 0L) {
//                    BigDecimal(0)
//                } else {
//                    BigDecimal(amount).multiply(BigDecimal(size.toString()))
//                        .divide(BigDecimal(AppConst.SAT_BTC), 8, BigDecimal.ROUND_HALF_UP)
//                }
//                if (BigDecimal(amount) == BigDecimal.ZERO || amount == "0.") {
//                    kMoney = BigDecimal(0)
//                }
//
//                //矿工费
//                tvAbsenteeismCurrency.text = "$kMoney BTC"
//                getRate("BTC")
//                transferFeeRate = BigDecimal(amount).toString()
//
//                when {
//                    BigDecimal(hourFee).subtract(BigDecimal(amount)) > BigDecimal.ZERO -> {
//                        tvGasFeePrompt.visibility = View.VISIBLE
//                        tvGasFeePrompt.text = getString(R.string.miner_fee_rate_is_too_low)
//                    }
//                    BigDecimal(amount).subtract(BigDecimal(fastestFee)) > BigDecimal.ZERO -> {
//                        tvGasFeePrompt.visibility = View.VISIBLE
//                        tvGasFeePrompt.text = getString(R.string.miner_fee_rate_is_too_high)
//                    }
//                    else -> {
//                        tvGasFeePrompt.visibility = View.INVISIBLE
//                    }
//                }
//            }
//            if (coinType == "ETH" || coinTag == "ERC20") {
//                kMoney = BigDecimal(amount).multiply(BigDecimal(21000))
//                    .divide(BigDecimal(AppConst.GWEI_ETH), 8, BigDecimal.ROUND_HALF_UP)
//
//                if (BigDecimal(amount) == BigDecimal.ZERO || amount == "0.") {
//                    kMoney = BigDecimal(0)
//                }
//                //矿工费
//                tvAbsenteeismCurrency.text = "$kMoney ETH"
//                getRate(coinType)
//                transferFeeRate =
//                    BigDecimal(amount).multiply(BigDecimal(AppConst.GWEI_ETH)).toString()
//
//                when {
//                    BigDecimal(ethHourFee).subtract(BigDecimal(amount)) > BigDecimal.ZERO -> {
//                        tvGasFeePrompt.visibility = View.VISIBLE
//                        tvGasFeePrompt.text = getString(R.string.miner_fee_rate_is_too_low)
//                    }
//                    BigDecimal(amount).subtract(BigDecimal(ethFastestFee)) > BigDecimal.ZERO -> {
//                        tvGasFeePrompt.visibility = View.VISIBLE
//                        tvGasFeePrompt.text = getString(R.string.miner_fee_rate_is_too_high)
//                    }
//                    else -> {
//                        tvGasFeePrompt.visibility = View.INVISIBLE
//                    }
//                }
//
//            }
//        }
//    }
//
//    /**
//     * 根据滑块显示矿工费
//     */
//    private fun showSeekBarSize(mProgress: Int) {
//        when (mProgress) {
//            0 -> {
//                if (coinType == "BTC" || coinTag == "OMNI") {//ETH没有size,BTC有size
//                    if (hourFee == "0") return
//                    transferFeeRate = hourFee
//                    kMoney = if (size == 0L) {
//                        BigDecimal(0)
//                    } else {
//                        BigDecimal(hourFee.toString()).multiply(BigDecimal(size.toString()))
//                            .divide(BigDecimal(AppConst.SAT_BTC), 8, BigDecimal.ROUND_HALF_UP)
//                    }
//                }
//            }
//            50 -> {
//                if (coinType == "BTC" || coinTag == "OMNI") {//ETH没有size,BTC有size
//                    if (halfHourFee == "0") return
//                    transferFeeRate = halfHourFee
//                    kMoney = if (size == 0L) {
//                        BigDecimal(0)
//                    } else {
//                        BigDecimal(halfHourFee.toString()).multiply(BigDecimal(size.toString()))
//                            .divide(BigDecimal(AppConst.SAT_BTC), 8, BigDecimal.ROUND_HALF_UP)
//                    }
//                }
//
//            }
//            100 -> {
//                if (coinType == "BTC" || coinTag == "OMNI") {//ETH没有size,BTC有size
//                    if (fastestFee == "0") return
//                    transferFeeRate = fastestFee
//                    kMoney = if (size == 0L) {
//                        BigDecimal(0)
//                    } else {
//                        BigDecimal(fastestFee.toString()).multiply(BigDecimal(size.toString()))
//                            .divide(BigDecimal(AppConst.SAT_BTC), 8, BigDecimal.ROUND_HALF_UP)
//                    }
//                }
//            }
//        }
//        if (coinType == "BTC" || coinTag == "OMNI") {//ETH没有size,BTC有size
//            //矿工费
//            tvAbsenteeismCurrency.text = "$kMoney BTC"
//            getRate("BTC")
//        }
//    }
//
//    private fun requestCameraPermissions(context: Context) {
//        if (com.blankj.utilcode.util.PermissionUtils.isGranted(
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.CAMERA
//            )
//        ) {//判断权限是否被授予
//            val bundle = Bundle();
//            bundle.putString("intent_type", "intent_type_transfer");
//            val intent = Intent(context, ScanQrActivity::class.java)
//            intent.putExtras(bundle)
//            startActivityForResult(intent, 2)
//        } else {
//            com.blankj.utilcode.util.PermissionUtils
//                .permission(
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.CAMERA
//                )//设置请求权限
////                .permissionGroup(PermissionConstants.CALENDAR)//设置请求权限组
////                .explain { activity, denied, shouldRequest ->//设置权限请求前的解释
////                }
//
//                .callback { isAllGranted, granted, deniedForever, denied ->
//                    LogUtils.d(granted, deniedForever, denied)
//
//                    if (isAllGranted) {
//                        val bundle = Bundle();
//                        bundle.putString("intent_type", "intent_type_transfer");
//                        val intent = Intent(context, ScanQrActivity::class.java)
//                        intent.putExtras(bundle)
//                        startActivityForResult(intent, 2)
//                        return@callback
//                    }
//                    if (deniedForever.isNotEmpty()) {
//                        val dialog = context?.let { MaterialDialog(it) }
//                        dialog?.title(R.string.title_permission)
//                        dialog?.message(R.string.permission_rationale_camera_message)
//                        dialog?.cancelOnTouchOutside(false)
//                        dialog?.negativeButton(R.string.cancel_0) {
//                            dialog?.dismiss()
//                        }
//                        dialog?.positiveButton(R.string.ok_0) {
//                            com.blankj.utilcode.util.PermissionUtils.launchAppDetailsSettings()//打开应用具体设置
//                        }
////                        dialog?.lifecycleOwner(this@WalletFragment)
//                        dialog?.show()
//                    }
//                }
//                .request()//开始请求
//        }
//    }
//
//    /**
//     * BTC交易获取到size
//     */
//    var size: Long? = 0
//    fun getEstimateTransactionSize(address: String) {
//        var estimateTransactionSizeRequest = EstimateTransactionSizeRequest()
//        estimateTransactionSizeRequest.address = address
//        try {
//            val estimateTransactionSizeResponse: EstimateTransactionSizeResponse =
//                Uv1.estimateTransactionSize(estimateTransactionSizeRequest)
//            size = estimateTransactionSizeResponse.size
//        } catch (e: Exception) {
//            Log.e("sdk_error_info", e.localizedMessage!!)
//        }
//    }
//
//    private val onClickListener = View.OnClickListener {
//        when (it.id) {
//            R.id.ivBack -> finish()
//            R.id.ivRightIcon -> {
//                requestCameraPermissions(this)
//            }
//            R.id.ivTransferAddress -> {
//                startActivityForResult(
//                    Intent(this, AddressBookActivity::class.java)
//                        .putExtra("pageType", pageType)
//                        .putExtra("coinType", coinType), REQUEST_CODE
//                )
//            }
//
//            R.id.tvAll -> {
//                etTransferAmount.setText(coinAmount)
//            }
//            R.id.ivMore -> {
//                if (rlGasPrice.isVisible) {
//                    hideCustomize()
//                    if (coinType == "BTC" || coinTag == "OMNI") {//ETH没有size,BTC有size
//                        showSeekBarSize(mProgress)
//                    }
//
//                    if (coinType == "ETH" || coinTag == "ERC20") {
//                        getEthFeeRate()
//                    }
//
//                } else {
//                    showCustomize()
//                }
//            }
//            R.id.tvMore -> {
//                if (rlGasPrice.isVisible) {
//                    hideCustomize()
//                    if (coinType == "BTC" || coinTag == "OMNI") {//ETH没有size,BTC有size
//                        showSeekBarSize(mProgress)
//                    }
//
//                    if (coinType == "ETH" || coinTag == "ERC20") {
//                        getEthFeeRate()
//                    }
//                } else {
//                    showCustomize()
//                }
//            }
//            R.id.btnCommit -> {
//                var transferAmount = etTransferAmount.text.toString().trim()
//                if (BigDecimal(transferAmount) < BigDecimal.ZERO || BigDecimal(transferAmount) == BigDecimal.ZERO) {
//                    ToastUtils.make().setGravity(Gravity.TOP, 0, 100)
//                        .show(R.string.server_error102604)
//                    return@OnClickListener
//                }
//
//                if (coinType == "BTC" || coinType == "ETH") {
//                    var transferMoney = BigDecimal(transferAmount).add(kMoney)
//                    if (transferMoney.subtract(BigDecimal(coinAmount)) > BigDecimal.ZERO) {
//                        ToastUtils.make().setGravity(Gravity.TOP, 0, 100)
//                            .show(R.string.insufficient_current_balance)
//                        return@OnClickListener
//                    }
//                } else {
//                    if (BigDecimal(transferAmount).subtract(BigDecimal(coinAmount)) > BigDecimal.ZERO) {
//                        ToastUtils.make().setGravity(Gravity.TOP, 0, 100)
//                            .show(R.string.insufficient_current_balance)
//                        return@OnClickListener
//                    }
//                }
//
//                if (coinTag == "OMNI") {
//                    var coinRepository = CoinRepository(this)
//                    var coin = coinRepository?.getCoinsByNameAndAddress("BTC", address)
//                    if (BigDecimal(coin?.balance).subtract(kMoney) < BigDecimal.ZERO) {
//                        ToastUtils.make().setGravity(Gravity.TOP, 0, 100)
//                            .show(R.string.balance_is_not_enough)
//                        return@OnClickListener
//                    }
//                }
//
//                if (coinType == "BTC" || coinTag == "OMNI") {
//                    if (BigDecimal(transferFeeRate) < BigDecimal.ONE) {
//                        ToastUtils.make().setGravity(Gravity.TOP, 0, 100)
//                            .show(R.string.fee_rate_cannot_be_lower_than_1)
//                        return@OnClickListener
//                    }
//                }
//
//                if (coinTag == "ERC20") {
//                    var coinRepository = CoinRepository(this)
//                    var coin = coinRepository?.getCoinsByNameAndAddress("ETH", address)
//                    if (BigDecimal(coin?.balance).subtract(kMoney) < BigDecimal.ZERO) {
//                        ToastUtils.make().setGravity(Gravity.TOP, 0, 100)
//                            .show(R.string.balance_is_not_enough)
//                        return@OnClickListener
//                    }
//                }
//
//                if (address == toAddress) {
//                    ToastUtils.make().setGravity(Gravity.TOP, 0, 100)
//                        .show(R.string.receiving_address_same_as_transfer_address)
//                    return@OnClickListener
//                }
//
//                var hasUnconfirmedTransaction = false
//                val recordRepository = RecordRepository(this@TransferActivity3)
//                val recordsByAddress = recordRepository.getRecordsByAddress(address)
//                recordsByAddress?.forEachIndexed { index, record ->
//                    val timeMins = TimeUtils.getTimeSpan(
//                        System.currentTimeMillis(),
//                        record?.startTime!!,
//                        TimeConstants.MIN
//                    )
//                    if (record.address == address && record.to_address == toAddress && record.time == 0L && BigDecimal(
//                            timeMins
//                        ) < BigDecimal.TEN
//                    ) {
//                        hasUnconfirmedTransaction = true
//                    }
//                }
//                if (hasUnconfirmedTransaction) {
//                    ToastUtils.make().setGravity(Gravity.TOP, 0, 100)
//                        .show(R.string.you_hava_an_unconfirmed_transaction)
//                    return@OnClickListener
//                }
//
//                /**
//                 * 校验输入的地址是否合法
//                 */
//                var validateAddressRequest = ValidateAddressRequest()
//                validateAddressRequest.address = toAddress
//                if (coinType == "BTC" || coinTag == "OMNI") {
//                    validateAddressRequest.coinType = "BTC"
//                }
//                if (coinType == "ETH" || coinTag == "ERC20") {
//                    validateAddressRequest.coinType = "ETH"
//                }
//                Sdk.validateAddress(validateAddressRequest, object : ValidateAddressCallback {
//                    override fun failure(err: java.lang.Exception?) {
//                        Log.e("validateAddress", "err==${err.toString()}")
//                        ToastUtils.make().setGravity(Gravity.TOP, 0, 100)
//                            .show(R.string.address_illegal)
//                    }
//
//                    override fun success(p0: ValidateAddressResponse?) {
//                        Log.e("validateAddress", "p0==${p0.toString()}")
//                        var valid = p0?.valid
//                        if (valid!!) {
//                            transferInfo()
//                        } else {
//                            ToastUtils.make().setGravity(Gravity.TOP, 0, 100)
//                                .show(R.string.address_illegal)
//                        }
//                    }
//                })
//            }
//        }
//    }
//
//    private fun showCustomize() {
//        if (coinType == "BTC" || coinTag == "OMNI") {
//            rlGas.visibility = View.GONE
//            tvGasPriceUnit.text = "sat/b"
//        }
//        ivMore.setImageResource(R.mipmap.icon_more_on)
//        rlGasPrice.visibility = View.VISIBLE
//        tvGarPrice.text = "Gas Price"
//        tvGasFeePrompt.visibility = View.VISIBLE
//        if (coinType == "ETH" || coinTag == "ERC20") {
//            rlGas.visibility = View.VISIBLE
//            tvGasPriceUnit.text = "GWEI"
//        }
//    }
//
//    private fun hideCustomize() {
//        etAmount.setText("")
//        ivMore.setImageResource(R.mipmap.icon_more_off)
//        rlGasPrice.visibility = View.GONE
//        rlGas.visibility = View.GONE
//        tvGasFeePrompt.visibility = View.INVISIBLE
//
//    }
//
//
//    var toAddress: String? = ""
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_CODE && resultCode == 3) {
//            toAddress = data?.getStringExtra("address")
//            etTransferAddress.setText(toAddress)
//        }
//
//        if (requestCode == 2 && resultCode == 1) {
//            toAddress = data?.getStringExtra("address")
//            etTransferAddress.setText(toAddress)
//        }
//    }
//
//    /**
//     * 考虑封装到dialogUtil中去
//     */
//    var pwdStr = ""
//    private fun showCommonDialog() {
//        val dialog = MaterialDialog(this)
//            .customView(R.layout.input_pwd_dialog)
//        dialog.cancelOnTouchOutside(false)
//        val customView = dialog.getCustomView()
//// Use the view instance, e.g. to set values or setup listeners
//        val ivClose = customView.findViewById<ImageView>(R.id.ivClose)
//        val pwd = customView.findViewById<EditText>(R.id.etPwd)
//        val ok = customView.findViewById<TextView>(R.id.tvOk)
//        val cbEye = customView.findViewById<CheckBox>(R.id.cbEye)
//        cbEye.setOnClickListener {
//            if (cbEye.isChecked) {
//                pwd.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
//            } else {
//                pwd.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
//            }
//
//            //键盘光标移到EditText末尾
//            pwd?.text?.length?.let { it1 -> Selection.setSelection(pwd?.text, it1) }
//        }
//        pwd.addTextChangedListener {
//            pwdStr = pwd.text.trim().toString()
//            cbEye.isEnabled = !pwdStr.isNullOrEmpty()
//            if (!pwdStr.isNullOrEmpty()) {
//                ok.isEnabled = StringUtils.checkString(pwdStr) && pwdStr.length > 7
//            } else {
//                ok.isEnabled = false
//            }
//
//        }
//        ok.setOnClickListener {//校验密码,然后请求转账接口
//            if (KeyboardUtils.isSoftInputVisible(this)) {//隐藏系统软键盘
//                pwd?.let { it1 -> KeyboardUtils.hideSoftInput(it1) }
//            }
//            val walletId = SPUtils.getInstance().getString("walletId")
//            var walletRepository = WalletRepository(this)
//            var walletById: Wallet? = walletId?.let { it1 -> walletRepository.getWalletById(it1) }
//
//            var password = walletById?.password?.trim()
//            var isPasswordCorrect =
//                password?.let { it1 -> RSAUtils.decryptByPrivateKey(it1, pwdStr) }
//            if (!isPasswordCorrect!!) {
//                ToastUtils.make().setGravity(Gravity.TOP, 0, 100).show(R.string.wrong_password)
//                return@setOnClickListener
//            }
//            transfer()
//            dialog.dismiss()
//        }
//
//        ivClose.setOnClickListener {
//            dialog.dismiss()
//        }
//        dialog.show()
//    }
//
//
//    /**
//     * 转账
//     */
//    private fun transfer() {//TODO 自定义
//        var memo = etContent.text.trim().toString()
//        var transferAmount = etTransferAmount.text.trim().toString()
//        val transferRequest = TransferRequest()
//        transferRequest.toAddress = toAddress
//        var transferFeeRateData = BigDecimal(transferFeeRate).longValueExact().toString()
//        transferRequest.feeRate = transferFeeRateData
//        transferRequest.keyId = keyId
//        transferRequest.keystoreDir = DirUtils.createKeyStoreDir()
//        transferRequest.passphrase = pwdStr
//        var dbTrasferAmount = "0"
//        when (coinTag) {
//            "OMNI" -> {
//                transferRequest.coinType = "BTC"
//                transferRequest.tokenType = coinType
//                transferRequest.amount = transferAmount
//                dbTrasferAmount = transferAmount
//            }
//            "ERC20" -> {
//                transferRequest.coinType = "ETH"
//                transferRequest.tokenType = coinType
//                transferRequest.amount =
//                    BigDecimal(transferAmount).multiply(BigDecimal(AppConst.ETH_TO_WEI))
//                        .stripTrailingZeros()
//                        .toPlainString()
//                dbTrasferAmount =
//                    BigDecimal(transferAmount).multiply(BigDecimal(AppConst.ETH_TO_WEI))
//                        .stripTrailingZeros()
//                        .toPlainString()
//            }
//            else -> {
//                if (coinType == "ETH") {//ETH转账用wei做单位
//                    transferRequest.amount =
//                        BigDecimal(transferAmount).multiply(BigDecimal(AppConst.ETH_TO_WEI))
//                            .stripTrailingZeros().toPlainString()
//                    dbTrasferAmount =
//                        BigDecimal(transferAmount).multiply(BigDecimal(AppConst.ETH_TO_WEI))
//                            .stripTrailingZeros().toPlainString()
//                } else {
//                    transferRequest.amount = transferAmount
//                    dbTrasferAmount = transferAmount
//                }
//                transferRequest.coinType = coinType
//            }
//        }
//        Log.e(
//            tag(),
//            "toAddress==$toAddress;transferAmount==$transferAmount;coinType==$coinType;halfHourFee==$halfHourFee;keyId==$keyId;dir==${DirUtils.createKeyStoreDir()};pwdStr==$pwdStr"
//        )
//        SdkUtils.transfer(transferRequest)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(object : Observer<Map<String, String>> {
//                override fun onComplete() {}
//
//                override fun onSubscribe(d: Disposable) {}
//                override fun onNext(map: Map<String, String>) {
//                    var txId = map["txId"]
//                    var keyId = map["keyId"]
////                    var coinType = map["coinType"]
//                    var recordRepository = RecordRepository(this@TransferActivity3)
//                    recordRepository.insert(
//                        Record(
//                            txId,
//                            address,
//                            dbTrasferAmount,
//                            "",
//                            0,
//                            "转账",
//                            coinType,
//                            coinTag,
//                            toAddress,
//                            memo = memo,
//                            startTime = System.currentTimeMillis()
//                        )
//                    )
//                    finish()
//                    ToastUtils.make().setGravity(Gravity.TOP, 0, 100).setDurationIsLong(true)
//                        .show(R.string.submitted_seccessfully)
//                }
//
//                override fun onError(e: Throwable) {
//                    ToastUtils.make().setGravity(Gravity.TOP, 0, 100).setDurationIsLong(true)
//                        .show(R.string.failed_ti_submit)
//                }
//            })
//    }
//
//    /**
//     * 获取ETH的费率
//     */
//    private fun getEthFeeRate() {
//        APIClient.instance.instanceRetrofit(NetApi::class.java)
//            .getEthFeeRate("https://www.gasnow.org/api/v3/gas/price")
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(object : APIResponse<EthFeeRate>(context = this) {
//                override fun success(data: EthFeeRate?) {
//                    Log.e(tag(), "onResponse==${data.toString()}")
//                    var slow = data?.slow
//                    var standard = data?.standard
//                    var rapid = data?.rapid
//
//                    ethHourFee = BigDecimal(slow)//0.4
//                        .divide(BigDecimal(AppConst.GWEI_ETH), 8, BigDecimal.ROUND_HALF_UP)
//                        .stripTrailingZeros().toPlainString()
//                    ethHalfHourFee = BigDecimal(standard)//0.6
//                        .divide(BigDecimal(AppConst.GWEI_ETH), 8, BigDecimal.ROUND_HALF_UP)
//                        .stripTrailingZeros().toPlainString()
//                    ethFastestFee = BigDecimal(rapid)
//                        .divide(BigDecimal(AppConst.GWEI_ETH), 8, BigDecimal.ROUND_HALF_UP)
//                        .stripTrailingZeros().toPlainString()
//
////                    tvSlower.text = "$ethHourFee GWEI\n${getString(R.string.forecast)}60${getString(R.string.minutes)}"
//                    tvSlower.text =
//                        "$ethHourFee GWEI\n${getString(R.string.forecast)}10${getString(R.string.minutes)}"
////                    tvRecommend.text = "$ethHalfHourFee GWEI\n${getString(R.string.forecast)}30${getString(R.string.minutes)}"
//                    tvRecommend.text =
//                        "$ethHalfHourFee GWEI\n${getString(R.string.forecast)}3${getString(R.string.minutes)}"
////                    tvFast.text = "$ethFastestFee GWEI\n${getString(R.string.forecast)}10${getString(R.string.minutes)}"
//                    tvFast.text =
//                        "$ethFastestFee GWEI\n${getString(R.string.forecast)}0.25${getString(R.string.minutes)}"
//
//                    when (mProgress) {
//                        0 -> {
//                            kMoney = BigDecimal(ethHourFee!!).multiply(BigDecimal(21000))
//                                .divide(BigDecimal(AppConst.GWEI_ETH), 8, BigDecimal.ROUND_HALF_UP)
//                            //矿工费
//                            tvAbsenteeismCurrency.text = "$kMoney ETH"
//                            getRate(coinType)
//                            transferFeeRate =
//                                BigDecimal(ethHourFee.toString()).multiply(BigDecimal(AppConst.GWEI_ETH))
//                                    .stripTrailingZeros().toPlainString()
//                        }
//                        50 -> {
//                            kMoney = BigDecimal(ethHalfHourFee!!).multiply(BigDecimal(21000))
//                                .divide(BigDecimal(AppConst.GWEI_ETH), 8, BigDecimal.ROUND_HALF_UP)
//                            //矿工费
//                            tvAbsenteeismCurrency.text = "$kMoney ETH"
//                            getRate(coinType)
//                            transferFeeRate =
//                                BigDecimal(ethHalfHourFee.toString()).multiply(BigDecimal(AppConst.GWEI_ETH))
//                                    .stripTrailingZeros().toPlainString()
//                        }
//                        100 -> {
//                            kMoney = BigDecimal(ethFastestFee!!).multiply(BigDecimal(21000))
//                                .divide(BigDecimal(AppConst.GWEI_ETH), 8, BigDecimal.ROUND_HALF_UP)
//                            //矿工费
//                            tvAbsenteeismCurrency.text = "$kMoney ETH"
//                            getRate(coinType)
//                            transferFeeRate =
//                                BigDecimal(ethFastestFee.toString()).multiply(BigDecimal(AppConst.GWEI_ETH))
//                                    .stripTrailingZeros().toPlainString()
//                        }
//                    }
//
//                }
//
//                override fun failure(errorMsg: String?) {
//                    Log.e(tag(), "onFailure==${errorMsg}")
//                    getETHTransactionFeeRate()
//                }
//
//            })
//
//
//    }
//
//
//    var transferFeeRate: String? = "0"//转账需要传的费率
//    var ethHourFee: String? = "0"
//    var ethHalfHourFee: String? = "0"
//    var ethFastestFee: String? = "0"
//
//    /**
//     * ETH转账获取费率
//     */
//    private fun getETHTransactionFeeRate() {
//        val estimateEthGasPriceRequest = EstimateEthGasPriceRequest()
//        Uv1Helper.estimateEthGasPrice(
//            this,
//            estimateEthGasPriceRequest,
//            object : Uv1Helper.ResponseDataCallback<EstimateEthGasPriceResponse> {
//                override fun onSuccess(data: EstimateEthGasPriceResponse?) {
//                    if (StringUtils.isEmpty(data?.feeRate)) {
//                        noData("ETH")
//                    } else {
//                        val feeRate: String = data?.feeRate!!
//                        ethHourFee = BigDecimal(feeRate).multiply(BigDecimal(0.7))//0.4
//                            .divide(BigDecimal(AppConst.GWEI_ETH), 8, BigDecimal.ROUND_HALF_UP)
//                            .stripTrailingZeros().toPlainString()
//                        ethHalfHourFee = BigDecimal(feeRate).multiply(BigDecimal(0.85))//0.6
//                            .divide(BigDecimal(AppConst.GWEI_ETH), 8, BigDecimal.ROUND_HALF_UP)
//                            .stripTrailingZeros().toPlainString()
//                        ethFastestFee = BigDecimal(feeRate).multiply(BigDecimal(1))
//                            .divide(BigDecimal(AppConst.GWEI_ETH), 8, BigDecimal.ROUND_HALF_UP)
//                            .stripTrailingZeros().toPlainString()
//
//                        tvSlower.text = StringBuilder().append("$ethHourFee GWEI\n")
//                            .append("${getString(R.string.forecast)}10${getString(R.string.minutes)}")
//
//                        tvRecommend.text = StringBuilder().append("$ethHalfHourFee GWEI\n")
//                            .append("${getString(R.string.forecast)}3${getString(R.string.minutes)}")
//
//                        tvFast.text = StringBuilder().append("$ethFastestFee GWEI\n")
//                            .append("${getString(R.string.forecast)}0.25${getString(R.string.minutes)}")
//
//                        kMoney = BigDecimal(ethHalfHourFee!!).multiply(BigDecimal(21000))
//                            .divide(BigDecimal(AppConst.GWEI_ETH), 8, BigDecimal.ROUND_HALF_UP)
//                        //矿工费
//                        tvAbsenteeismCurrency.text =
//                            StringBuilder().append("$kMoney").append(" ETH")
//                        getRate(coinType)
//                        transferFeeRate =
//                            BigDecimal(ethHalfHourFee.toString()).multiply(BigDecimal(AppConst.GWEI_ETH))
//                                .stripTrailingZeros().toPlainString()
//                    }
//                }
//
//                override fun onError(e: Exception?) {
//                    Log.e("sdk_error_info", e?.localizedMessage!!)
//                }
//
//            })
//    }
//
//
//    var hourFee: String? = "0"
//    var halfHourFee: String? = "0"
//    var fastestFee: String? = "0"
//
//    /**
//     * BTC转账获取费率
//     */
//    private fun getBTCTransactionFeeRate() {
//        val transactionFeeRateRequest = TransactionFeeRateRequest()
////        transactionFeeRateRequest.coinType = coinType //TODO
//        if (coinTag == "OMNI") {
//            transactionFeeRateRequest.coinType = "BTC"
//        } else {
//            transactionFeeRateRequest.coinType = coinType
//        }
//        Uv1Helper.transactionFeeRate(this, transactionFeeRateRequest,
//            object : Uv1Helper.ResponseDataCallback<TransactionFeeRateResponse> {
//                override fun onSuccess(data: TransactionFeeRateResponse?) {
//                    hourFee = data?.hourFee.toString()
//                    halfHourFee = data?.halfHourFee.toString()
//                    fastestFee = data?.fastestFee.toString()
//                    tvSlower.text =
//                        "$hourFee sat/b\n${getString(R.string.forecast)}60${getString(R.string.minutes)}"
//                    tvRecommend.text =
//                        "$halfHourFee sat/b\n${getString(R.string.forecast)}30${getString(R.string.minutes)}"
//                    tvFast.text =
//                        "$fastestFee sat/b\n${getString(R.string.forecast)}10${getString(R.string.minutes)}"
//                    kMoney = if (size == 0L) {
//                        BigDecimal(0)
//                    } else {
//                        BigDecimal(halfHourFee).multiply(BigDecimal(size.toString()))
//                            .divide(BigDecimal(AppConst.SAT_BTC), 8, BigDecimal.ROUND_HALF_UP)
//                    }
//                    //矿工费
//                    tvAbsenteeismCurrency.text = "$kMoney BTC"
//                    transferFeeRate = halfHourFee
//                    getRate("BTC")
//                }
//
//                override fun onError(e: java.lang.Exception?) {
//                    Log.e("sdk_erroe_info", e?.localizedMessage!!)
//                }
//
//            })
//    }
//
//    /**
//     * 无网络或无数据的情况,显示的页面
//     */
//    private fun noData(coinType: String) {
//        when (coinType) {
//            "BTC" -> {
//                tvSlower.text =
//                    "0 sat/b\n${getString(R.string.forecast)}60${getString(R.string.minutes)}"
//                tvRecommend.text =
//                    "0 sat/b\n${getString(R.string.forecast)}30${getString(R.string.minutes)}"
//                tvFast.text =
//                    "0 sat/b\n${getString(R.string.forecast)}10${getString(R.string.minutes)}"
//                tvAbsenteeismCurrency.text = "0 BTC"
//            }
//            "ETH" -> {
//                tvSlower.text =
//                    "0 GWEI\n${getString(R.string.forecast)}10${getString(R.string.minutes)}"
//                tvRecommend.text =
//                    "0 GWEI\n${getString(R.string.forecast)}3${getString(R.string.minutes)}"
//                tvFast.text =
//                    "0 GWEI\n${getString(R.string.forecast)}0.25${getString(R.string.minutes)}"
//                tvAbsenteeismCurrency.text = "0 ETH"
//            }
//            else -> {
//                tvSlower.text =
//                    "0 sat/b\n${getString(R.string.forecast)}60${getString(R.string.minutes)}"
//                tvRecommend.text =
//                    "0 sat/b\n${getString(R.string.forecast)}30${getString(R.string.minutes)}"
//                tvFast.text =
//                    "0 sat/b\n${getString(R.string.forecast)}10${getString(R.string.minutes)}"
//                tvAbsenteeismCurrency.text = "0 BTC"
//            }
//        }
//    }
//
//
//    /**
//     * 将币资产转换成相应的人民币、美元或者欧元等
//     */
//    private fun getRate(coinType: String) {
//        val queryList = mutableListOf<String>()
//        queryList.add(0, coinType)
//        APIClient.instance.instanceRetrofit(NetApi::class.java)
//            .tokenTicker(queryList)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(object : APIResponse<HttpExchangeRateBean>(this) {
//                override fun success(data: HttpExchangeRateBean?) {
//                    Log.e(tag(), "onResponse==${data}")
//                    var list = data?.list
//                    if (list.isNullOrEmpty()) return
//                    var exchangeRateBean = list[0]
//                    when (SPUtils.getInstance().getString("unit")) {
//                        "CNY" -> {
//                            var priceCny: String? = exchangeRateBean.price_cny
//                            var money = if (kMoney < BigDecimal.ZERO || kMoney == BigDecimal.ZERO) {
//                                "0"
//                            } else {
//                                BigDecimal(priceCny).multiply(kMoney)
//                                    .setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros()
//                                    .toPlainString()
//                            }
//                            Log.e("afgasfdg", "money==${money};kMoney==$kMoney")
//                            tvAbsenteeismRmb.text = "￥$money"
//                        }
//                        "USD" -> {
//                            var priceUsd: String? = exchangeRateBean.price_usd
//
//                            var money = if (kMoney < BigDecimal.ZERO || kMoney == BigDecimal.ZERO) {
//                                "0"
//                            } else {
//                                BigDecimal(priceUsd).multiply(kMoney)
//                                    .setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros()
//                                    .toPlainString()
//                            }
//
//                            tvAbsenteeismRmb.text = "$$money"
//                        }
//                        "EUR" -> {
//                            var priceEur: String? = exchangeRateBean.price_eur
//                            var money = if (kMoney < BigDecimal.ZERO || kMoney == BigDecimal.ZERO) {
//                                "0"
//                            } else {
//                                BigDecimal(priceEur).multiply(kMoney)
//                                    .setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros()
//                                    .toPlainString()
//                            }
//                            tvAbsenteeismRmb.text = "€$money"
//                        }
//                        else -> {
//                            var priceCny: String? = exchangeRateBean.price_cny
//                            var money = if (kMoney < BigDecimal.ZERO || kMoney == BigDecimal.ZERO) {
//                                "0"
//                            } else {
//                                BigDecimal(priceCny).multiply(kMoney)
//                                    .setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros()
//                                    .toPlainString()
//                            }
//                            Log.e("afgasfdg", "money==${money};kMoney==$kMoney")
//                            tvAbsenteeismRmb.text = "￥$money"
//                        }
//                    }
//                }
//
//                override fun failure(errorMsg: String?) {
//                    Log.e(tag(), "onFailure==${errorMsg}")
//                }
//
//            })
//
//    }
//
//
//    private fun transferInfo() {
//
//        var transferAmount = etTransferAmount.text.trim().toString()
//        var memo = etContent.text.trim().toString()
//
//        NiceDialog.init()
//            .setLayoutId(R.layout.layout_dialog_transfer_detail)
//            .setConvertListener(object : ViewConvertListener() {
//                override fun convertView(holder: ViewHolder?, dialog: BaseNiceDialog?) {
//                    val ivClose = holder?.getView<ImageView>(R.id.iv_close)
//                    val tvCoinMoney = holder?.getView<TextView>(R.id.tvCoinMoney)
//                    val tvCoinType = holder?.getView<TextView>(R.id.tvCoinType)
//                    val tvTransferAddress = holder?.getView<TextView>(R.id.tvTransferAddress)
//                    val tvKFee = holder?.getView<TextView>(R.id.tvKFee)
//                    val tvMemo = holder?.getView<TextView>(R.id.tvMemo)
//                    tvCoinMoney?.text = transferAmount
//                    tvCoinType?.text = coinType
//                    tvTransferAddress?.text = toAddress
//
//                    if (coinType == "BTC" || coinTag == "OMNI") { //矿工费
//                        tvKFee?.text = "$kMoney BTC"
//                    }
//                    if (coinType == "ETH" || coinTag == "ERC20") {
//                        tvKFee?.text = "$kMoney ETH"
//                    }
//                    tvMemo?.text = memo
//                    ivClose?.setOnClickListener { dialog?.dismiss() }
//                    val tvConfirm = holder?.getView<TextView>(R.id.tv_confirm)
//                    tvConfirm?.setOnClickListener {
//                        showCommonDialog()
//                        dialog?.dismiss()
//                    }
//
//
//                }
//            })
//            .setDimAmount(0.3f)
//            .setGravity(Gravity.BOTTOM)
//            .setOutCancel(true)
//            .setAnimStyle(R.style.BottomAnimation)
//            .show(supportFragmentManager)
//    }
//
//    private fun noMoney() {
//        tvAllAmount.text = "${getString(R.string.assets)} 0 $coinType"
//        tvAbsenteeismCurrency.text = "0$coinType"
//        when (SPUtils.getInstance().getString("unit")) {
//            "CNY" -> {
//                tvAbsenteeismRmb.text = "￥0"
//            }
//            "USD" -> {
//                tvAbsenteeismRmb.text = "$0"
//            }
//            "EUR" -> {
//                tvAbsenteeismRmb.text = "€0"
//            }
//            else -> {
//                tvAbsenteeismRmb.text = "￥0"
//            }
//        }
//
//        /**
//         * BTC费率和矿工费计算
//         */
//        if (coinType == "BTC" || coinTag == "OMNI") {//ETH没有size,BTC有size
//            tvSlower.text =
//                "10 sat/b\n${getString(R.string.forecast)}60${getString(R.string.minutes)}"
//            tvRecommend.text =
//                "20 sat/b\n${getString(R.string.forecast)}30${getString(R.string.minutes)}"
//            tvFast.text =
//                "30 sat/b\n${getString(R.string.forecast)}10${getString(R.string.minutes)}"
//        }
//
//        /**
//         *  ETH费率和矿工费计算
//         */
//        if (coinType == "ETH" || coinTag == "ERC20") {
//            tvSlower.text =
//                "10 GWEI\n${getString(R.string.forecast)}60${getString(R.string.minutes)}"
//            tvRecommend.text =
//                "20 GWEI\n${getString(R.string.forecast)}30${getString(R.string.minutes)}"
//            tvFast.text = "30 GWEI\n${getString(R.string.forecast)}10${getString(R.string.minutes)}"
//        }
//    }
//}
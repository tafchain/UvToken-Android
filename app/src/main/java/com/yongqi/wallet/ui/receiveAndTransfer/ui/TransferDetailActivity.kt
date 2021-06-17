package com.yongqi.wallet.ui.receiveAndTransfer.ui

import android.content.Intent
import android.text.InputType
import android.text.Selection
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.core.widget.addTextChangedListener
import api.*
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.blankj.utilcode.constant.TimeConstants
import com.blankj.utilcode.util.*

import com.gyf.immersionbar.ktx.immersionBar
import com.shehuan.nicedialog.BaseNiceDialog
import com.shehuan.nicedialog.NiceDialog
import com.shehuan.nicedialog.ViewConvertListener
import com.shehuan.nicedialog.ViewHolder
import com.yongqi.wallet.BuildConfig
import com.yongqi.wallet.R
import com.yongqi.wallet.api.NetApi
import com.yongqi.wallet.base.BaseActivity
import com.yongqi.wallet.bean.Coin
import com.yongqi.wallet.bean.EthFeeRate
import com.yongqi.wallet.bean.Record
import com.yongqi.wallet.config.AppConst
import com.yongqi.wallet.config.AppConst.ETH_TO_WEI
import com.yongqi.wallet.config.AppConst.GWEI_ETH
import com.yongqi.wallet.config.CoinConst
import com.yongqi.wallet.config.WalletConst
import com.yongqi.wallet.databinding.ActivityTransferDetailBinding
import com.yongqi.wallet.db.coin.CoinRepository
import com.yongqi.wallet.db.record.RecordRepository
import com.yongqi.wallet.db.wallet.WalletRepository
import com.yongqi.wallet.net.APIClient
import com.yongqi.wallet.net.APIResponse
import com.yongqi.wallet.ui.receiveAndTransfer.viewModel.TransferDetailViewModel
import com.yongqi.wallet.ui.vm.HttpApiHelper
import com.yongqi.wallet.utils.*
import com.yongqi.wallet.utils.StringUtils
import com.yongqi.wallet.view.LoadingDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_transfer_detail.*
import kotlinx.android.synthetic.main.common_title.*
import java.math.BigDecimal

class TransferDetailActivity :
    BaseActivity<ActivityTransferDetailBinding, TransferDetailViewModel>() {


    override fun getLayoutResource(): Int = R.layout.activity_transfer_detail

    companion object {
        const val CANCEL_FLAG = 0//取消交易
        const val ACCELERATE_FLAG = 1//交易加速
    }

    var txId = ""
    var address = ""
    var coinType = ""

    //    var name = ""
    var coinTag = ""
    private val recordRepository by lazy { RecordRepository(this) }
    private val record by lazy {
        recordRepository.getRecordByTxIdAddressAndCoinType(
            txId,
            address,
            coinType
        )
    }
    private val coinRepository by lazy { CoinRepository(this@TransferDetailActivity) }
    private val coin by lazy {
        coinRepository?.getCoinsByNameAndAddress("ETH", address)
    }

    private val erc20Coin by lazy {
        coinRepository?.getCoinsByNameAndAddress(coinType, address)
    }

    private val walletRepository by lazy { WalletRepository(this@TransferDetailActivity) }

    override fun initData() {

        immersionBar {
            statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
            titleBar(iTitle) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
        }
        ivBack.setOnClickListener(onClickListener)
        tvTitle.text = getString(R.string.details)
        rlQueryDetails.setOnClickListener(onClickListener)
        txId = intent.getStringExtra("tx_id").toString()
        address = intent.getStringExtra("address").toString()
        coinType = intent.getStringExtra("coinType").toString()
        val type = intent.getStringExtra("type").toString()
        cancelOrAccelerateFlag = intent.getIntExtra("flag", -1)
//        name = record?.name.toString()
        coinTag = record?.coin_tag.toString()
        tvMemo.text = record?.memo
        if (cancelOrAccelerateFlag != -1) {
            getEthTransactionStatus(isSuccess = { value ->
                if (value!! && cancelOrAccelerateFlag == CANCEL_FLAG) {
                    finish()
                    ToastUtils.make()
                        .show("${getString(R.string.transaction_completed_cannot_be_cancelled)}")
                } else if (value!! && cancelOrAccelerateFlag == ACCELERATE_FLAG) {
                    finish()
                    ToastUtils.make().show("${getString(R.string.transaction_completed)}")
                } else {
                    showCancelOrAccelerateDialog(cancelOrAccelerateFlag)
                }
            })

        }
        if (coinType == "ETH" || coinTag == "ERC20") {
            when (record?.result) {
                "1" -> {
                    ivResult.setImageResource(R.mipmap.iocn_xq_cg)
                    tvResult.text = getString(R.string.successful)
                    tvResult.setTextColor(resources.getColor(R.color.color_23))
                }
                "0" -> {
                    ivResult.setImageResource(R.mipmap.iocn_xq_sb)
                    tvResult.text = getString(R.string.failed)
                    tvResult.setTextColor(resources.getColor(R.color.color_14))
                }
                "404" -> {//TODO 新增
                    ivResult.setImageResource(R.mipmap.iocn_xq_cg)
                    tvResult.text = getString(R.string.waiting_for_confirmation)
                    tvResult.setTextColor(resources.getColor(R.color.color_23))
                }
                else -> {
                    if (record?.time == 0L) {
                        ivResult.setImageResource(R.mipmap.iocn_xq_cg)
                        tvResult.text = getString(R.string.waiting_for_confirmation)
                        tvResult.setTextColor(resources.getColor(R.color.color_23))
                        var timeMinis = TimeUtils.getTimeSpan(
                            System.currentTimeMillis(),
                            record?.startTime!!,
                            TimeConstants.MIN
                        )
                        if (record?.gasPrice != "-1" && timeMinis > 1) {
//                            ivResult.setImageResource(R.mipmap.icon_dabao)
//                            tvResult.text = getString(R.string.packing)
//                            tvResult.setTextColor(resources.getColor(R.color.color_23))
                            llFooter.visibility = VISIBLE
                            tvCancel.setOnClickListener(onClickListener)
                            tvAccelerate.setOnClickListener(onClickListener)
                        } else {
                            llFooter.visibility = GONE
                        }

                    }
                }
            }


            val minerFee =
                if (BigDecimal(record?.miner_fee) == BigDecimal.ZERO) {
                    "0"
                } else {
                    BigDecimal(record?.miner_fee).stripTrailingZeros().toPlainString()
                }

            if (coinTag != "ERC20") {
                var amount = "0"
                if (BigDecimal(record?.amount) >= BigDecimal(1000)) {
                    amount = BigDecimal(record?.amount.toString()).divide(
                        BigDecimal(ETH_TO_WEI)
                    ).stripTrailingZeros().toPlainString()
                    tvAmount.text = "$amount $coinType"
                }else{
                    tvAmount.text = "${record?.amount} $coinType"
                }
            }else{
                tvAmount.text = "${record?.amount} $coinType"
            }
            tvAbsenteeism.text = "$minerFee ETH"


        } else if (coinType == "BTC" || coinTag == "OMNI") {//BTC
            if (record?.time == 0L) {

                if (coinType == "BTC") {
                    setBtcStatus()
                } else {
                    ivResult.setImageResource(R.mipmap.iocn_xq_cg)
                    tvResult.text = getString(R.string.waiting_for_confirmation)
                    tvResult.setTextColor(resources.getColor(R.color.color_23))
                }
            } else {
                if (coinTag == "OMNI") {
                    if (record?.valid == "false") {
                        ivResult.setImageResource(R.mipmap.iocn_xq_sb)
                        tvResult.text = getString(R.string.failed)
                        tvResult.setTextColor(resources.getColor(R.color.color_14))
                    } else {
                        ivResult.setImageResource(R.mipmap.iocn_xq_cg)
                        tvResult.text = getString(R.string.successful)
                        tvResult.setTextColor(resources.getColor(R.color.color_23))
                    }
                } else {
                    ivResult.setImageResource(R.mipmap.iocn_xq_cg)
                    tvResult.text = getString(R.string.successful)
                    tvResult.setTextColor(resources.getColor(R.color.color_23))
                }

            }
            val transferAmount =
                if (BigDecimal(record?.amount) == BigDecimal.ZERO) {
                    "0"
                } else {
                    BigDecimal(record?.amount).stripTrailingZeros().toPlainString()
                }

            val minerFee =
                if (BigDecimal(record?.miner_fee) == BigDecimal.ZERO) {
                    "0"
                } else {
                    BigDecimal(record?.miner_fee).stripTrailingZeros().toPlainString()
                }

            tvAmount.text = "$transferAmount $coinType"
            tvAbsenteeism.text = "$minerFee BTC"
        } else if (coinType == CoinConst.TRX || coinTag == "TRC20") {//TRX
            if (record?.time == 0L) {
                ivResult.setImageResource(R.mipmap.iocn_xq_cg)
                tvResult.text = getString(R.string.waiting_for_confirmation)
                tvResult.setTextColor(resources.getColor(R.color.color_23))
            } else {
                if (record?.result == "0") {
                    ivResult.setImageResource(R.mipmap.iocn_xq_sb)
                    tvResult.text = getString(R.string.failed)
                    tvResult.setTextColor(resources.getColor(R.color.color_14))
                } else {
                    ivResult.setImageResource(R.mipmap.iocn_xq_cg)
                    tvResult.text = getString(R.string.successful)
                    tvResult.setTextColor(resources.getColor(R.color.color_23))
                }

            }
            val transferAmount =
                if (BigDecimal(record?.amount) == BigDecimal.ZERO) {
                    "0"
                } else {
                    BigDecimal(record?.amount).stripTrailingZeros().toPlainString()
                }

            val minerFee =
                if (BigDecimal(record?.miner_fee) == BigDecimal.ZERO) {
                    "0"
                } else {
                    BigDecimal(record?.miner_fee).stripTrailingZeros().toPlainString()
                }

            tvAmount.text = "$transferAmount $coinType"
            tvAbsenteeism.text = "$minerFee TRX"
        }

        if (record?.time == 0L) {
            tvTime.visibility = GONE
        } else {
            tvTime.visibility = VISIBLE
            tvTime.text = (record?.time?.times(1000))?.let { TimeUtils.millis2String(it) }
        }


        when (record?.type) {
            "转账" -> {
                tvReceiveAddress.text = record?.to_address
                tvPaymentAddress.text = record?.address
            }
            "收款" -> {//失败
                tvReceiveAddress.text = record?.address
                tvPaymentAddress.text = record?.to_address
            }
        }

        tvTransactionHash.text = record?.tx_id
        llTransactionHash.setOnClickListener(onClickListener)
        llReceiveAddress.setOnClickListener(onClickListener)
        llPaymentAddress.setOnClickListener(onClickListener)
        /**
         * 处理挖矿奖励的情况
         */
        if (type == "挖矿奖励") {
            rlAbsenteeism.visibility = GONE
            iLine1.visibility = GONE
            tvPaymentAddress.text = "CoinBase"
            tvReceiveAddress.text = address
        } else {
            rlAbsenteeism.visibility = VISIBLE
            iLine1.visibility = VISIBLE
        }

//        LogUtils.e("eth","${record?.startTime} ${record?.toString()}")
    }

    private val onClickListener = View.OnClickListener {
        when (it.id) {
            R.id.ivBack -> {
                finish()
            }
            R.id.llTransactionHash -> {
                val transactionHash = tvTransactionHash.text.toString().trim()
                ClipboardUtils.copyText(transactionHash)
                ToastUtils.make()
                    .show("${getString(R.string.this_transaction_hash_has_been_copied)}")
            }

            R.id.llReceiveAddress -> {
                val tvReceiveAddress = tvReceiveAddress.text.toString().trim()
                ClipboardUtils.copyText(tvReceiveAddress)
                ToastUtils.make().show("${getString(R.string.copy_success)}")
            }

            R.id.llPaymentAddress -> {
                val tvPaymentAddress = tvPaymentAddress.text.toString().trim()
                ClipboardUtils.copyText(tvPaymentAddress)
                ToastUtils.make().show("${getString(R.string.copy_success)}")
            }

            R.id.rlQueryDetails -> {//查询详情
                if (coinType == "ETH" || coinTag == "ERC20") {
                    startActivity(
                        Intent(this, TransferWebActivity::class.java)
                            .putExtra("hash", txId)
                            .putExtra("url", AppConst.ETH_URL)
                    )
                } else if (coinType == "BTC" || coinTag == "OMNI") {
                    startActivity(
                        Intent(this, TransferWebActivity::class.java)
                            .putExtra("hash", txId)
                            .putExtra("url", AppConst.BTC_URL)
                    )
                } else if (coinType == CoinConst.TRX || coinTag == "TRC20") {
                    var trxUrl = ""
                    if (BuildConfig.FLAVOR == "devtest") {
                        trxUrl = "https://shasta.tronscan.org/#/transaction/"
                    }
                    if (BuildConfig.FLAVOR == "online") {
                        val localLanguage = SPUtils.getInstance().getString("language","简体中文")
                        Log.e("localLanguage",localLanguage)
                        var urlLanguage: String
                        urlLanguage = if (localLanguage == "简体中文") {
                            "cn"
                        } else {
                            "en"
                        }
                        trxUrl = "https://trx.tokenview.com/" + urlLanguage + "/tx/"
                    }
                    startActivity(
                        Intent(this, TransferWebActivity::class.java)
                            .putExtra("hash", txId)
                            .putExtra("url", trxUrl)
                    )
                }
            }
            R.id.tvCancel -> {//取消 TODO
                getEthTransactionStatus(isSuccess = { value ->
                    if (value!!) {
                        finish()
                        ToastUtils.make()
                            .show("${getString(R.string.transaction_completed_cannot_be_cancelled)}")
                    } else {
                        showCancelOrAccelerateDialog(CANCEL_FLAG)
                    }
                })
            }
            R.id.tvAccelerate -> {//加速 TODO
                getEthTransactionStatus(isSuccess = { value ->
                    if (value!!) {
                        finish()
                        ToastUtils.make().show("${getString(R.string.transaction_completed)}")
                    } else {
                        showCancelOrAccelerateDialog(ACCELERATE_FLAG)
                    }
                })
            }
        }
    }

    /**
     * BTC：设置状态
     */
    private fun setBtcStatus() {
        val getBtcTransactionRequest = GetBtcTransactionRequest()
        getBtcTransactionRequest.txid = txId

        Uv1Helper.getBtcTransaction(
            this,
            getBtcTransactionRequest,
            object : Uv1Helper.ResponseDataCallback<GetBtcTransactionResponse> {
                override fun onSuccess(data: GetBtcTransactionResponse?) {
                    val confirmations = data?.confirmations.toString()
                    val trusted = data?.trusted.toString()
                    if (confirmations == "-1" && trusted == "false") {
                        ivResult.setImageResource(R.mipmap.iocn_xq_sb)
                        tvResult.text = getString(R.string.failed)
                        tvResult.setTextColor(resources.getColor(R.color.color_14))
                    } else {
                        ivResult.setImageResource(R.mipmap.iocn_xq_cg)
                        tvResult.text = getString(R.string.waiting_for_confirmation)
                        tvResult.setTextColor(resources.getColor(R.color.color_23))
                    }
                }

                override fun onError(e: java.lang.Exception?) {
                    ivResult.setImageResource(R.mipmap.iocn_xq_cg)
                    tvResult.text = getString(R.string.waiting_for_confirmation)
                    tvResult.setTextColor(resources.getColor(R.color.color_23))
                }
            })
    }


    /**
     * 以下所有代码都为ETH加速的代码
     * ETH:判断交易是否成功
     */
    private fun getEthTransactionStatus(isSuccess: (value: Boolean?) -> Unit = {}) {
        val queryETHTransactionRequest = QueryETHTransactionRequest()
        queryETHTransactionRequest.txId = txId
        Uv1Helper.getEthTransactionReceipt(
            this,
            queryETHTransactionRequest,
            object : Uv1Helper.ResponseDataCallback<QueryETHTransactionResponse> {
                override fun onSuccess(data: QueryETHTransactionResponse?) {
                    var value: Boolean? = data?.getValue()
                    isSuccess(value)
                }

                override fun onError(e: java.lang.Exception?) {
                    isSuccess(false)
                }
            })

    }

    var afterGasPrice = "0"
    var minerFee = "0"
    var cancelOrAccelerateFlag = -1

    /**
     * 显示1、取消 或 2、加速弹窗
     */
    private fun showCancelOrAccelerateDialog(flag: Int) {
        cancelOrAccelerateFlag = flag
        NiceDialog.init()
            .setLayoutId(R.layout.accelerate_dialog)
            .setConvertListener(object : ViewConvertListener() {
                override fun convertView(holder: ViewHolder?, dialog: BaseNiceDialog?) {
                    val tvCancelTheDeal = holder?.getView<TextView>(R.id.tvCancelTheDeal)
                    val btnCancel = holder?.getView<Button>(R.id.btnCancel)//取消

                    val ivAccelerate = holder?.getView<ImageView>(R.id.ivAccelerate)
                    val tvTransactionAcceleration =
                        holder?.getView<TextView>(R.id.tvTransactionAcceleration)
                    val btnConfirmAcceleration =
                        holder?.getView<Button>(R.id.btnConfirmAcceleration)//加速

                    val ivClose = holder?.getView<ImageView>(R.id.ivClose)
                    val tvCoinMoney = holder?.getView<TextView>(R.id.tvCoinMoney)//转账金额
                    val tvReceiveAddress = holder?.getView<TextView>(R.id.tvReceiveAddress)//收款地址
                    val tvPaymentAddress = holder?.getView<TextView>(R.id.tvPaymentAddress)//付款地址
                    val tvTransactionHash = holder?.getView<TextView>(R.id.tvTransactionHash)//交易哈希值

                    val rgFee = holder?.getView<RadioGroup>(R.id.rgFee)
                    val rbRecommended = holder?.getView<RadioButton>(R.id.rbRecommended)//推荐(约3分钟)
                    val rbHaste = holder?.getView<RadioButton>(R.id.rbHaste)//急速(约15秒)
                    val tvBeforeAccelerationGasPrice =
                        holder?.getView<TextView>(R.id.tvBeforeAccelerationGasPrice)//加速前gasPrice
                    val tvAfterAccelerationGasPrice =
                        holder?.getView<TextView>(R.id.tvAfterAccelerationGasPrice)//加速后gasPrice
                    val tvMinerFee = holder?.getView<TextView>(R.id.tvMinerFee)//矿工费
                    val tvMinerFeeCalculation =
                        holder?.getView<TextView>(R.id.tvMinerFeeCalculation)//矿工费计算公式

                    if (flag == CANCEL_FLAG) {
                        tvCancelTheDeal?.visibility = VISIBLE
                        btnCancel?.visibility = VISIBLE
                        ivAccelerate?.visibility = GONE
                        tvTransactionAcceleration?.visibility = GONE
                        btnConfirmAcceleration?.visibility = GONE

                    } else if (flag == ACCELERATE_FLAG) {
                        tvCancelTheDeal?.visibility = GONE
                        btnCancel?.visibility = GONE
                        ivAccelerate?.visibility = VISIBLE
                        tvTransactionAcceleration?.visibility = VISIBLE
                        btnConfirmAcceleration?.visibility = VISIBLE
                    }

                    ivClose?.setOnClickListener {
                        dialog?.dismiss()
                    }
                    tvCoinMoney?.text = "${record?.amount} $coinType"
                    tvReceiveAddress?.text = record?.to_address
                    tvPaymentAddress?.text = record?.address
                    tvTransactionHash?.text = record?.tx_id
                    //获取ETH的费率
                    var beforeGasPrice = BigDecimal(record?.gasPrice).divide(
                        BigDecimal(GWEI_ETH),
                        8,
                        BigDecimal.ROUND_HALF_UP
                    )
                    tvBeforeAccelerationGasPrice?.text =
                        StringBuilder().append(beforeGasPrice.stripTrailingZeros().toPlainString())
                            .append(" GWEI")
                    LoadingDialog.show(this@TransferDetailActivity)
                    HttpApiHelper.getEthFeeRate(this@TransferDetailActivity,
                        successC = { ethFeeRate ->
                            LoadingDialog.cancel()
                            Log.e(tag(), "onResponse==${ethFeeRate.toString()}")
                            var standard = ethFeeRate?.standard
                            var rapid = ethFeeRate?.rapid

                            var standardGasPrice = BigDecimal(standard).divide(
                                BigDecimal(GWEI_ETH),
                                8,
                                BigDecimal.ROUND_HALF_UP
                            )
                            var rapidGasPrice = BigDecimal(rapid).divide(
                                BigDecimal(GWEI_ETH),
                                8,
                                BigDecimal.ROUND_HALF_UP
                            )

                            rgFee?.setOnCheckedChangeListener(object :
                                RadioGroup.OnCheckedChangeListener {
                                override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                                    when (checkedId) {
                                        R.id.rbRecommended -> {
                                            if (BigDecimal(record?.gasPrice).subtract(
                                                    BigDecimal(
                                                        standard
                                                    )
                                                ) >= BigDecimal.ZERO
                                            ) {//1.25倍和1.5倍
                                                afterGasPrice =
                                                    beforeGasPrice.multiply(BigDecimal(1.25))
                                                        .stripTrailingZeros().toPlainString()
                                                minerFee = beforeGasPrice.multiply(BigDecimal(1.25))
                                                    .multiply(BigDecimal(21000)).divide(
                                                        BigDecimal(GWEI_ETH),
                                                        8,
                                                        BigDecimal.ROUND_HALF_UP
                                                    ).stripTrailingZeros().toPlainString()
                                            } else {
                                                afterGasPrice =
                                                    standardGasPrice.stripTrailingZeros()
                                                        .toPlainString()
                                                minerFee =
                                                    standardGasPrice.multiply(BigDecimal(21000))
                                                        .divide(
                                                            BigDecimal(GWEI_ETH),
                                                            8,
                                                            BigDecimal.ROUND_HALF_UP
                                                        ).stripTrailingZeros().toPlainString()
                                            }
                                            tvAfterAccelerationGasPrice?.text =
                                                StringBuilder().append(afterGasPrice)
                                                    .append(" GWEI")
                                            tvMinerFee?.text =
                                                StringBuilder().append(minerFee).append(" ETH")
                                            tvMinerFeeCalculation?.text =
                                                StringBuilder().append("≈Gas(21000)*Gas Price(")
                                                    .append(afterGasPrice).append(" GWEI)")
                                        }
                                        R.id.rbHaste -> {
                                            if (BigDecimal(record?.gasPrice).subtract(
                                                    BigDecimal(
                                                        standard
                                                    )
                                                ) >= BigDecimal.ZERO
                                            ) {//1.25倍和1.5倍
                                                afterGasPrice =
                                                    beforeGasPrice.multiply(BigDecimal(1.5))
                                                        .stripTrailingZeros().toPlainString()
                                                minerFee = beforeGasPrice.multiply(BigDecimal(1.5))
                                                    .multiply(BigDecimal(21000)).divide(
                                                        BigDecimal(GWEI_ETH),
                                                        8,
                                                        BigDecimal.ROUND_HALF_UP
                                                    ).stripTrailingZeros().toPlainString()
                                            } else {
                                                afterGasPrice = rapidGasPrice.stripTrailingZeros()
                                                    .toPlainString()
                                                minerFee = rapidGasPrice.multiply(BigDecimal(21000))
                                                    .divide(
                                                        BigDecimal(GWEI_ETH),
                                                        8,
                                                        BigDecimal.ROUND_HALF_UP
                                                    ).stripTrailingZeros().toPlainString()
                                            }
                                            tvAfterAccelerationGasPrice?.text =
                                                StringBuilder().append(afterGasPrice)
                                                    .append(" GWEI")
                                            tvMinerFee?.text =
                                                StringBuilder().append(minerFee).append(" ETH")
                                            tvMinerFeeCalculation?.text =
                                                StringBuilder().append("≈Gas(21000)*Gas Price(")
                                                    .append(afterGasPrice).append(" GWEI)")

                                        }
                                    }
                                }
                            })
                            //默认 选中推荐速率,解决RadioGroup不触发的bug
                            if (BigDecimal(record?.gasPrice).subtract(BigDecimal(standard)) >= BigDecimal.ZERO) {//1.25倍和1.5倍
                                afterGasPrice =
                                    beforeGasPrice.multiply(BigDecimal(1.25)).stripTrailingZeros()
                                        .toPlainString()
                                minerFee = beforeGasPrice.multiply(BigDecimal(1.25))
                                    .multiply(BigDecimal(21000))
                                    .divide(BigDecimal(GWEI_ETH), 8, BigDecimal.ROUND_HALF_UP)
                                    .stripTrailingZeros().toPlainString()
                            } else {
                                afterGasPrice =
                                    standardGasPrice.stripTrailingZeros().toPlainString()
                                minerFee = standardGasPrice.multiply(BigDecimal(21000))
                                    .divide(BigDecimal(GWEI_ETH), 8, BigDecimal.ROUND_HALF_UP)
                                    .stripTrailingZeros().toPlainString()
                            }
                            tvAfterAccelerationGasPrice?.text =
                                StringBuilder().append(afterGasPrice).append(" GWEI")
                            tvMinerFee?.text = StringBuilder().append(minerFee).append(" ETH")
                            tvMinerFeeCalculation?.text =
                                StringBuilder().append("≈Gas(21000)*Gas Price(")
                                    .append(afterGasPrice).append(" GWEI)")
                        },
                        failureC = { errorMsg ->
                            Log.e(tag(), "onFailure==${errorMsg}")
                            val estimateEthGasPriceRequest = EstimateEthGasPriceRequest()
                            Uv1Helper.estimateEthGasPrice(this@TransferDetailActivity,
                                estimateEthGasPriceRequest,
                                object :
                                    Uv1Helper.ResponseDataCallback<EstimateEthGasPriceResponse> {
                                    override fun onSuccess(data: EstimateEthGasPriceResponse?) {
                                        runOnUiThread {
                                            val feeRate: String = data?.feeRate!!
                                            val standard =
                                                BigDecimal(feeRate).multiply(BigDecimal(0.85))
                                            var standardGasPrice =
                                                BigDecimal(feeRate).multiply(BigDecimal(0.85))//0.6
                                                    .divide(
                                                        BigDecimal(GWEI_ETH),
                                                        8,
                                                        BigDecimal.ROUND_HALF_UP
                                                    )
                                            var rapidGasPrice =
                                                BigDecimal(feeRate).multiply(BigDecimal(1))
                                                    .divide(
                                                        BigDecimal(GWEI_ETH),
                                                        8,
                                                        BigDecimal.ROUND_HALF_UP
                                                    )

                                            rgFee?.setOnCheckedChangeListener(object :
                                                RadioGroup.OnCheckedChangeListener {
                                                override fun onCheckedChanged(
                                                    group: RadioGroup?,
                                                    checkedId: Int
                                                ) {
                                                    when (checkedId) {
                                                        R.id.rbRecommended -> {
                                                            if (BigDecimal(record?.gasPrice).subtract(
                                                                    standard
                                                                ) >= BigDecimal.ZERO
                                                            ) {//1.25倍和1.5倍
                                                                afterGasPrice =
                                                                    beforeGasPrice.multiply(
                                                                        BigDecimal(1.25)
                                                                    ).stripTrailingZeros()
                                                                        .toPlainString()
                                                                minerFee = beforeGasPrice.multiply(
                                                                    BigDecimal(1.25)
                                                                ).multiply(BigDecimal(21000))
                                                                    .divide(
                                                                        BigDecimal(GWEI_ETH),
                                                                        8,
                                                                        BigDecimal.ROUND_HALF_UP
                                                                    ).stripTrailingZeros()
                                                                    .toPlainString()
                                                            } else {
                                                                afterGasPrice =
                                                                    standardGasPrice.stripTrailingZeros()
                                                                        .toPlainString()
                                                                minerFee =
                                                                    standardGasPrice.multiply(
                                                                        BigDecimal(21000)
                                                                    ).divide(
                                                                        BigDecimal(GWEI_ETH),
                                                                        8,
                                                                        BigDecimal.ROUND_HALF_UP
                                                                    ).stripTrailingZeros()
                                                                        .toPlainString()
                                                            }
                                                            tvAfterAccelerationGasPrice?.text =
                                                                StringBuilder().append(afterGasPrice)
                                                                    .append(" GWEI")
                                                            tvMinerFee?.text =
                                                                StringBuilder().append(minerFee)
                                                                    .append(" ETH")
                                                            tvMinerFeeCalculation?.text =
                                                                StringBuilder().append("≈Gas(21000)*Gas Price(")
                                                                    .append(afterGasPrice)
                                                                    .append(" GWEI)")
                                                        }
                                                        R.id.rbHaste -> {
                                                            if (BigDecimal(record?.gasPrice).subtract(
                                                                    standard
                                                                ) >= BigDecimal.ZERO
                                                            ) {//1.25倍和1.5倍
                                                                afterGasPrice =
                                                                    beforeGasPrice.multiply(
                                                                        BigDecimal(1.5)
                                                                    ).stripTrailingZeros()
                                                                        .toPlainString()
                                                                minerFee = beforeGasPrice.multiply(
                                                                    BigDecimal(1.5)
                                                                ).multiply(BigDecimal(21000))
                                                                    .divide(
                                                                        BigDecimal(GWEI_ETH),
                                                                        8,
                                                                        BigDecimal.ROUND_HALF_UP
                                                                    ).stripTrailingZeros()
                                                                    .toPlainString()
                                                            } else {
                                                                afterGasPrice =
                                                                    rapidGasPrice.stripTrailingZeros()
                                                                        .toPlainString()
                                                                minerFee = rapidGasPrice.multiply(
                                                                    BigDecimal(21000)
                                                                ).divide(
                                                                    BigDecimal(GWEI_ETH),
                                                                    8,
                                                                    BigDecimal.ROUND_HALF_UP
                                                                ).stripTrailingZeros()
                                                                    .toPlainString()
                                                            }
                                                            tvAfterAccelerationGasPrice?.text =
                                                                StringBuilder().append(afterGasPrice)
                                                                    .append(" GWEI")
                                                            tvMinerFee?.text =
                                                                StringBuilder().append(minerFee)
                                                                    .append(" ETH")
                                                            tvMinerFeeCalculation?.text =
                                                                StringBuilder().append("≈Gas(21000)*Gas Price(")
                                                                    .append(afterGasPrice)
                                                                    .append(" GWEI)")

                                                        }
                                                    }
                                                }
                                            })
                                            //默认 选中推荐速率,解决RadioGroup不触发的bug
                                            if (BigDecimal(record?.gasPrice).subtract(standard) >= BigDecimal.ZERO) {//1.25倍和1.5倍
                                                afterGasPrice =
                                                    beforeGasPrice.multiply(BigDecimal(1.25))
                                                        .stripTrailingZeros().toPlainString()
                                                minerFee = beforeGasPrice.multiply(BigDecimal(1.25))
                                                    .multiply(BigDecimal(21000)).divide(
                                                        BigDecimal(GWEI_ETH),
                                                        8,
                                                        BigDecimal.ROUND_HALF_UP
                                                    ).stripTrailingZeros().toPlainString()
                                            } else {
                                                afterGasPrice =
                                                    standardGasPrice.stripTrailingZeros()
                                                        .toPlainString()
                                                minerFee =
                                                    standardGasPrice.multiply(BigDecimal(21000))
                                                        .divide(
                                                            BigDecimal(GWEI_ETH),
                                                            8,
                                                            BigDecimal.ROUND_HALF_UP
                                                        ).stripTrailingZeros().toPlainString()
                                            }
                                            tvAfterAccelerationGasPrice?.text =
                                                StringBuilder().append(afterGasPrice)
                                                    .append(" GWEI")
                                            tvMinerFee?.text =
                                                StringBuilder().append(minerFee).append(" ETH")
                                            tvMinerFeeCalculation?.text =
                                                StringBuilder().append("≈Gas(21000)*Gas Price(")
                                                    .append(afterGasPrice).append(" GWEI)")
                                        }

                                    }

                                    override fun onError(e: Exception?) {
                                        LoadingDialog.cancel()
                                        rgFee?.setOnCheckedChangeListener(object :
                                            RadioGroup.OnCheckedChangeListener {
                                            override fun onCheckedChanged(
                                                group: RadioGroup?,
                                                checkedId: Int
                                            ) {
                                                when (checkedId) {
                                                    R.id.rbRecommended -> {
                                                        afterGasPrice =
                                                            beforeGasPrice.multiply(BigDecimal(1.25))
                                                                .stripTrailingZeros()
                                                                .toPlainString()
                                                        minerFee =
                                                            beforeGasPrice.multiply(BigDecimal(1.25))
                                                                .multiply(BigDecimal(21000)).divide(
                                                                    BigDecimal(GWEI_ETH),
                                                                    8,
                                                                    BigDecimal.ROUND_HALF_UP
                                                                ).stripTrailingZeros()
                                                                .toPlainString()
                                                        tvAfterAccelerationGasPrice?.text =
                                                            StringBuilder().append(afterGasPrice)
                                                                .append(" GWEI")
                                                        tvMinerFee?.text =
                                                            StringBuilder().append(minerFee)
                                                                .append(" ETH")
                                                        tvMinerFeeCalculation?.text =
                                                            StringBuilder().append("≈Gas(21000)*Gas Price(")
                                                                .append(afterGasPrice)
                                                                .append(" GWEI)")
                                                    }
                                                    R.id.rbHaste -> {
                                                        afterGasPrice =
                                                            beforeGasPrice.multiply(BigDecimal(1.5))
                                                                .stripTrailingZeros()
                                                                .toPlainString()
                                                        minerFee =
                                                            beforeGasPrice.multiply(BigDecimal(1.5))
                                                                .multiply(BigDecimal(21000)).divide(
                                                                    BigDecimal(GWEI_ETH),
                                                                    8,
                                                                    BigDecimal.ROUND_HALF_UP
                                                                ).stripTrailingZeros()
                                                                .toPlainString()
                                                        tvAfterAccelerationGasPrice?.text =
                                                            StringBuilder().append(afterGasPrice)
                                                                .append(" GWEI")
                                                        tvMinerFee?.text =
                                                            StringBuilder().append(minerFee)
                                                                .append(" ETH")
                                                        tvMinerFeeCalculation?.text =
                                                            StringBuilder().append("≈Gas(21000)*Gas Price(")
                                                                .append(afterGasPrice)
                                                                .append(" GWEI)")
                                                    }
                                                }
                                            }
                                        })
                                        //默认 选中推荐速率,解决RadioGroup不触发的bug
                                        afterGasPrice = beforeGasPrice.multiply(BigDecimal(1.25))
                                            .stripTrailingZeros().toPlainString()
                                        minerFee = beforeGasPrice.multiply(BigDecimal(1.25))
                                            .multiply(BigDecimal(21000)).divide(
                                                BigDecimal(GWEI_ETH),
                                                8,
                                                BigDecimal.ROUND_HALF_UP
                                            ).stripTrailingZeros().toPlainString()
                                        tvAfterAccelerationGasPrice?.text =
                                            StringBuilder().append(afterGasPrice).append(" GWEI")
                                        tvMinerFee?.text =
                                            StringBuilder().append(minerFee).append(" ETH")
                                        tvMinerFeeCalculation?.text =
                                            StringBuilder().append("≈Gas(21000)*Gas Price(")
                                                .append(afterGasPrice).append(" GWEI)")
                                    }
                                })
                        })

                    btnCancel?.setOnClickListener { //取消
                        dialog?.dismiss()
                        getEthTransactionStatus(isSuccess = { value ->
                            if (value!!) {
                                finish()
                                ToastUtils.make()
                                    .show("${getString(R.string.transaction_completed_cannot_be_cancelled)}")
                            } else {//显示密码输入框
                                if (BigDecimal(coin?.balance).subtract(BigDecimal(minerFee)) < BigDecimal.ZERO) {
                                    ToastUtils.make().show(R.string.balance_is_not_enough)
                                    return@getEthTransactionStatus
                                }
                                showPwdDialog()
                            }
                        })

                    }
                    btnConfirmAcceleration?.setOnClickListener {//加速
                        dialog?.dismiss()
                        getEthTransactionStatus(isSuccess = { value ->
                            if (value!!) {//显示密码输入框
                                finish()
                                ToastUtils.make()
                                    .show("${getString(R.string.transaction_completed)}")
                            } else {
                                if (BigDecimal(coin?.balance).subtract(BigDecimal(minerFee)) < BigDecimal.ZERO) {
                                    ToastUtils.make().show(R.string.balance_is_not_enough)
                                    return@getEthTransactionStatus
                                }
                                showPwdDialog()
                            }
                        })
                    }

                }
            })
            .setDimAmount(0.3f)
            .setGravity(Gravity.BOTTOM)
            .setOutCancel(true)
            .setAnimStyle(R.style.BottomAnimation)
            .show(supportFragmentManager)
    }


    /**
     * 校验密码正确性
     * 考虑封装到dialogUtil中去
     */
    var pwdStr = ""
    private fun showPwdDialog() {
        erc20Coin?.wallet_id?.let {
            DialogUtils.showCommonVerifyPasswordDialog(this, supportFragmentManager, it,
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
        val memo = record?.memo
        val transferAmount = record?.amount
        val transferRequest = TransferRequest()
        transferRequest.toAddress = record?.to_address
        val transferFeeRateData = BigDecimal(afterGasPrice).multiply(BigDecimal(GWEI_ETH))
            .setScale(0, BigDecimal.ROUND_HALF_DOWN).stripTrailingZeros().toPlainString()
        transferRequest.feeRate = transferFeeRateData
        transferRequest.keyId = erc20Coin?.wallet_id
        transferRequest.keystoreDir = DirUtils.createKeyStoreDir()
        transferRequest.passphrase = pwdStr
        transferRequest.nonce = record?.nonce
        var dbTrasferAmount = "0"
        when (coinTag) {
            "ERC20" -> {
                transferRequest.coinType = "ETH"
                transferRequest.tokenType = coinType
                transferRequest.tokenAddress = erc20Coin?.contact_address
                var ERC20Money = if (BigDecimal(transferAmount) == BigDecimal.ZERO) {
                    "0"
                } else {//循环除以10的精度次方
                    var money = transferAmount
                    for (i in 0 until erc20Coin?.decimals!!) {
                        money = BigDecimal(money).multiply(BigDecimal(10)).stripTrailingZeros()
                            .toPlainString()
                    }
                    money
                }
                if (cancelOrAccelerateFlag == 0) {
                    transferRequest.amount = "0"
                    dbTrasferAmount = "0"
                } else {
                    transferRequest.amount = ERC20Money
                    dbTrasferAmount = transferAmount!!
                }
            }
            else -> {
                if (coinType == "ETH") {//ETH转账用wei做单位
                    if (cancelOrAccelerateFlag == 0) {
                        transferRequest.amount = "0"
                        dbTrasferAmount = "0"
                    } else {
                        transferRequest.amount =
                            BigDecimal(transferAmount).multiply(BigDecimal(ETH_TO_WEI))
                                .stripTrailingZeros().toPlainString()
                        dbTrasferAmount = BigDecimal(transferAmount).stripTrailingZeros()
                            .toPlainString()////.multiply(BigDecimal(ETH_TO_WEI))
                    }
                }
                transferRequest.coinType = coinType
            }
        }
//        Log.e(
//            tag(),
//            "toAddress==$toAddress;transferAmount==$transferAmount;coinType==$coinType;halfHourFee==$halfHourFee;keyId==$keyId;dir==${DirUtils.createKeyStoreDir()};pwdStr==$pwdStr"
//        )
        val walletType = WalletUtils.getWalletType(
            walletRepository,
            erc20Coin?.wallet_id
        )
        if (walletType == WalletConst.WALLET_TYPE_MULTI) {
            //多链钱包交易
            val transferFromHdWalletRequest = TransferFromHdWalletRequest()
            transferFromHdWalletRequest.account = erc20Coin?.account!!
            transferFromHdWalletRequest.change = erc20Coin?.change!!
            transferFromHdWalletRequest.coin = erc20Coin?.coin!!
            transferFromHdWalletRequest.index = erc20Coin?.index!!
            transferFromHdWalletRequest.transferRequest = transferRequest
            Uv1Helper.transferFromHdWallet(this, transferFromHdWalletRequest,
                object : Uv1Helper.ResponseDataCallback<TransferFromHdWalletResponse> {
                    override fun onSuccess(data: TransferFromHdWalletResponse?) {
                        LoadingDialog.cancel()
                        val txId = data?.transferResponse!!.txId
                        val keyId = data.transferResponse.keyId
                        val nonce = data?.transferResponse.nonce

                        var recordNew = Record(
                            txId,
                            address,
                            dbTrasferAmount,
                            "",
                            0,
                            "转账",
                            coinType,
                            coinTag,
                            record?.to_address,
                            memo = memo,
                            startTime = System.currentTimeMillis(),
                            gasPrice = transferFeeRateData,//新增
                            nonce = nonce//新增
                        )
                        recordNew?.uid = record?.uid!!
                        recordRepository.update(recordNew)
                        finish()
                        ToastUtils.make().setDurationIsLong(true)
                            .show(R.string.submitted_seccessfully)
                    }

                    override fun onError(e: Exception?) {
                        LoadingDialog.cancel()
                        if (e?.localizedMessage == "insufficient funds available to construct transaction") {
                            ToastUtils.make().show(R.string.balance_is_not_enough)
                        } else {
                            ToastUtils.make().setDurationIsLong(true)
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
                        var recordNew = Record(
                            txId,
                            address,
                            dbTrasferAmount,
                            "",
                            0,
                            "转账",
                            coinType,
                            coinTag,
                            record?.to_address,
                            memo = memo,
                            startTime = System.currentTimeMillis(),
                            gasPrice = transferFeeRateData,//新增
                            nonce = nonce//新增
                        )
                        recordNew?.uid = record?.uid!!
                        recordRepository.update(recordNew)
                        finish()
                        ToastUtils.make().setDurationIsLong(true)
                            .show(R.string.submitted_seccessfully)
                    }

                    override fun onError(e: java.lang.Exception?) {
                        LoadingDialog.cancel()
                        if (e?.localizedMessage == "insufficient funds available to construct transaction") {
                            ToastUtils.make().show(R.string.balance_is_not_enough)
                        } else {
                            ToastUtils.make().setDurationIsLong(true)
                                .show(R.string.failed_ti_submit)
                        }
                    }

                })
        }
    }
}
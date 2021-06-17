package com.yongqi.wallet.ui.receiveAndTransfer.ui

import android.content.Intent
import android.util.Log
import android.view.Gravity
import android.view.View
import io.reactivex.Observer
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import api.*
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.CollectionUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gyf.immersionbar.ktx.immersionBar
import com.shehuan.nicedialog.BaseNiceDialog
import com.shehuan.nicedialog.NiceDialog
import com.shehuan.nicedialog.ViewConvertListener
import com.shehuan.nicedialog.ViewHolder
import com.yongqi.wallet.App.Companion.context
import com.yongqi.wallet.api.NetApi
import com.yongqi.wallet.R
import com.yongqi.wallet.base.BaseActivity
import com.yongqi.wallet.bean.Record
import com.yongqi.wallet.bean.ResponseWrapper
import com.yongqi.wallet.bean.TxDetails
import com.yongqi.wallet.bean.TxHashs
import com.yongqi.wallet.config.AppConst
import com.yongqi.wallet.config.AppConst.ETH_TO_WEI
import com.yongqi.wallet.config.AppConst.TRX_TO_SUN
import com.yongqi.wallet.config.CoinConst
import com.yongqi.wallet.databinding.ActivityReceiveAndTransferBinding
import com.yongqi.wallet.db.record.RecordRepository
import com.yongqi.wallet.db.wallet.WalletRepository
import com.yongqi.wallet.net.APIClient
import com.yongqi.wallet.net.APIResponse
import com.yongqi.wallet.ui.createWallet.ui.BackupMnemonicActivity
import com.yongqi.wallet.ui.receiveAndTransfer.adapter.TransferRecordAdapter
import com.yongqi.wallet.ui.receiveAndTransfer.bean.TRXTransactionBean
import com.yongqi.wallet.ui.receiveAndTransfer.bean.TRXUv1TransactionBean
import com.yongqi.wallet.ui.receiveAndTransfer.receive.ui.ReceiveActivity
import com.yongqi.wallet.ui.receiveAndTransfer.transfer.ui.TRXTransferActivity
import com.yongqi.wallet.ui.receiveAndTransfer.transfer.ui.TransferActivity
import com.yongqi.wallet.ui.receiveAndTransfer.viewModel.ReceiveAndTransferViewModel
import com.yongqi.wallet.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_receive_and_transfer.*
import kotlinx.android.synthetic.main.common_title.*
import java.math.BigDecimal
import java.util.*
import api.ToTrxAddressRequest
import com.blankj.utilcode.util.GsonUtils
import com.yongqi.wallet.BuildConfig
import com.yongqi.wallet.config.AppConst.TRX_URL


class ReceiveAndTransferActivity :
    BaseActivity<ActivityReceiveAndTransferBinding, ReceiveAndTransferViewModel>() {
    /**
     * 1、交易地址address
     * 2、币资产
     * 3、coinType
     * 4、keyId
     * 5、密码passphrase
     */
    override fun getLayoutResource(): Int = R.layout.activity_receive_and_transfer

    private val recordRepository by lazy { RecordRepository(this) }


    var address = ""
    var coinAmount = ""
    var coinType = ""
    var keyId = ""
    var coinTag = ""
    var contactAddress = ""
    var decimals = 0
    override fun initData() {
        immersionBar {
            statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
            titleBar(iTitle) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
        }

        address = intent.getStringExtra("address").toString()//交易地址
        coinAmount = intent.getStringExtra("coinAmount").toString()//币资产
        coinType = intent.getStringExtra("coinType").toString()//币名字
        keyId = intent.getStringExtra("keyId").toString()//币id
        coinTag = intent.getStringExtra("coinTag").toString()//币标记
        val image = intent.getStringExtra("image").toString()//币图标
        contactAddress = intent.getStringExtra("contactAddress").toString()//币的合约地址
        decimals = intent.getIntExtra("decimals", 0)//币的精度


        if (coinTag.isEmpty()) {
            tvTitle.text = "$coinType"
        } else {
            tvTitle.text = "$coinType($coinTag)"
        }

        LoadImageUtils.loadImage(coinType, ivCoin, image + "")
        tvMoney.text = coinAmount
        tvAddress0.text = StringUtils.replaceByX(address)
        ivBack.setOnClickListener(onClickListener)
        ivCopy.setOnClickListener(onClickListener)
        tvReceive.setOnClickListener(onClickListener)
        tvTransfer.setOnClickListener(onClickListener)

    }

    override fun onResume() {
        super.onResume()
        queryTransactionTx()//TODO
    }


    private val onClickListener = View.OnClickListener {
        when (it.id) {
            R.id.ivBack -> {
                finish()
            }
            R.id.ivCopy -> {
                ClipboardUtils.copyText(address)
                ToastUtils.make().show("${getString(R.string.copy_success)}")
            }
            R.id.tvReceive -> {
                startActivity(
                    Intent(this, ReceiveActivity::class.java)
                        .putExtra("coinType", coinType)
                        .putExtra("coinTag", coinTag)
                        .putExtra("address", address)
                )
            }
            R.id.tvTransfer -> {
                val walletId = SPUtils.getInstance().getString("walletId")
                val walletRepository = WalletRepository(this)
                val walletById = walletRepository.getWalletById(walletId)
                if (!walletById?.is_backup!! && walletById.type == "Multi") {
                    showTipsDialog()
                } else {
                    if (coinType == CoinConst.TRX || coinTag == "TRC20") {
                        startActivity(
                            Intent(this, TRXTransferActivity::class.java)
                                .putExtra("address", address)
                                .putExtra("coinAmount", coinAmount)
                                .putExtra("coinType", coinType)
                                .putExtra("keyId", keyId)
                                .putExtra("coinTag", coinTag)
                                .putExtra("contactAddress", contactAddress)
                                .putExtra("decimals", decimals)
                        )
                    } else {
                        startActivity(
                            Intent(this, TransferActivity::class.java)
                                .putExtra("address", address)
                                .putExtra("coinAmount", coinAmount)
                                .putExtra("coinType", coinType)
                                .putExtra("keyId", keyId)
                                .putExtra("coinTag", coinTag)
                                .putExtra("contactAddress", contactAddress)
                                .putExtra("decimals", decimals)
                        )
                    }
                }
            }
        }
    }

    /**
     * 弹窗提示
     */
    private fun showTipsDialog() {
        NiceDialog.init()
            .setLayoutId(R.layout.safe_reminder_dialog)     //设置dialog布局文件
//                    .setTheme(R.style.MyDialog) // 设置dialog主题，默认主题继承自Theme.AppCompat.Light.Dialog
            .setConvertListener(object : ViewConvertListener() {
                override fun convertView(holder: ViewHolder?, dialog: BaseNiceDialog?) {
                    val tvBackup = holder?.getView<TextView>(R.id.tvBackup)
                    val tvCancel = holder?.getView<TextView>(R.id.tvCancel)
                    tvBackup?.setOnClickListener {//TODO 跳转到备份助记词页面
                        val walletId = SPUtils.getInstance().getString("walletId")
                        walletId?.let {
                            DialogUtils.showCommonVerifyPasswordDialog(this@ReceiveAndTransferActivity,
                                supportFragmentManager,
                                it,
                                block = { pwd ->
                                    startActivity(
                                        Intent(context, BackupMnemonicActivity::class.java)
                                            .putExtra("pwd", pwd)
                                            .putExtra("walletId", walletId)
                                    )
                                })
                        }
                        dialog?.dismiss()
                    }
                    tvCancel?.setOnClickListener {
                        dialog?.dismiss()
                    }
                }
            })
            .setDimAmount(0.3f)     //调节灰色背景透明度[0-1]，默认0.5f
//                    .setGravity(Gravity.CENTER)     //可选，设置dialog的位置，默认居中，可通过系统Gravity的类的常量修改，例如Gravity.BOTTOM（底部），Gravity.Right（右边），Gravity.BOTTOM|Gravity.Right（右下）
            .setMargin(38)     //dialog左右两边到屏幕边缘的距离（单位：dp），默认0dp
//                    .setWidth(270)     //dialog宽度（单位：dp），默认为屏幕宽度，-1代表WRAP_CONTENT
//            .setHeight(159)     //dialog高度（单位：dp），默认为WRAP_CONTENT
            .setOutCancel(false)     //点击dialog外是否可取消，默认true
            //.setAnimStyle(R.style.EnterExitAnimation)     //设置dialog进入、退出的自定义动画；根据设置的Gravity，默认提供了左、上、右、下位置进入退出的动画
            .show(supportFragmentManager)     //显示dialog

    }


    private fun queryTransactionTx() {
        refreshList()
        if (coinType == "BTC" || coinTag == "OMNI") {
            queryBtcTransaction()
        }

        if (coinType == "ETH" || coinTag == "ERC20") {
            queryETHTransaction()
        }

        if (coinType == CoinConst.TRX || coinTag == "TRC20") {
            queryTRXTransactionTxID()
        }
    }

    /**
     * 查询ETH交易详情
     */
    private fun queryEthTsDetail() {

        var records = recordRepository.getRecordsByAddressAndCointype(
            address,
            coinType
        ) as MutableList<Record>?

        val ethHashs: MutableList<String>? = mutableListOf()
        records?.forEachIndexed { index, record ->
            if (record.time == 0L || record.miner_fee == "0" || BigDecimal(record.confirmations) < BigDecimal(7)) {
                record.tx_id?.let { ethHashs?.add(it) }
            }
        }
        if (ethHashs.isNullOrEmpty() || ethHashs.size < 1) {
            refreshList()
        } else {
            /**
             * 查询ETH交易详情
             */
            APIClient.instance.instanceRetrofit(NetApi::class.java)
                .getTsEth(coinType, ethHashs)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : APIResponse<TxHashs>(this) {
                    override fun success(data: TxHashs?) {//更新数据库中的数据,刷新列表
                        Log.e(tag(), "onResponse==${data.toString()}")
                        var txhashs = data?.txhashs

                        var from_ = ""
                        var to_ = ""
                        var value_ = ""
                        txhashs?.forEachIndexed { index, txHashBean ->
                            var hash = txHashBean.hash//tx_id 交易id
                            var status = txHashBean.status//1:成功;0:失败
                            var timestamp = txHashBean.timestamp
                            var blockNumber = txHashBean.blockNumber
                            if (coinTag == "ERC20") {
//                                var inputDecode = txHashBean.inputDecode //TODO 修改
                                var logs = txHashBean.logs
                                logs?.forEachIndexed { index, inputDecodeBean ->
                                    if (inputDecodeBean?._address == contactAddress) {
                                        from_ =
                                            inputDecodeBean?._from.toString()//如果from是自己的address,则为转账
                                        to_ = inputDecodeBean?._to.toString()//如果to是自己的address,则为收款
                                        value_ =
                                            if (BigDecimal(inputDecodeBean?._value.toString()) == BigDecimal.ZERO) {
                                                "0"
                                            } else {//循环除以10的精度次方
                                                var money = inputDecodeBean?._value.toString()
                                                for (i in 0 until decimals) {
                                                    money = BigDecimal(money).divide(BigDecimal(10))
                                                        .stripTrailingZeros().toPlainString()
                                                }
                                                money
                                            }
                                    }
                                }
                            } else {
                                from_ = txHashBean.from_.toString()//如果from是自己的address,则为转账
                                to_ = txHashBean.to_.toString()//如果to是自己的address,则为收款
                                value_ =
                                    if (BigDecimal(txHashBean.value_.toString()) == BigDecimal.ZERO) {
                                        "0"
                                    } else {
                                        BigDecimal(txHashBean.value_.toString()).divide(
                                            BigDecimal(ETH_TO_WEI)
                                        ).stripTrailingZeros().toPlainString()
                                    }
                            }

                            records?.forEachIndexed { index, record ->
                                if (hash == record.tx_id && record.name == coinType) {
                                    var type = if (address == from_) {
                                        "转账"
                                    } else {
                                        "收款"
                                    }

                                    to_ = if (address == from_) {
                                        to_
                                    } else {
                                        from_
                                    }

                                    var weiMoney = BigDecimal(txHashBean.gasUsed).multiply(
                                        BigDecimal(txHashBean.gasPrice)
                                    ).toString()

                                    var minerFee = BigDecimal(weiMoney).divide(
                                        BigDecimal(AppConst.ETH_TO_WEI),
                                        8,
                                        BigDecimal.ROUND_HALF_UP
                                    ).stripTrailingZeros().toPlainString()


                                    var recordNew = timestamp?.let {
                                        Record(
                                            hash,
                                            address,
                                            value_,
                                            status,
                                            it,
                                            type,
                                            record.name,
                                            record.coin_tag,
                                            to_,
                                            blockNumber,
                                            minerFee,
                                            record.memo,
                                            valid = record.valid,
                                            confirmations = txHashBean.confirmations,
                                            gasPrice = txHashBean.gasPrice,
                                            nonce = txHashBean.nonce
                                        )
                                    }
                                    recordNew?.uid = record.uid
                                    recordRepository.update(recordNew) //status,from_,to_,value_,timestamp,hash
                                }
                            }
                        }
                        refreshList()
                    }

                    override fun failure(errorMsg: String?) {
                        Log.e(tag(), "onFailure==${errorMsg}")

                    }
                })
        }
    }

    /**
     * 查询TRX交易详情
     */
    private fun queryTRXTsDetail() {
        val records = recordRepository.getRecordsByAddressAndCointype(
            address,
            coinType
        ) as MutableList<Record>?

        val trxHashs: MutableList<String> = mutableListOf()
        records?.forEachIndexed { _, record ->
            if (record.time == 0L) {
                record.tx_id?.let { trxHashs.add(it) }
            }
        }
        if (trxHashs.isNullOrEmpty() || trxHashs.size < 1) {
            refreshList()
        } else {
            val queryTrxTransactionRequest = QueryTrxTransactionRequest()
            var trxTxIds = ""
            trxHashs.forEachIndexed { index, txId ->
                run {
                    trxTxIds += if (index == trxHashs.size - 1) {
                        txId
                    } else {
                        "$txId,"
                    }
                }
            }
            queryTrxTransactionRequest.txIds = trxTxIds
            Uv1Helper.getTrxTransaction(
                this,
                queryTrxTransactionRequest,
                object : Uv1Helper.ResponseDataCallback<QueryTrxTransactionResponse> {
                    override fun onSuccess(data: QueryTrxTransactionResponse?) {
                        val list: MutableList<TRXUv1TransactionBean> =
                            Gson().fromJson(
                                data!!.trxTransaction,
                                object : TypeToken<ArrayList<TRXUv1TransactionBean>>() {}.type
                            )
                        var from_ = ""
                        var to_ = ""
                        list.forEachIndexed { index, txHashBean ->
                            Log.e("getTrxTransaction",GsonUtils.toJson(txHashBean))
                            val hash = txHashBean.txid//tx_id 交易id
                            var status = "1"//1:成功;0:失败
                            if (txHashBean.result == "SUCCESS" || txHashBean.result == "success") {
                                status = "1"
                            }else{
                                status = "0"
                            }

                            val timestamp = txHashBean.blocktime/1000
                            val blockNumber = txHashBean.blockhash

                            from_ = txHashBean.from.toString()//如果from是自己的address,则为转账
                            to_ = txHashBean.to.toString()//如果to是自己的address,则为收款

                            val value:String = when {
                                BigDecimal(txHashBean.amount.toString()) == BigDecimal.ZERO -> {
                                    "0"
                                }
                                coinTag == "TRC20" -> {
                                    txHashBean.amount.toString()
                                }
                                else -> {
                                    BigDecimal(txHashBean.amount.toString()).divide(
                                        BigDecimal(TRX_TO_SUN)
                                    ).stripTrailingZeros().toPlainString()
                                }
                            }

                            val minerFee: String = if (txHashBean.fee == "0") {
                                "0"
                            }else{
                                BigDecimal(txHashBean.fee).divide(
                                    BigDecimal(TRX_TO_SUN),
                                    8,
                                    BigDecimal.ROUND_HALF_UP
                                ).stripTrailingZeros().toPlainString()
                            }


                            records?.forEachIndexed { index, record ->
                                if (hash == record.tx_id && record.name == coinType) {
                                    val type = if (address == from_) {
                                        "转账"
                                    } else {
                                        "收款"
                                    }

                                    to_ = if (address == from_) {
                                        to_
                                    } else {
                                        from_
                                    }


                                    val recordNew = Record(
                                        hash,
                                        address,
                                        value,
                                        status,
                                        timestamp,
                                        type,
                                        record.name,
                                        record.coin_tag,
                                        to_,
                                        blockNumber,
                                        minerFee,
                                        record.memo,
                                        valid = record.valid,
                                        confirmations = "",
                                        gasPrice = "-1",
                                        nonce = ""
                                    )
                                    recordNew.uid = record.uid
                                    recordRepository.update(recordNew) //status,from_,to_,value_,timestamp,hash
                                }
                            }
                        }
                        refreshList()
                    }

                    override fun onError(e: java.lang.Exception?) {

                    }

                })

//            APIClient.instance.instanceRetrofit(NetApi::class.java)
//                .getTsEth(coinType, ethHashs)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(object : APIResponse<TxHashs>(this) {
//                    override fun success(data: TxHashs?) {//更新数据库中的数据,刷新列表
//                        Log.e(tag(), "onResponse==${data.toString()}")
//                        var txhashs = data?.txhashs
//
//                        var from_ = ""
//                        var to_ = ""
//                        var value_ = ""
//                        txhashs?.forEachIndexed { index, txHashBean ->
//                            var hash = txHashBean.hash//tx_id 交易id
//                            var status = txHashBean.status//1:成功;0:失败
//                            var timestamp = txHashBean.timestamp
//                            var blockNumber = txHashBean.blockNumber
//                            if (coinTag == "ERC20") {
////                                var inputDecode = txHashBean.inputDecode //TODO 修改
//                                var logs = txHashBean.logs
//                                logs?.forEachIndexed { index, inputDecodeBean ->
//                                    if (inputDecodeBean?._address == contactAddress) {
//                                        from_ =
//                                            inputDecodeBean?._from.toString()//如果from是自己的address,则为转账
//                                        to_ = inputDecodeBean?._to.toString()//如果to是自己的address,则为收款
//                                        value_ =
//                                            if (BigDecimal(inputDecodeBean?._value.toString()) == BigDecimal.ZERO) {
//                                                "0"
//                                            } else {//循环除以10的精度次方
//                                                var money = inputDecodeBean?._value.toString()
//                                                for (i in 0 until decimals) {
//                                                    money = BigDecimal(money).divide(BigDecimal(10))
//                                                        .stripTrailingZeros().toPlainString()
//                                                }
//                                                money
//                                            }
//                                    }
//                                }
//                            } else {
//                                from_ = txHashBean.from_.toString()//如果from是自己的address,则为转账
//                                to_ = txHashBean.to_.toString()//如果to是自己的address,则为收款
//                                value_ =
//                                    if (BigDecimal(txHashBean.value_.toString()) == BigDecimal.ZERO) {
//                                        "0"
//                                    } else {
//                                        BigDecimal(txHashBean.value_.toString()).divide(
//                                            BigDecimal(ETH_TO_WEI)
//                                        ).stripTrailingZeros().toPlainString()
//                                    }
//                            }
//
//                            records?.forEachIndexed { index, record ->
//                                if (hash == record.tx_id && record.name == coinType) {
//                                    var type = if (address == from_) {
//                                        "转账"
//                                    } else {
//                                        "收款"
//                                    }
//
//                                    to_ = if (address == from_) {
//                                        to_
//                                    } else {
//                                        from_
//                                    }
//
//                                    var weiMoney = BigDecimal(txHashBean.gasUsed).multiply(
//                                        BigDecimal(txHashBean.gasPrice)
//                                    ).toString()
//
//                                    var minerFee = BigDecimal(weiMoney).divide(
//                                        BigDecimal(AppConst.ETH_TO_WEI),
//                                        8,
//                                        BigDecimal.ROUND_HALF_UP
//                                    ).stripTrailingZeros().toPlainString()
//
//
//                                    var recordNew = timestamp?.let {
//                                        Record(
//                                            hash,
//                                            address,
//                                            value_,
//                                            status,
//                                            it,
//                                            type,
//                                            record.name,
//                                            record.coin_tag,
//                                            to_,
//                                            blockNumber,
//                                            minerFee,
//                                            record.memo,
//                                            valid = record.valid,
//                                            confirmations = txHashBean.confirmations,
//                                            gasPrice = txHashBean.gasPrice,
//                                            nonce = txHashBean.nonce
//                                        )
//                                    }
//                                    recordNew?.uid = record.uid
//                                    recordRepository.update(recordNew) //status,from_,to_,value_,timestamp,hash
//                                }
//                            }
//                        }
//                        refreshList()
//                    }
//
//                    override fun failure(errorMsg: String?) {
//                        Log.e(tag(), "onFailure==${errorMsg}")
//
//                    }
//                })
        }
    }


    /**
     * 查询TRX交易哈希
     */
    private fun queryTRXTransactionTxID() {
        var tokenAddress = address
        tokenAddress = if (coinTag != "TRC20") {
            address
        } else {
            tokenAddress
        }

        val records = recordRepository.getRecordsByAddressAndCointype(
            address,
            coinType
        ) as MutableList<Record>?
        val arr = mutableListOf<Record>()
        records?.forEachIndexed { _, record ->

            if (coinType == CoinConst.TRX || coinTag == "TRC20") {// && record.name == "BTC"
                arr.add(record)
            }
        }

        APIClient.instance.instanceRetrofit(NetApi::class.java)
            .getTRXTransaction(BuildConfig.TRX_TRANSACTION_URL + "/v1/accounts/$tokenAddress/transactions")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<TRXTransactionBean> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: TRXTransactionBean) {
                    if (CollectionUtils.isEmpty(t.data)) {
                        refreshList()
                        return
                    }
                    val trxData = t.data
                    //向数据库record表里插入不存在的 txHash
                    val diffObject = mutableListOf<String>()
                    if (arr.size < 1) {
                        trxData?.forEachIndexed { _, dataBean ->
                            //to_address 不为空 主链 插入数据库
                            if (dataBean.raw_data.contract[0].type == "TransferContract" && coinType == "TRX" && coinTag != "TRC20") {
                                recordRepository.insert(
                                    Record(
                                        tx_id = dataBean.txID,
                                        address = address,
                                        name = coinType,
                                        coin_tag = coinTag
                                    )
                                )
                            }
                            //contract_address 不为空 代币
                            if (dataBean.raw_data.contract[0].type == "TriggerSmartContract" && coinTag == "TRC20") {
                                //获取contract_address 进行转换
                                val toTrxAddressRequest = ToTrxAddressRequest()
                                toTrxAddressRequest.address =
                                    dataBean.raw_data.contract[0].parameter.value.contract_address
                                Uv1Helper.toTrxAddress(
                                    this@ReceiveAndTransferActivity,
                                    toTrxAddressRequest,
                                    object : Uv1Helper.ResponseDataCallback<ToTrxAddressResponse> {
                                        override fun onSuccess(data: ToTrxAddressResponse?) {
                                            //转换后的地址与合约地址相同则是当前代币 则保存交易哈希
                                            if (data?.address == contactAddress) {
                                                recordRepository.insert(
                                                    Record(
                                                        tx_id = dataBean.txID,
                                                        address = address,
                                                        name = coinType,
                                                        coin_tag = coinTag
                                                    )
                                                )
                                            }
                                        }

                                        override fun onError(e: java.lang.Exception?) {

                                        }

                                    })
                            }
                        }

                    } else {//向数据库Record表里不存在数据,将查找出来的的数据存到数据库
                        trxData?.forEachIndexed { _, dataBean ->
                            var isHave = false
                            arr.forEachIndexed { _, record ->
                                if (record.tx_id == dataBean.txID && record.name == coinType) {
                                    isHave = true
                                }
                            }

                            if (dataBean.raw_data.contract[0].type == "TransferContract" && coinType == "TRX" && coinTag != "TRC20" && !isHave) {
                                recordRepository.insert(
                                    Record(
                                        tx_id = dataBean.txID,
                                        address = address,
                                        name = coinType,
                                        coin_tag = coinTag
                                    )
                                )
                            }

                            if (dataBean.raw_data.contract[0].type == "TriggerSmartContract" && coinTag == "TRC20" && !isHave) {
                                //获取contract_address 进行转换
                                val toTrxAddressRequest = ToTrxAddressRequest()
                                toTrxAddressRequest.address =
                                    dataBean.raw_data.contract[0].parameter.value.contract_address
                                Uv1Helper.toTrxAddress(
                                    this@ReceiveAndTransferActivity,
                                    toTrxAddressRequest,
                                    object : Uv1Helper.ResponseDataCallback<ToTrxAddressResponse> {
                                        override fun onSuccess(data: ToTrxAddressResponse?) {
                                            //转换后的地址与合约地址相同则是当前代币 则保存交易哈希
                                            if (data?.address == contactAddress) {
                                                recordRepository.insert(
                                                    Record(
                                                        tx_id = dataBean.txID,
                                                        address = address,
                                                        name = coinType,
                                                        coin_tag = coinTag
                                                    )
                                                )
                                            }
                                        }

                                        override fun onError(e: java.lang.Exception?) {

                                        }
                                    })
                            }
                        }
                    }

                    queryTRXTsDetail()

                }

                override fun onError(e: Throwable) {

                }

                override fun onComplete() {

                }

            })

    }



    /**
     * 查询ETH交易 hash
     */
    private fun queryETHTransaction() {
        var blockHeight: Long = 0

        var records0 = recordRepository.getRecordsByAddressAndCointype(
            address,
            coinType
        ) as MutableList<Record>?
        records0?.forEachIndexed { x, recordX ->
            records0?.forEachIndexed { y, recordY ->
                if (recordX.tx_id==recordY.tx_id && x != y){
                    recordRepository.delete(recordY)
                }
            }
        }
        var records = recordRepository.getRecordsByAddressAndCointype(
            address,
            coinType
        ) as MutableList<Record>?
        var arr = mutableListOf<Record>()
        records?.forEachIndexed { index, record ->
            if (record.block_height!! > blockHeight) {
                blockHeight = record?.block_height!!
            }
            if (coinType == "ETH" || coinTag == "ERC20") {// && record.name == "BTC"
                arr.add(record)
            }
        }
        var tokenAddress = ""
//        var mCoinType = if (coinType == "ETH") {
//            coinType
//        } else {
//            "$coinType _$coinTag"
//        }
        if (coinType == "ETH") {

        } else {
            tokenAddress = contactAddress
        }
        Log.e(tag(), "onResponse==${arr.toString()}")

        APIClient.instance.instanceRetrofit(NetApi::class.java)
            .getETHTsHash(
                coinType,
                address,
                blockHeight,
                tokenAddress = tokenAddress,
                confirmations = "1"
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : APIResponse<TxHashs>(this) {
                override fun success(data: TxHashs?) {
                    if (data?.txhashs.isNullOrEmpty()) {
                        refreshList()
                        return
                    }
                    Log.e(tag(), "onResponse==${data.toString()}")
                    var txhashs = data?.txhashs
                    //向数据库record表里插入不存在的txhash
                    var diffObject = mutableListOf<String>()
                    if (arr.size < 1) {
                        txhashs?.forEachIndexed { index, txHashBean ->
                            recordRepository.insert(
                                Record(
                                    tx_id = txHashBean.hash,
                                    address = address,
                                    name = coinType,
                                    coin_tag = coinTag
                                )
                            )
                        }
                    } else {//向数据库Record表里不存在数据,将查找出来的的数据存到数据库
                        txhashs?.forEachIndexed { index, txHashBean ->//TODO
                            var isHave = false
                            arr.forEachIndexed { index, record ->
                                if (record.tx_id == txHashBean.hash && record.name == coinType) {
//                                if (record.tx_id == txHashBean.txid) {
                                    isHave = true
                                }
                            }

                            if (!isHave) {
                                txHashBean.hash?.let { diffObject.add(it) }
                            }
                        }

                        if (diffObject.size > 0) {
                            diffObject.forEachIndexed { index, str ->
//                                var record = recordRepository.getRecordsTxId(txHashBean.hash)
//                                if (record == null) {
                                recordRepository.insert(
                                    Record(
                                        address = address,
                                        tx_id = str,
                                        name = coinType,
                                        coin_tag = coinTag
                                    )
                                )
//                                }
                            }
                        }

                    }

                    queryEthTsDetail()
                }

                override fun failure(errorMsg: String?) {
                    Log.e(tag(), "onFailure==${errorMsg}")

                }
            })
    }


    /**
     * 查询BTC交易 hash
     */
    private fun queryBtcTransaction() {
        var blockHeight: Long = 0
        val arr = mutableListOf<Record>()
        val records = recordRepository.getRecordsByAddressAndCointype(
            address,
            coinType
        ) as MutableList<Record>?
        records?.forEachIndexed { index, record ->
            if (record.block_height!! > blockHeight) {
                blockHeight = record?.block_height!!
            }
            if (coinTag == "OMNI" && record.coin_tag == "OMNI") {
                arr.add(record)
            } else if (coinType == "BTC" && record.name == "BTC") {
                arr.add(record)
            }
        }
        val getAddressesTxIdsRequest = GetAddressesTxIdsRequest()//TODO

        if (coinTag == "OMNI") {
            getAddressesTxIdsRequest.tokenType = "USDT"
        }
        getAddressesTxIdsRequest.coinType = "BTC"
        getAddressesTxIdsRequest.addresses = address
        getAddressesTxIdsRequest.startHeight = blockHeight
        Uv1Helper.getAddressesTxIds(this, getAddressesTxIdsRequest,
            object : Uv1Helper.ResponseDataCallback<GetAddressesTxIdsResponse> {
                override fun onSuccess(data: GetAddressesTxIdsResponse) {
                    val txIdsStr = data.txIds
                    Log.e("getAddressesTxIds", "txIds==${txIdsStr.toString()}")
                    if (txIdsStr.toString().trim().isNullOrEmpty()) {
                        refreshList()
                        return
                    }
                    val txIds = txIdsStr.trim().split(",")
                    //获取完所有交易,开始匹配数据库里的数据，没有相应记录的插入数据,有残缺数据的改为完整的数据，有完整数据的不进行操作
                    //向数据库record表里插入不存在的txhash
                    val diffObject = mutableListOf<String>()
                    if (arr.size < 1) {
                        txIds.forEachIndexed { index, s ->
                            recordRepository.insert(
                                Record(
                                    tx_id = s,
                                    address = address,
                                    name = coinType,
                                    coin_tag = coinTag
                                )
                            )
                        }
                    } else {//向数据库Record表里不存在数据,将查找出来的的数据存到数据库
                        txIds?.forEachIndexed { index, txI ->
//                            txIds?.forEachIndexed { index, txI ->//TODO
                            var isHave = false
                            arr.forEachIndexed { index, record ->
                                if (record.tx_id == txI) {
                                    isHave = true
                                }
                            }
                            if (!isHave) {
                                txI?.let { diffObject.add(it) }
                            }

                        }

                        if (diffObject.size > 0) {
                            diffObject.forEachIndexed { index, str ->
                                recordRepository.insert(
                                    Record(
                                        address = address,
                                        tx_id = str,
                                        name = coinType,
                                        coin_tag = coinTag
                                    )
                                )
                            }
                        }
                    }
                    queryBtcTsDetail()
                }

                override fun onError(e: Exception) {

                }

            })
    }

    /**
     * 查询BTC交易详情
     */
    private fun queryBtcTsDetail() {
        val records = recordRepository.getRecordsByAddressAndCointype(
            address,
            coinType
        ) as MutableList<Record>?
        val btcHashs: MutableList<String>? = mutableListOf()
        records?.forEachIndexed { index, record ->
            if (record.time == 0L || record.miner_fee == "0") {//TODO
                record.tx_id?.let { btcHashs?.add(it) }
            }
        }

        if (btcHashs.isNullOrEmpty() || btcHashs.size < 1) {
            refreshList()
        } else {
            val localCoinType = if (coinTag == "OMNI") {
                "USDT_OMNI"
            } else {
                coinType
            }
            APIClient.instance.instanceRetrofit(NetApi::class.java)
                .getTsBtc(localCoinType, btcHashs)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : APIResponse<TxDetails>(this) {
                    override fun success(data: TxDetails?) {//更新数据库中的数据,刷新列表
                        Log.e(tag(), "onResponse==${data.toString()}")
                        var dataList = data?.list
                        var txhashs = dataList?.txhashs
                        txhashs?.forEachIndexed { index, txHashBean ->
                            var time = txHashBean.blocktime
                            var txid = txHashBean.txid
                            var blockheight = txHashBean.blockheight
                            //var hash = txHashBean.hash
                            var fromAddresses = txHashBean.from_addresses
                            var toAddresses = txHashBean.to_addresses

                            //计算矿工费:fromAddresses里面所有amount相加的值,减去toAddresses里面所有amount相加的值,得到矿工费
                            var fromAmounts = BigDecimal("0")
                            var toAmounts = BigDecimal("0")
                            fromAddresses?.forEachIndexed { index, toAddress ->
                                fromAmounts = fromAmounts.add(BigDecimal(toAddress.amount))
                            }

                            toAddresses?.forEachIndexed { index, toAddress ->
                                toAmounts = toAmounts.add(BigDecimal(toAddress.amount))
                            }
//                            val  minerFee = fromAmounts.subtract(toAmounts).toString()
                            var minerFee = if (coinTag == "OMNI") {
                                BigDecimal(txHashBean.fee).stripTrailingZeros().toPlainString()
                            } else {
                                fromAmounts.subtract(toAmounts).stripTrailingZeros().toPlainString()
                            }

                            var fromAddress = ""
                            var toAddress = ""
                            var fromAmount = ""
                            var toAmount = ""
                            var targetAdress = ""
                            //from为空,to里有数据
                            if (fromAddresses.isNullOrEmpty() && toAddresses!!.size > 0) {
                                //如果to里有自己,那么取自己的那条数据为有效数据
                                var isSelfAddress = false
                                var maxAmount = "0"
                                var avalibleIndex = 0
                                toAddresses.forEachIndexed { index, curAddress ->
                                    var currentAddress = curAddress.address
                                    var currentAmount = curAddress.amount
                                    if (address == currentAddress) {
                                        isSelfAddress = true
                                        toAddress = address
                                        toAmount = currentAmount.toString()
                                    }
                                    var maxAmountD = BigDecimal(maxAmount)
                                    if (maxAmountD.subtract(BigDecimal(currentAmount)) < BigDecimal.ZERO) {
                                        avalibleIndex = index
                                    }
                                }
                                //计算出对方的地址
                                targetAdress = toAddresses[avalibleIndex].address.toString()
                                //如果没有自己的地址那么取最大金额的那条数据为有效数据
                                if (!isSelfAddress) {
                                    //from为空,to里面没有自己
                                } else {
                                    //保存到数据库
                                    var arrAll = recordRepository.getRecordListsTxId(txid)
                                    var arr = mutableListOf<Record>()
                                    arrAll?.forEachIndexed { index, record ->
                                        if ((record.name == coinType) && (record.address == address)) {
                                            arr.add(record)
                                        }
                                    }
                                    arr.forEachIndexed { index, record ->
                                        var amount = if (toAmount.isNotEmpty()) {
                                            toAmount
                                        } else {
                                            fromAmount
                                        }
                                        var recordNew = time?.let {
                                            Record(
                                                txid,
                                                address,
                                                amount,
                                                "",
                                                time ?: 0,
                                                "挖矿奖励",//TODO 矿工奖励
                                                record.name,
                                                record.coin_tag,
                                                toAddress,//TODO targetAdress
                                                blockheight,
                                                minerFee,
                                                record.memo,
                                                valid = if (coinTag == "OMNI") {
                                                    txHashBean.valid
                                                } else {
                                                    ""
                                                },
                                                confirmations = txHashBean.confirmations,
                                                gasPrice = txHashBean.gasPrice,
                                                nonce = txHashBean.nonce
                                            )
                                        }
                                        recordNew?.uid = record.uid
                                        recordRepository.update(recordNew) //status,from_,to_,value_,timestamp,hash
                                    }
                                }
                            }
                            //from有数据,to里无数据(这种情况不应该出现)
                            if (!fromAddresses.isNullOrEmpty() && toAddresses?.size!! < 1) {

                            }
                            //from和to里都有数据

                            if (!fromAddresses.isNullOrEmpty() && toAddresses?.size!! > 0) {
                                var bothSelf = false
                                fromAddresses.forEachIndexed { index, curAddress ->
                                    var currentFromAddress = curAddress.address
                                    toAddresses.forEachIndexed { index, toCurAddress ->
                                        var currentToAddress = toCurAddress.address
                                        if (currentFromAddress == currentToAddress && currentToAddress == address) {
                                            bothSelf = true
                                        }
                                        //如果转账方和收款方里都有自己的数据,那么在收款方里查找到除了自己那条数据外最大的值作为有效数据
                                        if (bothSelf) {
                                            var maxAmount = "0"
                                            var maxAmountIndex = 0
                                            toAddresses.forEachIndexed { index, curToAddress ->
                                                var currentToAddress = curToAddress.address
                                                var currentAmount = curToAddress.amount
                                                //移除自己的数据,查找最大的金额,作为实际收款人信息
                                                if (address != currentToAddress) {
                                                    var maxAmountD = BigDecimal(maxAmount)
                                                    if (maxAmountD.subtract(BigDecimal(currentAmount)) < BigDecimal.ZERO) {
                                                        maxAmount = currentAmount.toString()
                                                        maxAmountIndex = index
                                                    }
                                                }
                                            }
                                            fromAddress = address
                                            targetAdress =
                                                toAddresses[maxAmountIndex].address.toString()
                                            toAmount = toAddresses[maxAmountIndex].amount.toString()

                                            //保存到数据库里
                                            var arrAll = recordRepository.getRecordListsTxId(txid)
                                            var arr = mutableListOf<Record>()
                                            arrAll?.forEachIndexed { index, record ->
                                                if (record.name == coinType && record.address == address) {
                                                    arr.add(record)
                                                }
                                            }
                                            arr.forEachIndexed { index, record ->
                                                var recordNew = time?.let {
                                                    Record(
                                                        txid,
                                                        address,
                                                        toAmount,
                                                        "",
                                                        time ?: 0,
                                                        "转账",
                                                        record.name,
                                                        record.coin_tag,
                                                        targetAdress,
                                                        blockheight,
                                                        minerFee,
                                                        record.memo,
                                                        valid = if (coinTag == "OMNI") {
                                                            txHashBean.valid
                                                        } else {
                                                            ""
                                                        },
                                                        confirmations = txHashBean.confirmations,
                                                        gasPrice = txHashBean.gasPrice,
                                                        nonce = txHashBean.nonce
                                                    )
                                                }
                                                recordNew?.uid = record.uid
                                                recordRepository.update(recordNew) //status,from_,to_,value_,timestamp,hash
                                            }
                                        } else {//转账方和收款方不同时有自己,那么取出来from或者to里的自己那条数据
                                            var fromMaxAmountIndex = 0
                                            var toMaxAmountIndex = 0
                                            var toSelfAmountIndex = 0
                                            var fromMaxAmountD = BigDecimal(0)
                                            var toMaxAmountD = BigDecimal(0)
                                            var isFromSelf = false
                                            //在from里筛选出自己的那一条数据
                                            fromAddresses.forEachIndexed { index, curAddress ->
                                                var currentFromAddress = curAddress.address
                                                var currentAmount = curAddress.amount
                                                var currentAmountD = BigDecimal(currentAmount)
                                                if (address == currentFromAddress) {
                                                    fromAddress = address
                                                    fromAmount = currentAmount.toString()
                                                    isFromSelf = true
                                                }
                                                if (fromMaxAmountD.subtract(currentAmountD) < BigDecimal.ZERO) {
                                                    fromMaxAmountD = currentAmountD
                                                    fromMaxAmountIndex = index
                                                }
                                            }
                                            //在to里筛选出来自己的那一条数据
                                            toAddresses.forEachIndexed { index, curAddress ->
                                                var currentToaddress = curAddress.address
                                                var currentAmount = curAddress.amount
                                                var currentAmountD = BigDecimal(currentAmount)
                                                if (address == currentToAddress) {
                                                    toAddress = currentToAddress
                                                    toAmount = currentAmount.toString()
                                                    isFromSelf = false
                                                    toSelfAmountIndex = index
                                                }
                                                if (toMaxAmountD.subtract(currentAmountD) < BigDecimal.ZERO) {
                                                    toMaxAmountD = currentAmountD
                                                    toMaxAmountIndex = index
                                                }
                                            }
                                            var targetAmount = "0"
                                            if (isFromSelf) {
                                                targetAdress =
                                                    toAddresses[toMaxAmountIndex].address.toString()
                                                targetAmount =
                                                    toAddresses[toMaxAmountIndex].amount.toString()
                                            } else {
                                                targetAdress =
                                                    fromAddresses[fromMaxAmountIndex].address.toString()
                                                targetAmount =
                                                    toAddresses[toSelfAmountIndex].amount.toString()
                                            }
                                            //保存到数据库里
                                            var arrAll = recordRepository.getRecordListsTxId(txid)
                                            var arr = mutableListOf<Record>()
                                            arrAll?.forEachIndexed { index, record ->
                                                if (record.name == coinType && record.address == address) {
                                                    arr.add(record)
                                                }
                                            }
                                            arr.forEachIndexed { index, record ->
                                                var localType =
                                                    if (address != fromAddress) {//转账方不是自己
                                                        "收款"
                                                    } else {
                                                        "转账"
                                                    }
                                                var recordNew = time?.let {
                                                    Record(
                                                        txid,
                                                        address,
                                                        targetAmount,
                                                        "",
                                                        time ?: 0,
                                                        localType,
                                                        record.name,
                                                        record.coin_tag,
                                                        targetAdress,
                                                        blockheight,
                                                        minerFee,
                                                        record.memo,
                                                        valid = if (coinTag == "OMNI") {
                                                            txHashBean.valid
                                                        } else {
                                                            ""
                                                        },
                                                        confirmations = txHashBean.confirmations,
                                                        gasPrice = txHashBean.gasPrice,
                                                        nonce = txHashBean.nonce
                                                    )
                                                }
                                                recordNew?.uid = record.uid
                                                recordRepository.update(recordNew) //status,from_,to_,value_,timestamp,hash
                                            }
                                        }
                                    }
                                }
                            }

                        }
                        refreshList()
                    }

                    override fun failure(errorMsg: String?) {
                        Log.e(tag(), "onFailure==${errorMsg}")

                    }

                })
        }
    }


    fun refreshList() {

        var recordsByAddress = recordRepository.getRecordsByAddressAndCointype(
            address,
            coinType
        ) as MutableList<Record>?
        if (recordsByAddress.isNullOrEmpty()) {
//            rvTransferRecord.visibility = View.GONE
            nsv.visibility = View.GONE
            llNoData.visibility = View.VISIBLE
        } else {
//            rvTransferRecord.visibility = View.VISIBLE
            nsv.visibility = View.VISIBLE
            llNoData.visibility = View.GONE
        }

        var dataList = mutableListOf<Record>()
        recordsByAddress?.forEachIndexed { index, record ->
            if (!record.address.isNullOrEmpty() && !record.to_address.isNullOrEmpty()) {
                if (record.time == 0L) {
                    dataList.add(0, record)
                } else {
                    dataList.add(record)
                }
            }
        }
        if (CollectionUtils.isEmpty(dataList)) {
            nsv.visibility = View.GONE
            llNoData.visibility = View.VISIBLE
        }else{
            nsv.visibility = View.VISIBLE
            llNoData.visibility = View.GONE
        }
        val adapter = TransferRecordAdapter(
            R.layout.transfer_record_item,
            dataList,
            address,
            this
        )
        /**
         * Item 内子View的点击事件：
         *  先注册需要点击的子控件id（注意，请不要写在convert方法里）
         */
        adapter.addChildClickViewIds(R.id.tvCancel, R.id.tvAccelerate)
        adapter.setOnItemChildClickListener { adapter, view, position ->
            val record = adapter.data[position] as Record
            when (view.id) {
                R.id.tvCancel -> {//取消
                    startActivity(
                        Intent(this, TransferDetailActivity::class.java)
                            .putExtra("tx_id", record.tx_id)
                            .putExtra("address", address)
                            .putExtra("coinType", coinType)
                            .putExtra("type", record.type)
                            .putExtra("flag", 0)
                    )
                }
                R.id.tvAccelerate -> {//加速
                    startActivity(
                        Intent(this, TransferDetailActivity::class.java)
                            .putExtra("tx_id", record.tx_id)
                            .putExtra("address", address)
                            .putExtra("coinType", coinType)
                            .putExtra("type", record.type)
                            .putExtra("flag", 1)
                    )
                }
            }
        }

        val layoutManager = LinearLayoutManager(this)
        rvTransferRecord.layoutManager = layoutManager
        rvTransferRecord.adapter = adapter
        adapter.setOnItemClickListener { adapter, view, position ->
            val record = adapter.data[position] as Record
            startActivity(
                Intent(this, TransferDetailActivity::class.java)
                    .putExtra("tx_id", record.tx_id)
                    .putExtra("address", address)
                    .putExtra("coinType", coinType)
                    .putExtra("type", record.type)
            )
        }

    }
}
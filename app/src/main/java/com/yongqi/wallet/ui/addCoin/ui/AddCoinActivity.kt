package com.yongqi.wallet.ui.addCoin.ui

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SPUtils
import com.chad.library.adapter.base.BaseBinderAdapter
import com.gyf.immersionbar.ktx.immersionBar
import com.yongqi.wallet.R
import com.yongqi.wallet.api.NetApi
import com.yongqi.wallet.base.BaseActivity2
import com.yongqi.wallet.bean.*
import com.yongqi.wallet.config.AppConst.TOKEN_URL
import com.yongqi.wallet.config.CoinConst
import com.yongqi.wallet.databinding.ActivityAddCoinBinding
import com.yongqi.wallet.db.coin.CoinRepository
import com.yongqi.wallet.db.wallet.WalletRepository
import com.yongqi.wallet.net.APIClient
import com.yongqi.wallet.net.APIResponse
import com.yongqi.wallet.ui.addCoin.adapter.ERC20CoinAdapter
import com.yongqi.wallet.ui.addCoin.viewModel.AddCoinViewModel
import com.yongqi.wallet.ui.createWallet.ui.AddTokensActivity
import com.yongqi.wallet.utils.WalletUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_coin.*
import java.util.*

/**
 * 添加ERC20或TRC20的代币
 */
class AddCoinActivity : BaseActivity2<ActivityAddCoinBinding>() {

    override fun getLayoutResource(): Int = R.layout.activity_add_coin

    val viewModel by lazy {
        AddCoinViewModel()
    }

    private val walletId by lazy {
        SPUtils.getInstance().getString("walletId")
    }
    private val walletRepository by lazy {
        WalletRepository(this@AddCoinActivity)
    }
    private val wallet by lazy {
        walletId?.let { walletRepository.getWalletById(it) }
    }

    private val coinRepository by lazy {
        CoinRepository(this@AddCoinActivity)
    }
    private val coins by lazy {
        coinRepository.getCoinsById(walletId) as MutableList<Coin>?
    }

    private val adapter by lazy { // 可以直接快速使用，也可以继承BaseBinderAdapter类，重写自己的相关方法
        BaseBinderAdapter()
    }

    private var coinLists: MutableList<Any> = arrayListOf()//全局的数据
    private var trc20Lists: MutableList<Any> = arrayListOf()
    private var erc20Lists: MutableList<Any> = arrayListOf()

    override fun initData() {
        immersionBar {
            statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
            titleBar(llHead) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
        }
        adapter.addItemBinder(String::class.java,TokenClassificationItemBinder())
            .addItemBinder(Coin::class.java,ERC20TRC20ItemBinder())
        getRec20HotToken()
        if (WalletUtils.getWalletType(walletRepository, walletId) == 1) {
            tv_add_chain.visibility = View.VISIBLE
        }else{
            tv_add_chain.visibility = View.GONE
        }
        etSearch.addTextChangedListener {
            if (!it.isNullOrEmpty()) {
                val search = etSearch.text.trim().toString()
                val type = arrayListOf<String>()
                coins?.forEachIndexed { _, coin ->
                    if (coin.name== CoinConst.ETH){
                        type.add(CoinConst.ERC20)
                    }
                    if (coin.name==CoinConst.TRX){
                        type.add(CoinConst.TRC20)
                    }
                }
                APIClient.instance.instanceRetrofit(NetApi::class.java)
                    .fuzzySearch("${TOKEN_URL}fuzzysearch",type,search)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : APIResponse<List<CoinBean>?>(this) {
                        override fun success(data: List<CoinBean>?) {
                            Log.e(tag(), "success==${data.toString()}")
                            coinLists.clear()
                            trc20Lists.clear()
                            erc20Lists.clear()
                            tvHotCoin.visibility = View.GONE
                            tv_add_chain.visibility = View.GONE
                            notifyData(data)
                        }

                        override fun failure(errorMsg: String?) {
                            Log.e(tag(), "onFailure==${errorMsg.toString()}")
                            tvHotCoin.visibility = View.GONE
                            tv_add_chain.visibility = View.GONE
                            coinLists.clear()
                            trc20Lists.clear()
                            erc20Lists.clear()
                            notifyData(null)
                        }
                    })
            } else {
                if (WalletUtils.getWalletType(walletRepository, walletId) == 1) {
                    tv_add_chain.visibility = View.VISIBLE
                }else{
                    tv_add_chain.visibility = View.GONE
                }
                getRec20HotToken()
            }
        }
        tvCancel.setOnClickListener(onClickListener)
        tv_add_chain.setOnClickListener(onClickListener)
    }

    /**
     * 查询rec20热门代币
     */
    private fun getRec20HotToken() {
        val type = arrayListOf<String>()
        coins?.forEachIndexed { _, coin ->
            if (coin.name== CoinConst.ETH){
                type.add(CoinConst.ERC20)
            }
            if (coin.name==CoinConst.TRX){
                type.add(CoinConst.TRC20)
            }
        }
        APIClient.instance.instanceRetrofit(NetApi::class.java)
            .getErc20HotToken("${TOKEN_URL}hot",type)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : APIResponse<List<CoinBean>?>(this) {
                override fun success(data: List<CoinBean>?) {
                    Log.e(tag(), "success==${data.toString()}")
                    if (null == data) {
                        return
                    }
                    coinLists.clear()
                    trc20Lists.clear()
                    erc20Lists.clear()
                    tvHotCoin.visibility = View.VISIBLE
                    notifyData(data)

                }

                override fun failure(errorMsg: String?) {
                    coinLists.clear()
                    trc20Lists.clear()
                    erc20Lists.clear()
                    notifyData(null)
                }
            })
    }

    /**
     * 刷新列表数据
     */
    private fun notifyData(data: List<CoinBean>?) {
        var ethCoin = coinRepository.getCoinByWalletIdAndName(walletId, CoinConst.ETH)
        var trxCoin = coinRepository.getCoinByWalletIdAndName(walletId, CoinConst.TRX)

        data?.forEachIndexed { index, coinBean ->
            if (coinBean.type==CoinConst.ERC20){
                var hotCoin = Coin(
                    ethCoin?.address,
                    coinBean.symbol?.toUpperCase(),
                    walletId,
                    image = coinBean.logoURI,
                    coin = ethCoin?.coin?:0x8000003c ,
                    account = ethCoin?.account?:0x80000000,
                    change = ethCoin?.change?:0,
                    index = ethCoin?.index?:0,
                    key_id = ethCoin?.key_id,
                    coin_tag = coinBean.type,
                    contact_address = coinBean.address,
                    decimals = coinBean.decimals
                )
                if (wallet?.type == CoinConst.ETH|| wallet?.type == "Multi") {//钱包中含有ETH币
                    coins?.forEachIndexed { index, coin ->
                        if (coin.name == coinBean.symbol?.toUpperCase() && coin.coin_tag == CoinConst.ERC20) {//该钱包的数据库中已经存在该代币
                            hotCoin.isAdd = true
                        }
                    }
                }
                erc20Lists.add(hotCoin)

            }

            if (coinBean.type==CoinConst.TRC20){
                var hotCoin = Coin(
                    trxCoin?.address,
                    coinBean.symbol?.toUpperCase(),
                    walletId,
                    image = coinBean.logoURI,
                    coin = trxCoin?.coin?:0x8000003c ,
                    account = trxCoin?.account?:0x80000000,
                    change = trxCoin?.change?:0,
                    index = trxCoin?.index?:0,
                    key_id = trxCoin?.key_id,
                    coin_tag = coinBean.type,
                    contact_address = coinBean.address,
                    decimals = coinBean.decimals
                )
                if (wallet?.type ==  CoinConst.TRX || wallet?.type == "Multi") {//钱包中含有TRX币
                    coins?.forEachIndexed { index, coin ->
                        if (coin.name == coinBean.symbol?.toUpperCase() && coin.coin_tag == CoinConst.TRC20) {//该钱包的数据库中已经存在该代币
                            hotCoin.isAdd = true
                        }
                    }
                }
                trc20Lists.add(hotCoin)
            }
        }
        if (erc20Lists.size>0){
            coinLists.add(CoinConst.ERC20)
            coinLists.addAll(erc20Lists)
        }
        if (trc20Lists.size>0){
            coinLists.add(CoinConst.TRC20)
            coinLists.addAll(trc20Lists)
        }


        adapter.setList(coinLists)
//        val eRC20CoinAdapter = ERC20CoinAdapter(R.layout.erc_20_coin_list_item, coinLists)
        val layoutManager = LinearLayoutManager(this@AddCoinActivity)
        rvERC20Coin.layoutManager = layoutManager
        rvERC20Coin.adapter = adapter
        /**
         * Item 点击事件
         */
        adapter.setOnItemClickListener { adapter, view, position ->
            val coin = adapter.data[position]
            if (coin is Coin){
                if (coin.isAdd) {
                    coinRepository.delete(coin.wallet_id, coin.name, coin.coin_tag)
                    coin.isAdd = false
                } else {
                    coinRepository.insert(coin)
                    coin.isAdd = true
                }
                adapter.notifyItemChanged(position)
            }

        }
    }


    private val onClickListener = View.OnClickListener {
        when (it.id) {
            R.id.tvCancel -> {
                finish()
            }
            R.id.tv_add_chain -> {
                startActivity(
                    Intent(
                        this,
                        AddTokensActivity::class.java
                    )
                )
            }
        }
    }


}
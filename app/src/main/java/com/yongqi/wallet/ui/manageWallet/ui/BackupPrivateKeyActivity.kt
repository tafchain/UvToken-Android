package com.yongqi.wallet.ui.manageWallet.ui

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.gyf.immersionbar.ktx.immersionBar
import com.yongqi.wallet.BuildConfig
import com.yongqi.wallet.R
import com.yongqi.wallet.base.BaseActivity
import com.yongqi.wallet.bean.Coin
import com.yongqi.wallet.config.CoinConst
import com.yongqi.wallet.databinding.ActivityBackupPrivateKeyBinding
import com.yongqi.wallet.db.coin.CoinRepository
import com.yongqi.wallet.ui.manageWallet.adapter.CoinTypeAdapter
import com.yongqi.wallet.ui.manageWallet.viewModel.BackupPrivateKeyViewModel
import kotlinx.android.synthetic.main.activity_backup_private_key.*
import kotlinx.android.synthetic.main.common_title.*

class BackupPrivateKeyActivity : BaseActivity<ActivityBackupPrivateKeyBinding, BackupPrivateKeyViewModel>() {

    override fun getLayoutResource(): Int = R.layout.activity_backup_private_key

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.ivBack -> {
                finish()
            }
        }
    }

    override fun initData() {
        immersionBar {
            statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
            titleBar(iTitle) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
        }
        ivBack.setOnClickListener(onClickListener)
        tvTitle.text = getString(R.string.backup_private_key)


        val pwd = intent.getStringExtra("pwd")
        val walletId = intent.getStringExtra("walletId")
        val type = intent.getStringExtra("type")

        val coinRepository = CoinRepository(this)
        val coins: MutableList<Coin>? = coinRepository.getCoinsById(walletId) as MutableList<Coin>?
        val dataList = mutableListOf<Coin>()
        when (type) {
            "AECO" -> {
                coins?.forEachIndexed { index, coin ->
                    if (coin.name == "AECO") {
                        dataList.add(coin)
                    }
                }
            }
            else -> {
                coins?.forEachIndexed { index, coin ->
//                    if (coin.name == "BTC" || coin.name == "ETH" || coin.name == "AECO") {
                    if (BuildConfig.FLAVOR == "online" || BuildConfig.FLAVOR == "devtest") {
                        if (coin.name == "BTC" || coin.name == "ETH" || coin.name == CoinConst.TRX) {
                            dataList.add(coin)
                        }
                    } else {//if ( BuildConfig.FLAVOR == "onlineuvtest"|| BuildConfig.FLAVOR == "devuvtest") {
                        if (coin.name == "BTC" || coin.name == "ETH" || coin.name == "AECO") {
                            dataList.add(coin)
                        }
                    }
                }
            }
        }

        val adapter = CoinTypeAdapter(R.layout.coin_type_item, dataList)
        val layoutManager = LinearLayoutManager(this)
        rvCoinType.layoutManager = layoutManager
        rvCoinType.adapter = adapter
        /**
         * Item 点击事件
         */
        adapter.setOnItemClickListener { adapter, view, position ->
            var coin = adapter.data[position] as Coin
            startActivity(
                Intent(this, BackupPrivateKeyDetailActivity::class.java)
                    .putExtra("iconType", coin.name)
                    .putExtra("pwd", intent.getStringExtra("pwd"))
//                    .putExtra("keyId", coin.key_id)
                    .putExtra("walletId", walletId)
                    .putExtra("walletType",type)
            )
        }
    }


}
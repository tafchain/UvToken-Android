package com.yongqi.wallet.ui.createWallet.ui

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.gyf.immersionbar.ktx.immersionBar
import com.yongqi.wallet.R
import com.yongqi.wallet.base.BaseActivity
import com.yongqi.wallet.bean.MnemonicBean
import com.yongqi.wallet.databinding.ActivityConfirmMnemonicBinding
import com.yongqi.wallet.db.wallet.WalletRepository
import com.yongqi.wallet.ui.createWallet.adapter.SelectMnemonicAdapter
import com.yongqi.wallet.ui.createWallet.adapter.ShuffleMnemonicAdapter
import com.yongqi.wallet.ui.createWallet.viewModel.ConfirmMnemonicViewModel
import com.yongqi.wallet.ui.main.ui.HomePageActivity
import com.yongqi.wallet.utils.ActivityCollector
import kotlinx.android.synthetic.main.activity_confirm_mnemonic.*
import kotlinx.android.synthetic.main.common_title.*


class ConfirmMnemonicActivity :
    BaseActivity<ActivityConfirmMnemonicBinding, ConfirmMnemonicViewModel>() {

    override fun getLayoutResource(): Int = R.layout.activity_confirm_mnemonic


    /**
     * 助记词原始的顺序
     */
    private var mnemonicOriginalArray: MutableList<String> = arrayListOf()

    /**
     * 准备打乱顺序的助记词数据
     */
    private var shuffleMnemonicArray: MutableList<String> = arrayListOf()

    //用户选中的助记词数据
    private val mnemonicSelectArrayData: MutableList<MnemonicBean> = arrayListOf()

    //打乱顺序的助记词数据
    private val shuffleMnemonicArrayData: MutableList<MnemonicBean> = arrayListOf()


    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.ivBack -> {
                finish()
            }
            R.id.btnBackupCompleted -> {

                //TODO 修改钱包数据库中is_backup字段的值,跳转到主页
                val walletId = intent.getStringExtra("walletId")
                val walletRepository = WalletRepository(this@ConfirmMnemonicActivity)
                val wallet = walletId?.let { walletRepository.getWalletById(it) }
                wallet?.is_backup = true
                walletRepository.update(wallet)
                ActivityCollector.finishAll()
                startActivity(Intent(this, HomePageActivity::class.java))
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
        tvTitle.text = getString(R.string.confirm_mnemonic_title)
        val mnemonics = intent.getStringExtra("mnemonics")
        mnemonicOriginalArray = mnemonics?.split(" ") as MutableList<String>
        shuffleMnemonicArray = mnemonics.split(" ") as MutableList<String>

        shuffleMnemonicArray.shuffle()
        /**
         * 用户选中的助记词列表
         */
        val gridLayoutManager0 = GridLayoutManager(this, 3)
        val selectMnemonicAdapter =
            SelectMnemonicAdapter(R.layout.select_mnemonic_item, mnemonicSelectArrayData)
        rvSelectMnemonic.layoutManager = gridLayoutManager0
        rvSelectMnemonic.adapter = selectMnemonicAdapter

        /**
         * 乱序助记词列表
         */
        for (item in shuffleMnemonicArray) {
            shuffleMnemonicArrayData.add(MnemonicBean(true, item))
        }
        val gridLayoutManager = GridLayoutManager(this, 3)
        val shuffleMnemonicAdapter =
            ShuffleMnemonicAdapter(R.layout.shuffle_mnemonic_item, shuffleMnemonicArrayData)
        rvShuffleMnemonic.layoutManager = gridLayoutManager
        rvShuffleMnemonic.adapter = shuffleMnemonicAdapter
        /**
         * 给乱序助记词列表设置点击事件
         */
        shuffleMnemonicAdapter.setOnItemClickListener { adapter, _, position ->
            val mnemonic: MnemonicBean = adapter.data[position] as MnemonicBean
            //设置乱序的助记词列表item不可再点击
            mnemonic.enabled = false
            shuffleMnemonicAdapter.notifyDataSetChanged()

            /**
             * 构建选中的助记词数据
             */
            mnemonicSelectArrayData.add(mnemonic)
            for (index in mnemonicSelectArrayData.indices) {
                //如果相同index上选中助记词和原助记词相同,则isCorrect为true
                mnemonicSelectArrayData[index].isCorrect =
                    mnemonicSelectArrayData[index].mnemonic == mnemonicOriginalArray[index]
            }
            selectMnemonicAdapter.notifyDataSetChanged()

            var isEnabled = false
            if (mnemonicSelectArrayData.size < 1) {
                setTransparentStatusBar()
                tvMnemonicSequenceIsWrong.visibility = View.GONE
                return@setOnItemClickListener
            }
            mnemonicSelectArrayData.forEachIndexed { index, mnemonicBean ->
                if (mnemonicBean.mnemonic != mnemonicOriginalArray[index]) {
                    isEnabled = true
                }
            }
            if (isEnabled) {
                setStatusBarColor()
                tvMnemonicSequenceIsWrong.visibility = View.VISIBLE
            } else {
                setTransparentStatusBar()
                tvMnemonicSequenceIsWrong.visibility = View.GONE
            }

            btnBackupCompleted.isEnabled = mnemonicSelectArrayData.size == 12 && !isEnabled
        }
        /**
         * 给用户选中的助记词列表设置点击事件
         */
        selectMnemonicAdapter.setOnItemClickListener { adapter, _, position ->
            val mnemonic: MnemonicBean = adapter.data[position] as MnemonicBean
            //从选中助记词中移除点击的助记词
            adapter.removeAt(position)
            for (index in mnemonicSelectArrayData.indices) {
                //如果相同index上选中助记词和原助记词相同,则isCorrect为true
                mnemonicSelectArrayData[index].isCorrect =
                    mnemonicSelectArrayData[index].mnemonic == mnemonicOriginalArray[index]
            }
            selectMnemonicAdapter.notifyDataSetChanged()

            for (index in shuffleMnemonicArrayData.indices) {
                //将移除的助记词在乱序助记词中的状态变为可点击
                if (shuffleMnemonicArrayData[index].mnemonic == mnemonic.mnemonic) {
                    shuffleMnemonicArrayData[index].enabled = true
                }
            }
            shuffleMnemonicAdapter.notifyDataSetChanged()
//            btnBackupCompleted.isEnabled = false

            var isEnabled = false
            if (mnemonicSelectArrayData.size < 1) {
                setTransparentStatusBar()
                tvMnemonicSequenceIsWrong.visibility = View.GONE
                return@setOnItemClickListener
            }
            mnemonicSelectArrayData.forEachIndexed { index, mnemonicBean ->
                if (mnemonicBean.mnemonic != mnemonicOriginalArray[index]) {
                    isEnabled = true
                }
            }
            if (isEnabled) {
                setStatusBarColor()
                tvMnemonicSequenceIsWrong.visibility = View.VISIBLE
            } else {
                setTransparentStatusBar()
                tvMnemonicSequenceIsWrong.visibility = View.GONE
            }

            btnBackupCompleted.isEnabled = mnemonicSelectArrayData.size == 12 && !isEnabled
        }

        btnBackupCompleted.setOnClickListener(onClickListener)
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



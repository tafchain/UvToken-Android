package com.yongqi.wallet.ui.addCoin.viewModel

import android.view.Gravity
import com.blankj.utilcode.util.ToastUtils
import com.yongqi.wallet.App
import com.yongqi.wallet.base.BaseViewModel
import com.yongqi.wallet.databinding.ActivityAddCoinBinding
import com.yongqi.wallet.net.BaseLoadListener
import com.yongqi.wallet.ui.addCoin.model.AddCoinModel
import com.yongqi.wallet.ui.addCoin.model.AddCoinModelImpl

/**
 * author ：SunXiao
 * date : 2021/1/13 15:39
 * package：com.yongqi.wallet.ui.addCoin.viewModel
 * description :
 */
class AddCoinViewModel : BaseViewModel<ActivityAddCoinBinding>(), BaseLoadListener<Any> {

    lateinit var model: AddCoinModel


    fun loadData(fuzzy:String?){
        model = AddCoinModelImpl(App.context)
//        model.fuzzySearch(fuzzy,this)

    }

    override fun initUi() {

    }

    override fun initNet() {

//
    }

    override fun loadSuccess(t: Any) {
        ToastUtils.make().show(t.toString())
    }

    override fun loadFailure(message: String) {
        ToastUtils.make().show(message)
    }

    override fun loadStart() {

    }

    override fun loadComplete() {

    }

}
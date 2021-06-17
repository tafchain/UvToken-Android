package com.yongqi.wallet.ui.receiveAndTransfer.transfer.ui

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.gyf.immersionbar.ktx.immersionBar
import com.yongqi.wallet.R
import com.yongqi.wallet.base.BaseActivity
import com.yongqi.wallet.bean.AddressBean
import com.yongqi.wallet.bean.AddressBean.Companion.IMG_TEXT
import com.yongqi.wallet.bean.AddressBean.Companion.TEXT
import com.yongqi.wallet.databinding.ActivityAddressBookBinding
import com.yongqi.wallet.db.address.AddressRepository
import com.yongqi.wallet.ui.receiveAndTransfer.transfer.adapter.AddressMultiItemQuickAdapter
import com.yongqi.wallet.ui.receiveAndTransfer.transfer.viewModel.AddressBookViewModel
import kotlinx.android.synthetic.main.activity_address_book.*
import kotlinx.android.synthetic.main.common_title.*

class AddressBookActivity : BaseActivity<ActivityAddressBookBinding, AddressBookViewModel>() {


    override fun getLayoutResource(): Int = R.layout.activity_address_book
    private var allAddressBeanLists  = mutableListOf<AddressBean>()

    private var BTCAddressBeanLists  = mutableListOf<AddressBean>()
    private var ETHAddressBeanLists  = mutableListOf<AddressBean>()
    private var TRXAddressBeanLists  = mutableListOf<AddressBean>()

    override fun initData() {
        immersionBar {
//            fitsSystemWindows(true)//只适合纯色状态栏;解决状态栏和布局重叠问题，使用该属性,必须指定状态栏颜色
//            statusBarColor(R.color.transparent)
            statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
            titleBar(iTitle) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
        }
        ivBack.setOnClickListener(onClickListener)

        tvTitle.text = getString(R.string.address_book)
        ivRightIcon.visibility = View.VISIBLE
        ivRightIcon.setImageResource(R.mipmap.icon_tj_h)
        ivRightIcon.setOnClickListener(onClickListener)

    }

    override fun onResume() {
        super.onResume()
        allAddressBeanLists.clear()
        BTCAddressBeanLists.clear()
        ETHAddressBeanLists.clear()
        TRXAddressBeanLists.clear()

        val pageType = intent.getIntExtra("pageType", 1)

        var addressRepository = AddressRepository(this)
        when(pageType){
            1->{//如果是:从交易页面进来
                val coinType = intent.getStringExtra("coinType")
                val coinTag = intent.getStringExtra("coinTag")
                var addressByType = if (coinTag=="ERC20"||coinType == "ETH"){
                    addressRepository.getAddressByType("ETH")
                }else if (coinTag=="TRC20"||coinType == "TRX") {
                    addressRepository.getAddressByType("TRX")
                } else{
//                    addressRepository.getAddressByType("BTC")
                    addressRepository.getAddressByType(coinType)
                }

                if (addressByType.isNullOrEmpty()){
                    llNoData.visibility = View.VISIBLE
                    rvAddress.visibility = View.GONE
                }else{
                    llNoData.visibility = View.GONE
                    rvAddress.visibility = View.VISIBLE
                    val addressBean =  if (coinTag=="ERC20"||coinType == "ETH"){
                        AddressBean(0,"ETH","ETH ${getString(R.string.address)}","","",IMG_TEXT)
                    }else if (coinTag=="TRC20"||coinType == "TRX") {
                        AddressBean(0,"TRX","TRX ${getString(R.string.address)}","","",IMG_TEXT)
                    } else{
//                        AddressBean(0,"BTC","BTC ${getString(R.string.address)}","","",IMG_TEXT)
                        AddressBean(0,coinType,"$coinType ${getString(R.string.address)}","","",IMG_TEXT)
                    }
//                    val addressBean = AddressBean(0,coinType,"$coinType ${getString(R.string.address)}","","",IMG_TEXT)
                    allAddressBeanLists.add(addressBean)
                    addressByType?.forEachIndexed { index, address ->
                        var addressBean1 = AddressBean(address?.uid,
                            address?.type,
                            address?.name,
                            address?.address,
                            address?.des,
                            TEXT
                        )
                        allAddressBeanLists.add(addressBean1)
                    }
                }
            }
            2->{//如果是从我的->地址簿页面进来的
                setData()
            }
        }



        val adapter = AddressMultiItemQuickAdapter(allAddressBeanLists)
        val layoutManager = LinearLayoutManager(this)
        rvAddress.layoutManager = layoutManager
        rvAddress.adapter = adapter
        /**
         * Item 点击事件
         */
        adapter?.setOnItemClickListener { adapter, view, position ->
            var addressBean : AddressBean = adapter.data[position] as AddressBean
            if (addressBean.itemType==TEXT){
                when(pageType){
                    1->{//如果是:从交易页面进来
                        val intent= Intent()
                        intent.putExtra("address",addressBean.address)
                        setResult(3,intent)
                        finish()
                    }
                    2->{//如果是从我的->地址簿页面进来的;编辑地址
                        startActivity(Intent(this,EditAddressActivity::class.java)
                            .putExtra("uid",addressBean.uid))
                    }
                }
            }


        }
    }

    private val onClickListener = View.OnClickListener {
        when (it.id) {
            R.id.ivBack -> finish()
            R.id.ivRightIcon -> {
                startActivity(Intent(this, NewAddressActivity::class.java))
            }

        }
    }


    private fun setData(){
        var addressRepository = AddressRepository(this)
        var addressList = addressRepository.getAll()
        if (addressList.isNullOrEmpty()){
            llNoData.visibility = View.VISIBLE
            rvAddress.visibility = View.GONE
        }else{
            llNoData.visibility = View.GONE
            rvAddress.visibility = View.VISIBLE
            addressList?.forEachIndexed { index, address ->
                var addressBean1 = AddressBean(address?.uid,
                    address?.type,
                    address?.name,
                    address?.address,
                    address?.des,
                    TEXT
                )
                when(address?.type){
                    "BTC"->{ BTCAddressBeanLists.add(addressBean1) }
                    "ETH"->{ETHAddressBeanLists.add(addressBean1)}
                    "TRX"->{TRXAddressBeanLists.add(addressBean1)}
                }
            }
            if (!BTCAddressBeanLists.isNullOrEmpty()){
                val addressBeanBTC = AddressBean(0,"BTC","BTC  ${getString(R.string.address)}","","",IMG_TEXT)
                allAddressBeanLists.add(addressBeanBTC)
                allAddressBeanLists.addAll(BTCAddressBeanLists)
            }

            if (!ETHAddressBeanLists.isNullOrEmpty()){
                val addressBeanETH = AddressBean(0,"ETH","ETH  ${getString(R.string.address)}","","",IMG_TEXT)
                allAddressBeanLists.add(addressBeanETH)
                allAddressBeanLists.addAll(ETHAddressBeanLists)
            }

            if (!TRXAddressBeanLists.isNullOrEmpty()){
                val addressBeanTRX = AddressBean(0,"TRX","TRX  ${getString(R.string.address)}","","",IMG_TEXT)
                allAddressBeanLists.add(addressBeanTRX)
                allAddressBeanLists.addAll(TRXAddressBeanLists)
            }
        }
    }
}
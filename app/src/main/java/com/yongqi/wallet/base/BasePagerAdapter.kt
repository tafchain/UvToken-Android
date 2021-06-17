package com.yongqi.wallet.base

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * author ：SunXiao
 * date : 2021/1/8 13:40
 * package：com.yongqi.wallet.base
 * description :
 */
@Suppress("DEPRECATION")
abstract class BasePagerAdapter<T>(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    protected var mFragmentMap = SparseArray<Fragment>()

    protected var mDataList: List<T>? = null



    override fun getCount(): Int {
        return mDataList?.size ?: 0
    }

    /**
     * 设置数据列表
     *
     * @param dataList 数据列表
     */
    open fun setDataList(dataList: List<T>?) {
        mDataList = dataList
        notifyDataSetChanged()
    }

    /**
     * 释放缓存的Fragment
     */
    open fun release() {
        mFragmentMap.clear()
    }
}
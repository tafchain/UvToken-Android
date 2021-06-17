package com.yongqi.wallet.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.gyf.immersionbar.components.ImmersionFragment

abstract class BaseFragment<V: ViewDataBinding>: ImmersionFragment() {//, VM: BaseViewModel<V>

    protected var binding: V ? = null
//    protected var viewModel: VM? = null
    var isUIVisible: Boolean = false
    var isViewCreated: Boolean = false
    var isFirstLoad: Boolean = false
    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root: View? = layoutInflater.inflate(getLayoutResource(), container, false)
        return  root ?:   /* root null  就用后面的 */    super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind<V>(view)!!
//        viewModel = TUtil.getT<VM>(this, 1)!!
//        viewModel?.init(activity!!, binding!!, context!!)
        isViewCreated = true
        if (!isFirstLoad) {
            initView()
            lazyLoad()
            isFirstLoad = true
        }
    }

    private fun lazyLoad() {
        //这里进行双重标记判断,是因为setUserVisibleHint会多次回调,并且会在onCreateView执行前回调,必须确保onCreateView加载完毕且页面可见,才加载数据
        if (isViewCreated && isUIVisible) {
            loadData()
            //数据加载完毕,恢复标记,防止重复加载
            isViewCreated = false
            isUIVisible = false
        }
    }

    @Suppress("DEPRECATION")
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        //isVisibleToUser这个boolean值表示:该Fragment的UI 用户是否可见
        if (isVisibleToUser) {
            isUIVisible = true
            lazyLoad()
        } else {
            isUIVisible = false
        }
    }


    abstract fun initView()

    abstract fun loadData()

    abstract fun getLayoutResource(): Int

    fun tag(): String? {
        return this.javaClass.simpleName
    }



//    protected fun toast(text: String) {
//        android.widget.Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
//    }
}
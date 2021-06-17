package com.yongqi.wallet.net

import android.content.Context
import android.util.Log
import com.yongqi.wallet.bean.ResponseWrapper
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

// RxJava 自定义 操作符 简单的
// 拦截 自定义操作符，目的: 包装Bean 给拆成两份  如果成功  data--》UI     如果失败  msg--》UI
abstract class APIResponse<T>(val context: Context) : Observer<ResponseWrapper<T>> {

//    private var isShow: Boolean = true

//    // 次构造
//    constructor(context: Context, isShow: Boolean = false) : this(context) {
//        this.isShow = isShow
//    }

    lateinit var d :Disposable

    // 成功 怎么办
    abstract fun success(data: T?)

    // 失败 怎么办
    abstract fun failure(errorMsg: String?)

    // todo +++++++++++++++++++++++++++++++++  RxJava 相关的函数

    // 启点分发的时候
    override fun onSubscribe(d: Disposable) {
//        // 弹出 加载框
//        if (isShow) {
//            LoadingDialog.show(context)
//        }

//        if (!NetworkUtils.isConnected()){
//            Toasty.warning(context, "网络未连接", Toast.LENGTH_SHORT, true).show();
//            if (!d.isDisposed) {
//                d.dispose()
//            }
//            onError(IOException())
//        }

        this.d = d

    }

    // 上游流下了的数据
    override fun onNext(t: ResponseWrapper<T>) {
        if ((t.code == 0||t.code==200) && t.data != null) {//TODO
            // 成功
            success(t.data)
        }else{
            // 失败
//            failure(getErrorMsg(t.code))
            failure(t.msg)
        }

    }

    // 上游流下了的错误
    override fun onError(e: Throwable) {
        // 取消加载
//        LoadingDialog?.cancel()
//        failure(e.message)

        if (!d.isDisposed) {
            d.dispose()
        }
        failure(ApiExceptionUtil.exceptionHandler(e))
    }

    // 停止
    override fun onComplete() {
        // 取消加载
//        LoadingDialog?.cancel()

        if (!d.isDisposed) {
            d.dispose()
        }
    }
}
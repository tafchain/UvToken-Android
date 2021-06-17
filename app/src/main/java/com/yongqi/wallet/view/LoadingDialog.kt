package com.yongqi.wallet.view

import android.app.Dialog
import android.content.Context
import android.util.Log
import com.yongqi.wallet.R
import java.lang.Exception

// 加载框
// object 没有主构造 也没有次构造
object LoadingDialog {


    private var dialog: Dialog ? = null

    fun show(context: Context) {
        cancel()
        dialog = Dialog(context)
        dialog?.setContentView(
            R.layout.dialog_loading
        )
        dialog?.setCancelable(true)
        dialog?.setCanceledOnTouchOutside(false)
        try {
            dialog?.show()
        }catch (e:Exception) {
            Log.e("dialog_error_info","originally added here")
        }
    }

    fun cancel() {
        try {
            dialog?.dismiss()
        }catch (e:Exception) {
            Log.e("dialog_error_info","not attached to window manager")
        }
    }



}
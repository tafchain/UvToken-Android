package com.yongqi.wallet.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import com.afollestad.materialdialogs.MaterialDialog
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.PermissionUtils
import com.yongqi.wallet.R
import com.yongqi.wallet.ui.scan.ui.ScanQrActivity

object PermissionUtils {

    /**
     * 请求相机权限
     */
    fun requestCameraPermissions(context: Context) {
        if (PermissionUtils.isGranted(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {//判断权限是否被授予
            context.startActivity(Intent(context, ScanQrActivity::class.java))
        } else {
            PermissionUtils
                .permission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA)//设置请求权限
//                .permissionGroup(PermissionConstants.CALENDAR)//设置请求权限组
//                .explain { activity, denied, shouldRequest ->//设置权限请求前的解释
//                }
                .callback { isAllGranted, granted, deniedForever, denied ->
                    LogUtils.d(granted, deniedForever, denied)

                    if (isAllGranted) {
                        context.startActivity(Intent(context, ScanQrActivity::class.java))
                        return@callback
                    }
                    if (deniedForever.isNotEmpty()) {
                        val dialog = context?.let { MaterialDialog(it) }
                        dialog?.title(R.string.title_permission)
                        dialog?.message(R.string.permission_rationale_camera_message)
                        dialog?.cancelOnTouchOutside(false)
                        dialog?.negativeButton(R.string.cancel_0) {
                            dialog?.dismiss()
                        }
                        dialog?.positiveButton(R.string.ok_0) {
                            PermissionUtils.launchAppDetailsSettings()//打开应用具体设置
                        }
//                        dialog?.lifecycleOwner(this@WalletFragment)
                        dialog?.show()
                    }
                }
                .request()//开始请求
        }
    }

}
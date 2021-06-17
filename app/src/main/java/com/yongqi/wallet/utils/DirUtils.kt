package com.yongqi.wallet.utils

import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils

/**
 * author ：SunXiao
 * date : 2021/1/21 11:14
 * package：com.yongqi.wallet.utils
 * description :
 */
object DirUtils {

    fun createKeyStoreDir():String?{
        /**
         * 创建文件夹:如果不存在,则创建文件夹
         * 创建钱包:
         */
//        var internalAppFilesPath = PathUtils.getInternalAppFilesPath()
        var internalAppDbsPath = PathUtils.getInternalAppDbsPath()
        var createOrExistsDir = FileUtils.createOrExistsDir("$internalAppDbsPath/keystore")
        return "$internalAppDbsPath/keystore"
    }

    fun createSharedImageDir():String?{
        /**
         * 创建文件夹:如果不存在,则创建文件夹
         */
        var internalAppFilesPath = PathUtils.getInternalAppFilesPath()
        var createOrExistsDir = FileUtils.createOrExistsDir("$internalAppFilesPath/sharedImage")
        return "$internalAppFilesPath/sharedImage"
    }
}
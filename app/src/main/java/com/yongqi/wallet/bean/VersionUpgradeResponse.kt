package com.yongqi.wallet.bean


/**
 * data 版本更新bean

 */

// 通配符Java?   ====   kt *
// String ? 允许服务器字段是 null
data class VersionUpgradeResponse(
    val device_type: String?,
    val last_version: String?,//最后的版本
    val force_upgrade: Boolean? = false,//是否强制更新
    val content: String?,//更新内容
    val content_en:String?,
    val app_link: String?//下载链接
)


data class VersionUpgradeRequest(
    val device_type: Int? = 1,
    val current_version: String?,//当前版本号
    val lang_type:String?
)
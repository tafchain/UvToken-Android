package com.yongqi.wallet.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName

/**
 * author ：SunXiao
 * date : 2021/2/1 18:00
 * package：com.yongqi.wallet.bean
 * description :和H5交互的数据基类
 */
data class JsBean(
    val type: String? = "",
    //附加字段,客户端不需要关心,透传给服务端接口,Gson不解析字段
    @SerializedName("info")
    @JsonAdapter(RawStringJsonAdapter::class)
        val info: String
)


data class AddRecordBean(
    val status: String?,
    val `in`: String?,
    val out: String?,
    val getShare: String?,
    val address: String?
)


data class AddSignatureBean(val target: SignDataBean?)

data class SignDataBean(val data: String?, val signedData: String?)

data class UploadAddressBean(val address: String?)

data class CurrencyUnitBean(val currencyType: String?)

data class JsonInfo(
    val account_address: String?,
    val path: List<List<String>?>?,
    val nonce: String?,
    val deadline: String?,
    val amount_in: String?,
    val amount_out_min: String?
)


/**
 * author ：SunXiao
 * date : 2021/1/18 20:18
 * package：com.yongqi.wallet.bean
 * description :Arther最近交易
 */
@Entity(tableName = "Trade")
data class Trade(
    @ColumnInfo
    var address: String?,
    @ColumnInfo
    var status: String?,
//    @ColumnInfo//TODO 数据库版本迁移,删除字段
//    var firstCoin: String?,
//    @ColumnInfo//TODO 数据库版本迁移,删除字段
//    var getShare: String?,
//    @ColumnInfo//TODO 数据库版本迁移,删除字段
//    var lastCoin: String?,
//    @ColumnInfo//TODO 数据库版本迁移,删除字段
//    var result: Boolean?,
    @ColumnInfo//TODO 数据库版本迁移,新增字段
    var type: String?,
    @ColumnInfo//TODO 数据库版本迁移,新增字段
    var des: String?

) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    var uid: Int = 0


}

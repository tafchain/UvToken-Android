package com.yongqi.wallet.ui.receiveAndTransfer.adapter

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.blankj.utilcode.constant.TimeConstants
import com.blankj.utilcode.util.TimeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.yongqi.wallet.BuildConfig
import com.yongqi.wallet.R
import com.yongqi.wallet.bean.Record
import com.yongqi.wallet.config.AppConst.ETH_TO_WEI
import com.yongqi.wallet.config.CoinConst
import com.yongqi.wallet.utils.StringUtils
import java.math.BigDecimal

/**
 * author ：SunXiao
 * date : 2021/1/12 15:51
 * package：com.yongqi.wallet.ui.main.adapter
 * description :
 */
class TransferRecordAdapter(layoutResId: Int, walletListData: MutableList<Record>?) :
    BaseQuickAdapter<Record, BaseViewHolder>(layoutResId, walletListData) {

    var mInAddress: String? = ""
    var mContext: Context? = null

    constructor(
        layoutResId: Int,
        walletListData: MutableList<Record>?,
        address: String?,
        context: Context
    ) : this(layoutResId, walletListData) {
        mInAddress = address
        mContext = context
    }

    override fun convert(holder: BaseViewHolder, item: Record) {//TODO
        // TODO: 2021/6/10 1.0.9之后的版本删除
        if (item.name == CoinConst.ETH) {
            var amount = "0"
            if (BigDecimal(item.amount) >= BigDecimal(1000)) {
                amount = BigDecimal(item.amount.toString()).divide(
                    BigDecimal(ETH_TO_WEI)
                ).stripTrailingZeros().toPlainString()
                item.amount = amount
            }
        }

        when (item.type) {
            "挖矿奖励" -> {
                if (item.name == "ETH" || item.coin_tag == "ERC20") {
                    if (BigDecimal(item.amount) == BigDecimal.ZERO) {
                        holder.setText(R.id.tvTransactionMoney, "${item.amount}")
                    } else {
                        holder.setText(R.id.tvTransactionMoney, "+${item.amount}")
                    }

                } else {
                    if (BigDecimal(item.amount) == BigDecimal.ZERO) {
                        holder.setText(R.id.tvTransactionMoney, "0")
                    } else {
                        holder.setText(R.id.tvTransactionMoney, "+${BigDecimal(item.amount).stripTrailingZeros().toPlainString() ?: null}")
                    }

                }
                holder.setText(R.id.tvReceiveOrTransfer, context.getString(R.string.mining_rewards)).setText(R.id.tvAddress, StringUtils.replaceByX(item.address))
            }
            "转账" -> {
                if (item.name == "ETH" || item.coin_tag == "ERC20") {
                    if (BigDecimal(item.amount) == BigDecimal.ZERO) {
                        holder.setText(R.id.tvTransactionMoney, "${item.amount}")
                    } else {
                        holder.setText(R.id.tvTransactionMoney, "-${item.amount}")
                    }

                } else {
                    var amount = if (BigDecimal(item.amount) == BigDecimal.ZERO) {
                        holder.setText(R.id.tvTransactionMoney, "0")
                    } else {
                        holder.setText(R.id.tvTransactionMoney, "-${BigDecimal(item.amount).stripTrailingZeros().toPlainString() ?: null}")
                    }
                }
                holder.setText(R.id.tvReceiveOrTransfer, context.getString(R.string.transfer)).setText(R.id.tvAddress, StringUtils.replaceByX(item.to_address))
            }
            "收款" -> {
                if (item.name == "ETH" || item.coin_tag == "ERC20") {
                    if (BigDecimal(item.amount) == BigDecimal.ZERO) {
                        holder.setText(R.id.tvTransactionMoney, "${item.amount}")
                    } else {
                        holder.setText(R.id.tvTransactionMoney, "+${item.amount}")
                    }

                } else {
                    if (BigDecimal(item.amount) == BigDecimal.ZERO) {
                        holder.setText(R.id.tvTransactionMoney, "0")
                    } else {
                        holder.setText(R.id.tvTransactionMoney, "+${BigDecimal(item.amount).stripTrailingZeros().toPlainString() ?: null}")
                    }

                }
                holder.setText(R.id.tvReceiveOrTransfer, context.getString(R.string.receive))
                    .setText(R.id.tvAddress, StringUtils.replaceByX(item.to_address))
            }
        }

        var tvTime = holder.getView<TextView>(R.id.tvTime)
        if (item.time == 0L) {
            tvTime.visibility = View.GONE
        } else {
            tvTime.visibility = View.VISIBLE
            holder.setText(R.id.tvTime, (item.time?.times(1000))?.let { TimeUtils.millis2String(it) })
        }
        var tvPacking = holder.getView<TextView>(R.id.tvPacking)
        var llCancelAndAccelerate = holder.getView<LinearLayout>(R.id.llCancelAndAccelerate)
        if (item.name == "ETH" || item.coin_tag == "ERC20") {//新增加速功能
            var timeMinis = TimeUtils.getTimeSpan(System.currentTimeMillis(), item?.startTime!!, TimeConstants.MIN)
            if (item.gasPrice!="-1"&&timeMinis>1&&item.time==0L){//显示加速和取消按钮 设置点击事件
                tvPacking.visibility = View.VISIBLE
                llCancelAndAccelerate.visibility = View.VISIBLE
            }else{//隐藏加速和取消按钮
                tvPacking.visibility = View.GONE
                llCancelAndAccelerate.visibility = View.GONE
            }
        }else{// 隐藏加速和取消按钮
            tvPacking.visibility = View.GONE
            llCancelAndAccelerate.visibility = View.GONE
        }

    }

}
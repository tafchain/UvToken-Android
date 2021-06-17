package com.yongqi.wallet.utils

import android.app.Activity

/**
 * Created by SunXiao on 2020/5/21.
 */

object ActivityCollector {//单例模式

    private val activities = ArrayList<Activity>()

    fun addActivity(activity: Activity) {
        activities.add(activity)
    }

    fun removeActivity(activity: Activity) {
        activities.remove(activity)
    }

    /**
     * 随时随地的退出程序
     */
    fun finishAll() {
        for (activity in activities) {
            if (!activity.isFinishing) {
                activity.finish()
            }
        }
        activities.clear()
    }
}
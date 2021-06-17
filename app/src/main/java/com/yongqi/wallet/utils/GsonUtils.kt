package com.yongqi.wallet.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GsonUtils {
    public fun <T> jsonToList(jsonList: String): List<T> {
        return Gson().fromJson(jsonList, object : TypeToken<ArrayList<T>>() {}.type)
    }
}
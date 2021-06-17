package com.yongqi.wallet.net

import com.google.gson.JsonParseException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException


object ApiExceptionUtil {
    fun exceptionHandler(e: Throwable): String {
        return when (e) {
            is ConnectException -> {
                "无法连接服务器!"
            }
            is JsonParseException -> {
                "服务器返回错误!"
            }
            is SocketTimeoutException -> {
                "连接服务器超时!"
            }
            is HttpException -> {
                "404错误(网址不存在)"
            }
            else -> {
                "未知错误!"
            }
        }
    }
}
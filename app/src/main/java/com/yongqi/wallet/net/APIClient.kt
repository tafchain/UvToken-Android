package com.yongqi.wallet.net

import android.util.Log
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import com.yongqi.wallet.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.internal.platform.Platform
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 访问服务器的API接口的 客户端管理
 */
class APIClient {
    private object Holder {
        val INSTANCE = APIClient()
    }

    companion object {
        val instance = Holder.INSTANCE
    }

    class NetInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder()
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Cache-Control", "no-cache")
                .build()
            return chain.proceed(request)
        }
    }

    /**
     * 模糊查询币、查询费率等的地址
     *  API实例化这个，  XXXAPI实例化这个，   BBBAPI实例化
     */

    fun <T> instanceRetrofit(apiInterface: Class<T>): T {
        val loggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message -> //打印retrofit日志
            Log.i("RetrofitLog", "retrofitBack = $message")
        })
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        // OKHttpClient请求服务器
        val client = OkHttpClient().newBuilder()
        //打印网络请求日志
        val httpLoggingInterceptor: LoggingInterceptor = LoggingInterceptor.Builder()
            .loggable(BuildConfig.DEBUG)
            .setLevel(Level.BASIC)
            .log(Platform.INFO)
            .request("Request")
            .response("Response")
            .build()
        client.addInterceptor(httpLoggingInterceptor)

        val mOkHttpClient = client
            // 添加读取超时时间
            .readTimeout(120, TimeUnit.SECONDS)
            // 添加连接超时时间
            .connectTimeout(120, TimeUnit.SECONDS)
            // 添加写出超时时间
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            // 请求方  ←
            .client(mOkHttpClient)
            // 响应方  →
            // Response的事情  回来
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // RxJava来处理
//            .addConverterFactory(CustomGsonConverterFactory.create()) // Gson 来解析 --- JavaBean
            .addConverterFactory(GsonConverterFactory.create()) // Gson 来解析 --- JavaBean
            .build()

        return retrofit.create(apiInterface)
    }

//
//    /**
//     *  处理t交易记录模块的http请求
//     * API实例化这个，  XXXAPI实例化这个，   BBBAPI实例化
//     */
//    fun <T> instanceTransactionRetrofit(apiInterface: Class<T>): T {
//        val loggingInterceptor =
//            HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message -> //打印retrofit日志
//                Log.i("RetrofitLog", "retrofitBack = $message")
//            })
//        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
//
//        // OKHttpClient请求服务器
//        val client = OkHttpClient().newBuilder()
//        client.addInterceptor(loggingInterceptor)
////        client.addInterceptor(AndroidLoggingInterceptor.build())
//        val mOkHttpClient = client
//            // 添加读取超时时间
//            .readTimeout(10000, TimeUnit.SECONDS)
//            // 添加连接超时时间
//            .connectTimeout(10000, TimeUnit.SECONDS)
//            // 添加写出超时时间
//            .writeTimeout(10000, TimeUnit.SECONDS)
//            .build()
//        val retrofit: Retrofit = Retrofit.Builder()
//            .baseUrl(BASE_TRANSACTION_HTTP_URL)
//            // 请求方  ←
//            .client(mOkHttpClient)
//            // 响应方  →
//            // Response的事情  回来
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // RxJava来处理
////            .addConverterFactory(CustomGsonConverterFactory.create()) // Gson 来解析 --- JavaBean
//            .addConverterFactory(GsonConverterFactory.create()) // Gson 来解析 --- JavaBean
//            .build()
//
//        return retrofit.create(apiInterface);
//    }


    /**
     * 处理taf App的模块的http请求
     * API实例化这个，  XXXAPI实例化这个，   BBBAPI实例化
     */
    fun <T> instanceTafRetrofit(apiInterface: Class<T>): T {
        val loggingInterceptor =
            HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message -> //打印retrofit日志
                Log.i("RetrofitLog", "retrofitBack = $message")
            })
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        // OKHttpClient请求服务器
        val client = OkHttpClient().newBuilder()
        client.addInterceptor(loggingInterceptor)
//        client.addInterceptor(AndroidLoggingInterceptor.build())
        val mOkHttpClient = client
            // 添加读取超时时间
            .readTimeout(120, TimeUnit.SECONDS)
            // 添加连接超时时间
            .connectTimeout(120, TimeUnit.SECONDS)
            // 添加写出超时时间
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            // 请求方  ←
            .client(mOkHttpClient)
            // 响应方  →
            // Response的事情  回来
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // RxJava来处理
//            .addConverterFactory(CustomGsonConverterFactory.create()) // Gson 来解析 --- JavaBean
            .addConverterFactory(GsonConverterFactory.create()) // Gson 来解析 --- JavaBean
            .build()

        return retrofit.create(apiInterface);
    }

//    fun getApi(): NetApi {
//        val retrofit = Retrofit.Builder()
//            .baseUrl(AppConst.BASE_HTTP_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//        val netApi: NetApi = retrofit.create(NetApi::class.java)
//        return netApi
//    }
}
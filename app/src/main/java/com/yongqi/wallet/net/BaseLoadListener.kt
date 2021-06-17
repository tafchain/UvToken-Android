package com.yongqi.wallet.net

interface BaseLoadListener<T> {
    /**
     * 加载数据成功
     *
     */
    fun loadSuccess(t: T)
    /**
     * 加载失败
     *
     * @param message
     */
    fun loadFailure(message: String)
    /**
     * 开始加载
     */
    fun loadStart()
    /**
     * 加载结束
     */
    fun loadComplete()
}
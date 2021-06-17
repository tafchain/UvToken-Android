package com.yongqi.wallet.bean

/**
 * 左移动<==选中文本 + tab+shift； 选中文本 + tab==>右移
 * 包装Bean
 *  获取公钥
 *  {
        "code": 0,
        "data": {
            "pub_key": "-----BEGIN RSA PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2yXCcHNb0yTICWec5VCk\n0YQgR5TklKGaiSM8GSywqWrEpqwkGdt9v7FzSNWGrX/Pgz27u2iwG7HU00zp0rbW\n94/FGVTOYav9igAnBNOu1uq4rBDvWNpXfPfJeSCDg6HndAJ4IEHe7Vk7nh3/UbtD\nhA5v+34SyP+S5h3eO6pyk/GA2pOeYqnBHBlxojw5anwjGXaI0GbaavDPAJThkZqE\nJ/hIdFjWxFv2yw10SDOY6LQsp4QPYwBpG5wy2P/TWHVxBam8Iu5mzGu0rJPb9OyD\n3HVUCCKuWfP/GVMQYka07Y0y3bUUcLhEJolrJfc7hHqHrxI7RTtOLSpQOkIBPnDd\n5QIDAQAB\n-----END RSA PUBLIC KEY-----\n"
        },
        "msg": ""
    }
    //注册失败
    {
        "code": 100112,
        "msg": "verify code expired"
    }
 *
 */

data class ResponseWrapper<T> (val code:Int?, val data : T?, val msg:String)//TODO

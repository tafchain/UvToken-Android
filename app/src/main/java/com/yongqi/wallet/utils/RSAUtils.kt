package com.yongqi.wallet.utils

import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.EncodeUtils
import com.blankj.utilcode.util.EncodeUtils.base64Decode
import com.blankj.utilcode.util.EncryptUtils
import com.yongqi.wallet.config.AppConst
import com.yongqi.wallet.config.AppConst.privateKey
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher

/**
 * RSA算法，实现数据的加密解密。
 */
object RSAUtils {

    /**
     * 公钥加密
     * Return the bytes of RSA encryption or decryption.
     * @param data           The data.
     * @param pubKey         The key. 公钥
     * @return the bytes of RSA encryption or decryption
     */
    fun encryptByPublicKey(pwd:String?): String? {
        var pwdEncryptRSA = EncryptUtils.encryptRSA(
            pwd?.toByteArray(),
            EncodeUtils.base64Decode(AppConst.publicKey.toByteArray()),
            1024,
            "RSA/None/PKCS1Padding")

        return EncodeUtils.base64Encode2String(pwdEncryptRSA)
    }



    /**
     * 私钥解密
     * 并校验密码是否正确
     */
    fun decryptByPrivateKey(originalPwd:String, inputPwd:String): Boolean? {
        var base64Pwd = base64Decode(originalPwd)
        var pwdDecryptRSA = EncryptUtils.decryptRSA(
            base64Pwd,
            base64Decode(privateKey.toByteArray()),
            1024,
            "RSA/None/PKCS1Padding")//RSA解密
        var pwdByteArray = inputPwd?.toByteArray()
        return Arrays.equals(pwdDecryptRSA, pwdByteArray)
    }



    /**
     * 私钥解密
     * 并校验密码是否正确
     */
    fun decryptByPrivateKey(pwd:String): String? {
        var base64Pwd = base64Decode(pwd)
        var pwdDecryptRSA = EncryptUtils.decryptRSA(
            base64Pwd,
            base64Decode(privateKey.toByteArray()),
            1024,
            "RSA/None/PKCS1Padding")//RSA解密
//        var pwdByteArray = inputPwd?.toByteArray()
//        val md5 = MessageDigest.getInstance("MD5")
//        val hash = BigInteger(1,pwdDecryptRSA ).toString(16)
//        var decryptPwd = ArrayUtils.toString(pwdDecryptRSA)
        var decryptPwd = ConvertUtils.bytes2String(pwdDecryptRSA)
        return decryptPwd
    }



    /**
     * 公钥加密
     * Return the bytes of RSA encryption or decryption.
     * @param data           The data.
     * @param pubKey         The key. 公钥
     * @return the bytes of RSA encryption or decryption
     */
    fun encryptByPublicKey(data: ByteArray?, pubKey: ByteArray?): ByteArray? {
        val x509KeySpec = X509EncodedKeySpec(pubKey)
        // KEY_ALGORITHM 指定的加密算法
        var keyFactory: KeyFactory? = null
        try {
            keyFactory = KeyFactory.getInstance("RSA")
            //获取公钥对象
            val publicKey = keyFactory.generatePublic(x509KeySpec)
            //RSA/ECB/PKCS1Padding
            val temp = keyFactory.algorithm
            //返回实现指定转换的 Cipher 对象
            val cipher =
                Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            return cipher.doFinal(data)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
package com.yongqi .wallet.utils

import java.util.regex.Matcher
import java.util.regex.Pattern

object StringUtils {

    /**
     * Return whether the string is null or 0-length.
     *
     * @param s The string.
     * @return `true`: yes<br></br> `false`: no
     */
    fun isEmpty(s: CharSequence?): Boolean {
        return s == null || s.isEmpty()
    }

    /**
     * 判断字符串是否包含数字
     */
    fun isContainNumber(input: String): Boolean {
        if (input.isNullOrEmpty()) return false
        val p: Pattern = Pattern.compile("[0-9]")
        val m: Matcher = p.matcher(input)
        return m.find()
    }

    /**
     * 判断字符串是否包含小写字母
     */
    fun isContainsLowerCase(input: String): Boolean {
        if (input.isEmpty()) return false
        val matcher = Pattern.compile("[a-z]").matcher(input)
        return matcher.matches()
    }

    /**
     * 判断字符串是否包含大写字母
     */
    fun isContainsCapital(input: String): Boolean {
        if (input.isEmpty()) return false
        val matcher = Pattern.compile("[A-Z]").matcher(input)
        return matcher.matches()
    }

    /**
     * 判断字符串是否包含字母
     */
    fun isContainsLetter(input: String): Boolean {
        if (input.isEmpty()) return false
        val matcher = Pattern.compile(".*[a-zA-Z]+.*").matcher(input)
        return matcher.matches()
    }


    /**
     * 判断字符串是否包含大小写字母和数字
     */
    fun checkString(input: String): Boolean {
        if (input.isEmpty()) return false
        var ch: Char
        var capitalFlag = false
        var lowerCaseFlag = false
        var numberFlag = false
        for (element in input) {
            ch = element
            when {
                Character.isDigit(ch) -> {
                    numberFlag = true
                }
                Character.isUpperCase(ch) -> {
                    capitalFlag = true
                }
                Character.isLowerCase(ch) -> {
                    lowerCaseFlag = true
                }
            }
            if (numberFlag && capitalFlag && lowerCaseFlag) return true
        }
        return false
    }


    fun replaceByX(address: String?): String {
        if (address.isNullOrEmpty())return ""
        if (address.length <8)return ""
        return address.substring(0, 7) + "..." + address.substring(
            address.length - 7,
            address.length
        )
    }



}
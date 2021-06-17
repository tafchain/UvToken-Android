package com.yongqi.wallet.utils;

import com.yongqi.wallet.config.CstCharset;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;


/**
 * @author : Zhenshui.Xia
 * @date   : 2013-9-19
 * @desc   : 字符串处理工具类
 *
 * public method
 * 	<li>isEmpty(String)					字符串是否为空 </li>
 * 	<li>isNotEmpty(String)				字符串是否为非空 </li>
 * 	<li>isBlank(String)					字符串是否为空格串 </li>
 * 	<li>isNotBlank(String)				字符串是否非空格串 </li>
 *  <li>nullToEmpty(String)				将null转换为空串 </li>
 *  <li>nullToString(String)			将null转换为字符串NULL </li>
 *  <li>halfToFull(String)				半角转全角 </li>
 *  <li>fullToHalf(String)				全角转半角 </li>
 *  <li>htmlEscapeCharsToString(String)	处理html中的特殊字符串 </li>
 *  <li>utf8UrlEncode(String)			将字符串用UTF-8编码 </li>
 *  <li>urlEncode(String)				将字符串用指定的编码进行编码 </li>
 */
public class UtilString {

    /**
     * 字符串是否为空
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return (str == null || str.length() == 0);
    }

    /**
     * 字符串是否为非空
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 字符串是否为空格串
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        return (str == null || str.trim().length() == 0);
    }

    /**
     * 字符串是否非空格串
     * @param str
     * @return
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }


    /**
     * 将null转换为空串,如果参数为非null，则直接返回
     * @param str
     * @return
     */
    public static String nullToEmpty(String str) {
        return (str == null ? "" : str);
    }


    /**
     * 将null转换为字符串"null",如果参数为非null，则直接返回
     * @param str
     * @return
     */
    public static String nullToString(String str) {
        return (str == null ? "null" : str);
    }


    /**
     * 半角转全角
     * @param half
     * @return 全角字符串.
     */
    public static String halfToFull(String half) {
        if(isEmpty(half)) return half;

        char c[] = half.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);
            }
        }

        return new String(c);
    }


    /**
     * 全角转半角
     * @param full
     * @return 半角字符串
     */
    public static String fullToHalf(String full) {
        if(isEmpty(full)) return full;

        char c[] = full.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);
            }
        }

        String result = new String(c);
        return result;
    }

    /**
     * 处理html中的特殊字符串
     *
     * <pre>
     * htmlEscapeCharsToString(null) = null;
     * htmlEscapeCharsToString("") = "";
     * htmlEscapeCharsToString("mp3") = "mp3";
     * htmlEscapeCharsToString("mp3&lt;") = "mp3<";
     * htmlEscapeCharsToString("mp3&gt;") = "mp3\>";
     * htmlEscapeCharsToString("mp3&amp;mp4") = "mp3&mp4";
     * htmlEscapeCharsToString("mp3&quot;mp4") = "mp3\"mp4";
     * htmlEscapeCharsToString("mp3&lt;&gt;&amp;&quot;mp4") = "mp3\<\>&\"mp4";
     * </pre>
     *
     * @param html
     * @return
     */
    public static String htmlEscapeCharsToString(String html) {
        return isBlank(html) ? html : html.replaceAll("&lt;", "<").replaceAll("&gt;", ">")
                .replaceAll("&amp;", "&").replaceAll("&quot;", "\"");
    }


    /**
     * 将字符串用UTF-8编码
     *
     * <pre>
     * utf8Encode(null)        =   null
     * utf8Encode("")          =   "";
     * utf8Encode("aa")        =   "aa";
     * utf8Encode("啊啊啊啊")   = "%E5%95%8A%E5%95%8A%E5%95%8A%E5%95%8A";
     * </pre>
     *
     * @param str
     * @return
     * @throws
     */
    public static String utf8UrlEncode(String str) {
        return urlEncode(str, CstCharset.UTF_8);
    }

    /**
     * 将字符串用指定的编码进行编码，发生异常时，源字符串直接返回，不做编码
     * @param str
     * @param charset
     * @return
     */
    public static String urlEncode(String str, String charset) {
        if (!isEmpty(str)) {
            try {
                return URLEncoder.encode(str, charset);
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
        }

        return str;
    }

    /**
     * 将字符串用UTF-8解码
     * @param str
     * @return
     */
    public static String utf8UrlDecode(String str) {
        return urlDecode(str, CstCharset.UTF_8);
    }

    /**
     * 将字符串用指定的编码进行解码，发生异常时，源字符串直接返回，不做解码
     * @param str
     * @param charset
     * @return
     */
    public static String urlDecode(String str, String charset) {
        if (!isEmpty(str)) {
            try {
                return URLDecoder.decode(str, charset);
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
        }

        return str;
    }

    /**
     * 手机号隐藏
     * @param phone
     * @return
     */
    public static String phoneHide(String phone) {
        if (!isEmpty(phone)) {
            int length = phone.length();
            String start = phone.substring(0, 3);
            String end = phone.substring(length - 4, length);
            return start + "****" + end;
        }
        return phone;
    }

    public static String cn2py(String SourceStr) {
        String Result = "";
        int StrLength = SourceStr.length();
        int i;
        try {
            for (i = 0; i < StrLength; i++) {
                Result += Char2Initial(SourceStr.charAt(i));
            }
        } catch (Exception e) {
            Result = "";
            e.printStackTrace();
        }
        return Result;
    }

    // ------------------------private方法区------------------------
    /**
     * 输入字符,得到他的声母,英文字母返回对应的大写字母,其他非简体汉字返回 '0' 　　* 　　
     */
    private static char Char2Initial(char ch) {
        // 对英文字母的处理：小写字母转换为大写，大写的直接返回
        if (ch >= 'a' && ch <= 'z') {
            return (char) (ch - 'a' + 'A');
        }
        if (ch >= 'A' && ch <= 'Z') {
            return ch;
        }
        // 对非英文字母的处理：转化为首字母，然后判断是否在码表范围内，
        // 若不是，则直接返回。
        // 若是，则在码表内的进行判断。
        int gb = gbValue(ch);// 汉字转换首字母
        if ((gb < BEGIN) || (gb > END))// 在码表区间之前，直接返回
        {
            return ch;
        }
        int i;
        for (i = 0; i < 26; i++) {// 判断匹配码表区间，匹配到就break,判断区间形如“[,)”
            if ((gb >= table[i]) && (gb < table[i + 1])) {
                break;
            }
        }
        if (gb == END) {// 补上GB2312区间最右端
            i = 25;
        }
        return initialtable[i]; // 在码表区间中，返回首字母
    }
    /**
     * 取出汉字的编码 cn 汉字 　　
     */
    private static int gbValue(char ch) {// 将一个汉字（GB2312）转换为十进制表示。
        String str = new String();
        str += ch;
        try {
            byte[] bytes = str.getBytes("GB2312");
            if (bytes.length < 2) {
                return 0;
            }
            return (bytes[0] << 8 & 0xff00) + (bytes[1] & 0xff);
        } catch (Exception e) {
            return 0;
        }
    }

    // 简体中文的编码范围从B0A1（45217）一直到F7FE（63486）
    private static int BEGIN = 45217;
    private static int END = 63486;

    // 按照声 母表示，这个表是在GB2312中的出现的第一个汉字，也就是说“啊”是代表首字母a的第一个汉字。
    // i, u, v都不做声母, 自定规则跟随前面的字母
    private static char[] chartable = { '啊', '芭', '擦', '搭', '蛾', '发', '噶', '哈', '哈', '击', '喀', '垃', '妈', '拿', '哦', '啪', '期', '然', '撒', '塌', '塌', '塌', '挖', '昔', '压', '匝', };

    // 二十六个字母区间对应二十七个端点
    // GB2312码汉字区间十进制表示
    private static int[] table = new int[27];

    // 对应首字母区间表
    private static char[] initialtable = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'h', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 't', 't', 'w', 'x', 'y', 'z', };

    // 初始化
    static {
        for (int i = 0; i < 26; i++) {
            table[i] = gbValue(chartable[i]);// 得到GB2312码的首字母区间端点表，十进制。
        }
        table[26] = END;// 区间表结尾
    }
}

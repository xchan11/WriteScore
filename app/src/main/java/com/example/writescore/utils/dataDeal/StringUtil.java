package com.example.writescore.utils.dataDeal;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
* Create by zxj on 2019/9/28
* */
public class StringUtil {

    /**
     * 描述：手机号格式验证.
     *
     * @param str 指定的手机号码字符串
     * @return 是否为手机号码格式:是为true，否则false
     */
//    public static Boolean isMobileNo(String str) {
//        Boolean isMobileNo = false;
//        try {
//            Pattern p = Pattern.compile("^((13[0-9])|(17[0-1,6-8])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
//            Matcher m = p.matcher(str);
//            isMobileNo = m.matches();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return isMobileNo;
//    }

    public static Boolean isMobileNo(String str) {
        return str.length()>8;
//        Boolean isMobileNo = false;
//        try {
//            Pattern p = Pattern.compile("^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[0-35-9]))\\d{8}$");
//            Matcher m = p.matcher(str);
//            isMobileNo = m.matches();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return isMobileNo;
    }




//    利用java原生类实现SHA256加密
    public static String getSHA256(String str){
        MessageDigest messageDigest;
        String encodestr = "";
        try{
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodestr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodestr;
    }

//    将byte转化为16进制
    private static String byte2Hex(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for(int i=0;i<bytes.length;i++){
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if(temp.length() == 1){
                //只有一位进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }

    /**
     * 截取日期，只需要年月日
     * @param s
     * @return
     */
    public static String interceptDate(String s) {
        Pattern p=Pattern.compile("(\\d{4})-(\\d{1,2})-(\\d{1,2})");
        Matcher m=p.matcher(s);
        while (m.find()) {
            return m.group();
        }
        return "";
    }

}

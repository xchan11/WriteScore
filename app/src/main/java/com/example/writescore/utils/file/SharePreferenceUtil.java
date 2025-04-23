package com.example.writescore.utils.file;


import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtil {
    private  static SharedPreferences sp;

    public static void init(Context context, String name) {
        sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static void putString(String key,String value){
        sp.edit().putString(key, value).apply();
    }

    public static void putLong(String key,Long value){
        sp.edit().putLong(key, value).apply();
    }
    public static void putInt(String key,Integer value){
        sp.edit().putInt(key, value).apply();
    }
    public static void putBoolean(String key,Boolean value){
        sp.edit().putBoolean(key, value).apply();
    }

    public static String getString(String key ){
        return sp.getString(key,"");
    }

    public static Long getLong(String key){return sp.getLong(key, 0);}
    public static String getString(String key,String defaultString){
        return sp.getString(key,defaultString);
    }
    public static Integer getInt(String key){
        return sp.getInt(key,-500);
    }
    public static Integer getInt(String key,int def){
        return sp.getInt(key,def);
    }
    public static boolean getBoolean(String key){
        return sp.getBoolean(key,false);
    }
    public static boolean getBoolean(String key,Boolean defaultVal){
        return sp.getBoolean(key,defaultVal);
    }

    private static final String USER_PHONE = "user_phone";

    public static void setCachedUserPhone(String userPhone) {
        if (null != sp) {
            sp.edit().putString(USER_PHONE, userPhone).apply();
        }
    }
    public static String getCachedUserPhone() {
        if (null != sp) {
            return sp.getString(USER_PHONE,"");
        }
        return "";
    }


    private static final String IS_AUTO_LOGIN = "is_auto_login";
    public static void setAutoLogin(boolean isAuto){
        if(null!=sp)
            sp.edit().putBoolean(IS_AUTO_LOGIN,isAuto).apply();
    }

    public static Boolean getIsAutoLogin() {
        if (null != sp) {
            return sp.getBoolean(IS_AUTO_LOGIN, false);
        }
        return false;
    }

    private static final String USER_PASSWORD = "user_psw";

    public static void setCachedPsw(String psw) {
        if (null != sp) {
            sp.edit().putString(USER_PASSWORD, psw).apply();
        }
    }

    public static String getCachedPsw() {
        if (null != sp) {
            return sp.getString(USER_PASSWORD, "");
        }
        return "";
    }

    private static final String IS_VERIFIED_CODE_LOGIN = "IS_VERIFIED_CODE_LOGIN";
    private static final String LASE_LOGIN_ROLE = "LASE_LOGIN_ROLE";
    public static void verifiedLogin(boolean isVerified, String role){
        if(null!=sp){
            sp.edit().putBoolean(IS_VERIFIED_CODE_LOGIN,isVerified).apply();
            sp.edit().putString(LASE_LOGIN_ROLE,role).apply();
        }
    }

    public static Boolean isVerifiedLogin() {
        if (null != sp) {
            return sp.getBoolean(IS_VERIFIED_CODE_LOGIN, false);
        }
        return false;
    }

    public static String lastLoginRole() {
        if (null != sp) {
            return sp.getString(LASE_LOGIN_ROLE, "");
        }
        return "";
    }
}

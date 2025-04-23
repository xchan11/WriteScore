package com.example.writescore.utils.ui;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    static public String ErrorMessage = "网络或服务器错误";
        //会按规定时间等待toast消息队列
    public static void ToastMsg(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
    public static void ToastLong(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }
    public static void ToastError(Context context){
        Toast.makeText(context,ErrorMessage,Toast.LENGTH_SHORT).show();
    }

    // Toast对象
    private static Toast toast = null;

    /**
     * 显示Toast,会立刻覆盖原来的toast消息
     */
    public static void coverToast(Context context, String text) {
        if (toast == null) {
            toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }
        toast.setText(text);
        toast.show();
    }
    public static void coverToastLong(Context context, String text) {
        if (toast == null) {
            toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
        }
        toast.setText(text);
        toast.show();
    }
}

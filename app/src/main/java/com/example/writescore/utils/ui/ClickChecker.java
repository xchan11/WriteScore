package com.example.writescore.utils.ui;

/**
 * 事件拦截专用工具，防止频繁操作 */
public class ClickChecker {
    private static long lastTimeClick = 0;

    //上次有效点击时间是否还不到millisSecond毫秒
    public static boolean checkTime(long millisSecond){
        if(System.currentTimeMillis()-lastTimeClick < millisSecond){
            return true;
        }
        else {
            lastTimeClick = System.currentTimeMillis();
            return false;
        }
    }
    //清除拦截
    public static void cleanTime(){
        lastTimeClick = 0;
    }
    
}

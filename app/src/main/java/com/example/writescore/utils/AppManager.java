package com.example.writescore.utils;

import static tuorong.com.healthy.MyApplication.appContext;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;

import java.util.Stack;

/**
 * Activity统一管理类
 */
public class AppManager {
    private static Stack<Activity> stack;
    private static AppManager manager;

    /**
     * 获取实例
     */
    public static AppManager getInstance() {
        if (manager == null) {
            manager = new AppManager();
            stack = new Stack<>();
        }
        return manager;
    }

    /**
     * 添加 Activity
     */
    public void addActivity(Activity activity) {
        stack.add(activity);
    }

    /**
     * 移除 Activity
     */
    public void removeActivity(Activity activity) {
        stack.remove(activity);
    }

    /**
     * 结束所有 Activity
     */
    public void removeAll() {
        for (Activity activity : stack) {
            if (activity != null) {
                activity.finish();
            }
        }
        stack.clear();
    }

    public static boolean isDeveloperModeEnabled() {
        // 检查开发者模式是否启用
        int developerModeEnabled = Settings.Global.getInt(appContext.getContentResolver(),
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0);
        return developerModeEnabled == 1;
    }

}

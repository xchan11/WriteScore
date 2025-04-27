package tuorong.com.healthy.utils

import android.annotation.SuppressLint
import android.os.Build
import android.text.TextUtils
import android.util.Log
import tuorong.com.healthy.BuildConfig
import tuorong.com.healthy.manager.GlobalConfig

object LogUtils {
    //把捕获的异常等日志上传到服务器
    @SuppressLint("SuspiciousIndentation")
    fun uploadLog(tag: String?, content: String?) {
        var info =
            "\n上传手机时间 : ${System.currentTimeMillis()}"+
            "\n安卓系统版本 : ${Build.VERSION.RELEASE}"+//安卓版本
            "\nsdkVersion : ${Build.VERSION.SDK_INT}"+ //SDK版本
            "\n手机型号 : ${Build.MODEL}"+// 获取手机型号
            "\n制造商 : ${Build.MANUFACTURER}"+// 获取手机制造商
            "\nappVersion : ${BuildConfig.VERSION_CODE}"//app版本
            info.length
        //MyApplication.apiService.uploadLog(tag,MyApplication.user?.userId, info, content)
    }
    @JvmStatic
    fun e(text: String?) {
        if (GlobalConfig.isDebug()) {
            if (!TextUtils.isEmpty(text)) {
                Log.e("family", text!!)
                // TODO: 2021/6/1 修改写入路径后启用该方法
//                writeToFile(text);
            }
        }
    }

    fun d(tag: String = TAG, msg: String?) {
        if (GlobalConfig.isDebug()) {
            Log.d(tag, msg ?: "")
        }
    }

    fun w(tag: String = TAG, msg: String, e: Throwable? = null) {
        Log.w(tag, msg, e)
    }

    fun e(tag: String = TAG, msg: String, e: Throwable? = null) {
        Log.e(tag, msg, e)
    }

    private const val TAG = "LogUtils"
}

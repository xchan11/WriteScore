package tuorong.com.healthy.api.interceptor

import android.util.Log
import tuorong.com.healthy.utils.NetLogUtil

object LogPrinter {
    //为了方便打断点
    fun printNetLog(message:String){
        when{
            message.contains("<--") ->{
                printLog(message)
            }
            message.contains("-->") ->{
                printLog(message)
            }
            message.contains("{\"code\":") ->{
                printLog(message)
            }
           else ->{
               printLog(message)
           }
        }
    }

    private fun printLog(message:String){
         NetLogUtil.writeLog("",message)
         logLongMessage("NetInfoLog", message)
    }

    /**
    * 超长日志自动换行防止被吞
    * */
    private fun logLongMessage(tag: String, message: String) {
        val maxLogSize = 1000
        var i = 0
        while (i < message.length) {
            if (i + maxLogSize < message.length) {
                Log.d(tag, message.substring(i, i + maxLogSize))
            } else {
                Log.d(tag, message.substring(i))
            }
            i += maxLogSize
        }
    }
}
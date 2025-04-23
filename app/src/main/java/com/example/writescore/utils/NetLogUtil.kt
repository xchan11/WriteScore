package tuorong.com.healthy.utils
import android.content.Context
import android.os.Build
import android.text.TextUtils
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import tuorong.com.healthy.MyApplication
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files

/**
 * 网络日志读写工具
 * 请勿将其它日志写在本文件，需要的自己另外创建
 */
object NetLogUtil {
    private fun getLogFile(context: Context?): File {
        return File(context!!.filesDir, "network_logs.txt")
    }

    fun writeLog(tag: String, message: String) {
        if (TextUtils.isEmpty(message)) return
        if (message.length < 3) return
        val logFile = getLogFile(MyApplication.appContext)
        try {
            BufferedWriter(FileWriter(logFile, true)).use { writer ->
                writer.write("$tag: $message")
                writer.newLine()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun readLogs(context: Context?): String {
        val logFile = getLogFile(context)
        val logContent = StringBuilder()
        try {
            var lines: List<String?>? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                lines = Files.readAllLines(logFile.toPath())
            }
            for (line in lines!!) {
                logContent.append(line).append("\n")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return logContent.toString()
    }

    fun clearLogs(context: Context?): Boolean {
        val logFile = getLogFile(context)
        return try {
            FileWriter(logFile, false).close()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }


    fun getInitialLogContent(context: Context): String {
        var logContent = readLogs(context)
        if (logContent.length > 60000) {
            logContent =
                cleanTip + "\n" + logContent.substring(logContent.length - 60000, logContent.length)
            clearLogs(context)
            writeLog("", logContent)  // 更新日志文件
        }
        return logContent
    }


    private val cleanTip = "日志长度超过6w，已清空旧的"


    private val longCharSize = 400 // 超长行的字符数限制
    fun formatLogContent(content: String, showLongResponse: Boolean): AnnotatedString {
        return buildAnnotatedString {
            content.split("\n").forEach { line ->
                when {
                    line.contains("{\"code\"") -> {
                        if (showLongResponse || line.length <= longCharSize) {
                            withStyle(style = SpanStyle(color = Color(0xFF00C4FF))) {
                                append(line)
                            }
                        } else {
                            withStyle(style = SpanStyle(color = Color(0xFF17A2FF))) {
                                append(line.substring(0, longCharSize))
                            }
                            withStyle(style = SpanStyle(color = Color(0xFF99FF00))) {
                                append(" ...... 省略${line.length - longCharSize}个字符")
                            }
                        }
                    }

                    line.contains("<--") && line.contains("http") -> {
                        withStyle(style = SpanStyle(color = Color.Red)) {
                            append(line)
                        }
                    }

                    line.contains(cleanTip) -> {
                        withStyle(style = SpanStyle(color = Color(0xFF99FF00))) {
                            append(line)
                        }
                    }

                    line.contains("发起socket连接") -> {
                        withStyle(style = SpanStyle(color = Color(0xFFFFFF00))) {
                            append(line)
                        }
                    }

                    else -> {
                        withStyle(style = SpanStyle(color = Color(0xFFF6F6F3))) {
                            append(line)
                        }
                    }
                }
                append("\n")
            }
        }
    }

}
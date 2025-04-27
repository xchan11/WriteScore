package tuorong.com.healthy.utils.message

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun formatTimestamp1(expectSendTime: Long?): String {
    if(expectSendTime==null)
        return "- -"
    val calendar = Calendar.getInstance()
    val currentTime = System.currentTimeMillis()

    // 获取当前时间的信息
    val currentYear = calendar.get(Calendar.YEAR)
    val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)

    if(expectSendTime > 1000000000000L)//自动根据秒级和毫秒级换算
        calendar.timeInMillis = expectSendTime
    else
    // 设置时间戳的时间信息
        calendar.timeInMillis = expectSendTime* 1000
    val timestampYear = calendar.get(Calendar.YEAR)
    val timestampWeek = calendar.get(Calendar.WEEK_OF_YEAR)

    return when {
        // 本周
        currentWeek == timestampWeek && currentYear == timestampYear -> {
            val dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.time)
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            "$dayOfWeek ${timeFormat.format(calendar.time)}"
        }
        // 今年但不是本周
        currentYear == timestampYear -> {
            val dateFormat = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
            dateFormat.format(calendar.time)
        }
        // 不是今年
        else -> {
            val fullDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            fullDateFormat.format(calendar.time)
        }
    }
}

//强行转为秒级时间戳

package tuorong.com.healthy.utils.message

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
fun formatTimestamp(timestamp: Long?): String {
    if (timestamp == null)
        return "- -"

    val calendar = Calendar.getInstance()
    val currentTime = System.currentTimeMillis()

    // 获取当前时间的信息
    val currentYear = calendar.get(Calendar.YEAR)
    val currentDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
    val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)

    // 设置时间戳的时间信息
    if (timestamp > 1000000000000L) // 自动根据秒级和毫秒级换算
        calendar.timeInMillis = timestamp
    else
        calendar.timeInMillis = timestamp * 1000

    val timestampYear = calendar.get(Calendar.YEAR)
    val timestampDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
    val timestampWeek = calendar.get(Calendar.WEEK_OF_YEAR)

    return when {
        // 今天
        currentYear == timestampYear && currentDayOfYear == timestampDayOfYear -> {
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            timeFormat.format(calendar.time)
        }
        // 昨天
        currentYear == timestampYear && currentDayOfYear - timestampDayOfYear == 1 -> {
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            "昨天 ${timeFormat.format(calendar.time)}"
        }
        // 本周（但不是今天或昨天）
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

/**
 * 会话列表显示的时间
 * */
fun getTimeStrInContact(timestamp: Long?): String {
    if (timestamp == null)
        return "- -"

    val calendar = Calendar.getInstance()
    val currentTime = System.currentTimeMillis()

    // 获取当前时间的信息
    val currentYear = calendar.get(Calendar.YEAR)
    val currentDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
    val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)

    // 设置时间戳的时间信息
    if (timestamp > 1000000000000L) // 自动根据秒级和毫秒级换算
        calendar.timeInMillis = timestamp
    else
        calendar.timeInMillis = timestamp * 1000

    val timestampYear = calendar.get(Calendar.YEAR)
    val timestampDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
    val timestampWeek = calendar.get(Calendar.WEEK_OF_YEAR)

    return when {
        // 今天
        currentYear == timestampYear && currentDayOfYear == timestampDayOfYear -> {
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            timeFormat.format(calendar.time)
        }
        // 昨天
        currentYear == timestampYear && currentDayOfYear - timestampDayOfYear == 1 -> {
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            "昨天 ${timeFormat.format(calendar.time)}"
        }
        // 本周（但不是今天或昨天）
        currentWeek == timestampWeek && currentYear == timestampYear -> {
            val dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.time)
            "$dayOfWeek"
        }
        // 今年但不是本周
        currentYear == timestampYear -> {
            val dateFormat = SimpleDateFormat("MM-dd", Locale.getDefault())
            dateFormat.format(calendar.time)
        }
        // 不是今年
        else -> {
            val fullDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            fullDateFormat.format(calendar.time)
        }
    }
}




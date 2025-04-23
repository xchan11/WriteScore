package tuorong.com.healthy.utils

/**
 * 防抖动工具类
 */
object AntiShakeUtil {
    private var lastClicked: Long? = 0L
    private const val DELAY_MILLS: Long = 1000
    fun isShake(): Boolean {
        val currentTime = System.currentTimeMillis()
        var lastClickMills = lastClicked ?: 0
        return if (currentTime - lastClickMills > DELAY_MILLS) {
            lastClicked = currentTime
            true
        } else {
            false
        }
    }
}
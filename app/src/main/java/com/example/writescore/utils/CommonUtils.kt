package tuorong.com.healthy.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.Gson
import org.json.JSONObject
import tuorong.com.healthy.BuildConfig
import tuorong.com.healthy.MyApplication.appContext
import tuorong.com.healthy.MyApplication.currentAty
import tuorong.com.healthy.R
import tuorong.com.healthy.manager.GlobalConfig
import tuorong.com.healthy.model.RequestType
import tuorong.com.healthy.ui.LoginActivity
import tuorong.com.healthy.ui.userUi.dialogFragment.showIAbnormalDialog
import tuorong.com.healthy.utils.LoginJumpingManager.ROLE_ADMIN
import tuorong.com.healthy.utils.LoginJumpingManager.ROLE_AGENT
import tuorong.com.healthy.utils.LoginJumpingManager.ROLE_CONSUMER
import tuorong.com.healthy.utils.LoginJumpingManager.ROLE_EXPERT
import tuorong.com.healthy.utils.LoginJumpingManager.ROLE_PROVIDER
import tuorong.com.healthy.utils.file.SharePreferenceUtil
import tuorong.com.healthy.utils.ui.ToastUtil
import java.io.File
import java.io.UnsupportedEncodingException
import java.lang.reflect.Type
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.floor


fun String?.logD(): String {
    if (GlobalConfig.isDebug())
        Log.d("CommonUtils-LogD", this ?: "")
    return this ?: ""
}

fun String?.toastCover(): String {
    ToastUtil.coverToast(appContext, this)
    Log.d("toastInCommon",this?:"null")
    return this ?: ""
}

fun String?.toastCoverLong(): String {
    ToastUtil.coverToastLong(appContext, this)
    Log.d("toastInCommonLong",this?:"null")
    return this ?: ""
}

fun String?.dgToastCover(): String {
    if (GlobalConfig.isDebug()) {
        ToastUtil.coverToast(appContext, this)
        return this ?: ""
    }
    return this ?: ""
}

fun String?.dgToast(): String {
    if(GlobalConfig.isDebug()){
        Toast.makeText(appContext, this ?: "null", Toast.LENGTH_SHORT).show()
        return this ?: ""
    }
    return this ?: ""
}

fun String?.toast(): String {
    Toast.makeText(appContext, this ?: "null", Toast.LENGTH_SHORT).show()
    return this ?: ""
}

fun String?.toastDebug(): String {
    if (GlobalConfig.isDebug())
        Toast.makeText(appContext, this ?: "null", Toast.LENGTH_SHORT).show()
    return this ?: ""
}

fun String?.toastLong(): String {
    if (GlobalConfig.isDebug())
        Toast.makeText(appContext, this ?: "null", Toast.LENGTH_LONG).show()
    return this ?: ""
}


fun <T : View> T?.show(): T? {
    this?.visibility = View.VISIBLE
    return this
}

fun <T : View> T?.hide(): T? {
    this?.visibility = View.GONE
    return this
}

fun <T : View> T?.invisible(): T? {
    this?.visibility = View.INVISIBLE
    return this
}

fun <T : View> T?.toShow(show: Boolean): T? {
    if (show)
        this?.visibility = View.VISIBLE
    else
        this?.visibility = View.GONE
    return this
}
fun <T : View> T?.toVisible(show: Boolean): T? {
    if (show)
        this?.visibility = View.VISIBLE
    else
        this?.visibility = View.INVISIBLE
    return this
}
//屏蔽触摸
fun View.stopTouchEvent() {
    this.setOnTouchListener { _, _ ->
        true
    }
}

fun String?.logD(tag: String = ""): String {
    if (GlobalConfig.isDebug()) {
        Log.d(if (tag.isNotEmpty()) tag else "Lam:LogD", this ?: "null")
    }
    return this ?: ""
}

//时间戳转换成正常的时间
fun Long.convertTimestampToDateTime(): String {
    val date = Date(this)
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return simpleDateFormat.format(date)
}

fun Long.convertTimestampToYMDHMS(): String {
    val date = Date(this)
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return simpleDateFormat.format(date)
}

fun Long.convertTimestampToYear(): String {
    val date = Date(this)
    val simpleDateFormat = SimpleDateFormat("yyyy", Locale.getDefault())
    return simpleDateFormat.format(date)
}

fun Long.convertTimestampToMonthDay(): String {
    val date = Date(this)
    val simpleDateFormat = SimpleDateFormat("MM-dd", Locale.getDefault())
    return simpleDateFormat.format(date)
}

fun String.convertMMDDToDate(): String? {
    val inputFormat = SimpleDateFormat("MM-dd", Locale.getDefault())
    val outputFormat = SimpleDateFormat("MM月dd日", Locale.getDefault())

    val date = inputFormat.parse(this)
    return outputFormat.format(date)
}

fun Drawable.sameAs(drawable: Drawable?): Boolean {
    if (drawable == null) return false
    if (this is BitmapDrawable && drawable is BitmapDrawable) {
        return this.bitmap.sameAs(drawable.bitmap)
    }
    return false
}


/*榜单数字显示规则:补上K和M 保留一位小数 */
fun Double?.formatNumber(): String {
    when {
        this == null -> return "0"
        this > 10000000 -> return "${(removeDecimalPart(this / 1000000))}M"
        this > 10000 -> return "${(removeDecimalPart(this / 1000))}K"
        else -> this.toString().apply {
            val end = if (this@apply.contains("."))
                this@apply.lastIndexOf(".")
            else this@apply.length
            return this@apply.substring(0, end)
        }
    }
}

fun removeDecimalPart(input: Double): Double {
    return floor(input * 10.0) / 10.0
}

//设置文字或drawable资源
fun TextView.setTextOrDrawable(s: String?, d: Int?) {
    s?.let {
        text = it
    }
    d?.let {
        setCompoundDrawablesWithIntrinsicBounds(it, 0, 0, 0)
        text = ""
    }
}


/**把秒数转换成 时：分：秒 的格式*/
fun Int.toTime(): String {
    val hour = this / 3600
    val min = (this - hour * 3600) / 60
    val second = this - hour * 3600 - min * 60
    val hourStr = if (hour < 10) "0$hour" else hour.toString()
    val minStr = if (min < 10) "0$min" else min.toString()
    val secStr = if (second < 10) "0$second" else second.toString()
    return if (hour < 1) "$minStr:$secStr" else "$hourStr:$minStr:$secStr"
}

fun Int.dpToPx(context: Context): Int {
    val displayMetrics = context.resources.displayMetrics
    return (this * (displayMetrics.densityDpi / 160f)).toInt()
}

fun roundToNearestSecond(timestamp: Long): Long {
    val seconds = timestamp / 1000
    val milliseconds = timestamp % 1000
    return if (milliseconds >= 500) {
        seconds + 1
    } else {
        seconds
    }
}

fun Int.seconds2Standard(): String{
    val hour = "%02d".format(this / 3600)
    val minute = "%02d".format(this % 3600 / 60)
    val second = "%02d".format(this % 3600 % 60)
    return if(hour == "00")
        "$minute:$second"
    else
        "$hour:$minute:$second"
}

fun getVideoFrameFromFile(videoFile: File): Bitmap? {
    val retriever = MediaMetadataRetriever()
    var bitmap: Bitmap? = null
    try {
        // 设置数据源为本地文件
        retriever.setDataSource(videoFile.absolutePath)

        // 获取第一帧（在0微秒位置）
        bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        retriever.release()
    }
    return bitmap
}


fun <T> String.getJsonData(): T? {
    try {
        val jsonObject = JSONObject(this)
        val status = jsonObject.optInt("status")

        if (status == 200) {
            val data = jsonObject.opt("data")
            // 在这里根据需要进行类型转换
            @Suppress("UNCHECKED_CAST")
            return data as T
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return null
}

fun View.setClick(id: Int, click: () -> Unit) {
    this.findViewById<View>(id).setOnClickListener {
        click.invoke()
    }
}

fun View.setShow(id: Int, show: Boolean) {
    this.findViewById<View>(id).toShow(show)
}

/**
 * 2023-09-27
 * 加载圆角图片 并支持gif
 * @param url 图片地址
 * @param round 圆角像素，为0时 为圆形图片
 */
fun glideImg(mContext: Context?, head: ImageView, url: String?, round: Int) {
    try {
        val options = RequestOptions()
            .transforms(CenterCrop(), RoundedCorners(round))
        val a: Activity? = mContext as FragmentActivity?
        if (a == null || a.isFinishing || a.isDestroyed) return
        if (url == null) { //地址为空 用默认图片
//            glideImg(mContext, head, R.drawable.icon_default, round)
        }
        if (round > 0) {
            Glide.with(mContext!!).load(url).centerCrop().apply(options).into(head)
        } else { //圆角像素为0时，加载圆形图片
            Glide.with(mContext!!).load(url).circleCrop().into(head)
        }
    } catch (e: java.lang.Exception) {
    }
}

fun glideImg(mContext: Context?, head: ImageView?, url: Int, round: Int) {
    try {
        val options = RequestOptions()
            .transforms(CenterCrop(), RoundedCorners(round))
        val a: Activity? = mContext as FragmentActivity?
        if (a == null || a.isFinishing || a.isDestroyed) return
        if (url == 0) return
        if (round > 0) {
            Glide.with(mContext!!).load(url).centerCrop().apply(options).into(
                head!!
            )
        } else Glide.with(mContext!!).load(url).circleCrop().into(head!!)
    } catch (e: java.lang.Exception) {
    }

}

fun copyToClipboard(context: Context, text: String, label: String) {
    // 获取系统剪切板管理器
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    // 创建 ClipData 对象并将文本添加到剪切板
    val clipData = ClipData.newPlainText(label, text)
    clipboardManager.setPrimaryClip(clipData)
}

// 根据索引选择 RadioButton
fun RadioGroup.selectRadioButtonByIndex(index: Int) {
    if (index in 0 until childCount) {
        val radioButton = getChildAt(index) as? RadioButton
        radioButton?.isChecked = true
    }
}

// 获取所选 RadioButton 的索引
fun RadioGroup.getSelectedRadioButtonIndex(): Int {
    for (i in 0 until childCount) {
        val radioButton = getChildAt(i) as? RadioButton
        if (radioButton?.isChecked == true) {
            return i
        }
    }
    return -1  // 如果没有选中的 RadioButton，则返回 -1
}

@SuppressLint("SuspiciousIndentation")
fun <T> getDataFromGson(json: String?, clazz: Type): T? {
    try {
        val gson = Gson()
        val result: T = gson.fromJson(json, clazz)
        return result
    } catch (e: Exception) {
        return null
    }
}

fun ImageView.load(url: String) {
    if (currentAty == null)
        return
    if (currentAty.isDestroyed || currentAty.isFinishing)//防止活动被销毁时，加载导致崩溃
        return
    Glide.with(currentAty).load(url).into(this)
}

fun ImageView.load(url: String, default: Int) {
    if (currentAty == null)
        return
    if (currentAty.isDestroyed || currentAty.isFinishing)//防止活动被销毁时，加载导致崩溃
        return
    Glide.with(currentAty).load(url).placeholder(default).error(default).into(this)
}

fun ImageView.loadCenter(url: String) {
    if (this == null||url.isNullOrEmpty())
        return
    Glide.with(this).load(url).centerCrop().into(this)
}

fun ImageView.load(resId: Int?) {
    resId?:return
    if (currentAty == null)
        return
    if (currentAty.isDestroyed || currentAty.isFinishing)
        return
    Glide.with(currentAty).load(resId).into(this)
}

fun ImageView.load(url: String, options: RequestOptions) {
    if(url.isEmpty()) return
    if (currentAty == null)
        return
    if (currentAty.isDestroyed || currentAty.isFinishing)
        return
    Glide.with(currentAty).load(url).apply(options).into(this)
}
fun ImageView?.loadCircle(url: String,default: Int = 0) {
    this?:return
    if(default==0)
        Glide.with(this).load(url).circleCrop().into(this)
    else
        Glide.with(this).load(url).placeholder(default).error(default).circleCrop().into(this)
}
fun ImageView?.loadCorner(url: String, radius: Int) {
    if(url.isEmpty()) return
    if (this == null)
        return
    val options = RequestOptions().transform(
        CenterCrop(),
        RoundedCorners(radius.dp(this))
    )
    Glide.with(this).load(url).apply(options).into(this)
}

fun ImageView?.loadCorner(url: String, radius: Int, @DrawableRes default: Int) {
    if(url.isEmpty()) return
    if (this == null)
        return
    val options = RequestOptions().transform(
        CenterCrop(),
        RoundedCorners(radius.dp(this))
    )
    Glide.with(this).load(url).placeholder(default).error(default).apply(options).into(this)
}

//获取hours小时前的时间戳
fun getXHoursAgoTimestamp(hours: Float): Long {
    val now = System.currentTimeMillis()
    val ago = now - (hours * 60F * 60F * 1000)
    return ago.toLong()
}

fun List<View>.setOnClickListener(listener: View.OnClickListener) {
    this.forEach {
        it.setOnClickListener(listener)
    }
}

fun FragmentActivity.setFragment(id: Int, fragment: Fragment) {
    supportFragmentManager.beginTransaction().replace(id, fragment).commit()
}

fun View?.setWidMatch() {
    if (this == null)
        return
    val layoutParams: ViewGroup.LayoutParams = this.layoutParams
    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
    this.layoutParams = layoutParams
}

fun View?.setMarginBottomDp(margin: Int) {
    if (this == null)
        return
    val marginBottomInPx = (margin * resources.displayMetrics.density + 0.5f).toInt()
    val layoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
    layoutParams.bottomMargin = marginBottomInPx
    this.layoutParams = layoutParams
}

fun View?.setMarginTopDp(margin: Int) {
    if (this == null)
        return
    val marginBottomInPx = (margin * resources.displayMetrics.density + 0.5f).toInt()
    val layoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
    layoutParams.topMargin = marginBottomInPx
    this.layoutParams = layoutParams
}

fun View?.setMarginStartDp(margin: Int) {
    if (this == null)
        return
    val marginBottomInPx = (margin * resources.displayMetrics.density + 0.5f).toInt()
    val layoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
    layoutParams.marginStart = marginBottomInPx
    this.layoutParams = layoutParams
}

fun View?.setMarginEndDp(margin: Int) {
    if (this == null)
        return
    val marginBottomInPx = (margin * resources.displayMetrics.density + 0.5f).toInt()
    val layoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
    layoutParams.marginEnd = marginBottomInPx
    this.layoutParams = layoutParams
}

fun View?.setPadding2(padding: Int) {
    if (this == null)
        return
    val paddingInPx = (padding * resources.displayMetrics.density + 0.5f).toInt()
    setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx)
}

fun pxToDp(px: Int): Int {
    val density = Resources.getSystem().displayMetrics.density
    return (px / density + 0.5f).toInt()
}

fun View?.setHeightDp(num: Int) {
    if (this == null)
        return
    val dpInPx = (num * resources.displayMetrics.density + 0.5f).toInt()
    layoutParams?.let {
        it.height = dpInPx
        layoutParams = it
    }
    requestLayout()
}

fun View?.setWidthDp(num: Int) {
    if (this == null)
        return
    val dpInPx = (num * resources.displayMetrics.density + 0.5f).toInt()
    layoutParams?.let {
        it.width = dpInPx
        layoutParams = it
    }
    requestLayout()
}

fun View?.setWidthHeightDp(wid: Int, hei: Int) {
    if (this == null)
        return
    val dpWid = (wid * resources.displayMetrics.density + 0.5f).toInt()
    val dpHei = (hei * resources.displayMetrics.density + 0.5f).toInt()
    layoutParams?.let {
        it.width = dpWid
        it.height = dpHei
        layoutParams = it
    }
    requestLayout()
}

fun Int?.dp(v: View?): Int {//dp转px
    if (v == null) return (this ?: 1) * 2
    return ((this ?: 1) * v.resources.displayMetrics.density + 0.5f).toInt()
}

fun Int?.dp(): Int {//dp转px
    return ((this?:1) * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
}
fun ImageView?.loadFit(context: Context?, imageUrl: String?) {
    this ?: return
    context ?: return
    imageUrl ?: return
    if (context is FragmentActivity) {
        if (context.isFinishing || context.isDestroyed)
            return
    }
    Glide.with(context)
        .load(imageUrl)
        .into(object : CustomTarget<Drawable?>() {
            override fun onResourceReady(
                resource: Drawable,
                transition: Transition<in Drawable?>?
            ) {
                // 在图片加载完成后获取图片的宽度和高度
                val width = resource.intrinsicWidth
                val height = resource.intrinsicHeight

                // 根据图片的宽高比动态设置 ImageView 的大小
                this@loadFit.run {
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    adjustViewBounds = true // 保持宽高比
                    setImageDrawable(resource)
                }
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                // Placeholder removed
            }
        })

}

fun String.getSHA256(): String{
    try {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(this.toByteArray(StandardCharsets.UTF_8))
        return byte2HexSHA(messageDigest.digest())
    }catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    } catch (e: UnsupportedEncodingException) {
        e.printStackTrace()
    }
    return ""
}

private fun byte2HexSHA(bytes: ByteArray): String {
    return bytes.joinToString("") { "%02x".format(it) }
}

fun <T> RequestType<T>?.isOk(havaDataEntry: Boolean = false): Boolean {
    if(GlobalConfig.isDebug() && (this == null || this.code != 0)){
        val requestType = this ?: RequestType(100000, "接口请求失败 requestType == null", "")
        currentAty.supportFragmentManager.showIAbnormalDialog(requestType)
    }
    if (this == null) {
        "系统异常".toastCover()
        return false
    }
    val context = currentAty
    if (code == 40301) {
        "操作频繁,请稍后重试".toastCover()
        return false
    } else if (this.code == 40100 && this.message.equals(context.getString(R.string.CANT_LOGIN))) {
        context.getString(R.string.PHONE_PASSWORD_CACHE_FAILURE).toast()
        SharePreferenceUtil.verifiedLogin(false, "")
        if (context != null){
            LoginActivity.startActivity(context, context.getString(R.string.CANT_LOGIN))
            context.finish()
        }
        return false
    } else if (this.code == 0) {
        if (havaDataEntry)
            return this.data != null
        return true
    }
    return false
}


fun <T> T.isOneOf(vararg options: T): Boolean {
    return this in options
}

fun calculateAge(birthdayStr: String?): Int {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    if (birthdayStr.isNullOrEmpty()) return 0
    val birthday = formatter.parse(birthdayStr) ?: return 0
    val today = Calendar.getInstance()
    val birthDate = Calendar.getInstance().apply { time = birthday }
    var age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)
    if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
        age--
    }

    return age
}
fun <T> List<T?>?.toSaveList():MutableList<T>{
    if(this.isNullOrEmpty())
        return mutableListOf<T>()
    val resultList = this.filterNotNull()
    return resultList.toMutableList()
}
@Suppress("DEPRECATION")
fun Context.getDisplaySize(): Pair<Int, Int>{
    val windowManager = getSystemService<WindowManager>()!!
    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
        val bounds = windowManager.currentWindowMetrics.bounds
        bounds.width() to bounds.height()
    }else{
        val point = Point()
        windowManager.defaultDisplay?.getSize(point)
        point.x to point.y
    }
}

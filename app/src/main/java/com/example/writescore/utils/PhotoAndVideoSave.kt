package tuorong.com.healthy.utils

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import tuorong.com.healthy.adapter.MessageAdapter.Companion.MSG_PHOTO
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

object PhotoAndVideoSave {
    fun saveMedia(context: Context, url: String, type: Int) {
        Executors.newSingleThreadExecutor().execute {
            try {
                //验证URL是否有效
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connectTimeout = 10000 //设置连接超时
                connection.readTimeout = 10000 //设置读取超时
                connection.requestMethod = "GET"

                val responseCode = connection.responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    //响应200以外的访问失败
                    throw Exception("无法访问资源，请检查链接")
                }
                val inputStream = connection.inputStream ?: throw Exception("无法获取输入流")
                val fileType = getFileExtension(url)
                val fileName = if (type == MSG_PHOTO) {
                    "IMG_${System.currentTimeMillis()}.$fileType"
                } else {
                    "VID_${System.currentTimeMillis()}.$fileType"
                }

            val resolver = context.contentResolver
            var uri: Uri? = null

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    //安卓10及以上适配
                    val mimeType = getType(fileType, type)
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                        put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                        if (type == MSG_PHOTO) {
                            put(
                                MediaStore.MediaColumns.RELATIVE_PATH,
                                Environment.DIRECTORY_PICTURES + "/wellness"
                            )
                        } else {
                            put(
                                MediaStore.MediaColumns.RELATIVE_PATH,
                                Environment.DIRECTORY_MOVIES + "/wellness"
                            )
                        }
                    }

                    uri = if (type == MSG_PHOTO) {
                        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    } else {
                        resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
                    }

                    uri?.let {
                        resolver.openOutputStream(it).use { outputStream ->
                            inputStream.copyTo(outputStream!!)
                        }
                    }
                } else {
                    //安卓9及以下适配
                    val directory = if (type == MSG_PHOTO) {
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/wellness"
                    } else {
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).toString() + "/wellness"
                    }

                    val file = File(directory)
                    if (!file.exists()) {
                        file.mkdirs()
                    }

                    val mediaFile = File(file, fileName)
                    FileOutputStream(mediaFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                    uri = Uri.fromFile(mediaFile)

                    // 通知媒体库更新
                    context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
                }

            if (context is Activity) {
                    (context as? Activity)?.runOnUiThread {
                        val message = if (url.isNotEmpty()) {
                            "保存成功"
                        }else{
                            "保存失败"
                        }
                        message.toast()
                    }
                }
            } catch (e: Exception) {
                if (context is Activity) {
                    (context as? Activity)?.runOnUiThread {
                        "保存失败，无法访问资源".toastCover()
                    }
                }
            }
        }
    }
    /**
    *  根据文件类型返回的MIME类型
     *  图片的MIME类型有：jpeg、png、gif
     * 视频为 mp4
    * */
    //获取文件扩展名
    private fun getFileExtension(url: String): String {
        val extension = url.substringAfterLast('.', missingDelimiterValue = "jpg").lowercase()
        return when (extension) {
            "jpeg", "jpg" -> "jpg"
            "png" -> "png"
            "gif" -> "gif"
            else -> "jpg"
        }
    }

    //获取文件类型
    private fun getType(fileType: String, type: Int): String {
        return when {
            type == MSG_PHOTO && fileType == "jpg" -> "image/jpeg"
            type == MSG_PHOTO && fileType == "png" -> "image/png"
            type == MSG_PHOTO && fileType == "gif" -> "image/gif"
            type == MSG_PHOTO -> "image/jpeg"
            else -> "video/mp4"
        }
    }
}
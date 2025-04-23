package tuorong.com.healthy.utils.file

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import androidx.fragment.app.FragmentActivity
import tuorong.com.healthy.utils.toastCover
import java.io.File

object VideoUploadUtil {
    private var uploadProgress = 0
    private var urlCover = ""
    private var urlVideo = ""
    private var fileName = ""
    private var coverUploadDown = false
    private var videoUploadDown = false
    var uploading = false
    fun selectVideoAndUpload(
        activity: FragmentActivity,
        finish: (urlCover: String, urlVideo: String, fileName: String, isSuccess: Boolean) -> Unit
    ) {
        var uploadCount = 0

        UploadPictureUtil.selectPhotoAndPost(1, 2, activity) {
            if (it.isNullOrEmpty())
                return@selectPhotoAndPost
            else {
                val bitmap = getVideoFrameFromFile(it[0])
                if (bitmap == null) {
                    "视频格式不支持".toastCover()
                    return@selectPhotoAndPost
                }
                uploading = true

                UploadPictureUtil.startUpload(bitmap, "chat") { url, success ->
                    urlCover = url
                    uploadProgress += if (success) {
                        2
                    } else
                        1
                    coverUploadDown = true
                    uploadCount++
                    if (uploadCount == 2) {
                        finish(urlCover, urlVideo, fileName, uploadProgress > 2)
                        uploading = false
                    }
                }
                UploadPictureUtil.startUpload(it[0], "chat") { url, success, fName ->
                    urlVideo = url
                    if (success) {
                        uploadProgress += 2
                        fileName = fName
                    }
                    videoUploadDown = true
                    uploadCount++
                    if (uploadCount == 2) {
                        finish(urlCover, urlVideo, fileName, uploadProgress > 2)
                        uploading = false
                    }
                }
            }
        }
    }

    private fun getVideoFrameFromFile(videoFile: File): Bitmap? {
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
}
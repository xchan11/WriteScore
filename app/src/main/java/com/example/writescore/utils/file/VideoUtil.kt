package tuorong.com.healthy.utils.file

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.media.MediaMetadataRetriever
import android.os.Build
import android.util.Log
import java.net.URL

fun fetchVideoFrameAsync(videoUrl: String, onResult: (Bitmap?) -> Unit) {
    Thread {
        val bitmap = getVideoFrameFromUrl(videoUrl)
        // 回调到主线程
        Handler(Looper.getMainLooper()).post {
            onResult(bitmap)
        }
    }.start()
}


fun getVideoFrameFromUrl(videoUrl: String): Bitmap? {
    var bitmap: Bitmap? = null
    val retriever = MediaMetadataRetriever()
    try {
        // Set data source - this will require internet permission
        retriever.setDataSource(videoUrl, HashMap<String, String>())

        // Retrieve the first frame of the video
        bitmap = retriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST)
    } catch (e: Exception) {
        Log.e("VideoFrameError", "Error retrieving video frame: ${e.message}")
    } finally {
        // Release the MediaMetadataRetriever
        retriever.release()
    }
    return bitmap
}
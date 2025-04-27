@file:Suppress("DEPRECATION")

package tuorong.com.healthy.utils

import android.content.Context
import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import okio.IOException
import tuorong.com.healthy.MyApplication.currentAty
import tuorong.com.healthy.manager.MessageManager.sendVoiceMsg
import tuorong.com.healthy.model.message.IMMessage
import tuorong.com.healthy.utils.file.UploadFileHelper
import java.io.File


object AudioRecordManager {
    private const val RECORDING_TIMEOUT_MS = 60_000L
    private var recordingStartTime: Long = 0
    const val TAG = "SoundRecordManager"
    private var timeoutRunnable: Runnable? = null
    private val handler = Handler(Looper.getMainLooper())
    val recordingStateLiveData = MutableLiveData<Boolean>()
    const val MIN_RECORD_TIME = 500L // 最小录音时间，单位是毫秒

    private var mediaRecord: MediaRecorder? = null
    private var outputFilePath: String? = null
    private var isRecording = false
    private var onStop: ((IMMessage, Boolean) -> Unit?)? = null

    fun startRecording(context: Context, onStop: (IMMessage, Boolean) -> Unit) {
        if (isRecording) {
            return
        }
        onStop.let { this.onStop = it }
        outputFilePath = getOutputFilePath(context)
        mediaRecord = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFilePath)

            try {
                prepare()
                start()
                timeoutRunnable?.let {
                    handler.removeCallbacks(it)
                }
                isRecording = true
                recordingStateLiveData.postValue(true)
                recordingStartTime = System.currentTimeMillis()
                startRecordingTimeout()
            } catch (e: IOException) {
                Log.e(TAG, "IOException during prepare/start: ${e.message}")
                e.printStackTrace()
                resetRecorder()
            } catch (e: IllegalStateException) {
                Log.e(TAG, "IllegalStateException during prepare/start: ${e.message}")
                e.printStackTrace()
                resetRecorder()
            } catch (e: Exception) {
                Log.e(TAG, "其他异常: ${e.message}")
                e.printStackTrace()
                resetRecorder()
            }
        }
    }

    fun stopRecording(elapsedTime: Long) {
        if (!isRecording) {
            return
        }
        mediaRecord?.apply {
            try {
                stop()
                release()
                mediaRecord = null
                outputFilePath?.let {
                    val file = File(it)
                    UploadFileHelper.startUploadFile(currentAty, file, "") { url, _, success ->
                        if (success) {
                            recordingStateLiveData.postValue(false)
                            sendVoiceMsg(url, roundToNearestSecond(elapsedTime).toInt()) { message: IMMessage ->
                                onStop?.invoke(message, false)
                            }
                            isRecording = false
                        } else {
                            Log.d(TAG, "UploadFailure")
                        }
                        file.delete()
                    }
                }
                timeoutRunnable?.let {
                    handler.removeCallbacks(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun cancelRecording() {
        if (!isRecording) {
            return
        }
        mediaRecord?.apply {
            try {
                reset()
                release()
                mediaRecord = null
                isRecording = false
                recordingStateLiveData.postValue(false)
                outputFilePath?.let { File(it).delete() }
                timeoutRunnable?.let {
                    handler.removeCallbacks(it)
                }
            } catch (e: Exception) {
                Log.e("SoundRecordManager", "${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun startRecordingTimeout() {
        timeoutRunnable = Runnable {
            if (isRecording) {
                stopRecording(RECORDING_TIMEOUT_MS)
            }
        }
        handler.postDelayed(timeoutRunnable!!, RECORDING_TIMEOUT_MS)
    }

    private fun resetRecorder() {
        mediaRecord?.apply {
            try {
                reset()
                release()
            } catch (e: Exception) {
                Log.e(TAG, "${e.message}")
                e.printStackTrace()
            } finally {
                mediaRecord = null
                isRecording = false
            }
        }
    }

    fun adjustAudioWidth(llBoxContext: ViewGroup, elapsedTime: String) {
        llBoxContext.layoutParams.width = getDynamicWidth(elapsedTime.toInt())
        llBoxContext.requestLayout()
    }

    fun getDynamicWidth(elapsedTime: Int): Int {
        return when {
            elapsedTime <= 10 -> {
                220 + elapsedTime * 10
            }

            elapsedTime <= 20 -> {
                230 + elapsedTime * 8
            }

            else -> {
                220 + elapsedTime * 5
            }
        }
    }

    private fun getOutputFilePath(context: Context): String? {
        return try {
            val storageDir = context.filesDir
            val file = File(storageDir, "recording_${System.currentTimeMillis()}.m4a")
            file.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
            null
        }
    }
}
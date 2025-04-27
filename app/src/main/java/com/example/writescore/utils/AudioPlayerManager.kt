package tuorong.com.healthy.utils

import android.graphics.drawable.AnimationDrawable
import android.media.MediaPlayer
import android.util.Log
import android.widget.ImageView
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import tuorong.com.healthy.MyApplication.apiService
import tuorong.com.healthy.MyApplication.currentAty
import tuorong.com.healthy.R
import java.util.LinkedList

object AudioPlayerManager {
    private var mediaPlayer: MediaPlayer? = null
    private var currentPlayingMsgId: String? = null

    val audioMsgQueue: LinkedList<AudioMsg> = LinkedList() //语音消息队列
    val redPoints: MutableMap<String, Boolean> = mutableMapOf()
    private var status: PlayingStatus = PlayingStatus.DONE
    private val playingWaveImageViews = mutableMapOf<String, Pair<ImageView, Boolean>>()

    fun playAudio(audioUrl: String, msgId: String, waveImageView: ImageView, isMine: Boolean,  ivRedPoint: ImageView?, position: Int): Boolean {
        if (currentPlayingMsgId == msgId) {
            stopAudio()
            stopWaveAnimation(msgId)
            return false
        }

        stopAudio()

        mediaPlayer = MediaPlayer().apply {
            setDataSource(audioUrl)
            prepareAsync()
            setOnPreparedListener {
                start()
                markMsgRead(msgId)
                ivRedPoint?.let { it.hide() }
                stopAllWaveAnimations() // 确保在新音频开始播放时停止所有动画
                startWaveAnimation(waveImageView, msgId, isMine)
            }
            setOnCompletionListener {
                stopAudio()
                stopWaveAnimation(msgId)
                status = PlayingStatus.DONE
                playNextInQueue()
            }
        }
        currentPlayingMsgId = msgId
        return true
    }

    fun stopAudio() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        stopAllWaveAnimations()
        mediaPlayer = null
        currentPlayingMsgId = null
    }

    private fun startWaveAnimation(waveImageView: ImageView, msgId: String, isMine: Boolean) {
        waveImageView.setImageResource(R.drawable.audio_animation)
        waveImageView.rotation = if (isMine) 270f else 90f
        val animationDrawable = waveImageView.drawable as? AnimationDrawable
        animationDrawable?.let {
            it.start()
            playingWaveImageViews[msgId] = Pair(waveImageView, isMine)
        } ?: Log.e("AudioPlayerManager", "播放失败")
    }

    private fun stopWaveAnimation(msgId: String) {
        val waveImageViewPair = playingWaveImageViews[msgId]
        waveImageViewPair?.let { (waveImageView, isMine) ->
            waveImageView.rotation = if (isMine) 270f else 90f
            waveImageView.setImageResource(R.drawable.three_wave)
        }
        playingWaveImageViews.remove(msgId)
    }

    private fun stopAllWaveAnimations() {
        playingWaveImageViews.forEach { (_, waveImageViewPair) ->
            val (waveImageView, isMine) = waveImageViewPair
            waveImageView.rotation = if (isMine) 270f else 90f
            waveImageView.setImageResource(R.drawable.three_wave)
        }
        playingWaveImageViews.clear()
    }

    fun playNextInQueue(){
        if(status == PlayingStatus.PLAYING) return
        audioMsgQueue.poll()?.let { audio ->
            status = PlayingStatus.PLAYING
            playAudio(audio.content, audio.id, audio.imageView, false, audio.redPoint, audio.position)
        } ?: run {
            status = PlayingStatus.DONE
        }
    }

    private fun markMsgRead(msgId: String){
        val msgList = listOf(msgId)
        val requestBody = msgList.toString().toRequestBody("application/json".toMediaType())
        apiService.markMsgRead(requestBody).observe(currentAty){
            if(it.isOk()){
                redPoints[msgId] = false
            }
            else Log.d("markMsgRead: " , it?.message.toString())
        }
    }
}

enum class PlayingStatus{
    PLAYING,
    DONE,
}

class AudioMsg(
    var id: String = "",
    var content: String="",
    var imageView: ImageView,
    var redPoint: ImageView,
    var position: Int
)

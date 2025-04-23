package tuorong.com.healthy.utils.ui

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import tuorong.com.healthy.utils.hide
import tuorong.com.healthy.utils.show

/**
 * @param url 动画地址
 * @param times 播放次数
 * @param finish 回调方法
 * 指定gif播放动画次数，完成后回调
 * */
fun playGif(context: Context, imageView: ImageView, url: String, times: Int, finish: () -> Unit) {
    Glide.with(context)
        .asGif()
        .load(url)
        .into(object : CustomTarget<GifDrawable>() {
            override fun onResourceReady(
                resource: GifDrawable, transition: Transition<in GifDrawable?>?
            ) {
                limitTimes(imageView, resource, times, finish)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                // 清除加载时的处理
                Log.d("limitTimes", "调用失败")
            }
        })

}

/**
 * @param url 动画地址
 * @param times 播放次数
 * @param finish 回调方法
 * 指定webp播放动画次数，完成后回调
 * */
fun playWebp(context: Context, imageView: ImageView, url: String, times: Int, finish: () -> Unit) {
    imageView.show()
    Glide.with(context)
        .load(url)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean
            ): Boolean {
                return false
            }

            @RequiresApi(Build.VERSION_CODES.P)
            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                limitTimes(imageView, resource, times, finish)
                return true
            }
        })
        .into(imageView)
}

fun limitTimes(imageView: ImageView, resource: Drawable, times: Int, finish: () -> Unit) {
    if (resource is GifDrawable) {
        // 设置 ImageView 显示 GIF 图像
        imageView.setImageDrawable(resource)
        if (times > 0) {
            resource.setLoopCount(times)  // 播放2次
            resource.registerAnimationCallback(object :
                Animatable2Compat.AnimationCallback() {
                override fun onAnimationEnd(drawable: Drawable?) {
                    super.onAnimationEnd(drawable)
                    resource.stop()
                    finish.invoke()
                }

                override fun onAnimationStart(drawable: Drawable?) {
                    super.onAnimationStart(drawable)
                    finish.invoke()
                }
            })
        }
        resource.start()
    }
//    else if (resource is WebpDrawable) {
//        // 播放 WebP 动画
//        resource.start()
//        resource.loopCount=times
//        val a =object : Animatable2Compat.AnimationCallback() {
//            override fun onAnimationEnd(drawable: Drawable?) {
//                resource.stop()
//                imageView.hide()
//                finish.invoke()
//            }
//        }
//        // 在动画播放完成后的监听
//        resource.registerAnimationCallback(a)
//    }
}

/**
 * 光晕闪烁，透明度和大小同时变化
 * */
fun View?.shining() {
    this?.let { view ->
        val duration = 2500L // 动画周期为2.5秒
        val alphaAnimation = ObjectAnimator.ofFloat(view, View.ALPHA, 0.9f, 0.4f, 0.9f) // 不透明度动画
        val scaleAnimationX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1.0f, 0.5f, 1.0f) // 大小动画
        val scaleAnimationY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1.0f, 0.5f, 1.0f) // 大小动画

        alphaAnimation.repeatCount = ObjectAnimator.INFINITE // 设置为无限循环
        scaleAnimationX.repeatCount = ObjectAnimator.INFINITE // 设置为无限循环
        scaleAnimationY.repeatCount = ObjectAnimator.INFINITE // 设置为无限循环

        alphaAnimation.duration = duration
        scaleAnimationX.duration = duration
        scaleAnimationY.duration = duration

        alphaAnimation.interpolator = AccelerateDecelerateInterpolator()
        scaleAnimationX.interpolator = AccelerateDecelerateInterpolator()
        scaleAnimationY.interpolator = AccelerateDecelerateInterpolator()

        alphaAnimation.start()
        scaleAnimationX.start()
        scaleAnimationY.start()
    }
}
package tuorong.com.healthy.utils.message

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.request.target.CustomViewTarget

// 将 dp 转换为 px
fun Context.dpToPx(dp: Float): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()
}

/**
 * *按照比例裁剪显示图片的函数
 * 规则：
 * 长边:短边 比例大于2的时候，按找长边200dp,短边100dp中心裁剪
 * 1:1时 按照150dp
 * 否则按照短边120dp，长边自适应
 */
fun loadImageWithCustomCrop(
    context: Context,
    imageView: ImageView,
    url: String
) {
    Glide.with(context)
        .asBitmap()
        .load(url)
        .into(object : CustomViewTarget<ImageView, Bitmap>(imageView) {
            override fun onLoadFailed(errorDrawable: Drawable?) {
                // 处理加载失败的情况
                imageView.setImageDrawable(errorDrawable)
            }

            override fun onResourceCleared(placeholder: Drawable?) {
                // 处理清理资源的情况
                imageView.setImageDrawable(placeholder)
            }

            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                // 获取图片的宽高比
                val width = resource.width
                val height = resource.height
                val aspectRatio = width.toFloat() / height.toFloat()

                val layoutParams = imageView.layoutParams
//                when {
//                    aspectRatio > 2f -> {
//                        // 宽高比超过2:1，长边 200dp，短边 100dp
//                        layoutParams.width = context.dpToPx(200f)
//                        layoutParams.height = context.dpToPx(100f)
//                    }
//                    aspectRatio < 0.5f -> {
//                        // 高宽比超过2:1，长边 200dp，短边 100dp
//                        layoutParams.width = context.dpToPx(100f)
//                        layoutParams.height = context.dpToPx(200f)
//                    }
//                    else -> {
//                        // 宽高比在 2:1 以内，按比例缩放，短边不小于 100dp
//                        val scaledWidth: Int
//                        val scaledHeight: Int
//                        if (width > height) {
//                            scaledWidth = context.dpToPx(150f)
//                            scaledHeight = (scaledWidth / aspectRatio).toInt()
//                            if (scaledHeight < context.dpToPx(100f)) {
//                                layoutParams.width = (context.dpToPx(100f) * aspectRatio).toInt()
//                                layoutParams.height = context.dpToPx(100f)
//                            } else {
//                                layoutParams.width = scaledWidth
//                                layoutParams.height = scaledHeight
//                            }
//                        } else {
//                            scaledHeight = context.dpToPx(150f)
//                            scaledWidth = (scaledHeight * aspectRatio).toInt()
//                            if (scaledWidth < context.dpToPx(100f)) {
//                                layoutParams.height = (context.dpToPx(100f) / aspectRatio).toInt()
//                                layoutParams.width = context.dpToPx(100f)
//                            } else {
//                                layoutParams.height = scaledHeight
//                                layoutParams.width = scaledWidth
//                            }
//                        }
//                    }
//                }

               //imageView.layoutParams = layoutParams
                imageView.setImageBitmap(resource)
            }
        })
}

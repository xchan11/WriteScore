package tuorong.com.healthy.utils.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

/**
 * 防抖点击
 * */
fun View.setStableClickListener(time:Long=900,onClickListener: View.OnClickListener){
    this.setOnClickListener {
        if(ClickChecker.checkTime(time))
            return@setOnClickListener
        onClickListener.onClick(it)
    }
}

@SuppressLint("ClickableViewAccessibility")
fun View.setupGestureListeners(
    context: Context,
    onDoubleTap: () -> Unit,
    onLongPress: (View) -> Unit
) {
    val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {

        override fun onDoubleTap(e: MotionEvent): Boolean {
            onDoubleTap()
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            onLongPress(this@setupGestureListeners)
        }
    })

    // 设置触摸监听器
    this.setOnTouchListener { _, event ->
        gestureDetector.onTouchEvent(event)
        true
    }
}

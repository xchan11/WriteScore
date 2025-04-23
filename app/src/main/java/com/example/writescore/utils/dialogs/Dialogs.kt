package tuorong.com.healthy.utils.dialogs

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager.LayoutParams
import android.widget.FrameLayout
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDialog
import tuorong.com.healthy.R

fun Activity.createDefaultDialog(
    contentView: View,
    isCancelable: Boolean = true,
    dimAmount: Float = 0.6f,
    @StyleRes
    defaultTheme: Int = R.style.CustomDialog,
    @StyleRes
    windowAnima: Int = R.style.DialogAnimation,
    onTouchEvent: (event: MotionEvent) -> Boolean = { false },
): Dialog {
    val dialog = object : AppCompatDialog(this, defaultTheme) {
        override fun onTouchEvent(event: MotionEvent): Boolean {
            val hook = onTouchEvent(event)
            return if (!hook) {
                super.onTouchEvent(event)
            } else {
                true
            }
        }
    }

    val wrapper = FrameLayout(this)
    val wrapperLayoutParams = FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
    wrapper.layoutParams = wrapperLayoutParams
    wrapper.addView(contentView)
    if(isCancelable){
        wrapper.setOnClickListener{
            dialog.dismiss()
        }
        contentView.setOnClickListener{}
    }
    dialog.apply {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(wrapper)
        setCanceledOnTouchOutside(isCancelable)
        setCancelable(isCancelable)
    }
    dialog.window?.apply {
        if(dimAmount < 0.01f){
            clearFlags(LayoutParams.FLAG_DIM_BEHIND)
            setDimAmount(0.0f)
        }else{
            addFlags(LayoutParams.FLAG_DIM_BEHIND)
            setDimAmount(dimAmount)
        }
        addFlags(LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        setWindowAnimations(windowAnima)
    }
    return dialog
}
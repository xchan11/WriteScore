package tuorong.com.healthy.utils.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.annotations.Until
import tuorong.com.healthy.R

class BottomSheetUtil(private val context: Context) {

    data class BottomSheetButton(
        val label: String,
        val action: () -> Unit
    )

    fun showBottomSheet(
        title: String? = null,
        buttons: List<BottomSheetButton>,
        showCloseButton: Boolean = true
    ) {

        val bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialog)
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_layout, null)
        val buttonContainer = view.findViewById<LinearLayout>(R.id.button_container)
        val tvTitle = view.findViewById<TextView>(R.id.tv_title)
        val dvLine = view.findViewById<View>(R.id.divider_line)

        buttonContainer.removeAllViews()

        //可选标题，不设置则不显示
        if (title != null) {
            tvTitle.text = title
            tvTitle.visibility = View.VISIBLE
        } else {
            tvTitle.visibility = View.GONE
            dvLine.visibility = View.GONE
        }

        //添加按钮
        buttons.forEachIndexed { index, button ->
            val buttonView = Button(context).apply {
                text = button.label
                background = null //设置按钮背景，否则为灰色

                setOnClickListener {
                    button.action.invoke()
                    bottomSheetDialog.dismiss()
                }
            }
            buttonContainer.addView(buttonView)

            //按选项数量，计算分割线数量并添加
            //最后一个选项后面也要添加分割线，因为”关闭“是单独设置的
            if (index < buttons.size) {
                val divider = View(context).apply {
                    layoutParams = LinearLayout.LayoutParams (
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1 // 分割线高度
                    )
                    setBackgroundColor(0xFFB0B0B0.toInt()) //设置分割线颜色
                }
                buttonContainer.addView(divider)
            }
        }

        //添加关闭按钮
        if (showCloseButton) {
            val closeButton = Button(context).apply {
                text = "关闭"
                background = null //设置按钮背景，否则为灰色

                setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
            }
            buttonContainer.addView(closeButton)
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }
}


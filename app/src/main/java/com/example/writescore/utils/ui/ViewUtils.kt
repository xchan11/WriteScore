package tuorong.com.healthy.utils.ui

import androidx.recyclerview.widget.RecyclerView

fun RecyclerView?.setOnHeadAttach(onAttach:()->Unit){
    this?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            // 检测是否滑动到顶部
            if (!recyclerView.canScrollVertically(-1)) {
                // 触发懒加载
                onAttach.invoke()
            }
        }
    })
}

fun createFootAttach(onAttach:()->Unit):RecyclerView.OnScrollListener{
    val onListener = object :RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            // 检测是否滑动到底部
            if (!recyclerView.canScrollVertically(1)) {
                // 触发懒加载
                onAttach.invoke()
            }
        }
    }
    return onListener
}


package tuorong.com.healthy.utils

import android.content.Context
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tuorong.com.healthy.MyApplication.apiService
import tuorong.com.healthy.model.RequestType
import tuorong.com.healthy.model.message.IMMessage
import tuorong.com.healthy.utils.cookie_tool.JSONBodyBuilder
import tuorong.com.healthy.utils.cookie_tool.JSONBodyBuilder.addParams
import tuorong.com.healthy.utils.cookie_tool.JSONBodyBuilder.submit
object HandleBookMark {
    fun handleBookMark(context: Context, item: IMMessage) {
        val expertUserId = item.fromUserId
        val requestBody = JSONBodyBuilder.build().apply {
            addParams("fromUserId", expertUserId)
            addParams("messageIdList", listOf(item.id))
        }.submit()
        apiService.getMessageCollection(requestBody).enqueue(object : Callback<RequestType<*>> {
            override fun onResponse(
                call: Call<RequestType<*>>,
                response: Response<RequestType<*>>
            ) {
                if (response.isSuccessful) {
                    val collectionResponse = response.body()
                    collectionResponse?.let {
                        "已收藏该消息".toastCover()
                    }
                } else {
                    "收藏失败，请重试！".toastCover()
                }
            }

            override fun onFailure(call: Call<RequestType<*>>, t: Throwable) {
                "网络错误".toastCover()
            }
        })
    }
}

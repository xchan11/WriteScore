package tuorong.com.healthy.utils

import android.content.Context
import tuorong.com.healthy.api.RetrofitRequest
import tuorong.com.healthy.ui.LoginActivity
import tuorong.com.healthy.utils.cookie_tool.AppCookieJar


class LoginUtil {
    companion object {
        @JvmStatic
        fun logoutUser(context: Context) {
            // 调用登出接口，观察回调
            LoginInfoManager.removeAll()
            val cookieJar =
                RetrofitRequest.getInstance().okHttpClient.cookieJar as AppCookieJar
            cookieJar.clear()
            LoginActivity.startActivity(context, "ReLogin App")
            AppManager.getInstance().removeAll() // 结束所管理的所有Activity
        }
    }
}
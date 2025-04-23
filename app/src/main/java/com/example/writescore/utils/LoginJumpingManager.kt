package tuorong.com.healthy.utils

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import com.google.gson.internal.LinkedTreeMap
import tuorong.com.healthy.BuildConfig
import tuorong.com.healthy.MyApplication
import tuorong.com.healthy.MyApplication.apiService
import tuorong.com.healthy.MyApplication.currentAty
import tuorong.com.healthy.api.socket.WebSocketManager
import tuorong.com.healthy.model.LoginInfo
import tuorong.com.healthy.model.RequestType
import tuorong.com.healthy.ui.expertUi.acitvity.ExpertMainActivity
import tuorong.com.healthy.ui.serviceUi.acitvity.ServiceMainActivity
import tuorong.com.healthy.ui.userUi.activity.UserMainActivity

object LoginJumpingManager {
    private var loginInfo: LoginInfo? = null // 账号密码登录跳转用的
    private var roles: String = "" // 短信登录跳转用的

    const val ROLE_CONSUMER = "consumer"//普通客户
    const val ROLE_EXPERT = "expert"//专家
    const val ROLE_AGENT = "agent"//客服
    const val ROLE_ADMIN = "admin"//客服
    const val ROLE_PROVIDER = "provider"//客服
    fun rolesJumping(
        loginInfo: LoginInfo? = null,
        role: String? = null,
        context: FragmentActivity? = null,
        callback: (() -> Unit)? = null
    ) {
        MyApplication.degree = loginInfo?.degree?:0
        this.loginInfo = loginInfo
        val cxt = context ?: currentAty
        roles = loginInfo?.role ?: role ?: ""
        when (roles) {
            ROLE_CONSUMER -> {
                UserMainActivity.startActivity(cxt)
                getDetail(cxt, callback) { apiService.userDetail }
            }

            ROLE_EXPERT -> {
                ExpertMainActivity.startActivity(cxt)
                getDetail(cxt, callback) { apiService.expertDetail }
            }

            ROLE_AGENT -> {
                ServiceMainActivity.startActivity(cxt)
                getDetail(cxt, callback) { apiService.agentDetail }
            }

            else -> {
                callback?.invoke()
                "用户不存在或密码错误".toastCover()
                return
            }
        }
    }

    private fun getDetail(context: FragmentActivity?, callback: (() -> Unit)? = null, func: () -> LiveData<RequestType<*>>) {
        func.invoke().observe(context ?: currentAty) {
            if (it.isOk()) {
                loginInfo = LoginInfo(
                    loginInfo?.id ?: "",
                    loginInfo?.role ?: roles,
                    loginInfo?.degree?:0,
                    RoleFactory.createRole(it.data as LinkedTreeMap<*, *>)
                )
                LoginInfoManager.updateLoginInfo(loginInfo!!)
                LoginInfoManager.loginStatus.postValue(true)//登录状态同步
                WebSocketManager.startConnection()//开启socket连接

                callback?.invoke()
            } else {
                    it?.message?.toastCover()
                }
        }
    }
}
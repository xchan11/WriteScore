package tuorong.com.healthy.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import tuorong.com.healthy.MyApplication.apiService
import tuorong.com.healthy.MyApplication.currentAty
import tuorong.com.healthy.model.LoginInfo
import tuorong.com.healthy.ui.LoginActivity
import tuorong.com.healthy.utils.LoginJumpingManager.ROLE_AGENT
import tuorong.com.healthy.utils.LoginJumpingManager.ROLE_CONSUMER
import tuorong.com.healthy.utils.LoginJumpingManager.ROLE_EXPERT
import tuorong.com.healthy.utils.cookie_tool.JSONBodyBuilder
import tuorong.com.healthy.utils.cookie_tool.JSONBodyBuilder.addParams
import tuorong.com.healthy.utils.cookie_tool.JSONBodyBuilder.submit

class LoginInfoManager {
    companion object {
        var loginStatus = MutableLiveData<Boolean>()

        private var loginInfoLiveData = MutableLiveData<LoginInfo>()

        private var spForGetLoginInfo: SharedPreferences? = null
        private var editorSetLoginInfo: SharedPreferences.Editor? = null

        private var loginInfoKey = "GET_INFO"


        var loginInfo: LoginInfo? = null
            get() {
                if (field == null) {
                    field = getLoginInfoByEditor()
                }
                if (field == null) {
                    removeAll()
                    LoginActivity.startActivity(currentAty, "LoginInfoManager")
                }
                return field
            }

        fun updateLoginInfo(loginInfo: LoginInfo?) {
            if (loginInfo != null) {
                loginInfoLiveData.postValue(loginInfo)
                this.loginInfo = loginInfo
                setLoginInfoBySp(loginInfo)
            }
        }

        private fun setLoginInfoBySp(loginInfo: LoginInfo) {
            val json = Gson().toJson(loginInfo)
            if (editorSetLoginInfo == null) {
                init(currentAty)
            }
            editorSetLoginInfo?.putString(loginInfoKey, json)?.apply()
        }

        private fun getLoginInfoByEditor(): LoginInfo? {
            if (spForGetLoginInfo == null) {
                init(currentAty)
            }
            if (spForGetLoginInfo!!.contains(loginInfoKey)) {
                val json = spForGetLoginInfo!!.getString(loginInfoKey, "")
                return Gson().fromJson(json, LoginInfo::class.java)
            }
            return null
        }

        fun observeLoginInfo(lifecycle: LifecycleOwner, onChange: (LoginInfo) -> Unit) {
            loginInfoLiveData.observe(lifecycle) {
                if (it == null || it.role == "") {
                    val loginInfo = getLoginInfoByEditor()
                    loginInfo?.let { updateLoginInfo(loginInfo) }
                }
                if (it == null || it.role == "") {
                    removeAll()
                    LoginActivity.startActivity(currentAty, "Login status expired")
                } else {
                    onChange.invoke(it)
                }
            }
        }

        fun init(context: Context) {
            if (spForGetLoginInfo == null) {
                spForGetLoginInfo =
                    context.getSharedPreferences("GET_LOGIN_INFO", Context.MODE_PRIVATE)
            }
            if (editorSetLoginInfo == null) {
                editorSetLoginInfo = spForGetLoginInfo?.edit()
            }
        }

        fun removeAll() {
            editorSetLoginInfo?.clear()?.apply()
        }

        fun getCurrentUid(): String {
            return loginInfo?.id ?: ""
        }

        fun getCurrentAvatar(): String {
            return loginInfoLiveData.value?.userInfo?.avatar ?: ""
        }

        fun isAgent(): Boolean {
            return this.loginInfo?.role == ROLE_AGENT
        }

        fun isConsumer(): Boolean {
            return this.loginInfo?.role == ROLE_CONSUMER
        }

        fun isExpert(): Boolean {
            return this.loginInfo?.role == ROLE_EXPERT
        }

        fun editInfo(
            fieldName: String,
            lifecycle: LifecycleOwner,
            callback: (message: String?) -> Unit = {}
        ) {
            val fieldValue = getEntryValue(fieldName)
            val jsonObject = JSONBodyBuilder.build().apply {
                addParams("id", getCurrentUid())
                addParams(fieldName, fieldValue)
            }.submit()
            apiService.updateUserInfo(jsonObject).observe(lifecycle) {
                if(it.isOk()) {
                    callback.invoke(it?.message)
                    return@observe
                }
                else{
                    it?.message?.toastCover()
                }
            }
        }

        private fun getEntryValue(fieldName: String): Any? {
            loginInfo ?: return null
            val userInfo = loginInfo!!.userInfo
            return when(fieldName){
                "age" -> userInfo.age
                "sex" -> userInfo.sex
                "email" -> userInfo.email
                "avatar" -> userInfo.avatar
                "name" -> userInfo.name
                "nickName" -> userInfo.nickName
                "trueName" -> userInfo.trueName
                "birthday" -> userInfo.birthday
                "educational" -> userInfo.educational
                "expertName" -> userInfo.expertName
                "job" -> userInfo.job
                "profile" -> userInfo.profile
                else -> null
            }
        }
    }
}
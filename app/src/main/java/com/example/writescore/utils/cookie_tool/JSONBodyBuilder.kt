package tuorong.com.healthy.utils.cookie_tool

import com.google.gson.GsonBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import tuorong.com.healthy.MyApplication.currentAty
import tuorong.com.healthy.R
import tuorong.com.healthy.model.RequestType
import tuorong.com.healthy.ui.LoginActivity
import tuorong.com.healthy.utils.file.SharePreferenceUtil
import tuorong.com.healthy.utils.toastCover
import java.lang.Exception


object JSONBodyBuilder {
    fun build(): JSONObject {
        return JSONObject()
    }

    fun JSONObject.addParams(key: String, value: Any?): JSONObject {
        if (value is List<*>) {
            val jsonArray = JSONArray()
            value.forEach {
                if(it.isBaseDataStruct())//还没加全 需要者自加
                    jsonArray.put(it)
                else
                    jsonArray.put(it.toJson().toJSONObject())
            }
            this.put(key, jsonArray)
        } else {
            this.put(key, value)
        }
        return this
    }

    fun JSONObject.submit(): RequestBody {
        val requestBody =
            RequestBody.create("application/json".toMediaTypeOrNull(), this.toString())
        return requestBody
    }

    fun easyPager():RequestBody{
        return build()
            .addParams("pageIndex",1)
            .addParams("pageSize",9999)
            .submit()
    }

}

fun createJsonBody(maps: Map<String, Any>): RequestBody {

    val jsonObject = JSONBodyBuilder.build()
    maps.keys.forEach {
        jsonObject.put(it, maps[it])
    }
    val requestBody =
        RequestBody.create("application/json".toMediaTypeOrNull(), jsonObject.toString())
    return requestBody

}
fun createJsonBody(pair: Pair<String, Any>): RequestBody {
    val jsonObject = JSONBodyBuilder.build()
    val maps = mapOf(pair)
    maps.keys.forEach {
        jsonObject.put(it, maps[it])
    }
    val requestBody =
        RequestBody.create("application/json".toMediaTypeOrNull(), jsonObject.toString())
    return requestBody

}

fun <T> T.toJson(): String {
    val gson = GsonBuilder().setPrettyPrinting().create()
    return gson.toJson(this)
}

fun String?.couldBeJson(): Boolean {
    return if (this.isNullOrEmpty()) false
    else {
        try {
            val obj = JSONObject(this)
            obj.length() != 0
        } catch (e: Exception) {
            false
        }
    }
}

fun String?.toJSONObject(): JSONObject? {
    return if (this.isNullOrEmpty()) null
    else {
        try {
            val obj = JSONObject(this)
            return obj
        } catch (e: Exception) {
            null
        }
    }
}
fun String?.toJSONArray(): JSONArray? {
    return if (this.isNullOrEmpty()) null
    else {
        try {
            val array = JSONArray(this)
            return array
        } catch (e: Exception) {
            null
        }
    }
}

/**
 * @param allowNull 是否允许空数据
 * @param toastForce 是否强制在此方法内完成toast
 * */
fun <T> Call<RequestType<T>>.sendRequest(success:(T?)->Unit, fail:(String)->Unit, allowNull:Boolean=false, toastForce:Boolean = false){
    this.enqueue(object : retrofit2.Callback<RequestType<T>>{
        override fun onResponse(call: Call<RequestType<T>>, response: Response<RequestType<T>>) {
            val responseCode = response.body()?.code
            val message = response.body()?.message?:"未知错误"
            if(toastForce)
                message.toastCover()
            if (responseCode == 40301) {
                fail.invoke("操作频繁,请稍后重试")
            } else if (responseCode == 40100) {
                SharePreferenceUtil.verifiedLogin(false, "")
                if (currentAty != null){
                    LoginActivity.startActivity(currentAty, currentAty.getString(R.string.CANT_LOGIN))
                    currentAty.finish()
                }
            } else if(responseCode != 0){
                 fail.invoke(message)
            } else {
                response.body()?.data?.let{
                    success.invoke(it)
                }
                if(response.body()?.data==null){
                    if(allowNull)
                        success.invoke(null)
                    else
                       fail.invoke("数据为空")
                }
            }
        }

        override fun onFailure(call: Call<RequestType<T>>, t: Throwable?) {
            val msg = t?.message.errorTransfer()
            fail.invoke(msg)
            if(toastForce)
                msg.toastCover()
        }

    })
}

/**
 * 错误信息转换
 * */
fun String?.errorTransfer():String{
    if(this==null)
        return "未知错误！"
    else if(this.contains("Unable to resolve host")||this.contains("No address associated with hostname"))
        return "网络连接失败！"
    else if(this.contains("Null Pointer"))
        return "异常Null Pointer"
    else return this
}
fun Any?.isBaseDataStruct():Boolean{
    return (this==null||this is String||this is Int||this is Boolean||this is Long||this is Float)
}
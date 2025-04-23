package tuorong.com.healthy.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.lang.ref.WeakReference

/**订阅、通知，自动解绑*/
class LiveSubscriber {
    companion object {
        /**
         * 映射 key:String -> value:LiveData
         * */
        private var liveDataMap: MutableMap<String, MutableLiveData<Any?>> = mutableMapOf()

        fun LifecycleOwner.subScribeMsg(tag: String, change: (Any?) -> Unit) {
            var liveData =
                if (getMapLiveDatas()[tag] != null)
                     getMapLiveDatas()[tag]!! //取出已有的livedata
                else
                    MutableLiveData<Any?>()

            val observer = Observer<Any?> { value ->
                change(value)
            }

            liveData.observe(this@subScribeMsg, observer)

            getMapLiveDatas()[tag] = liveData
        }


        private fun getMapLiveDatas(): MutableMap<String, MutableLiveData<Any?>> {
            if (liveDataMap.isNullOrEmpty())
                liveDataMap = mutableMapOf()
            return liveDataMap
        }

        fun post(tag: String, value: Any) {
//            "${getMapLiveDatas()[tag]==null}".toastDebug()
            getMapLiveDatas()[tag]?.postValue(value)
        }
        /**
         * 同步覆盖，为了迅速覆盖缓存
         * */
        fun setValue(tag: String, value: Any){
            getMapLiveDatas()[tag]?.value = value
        }

        fun clear() {
            for ((key, liveDataRef) in getMapLiveDatas()) {
                liveDataRef.postValue(null)
                getMapLiveDatas().remove(key)
            }
        }
        /**
         * 根据key删除指定LiveData
         * */
        fun clear(tag: String) {
            getMapLiveDatas().remove(tag)
        }
    }
}

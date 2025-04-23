package tuorong.com.healthy.utils

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import tuorong.com.healthy.MyApplication.appContext
import kotlin.coroutines.CoroutineContext

class GlobalCoroutineExceptionHandler: CoroutineExceptionHandler {
    override val key: CoroutineContext.Key<*>
        get() = CoroutineExceptionHandler

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        Log.d(context.toString(), "handleException: $exception")
        context to appContext
    }
}
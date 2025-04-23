package tuorong.com.healthy.utils.permission

import androidx.annotation.MainThread
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@MainThread
suspend fun FragmentActivity.permissionsRequestSimplifySuspend(vararg permissions: String): Boolean {
    return suspendCancellableCoroutine { cont ->
        this.permissionsRequestSimplify(
            permissions = permissions,
            error = {
                if (cont.isActive && !(cont.isCancelled || cont.isCompleted)) {
                    cont.resumeWithException(Throwable(it))
                }
            },
            callback = {
                if (cont.isActive && !(cont.isCancelled || cont.isCompleted)) {
                    cont.resume(it)
                }
            })
    }
}
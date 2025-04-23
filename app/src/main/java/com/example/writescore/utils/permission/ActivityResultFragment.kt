package tuorong.com.healthy.utils.permission

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import tuorong.com.healthy.utils.LogUtils
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.random.Random

@Suppress("DEPRECATION")
internal class ActivityResultFragment : Fragment {

    private val callback: ((resultCode: Int, resultData: Intent?) -> Unit)?
    private val error: ((msg: String) -> Unit)?
    private val targetActivityIntent: Intent?

    private val hasInvokeCallback: AtomicBoolean = AtomicBoolean(false)

    private var lastRequestCode: Int? = null

    constructor() {
        this.callback = null
        this.error = null
        this.targetActivityIntent = null
    }

    constructor(
        targetActivityIntent: Intent,
        error: (msg: String) -> Unit,
        callback: (resultCode: Int, resultData: Intent?) -> Unit
    ) {
        this.targetActivityIntent = targetActivityIntent
        this.error = error
        this.callback = callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = activity
        val targetActivityIntent = this.targetActivityIntent
        if (targetActivityIntent == null) {
            if (hasInvokeCallback.compareAndSet(false, true)) {
                error?.invoke("目标Activity的intent为空")
            }
            finishCurrentFragment()
            return
        }

        if (context == null) {
            LogUtils.e(TAG, "附着的activity为空")
            if (hasInvokeCallback.compareAndSet(false, true)) {
                error?.invoke("附着的activity为空")
            }
            finishCurrentFragment()
            return
        }

        val requestCode = Random(System.currentTimeMillis()).nextInt(0, 65535)
        lastRequestCode = requestCode
        try {
            startActivityForResult(targetActivityIntent, requestCode)
        } catch (e: Throwable) {
            if (hasInvokeCallback.compareAndSet(false, true)) {
                error?.invoke("StartActivityResultError: ${e.message}")
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == lastRequestCode) {
            finishCurrentFragment()
            if (hasInvokeCallback.compareAndSet(false, true))
                callback?.invoke(resultCode, data)
        }
    }

    private fun finishCurrentFragment() {
        val tc = parentFragmentManager.beginTransaction()
        tc.remove(this)
        tc.commitAllowingStateLoss()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (hasInvokeCallback.compareAndSet(false, true)) {
            error?.invoke("Fragment exit unexpectedly.")
        }
    }
}

private const val TAG = "ActivityResultFragment"
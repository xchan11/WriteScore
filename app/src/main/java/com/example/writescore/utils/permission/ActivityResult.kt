package tuorong.com.healthy.utils.permission

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import tuorong.com.healthy.utils.toastCover

fun FragmentActivity.startActivityResult(
    targetActivityIntent: Intent,
    error:(msg: String)-> Unit = {it.toastCover()},
    callback:(resultCode: Int, resultData: Intent?) -> Unit
){
    val fragment = ActivityResultFragment(
        targetActivityIntent = targetActivityIntent,
        error = error,
        callback = callback
    )

    val tc = supportFragmentManager.beginTransaction()
    tc.add(fragment, "ActivityResultFragment#${System.currentTimeMillis()}")
    tc.commitAllowingStateLoss()
}

fun Fragment.startActivityResult(
    targetActivityIntent: Intent,
    error: (msg: String) -> Unit,
    callback: (resultCode: Int, resultData: Intent?) -> Unit
){
    val act = this.activity
    assert(act != null) { "Fragment's parent activity is null." }
    act!!.startActivityResult(targetActivityIntent, error, callback)
}
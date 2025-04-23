package tuorong.com.healthy.utils.permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import tuorong.com.healthy.utils.LogUtils
import tuorong.com.healthy.utils.dgToast

fun FragmentActivity.permissionsRequestSimplify(
    vararg permissions: String,
    error: (msg: String) -> Unit = { it.dgToast() },
    callback: (grant: Boolean) -> Unit
){
    permissionsRequest(permissions = permissions, error = error) { _, notGranted ->
        callback(notGranted.isEmpty())
    }
}

fun Fragment.permissionsRequest(vararg permissions: String, error: (msg: String) -> Unit, callback: (granted: Set<String>, notGranted: Set<String>) -> Unit) {
    val act = this.activity
    assert(act != null) { "Fragment's parent activity is null." }
    act!!.permissionsRequest(permissions = permissions, error = error, callback = callback)
}

fun FragmentActivity.permissionsRequest(
    vararg permissions: String,
    error: (msg: String) -> Unit,
    callback: (granted: Set<String>, notGranted: Set<String>) -> Unit
) {
    assertMainThread { "permissionsRequest() 必须运行在主线程" }
    if (permissions.isEmpty()) {
        LogUtils.w(msg = "需要请求权限为空")
        callback(emptySet(), emptySet())
        return
    }

    val permissionsSet = permissions.toSet()
    val needRequestSet = permissionsSet.filter { !permissionCheck(it) }.toSet()
    val notNeedRequest = permissionsSet - needRequestSet

    if (needRequestSet.isEmpty()) {
        callback(permissionsSet, emptySet())
        LogUtils.d(msg = "所有请求的权限都已经被允许,无需重复请求: $permissionsSet")
        return
    }

    val intent = Intent(ACTION_REQUEST_PERMISSIONS).putExtra(
        EXTRA_PERMISSIONS,
        needRequestSet.toTypedArray()
    )

    startActivityResult(
        targetActivityIntent = intent,
        error = error,
        callback = { resultCode, resultData ->
            if (resultCode == Activity.RESULT_OK && resultData != null) {
                val resultDataPermissions = resultData.getStringArrayExtra(EXTRA_PERMISSIONS)
                val resultDataGrantResult =
                    resultData.getIntArrayExtra(EXTRA_PERMISSION_GRANT_RESULTS)
                if (resultDataPermissions == null || resultDataGrantResult == null) {
                    error("Unknown error.")
                } else {
                    val grantState =
                        resultDataGrantResult.map { it == PackageManager.PERMISSION_GRANTED }
                    val permissionsAndGrantState =
                        resultDataPermissions.filterNotNull().zip(grantState).toMap()
                    val granted = permissionsAndGrantState.filter { it.value }.map { it.key }
                        .toSet() + notNeedRequest
                    val deny = permissionsAndGrantState.filter { !it.value }.map { it.key }.toSet()
                    callback(granted, deny)
                }
            } else {
                error("Unknown error: $resultCode")
            }
        }
    )
}

fun Context.permissionCheck(permission: String): Boolean{
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}


private const val ACTION_REQUEST_PERMISSIONS =
    "androidx.activity.result.contract.action.REQUEST_PERMISSIONS"

private const val EXTRA_PERMISSIONS =
    "androidx.activity.result.contract.extra.PERMISSIONS"

private const val EXTRA_PERMISSION_GRANT_RESULTS =
    "androidx.activity.result.contract.extra.PERMISSION_GRANT_RESULTS"

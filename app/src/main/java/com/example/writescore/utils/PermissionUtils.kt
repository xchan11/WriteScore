package tuorong.com.healthy.utils

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.request.ExplainScope
import com.permissionx.guolindev.request.ForwardScope
import tuorong.com.healthy.utils.ui.ToastUtil


object PermissionUtils {
    //封装权限单请工具
    //方案一：PermissionX
    fun requestPermission(
        activity: FragmentActivity,
        permission: String,
        onGet: () -> Unit
    ) {//todo 把这个方法和下面那个合并
        PermissionX.init(activity)
//                 .permissions(*activity.resources.getStringArray(R.array.permissionsArray))
            .permissions(permission)
            .onExplainRequestReason { scope: ExplainScope, deniedList: List<String?>?, _: Boolean ->
                try {
                    scope.showRequestReasonDialog(
                        deniedList as List<String>,
                        "即将申请的权限是程序必须依赖的权限",
                        "我已明白"
                    )
                } catch (e: Exception) {
                }
            }
            .onForwardToSettings { scope: ForwardScope, deniedList: List<String?>? ->
                try {
                    scope.showForwardToSettingsDialog(
                        deniedList as List<String>,
                        "您需要去应用程序设置当中手动开启权限",
                        "我已明白"
                    )
                } catch (e: Exception) {
                }
            }
            .request { allGranted: Boolean, grantedList: List<String?>?, deniedList: List<String?> ->
                if (allGranted) {
                    onGet.invoke()
                } else {
                    ToastUtil.ToastMsg(activity, "您拒绝了如下权限：$deniedList")
                }
            }
    }


    //方案二：原生 需要override onRequestPermissionsResult,获取相机时有问题
    var onPermitted: () -> Unit = {}// 把获取到之后要做的操作保存起来
    fun requestStoragePermission(
        activity: FragmentActivity,
        permissionName: String,
        getPermission: () -> Unit = {}
    ) {
        // 检查是否已经授予读取相册的权限
        if (ContextCompat.checkSelfPermission(activity, permissionName)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // 如果没有权限，请求权限
            requestStoragePermission(activity)
            onPermitted = getPermission
        } else {
            getPermission.invoke()
        }
    }

    val REQUEST_PERMISSION_CODE = 100
    private fun requestStoragePermission(activity: FragmentActivity) {
        // 请求读取相册的权限
        ActivityCompat.requestPermissions(
            activity, arrayOf<String>(READ_EXTERNAL_STORAGE),
            REQUEST_PERMISSION_CODE
        )
    }


}
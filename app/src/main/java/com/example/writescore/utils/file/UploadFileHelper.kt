package tuorong.com.healthy.utils.file

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.qiniu.android.http.ResponseInfo
import com.qiniu.android.storage.Configuration
import com.qiniu.android.storage.UploadManager
import com.qiniu.android.storage.UploadOptions
import org.json.JSONObject
import tuorong.com.healthy.utils.permission.permissionsRequestSimplify
import tuorong.com.healthy.Config
import tuorong.com.healthy.MyApplication.apiService
import tuorong.com.healthy.R
import tuorong.com.healthy.utils.toast
import tuorong.com.healthy.utils.toastCover
import java.io.File
import java.util.Date

object UploadFileHelper {
    //七牛云请求数据
    private var isUploading = false
    private var mActivity: FragmentActivity? = null
    val loadProgressMutableLiveData: MutableLiveData<Double> = MutableLiveData()

    // 初始化、执行上传
    private var isCancelled = false

    private var UploadTaskListener: UploadTaskListener? = null


    private var uploadManager: UploadManager? = null
        get() {
            if (field == null)
                synchronized(UploadFileHelper::class.java) {
                    val config = Configuration.Builder()
                        .connectTimeout(90)       // 链接超时。默认90秒
                        .useHttps(false)                  // 是否使用https上传域名
                        .useConcurrentResumeUpload(true) // 使用并发上传，使用并发上传时，除最后一块大小不定外，其余每个块大小固定为4M，
                        .resumeUploadVersion(Configuration.RESUME_UPLOAD_VERSION_V2) // 使用新版分片上传
                        .concurrentTaskCount(3)          // 并发上传线程数量为3
                        .responseTimeout(90)     // 服务器响应超时。默认90秒
                        .build()
                    // 重用uploadManager。一般地，只需要创建一个uploadManager对象
                    field = UploadManager(config)
                }
            return field
        }

    private var finish: (String, String, Boolean) -> Unit = { _, _, _ -> }

    fun selectFile(
        activity: FragmentActivity,
        finish: (String, String, Boolean) -> Unit
    ) {
        mActivity = activity
        this.finish = finish
        activity.permissionsRequestSimplify(Manifest.permission.READ_EXTERNAL_STORAGE) {
            if(it){
                openFileSelector(activity)
            }else
                activity.getString(R.string.STORAGE_PERMISSION).toast()
        }
    }

    // 打开文件选择器
    private fun openFileSelector(activity: FragmentActivity) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        activity.startActivityForResult(intent, REQUEST_CODE_SELECT_FILE)
    }


    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_SELECT_FILE && resultCode == FragmentActivity.RESULT_OK) {
            data?.data?.let { uri ->
                val file = FileUtil.getFileFromUri(mActivity, uri)
                val fileName = FileUtil.getFileName(mActivity, uri)
                if (!fileName.endsWith(".pdf")) {
                    "文件仅限PDF格式".toastCover()
                    return
                }
                file?.let {
                    mActivity?.let { activity ->
                        UploadTaskListener?.onChange(fileName)
                        startUploadFile(activity, file, fileName, finish)
                    }
                }
            }
        }
    }

    /**
     * 上传文件
     * @param activity
     * @param file 文件
     * @param finish 监听回调
     */
    @SuppressLint("SimpleDateFormat")
    fun startUploadFile(
        activity: FragmentActivity,
        file: File?,
        fileName: String,
        finish: (String, String, Boolean) -> Unit
    ) {
        if (!isUploading) {
            isUploading = true
        } else {
            "当前正在执行上传任务".toastCover()
            finish.invoke("", fileName, false)
            return
        }
        apiService.upToken.observe(activity) { requestType ->
            if (requestType != null && requestType.code == 0) { //获取token成功
                val token = requestType.data.toString()
                file?.let {
                    val simpleDateFormat: DateFormat = SimpleDateFormat("yyyyMMddHHmmss")
                    val key = "medical_report/" +
                            simpleDateFormat.format(Date()) + file.name.substring(
                        file.name.lastIndexOf(
                            "."
                        )
                    )
                    uploadManager?.put(
                        file,
                        key,
                        token,
                        { _: String?, info: ResponseInfo, res: JSONObject? ->
                            if (info.isOK && res != null) {
                                try {
                                    val url =
                                        "http://${Config.getQiNiuFile_URL}${res.getString("key")}"
                                    finish.invoke(url, fileName, true)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    finish.invoke("", fileName, false)
                                }
                            } else {
                                //如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
                                finish.invoke("", fileName, false)
                            }
                            isUploading = false
                        },
                        uploadOptions
                    )
                }
            } else {
                finish.invoke("", fileName, false)
                isUploading = false
            }
        }
    }

    private var uploadOptions = UploadOptions(
        null,
        "application/octet-stream", // 设置合适的 MIME 类型
        false,
        { key, percent ->
            loadProgressMutableLiveData.postValue(percent)
        },
        { isCancelled }
    )

    // 点击取消按钮，让 UpCancellationSignal##isCancelled() 方法返回 true，以停止上传
    fun cancelUpload() {
        isCancelled = true
    }

    private const val REQUEST_CODE_SELECT_FILE = 2
    fun setUploadTaskListener(listener: UploadTaskListener) {
        this.UploadTaskListener = listener
    }
}

interface UploadTaskListener {
    fun onChange(fileName: String)
}
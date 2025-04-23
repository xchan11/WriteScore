package tuorong.com.healthy.utils.file

import android.content.Context
import android.graphics.Bitmap
import android.icu.text.DateFormat
import android.os.Environment
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.qiniu.android.http.ResponseInfo
import com.qiniu.android.storage.Configuration
import com.qiniu.android.storage.UploadManager
import org.json.JSONObject
import tuorong.com.healthy.Config
import tuorong.com.healthy.MyApplication.apiService
import tuorong.com.healthy.MyApplication.currentAty
import tuorong.com.healthy.utils.file.FileUtil.bitmapToByteArray
import tuorong.com.healthy.utils.toastCover
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


/**
 * 上传图片所用的工具类
 * 2021-12-6 lam
 * 两个抽象方法都传回了图片在七牛云中的地址
 * 如果需要上传到java服务器,需要在第一个抽象方法体里调用UploadtoServer
 */
class UploadPictureUtil(var mcontext: Context, var token: String) {

    companion object {
        /*压缩图片*/
        fun compressImage(bitmap: Bitmap): File {
            val baos = ByteArrayOutputStream()
            //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            var options = 100
            //循环判断如果压缩后图片是否大于500kb,大于继续压缩
            while (baos.toByteArray().size / 1024 > 500) {
                baos.reset() //重置baos即清空baos
                options -= 10 //每次都减少10
                //这里压缩options%，把压缩后的数据存放到baos中
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos)
                val length = baos.toByteArray().size.toLong()
            }
            val format = SimpleDateFormat("yyyyMMddHHmmss")
            val date = Date(System.currentTimeMillis())
            //图片名
            val filename = format.format(date)
            val file = File(Environment.getExternalStorageDirectory(), "$filename.png")
            try {
                val fos = FileOutputStream(file)
                try {
                    fos.write(baos.toByteArray())
                    fos.flush()
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            Log.d("compress", "compressImg $file")
            return file
        }

        @Volatile
        private var qiNiuUploadManager: UploadManager? = null
        private val mangerInstance: UploadManager?
            get() {
                if (qiNiuUploadManager == null) {
                    synchronized(UploadPictureUtil::class.java) {
                        if (qiNiuUploadManager == null) {
                            val config = Configuration.Builder()
                                .useHttps(true)
                                .build()
                            // 重用uploadManager。一般地，只需要创建一个uploadManager对象
                            qiNiuUploadManager = UploadManager(config)
                        }
                    }
                }
                return qiNiuUploadManager
            }
        /**
         * 选取媒体文件
         * @param num 文件数（图片）
         * @param type 1图片 2图片或视频
         * @param finish 回调闭包 参数(图片数组下标 -1为失败; 文件类型; 地址; 是否成功)
         * 其中 文件类型 ：-1为失败 0未知 1图片 2视频 3音频 4 pdf
         * */
        @JvmStatic
        fun selectPhotoAndPost(num:Int, type:Int, activity:FragmentActivity, finish:(List<File>?)->Unit){

            PictureSelector.create(activity)
                .openGallery(type)//指定上传文件类型
                .setImageEngine(GlideEngine.createGlideEngine())//设置加载引擎为Glide
                .setMaxSelectNum(num)//最大文件数为num
                .setSelectMaxDurationSecond(4*60)//小于x分钟
                .setSelectMaxFileSize(1024*80)//限制文件小于y
                .setMinSelectNum(0)
//            .setMaxVideoSelectNum(3)
                .forResult(object : OnResultCallbackListener<LocalMedia?> {
                    override fun onResult(result: ArrayList<LocalMedia?>) {
                        if (result.size > 0) {
                            var files = result.mapNotNull { convertToLocalFile(it) }
                            finish.invoke(files)
                        }
                    }
                    override fun onCancel() {
                        finish.invoke(null)
                    }
                })
        }
        @JvmStatic
        fun selectSinglePhoto(activity: FragmentActivity, finish: (String, Boolean) -> Unit) {
            selectPhotoUpload(1, 1, activity){ _, _, url,_, success->
                finish.invoke(url,success)
            }
        }
        /**
         * 选取媒体文件
         * @param num 文件数（图片）
         * @param type 1图片 2图片或视频
         * @param finish 回调闭包 参数(图片数组下标 -1为失败; 文件类型; 地址; 是否成功)
         * 其中 文件类型 ：-1为失败 0未知 1图片 2视频 3音频 4 pdf
         * */
        @JvmStatic
        fun selectPhotoUpload(num:Int, type:Int, activity:FragmentActivity,pathName: String="avatar",
                              finish:(index:Int, type:Int, url:String,fileName: String, success:Boolean)->Unit){

            PictureSelector.create(activity)
                .openGallery(type)//指定上传文件类型
                .setImageEngine(GlideEngine.createGlideEngine())//设置加载引擎为Glide
                .setMaxSelectNum(num)//最大文件数为4
                .setSelectMaxDurationSecond(4*60)//小于x分钟
                .setSelectMaxFileSize(1024*80)//限制文件小于y
                .setMinSelectNum(0)
//            .setMaxVideoSelectNum(3)
                .forResult(object : OnResultCallbackListener<LocalMedia?> {
                    override fun onResult(result: ArrayList<LocalMedia?>) {
                        if (result.size > 0) {
                            val files = result.map { convertToLocalFile(it) }
                            if(files.isEmpty()){
                                finish.invoke(-1,-1,"","",false)
                                return
                            }
                            startUpload(activity, files,pathName) { index: Int, url: String, success: Boolean ->
                                if(index>=0) {
                                    val fileName = files[index]?.name
                                    finish.invoke(index, files[index].getType(), url, fileName ?: "", success)
                                }
                                else{
                                    url.toastCover()
                                }
                            }
                        }
                    }
                    override fun onCancel() {
                        finish.invoke(-1,-1,"","", false)
                    }
                })
        }

        /*上传单个Bitmap*/
        @JvmStatic
        fun startUpload(bitmap: Bitmap,pathName:String = "avatar",finish:(String,Boolean)->Unit) {
            apiService.upToken.observe(currentAty){ requestType ->
                if (requestType?.code == 0) {
                    val token = requestType.data?.toString()?:""
                    val simpleDateFormat: DateFormat =
                        android.icu.text.SimpleDateFormat("yyyyMMddHHmmss")
                    val key = pathName + "/" +
                            simpleDateFormat.format(Date())+"Bitmap"
                    val bytes = bitmapToByteArray(bitmap)
                    mangerInstance?.put(bytes, key, token,
                        { _: String?, info: ResponseInfo, res: JSONObject? ->
                            if (res != null && info.isOK) {//res包含hash、key等信息，具体字段取决于上传策略的设置
                                try {
                                    val url = "https://" + Config.getQiNiuFile_URL + res.getString("key")
                                    finish.invoke(url, true)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    finish.invoke("",false)
                                }
                            } else {
                                finish.invoke("",false)
                            }
                        }, null)
                }
                else finish.invoke("",false)
            };
        }

        /*上传单个文件*/
        @JvmStatic
        fun startUpload(file:File?,pathName:String = "avatar",finish:(String,Boolean, String)->Unit) {
            apiService.upToken.observe(currentAty){ requestType ->
                if (requestType?.code == 0) {
                    val token = requestType.data?.toString()?:""
                    val simpleDateFormat: DateFormat =
                        android.icu.text.SimpleDateFormat("yyyyMMddHHmmss")
                    val fileName = file?.name
                    val key = pathName + "/" +
                            simpleDateFormat.format(Date()) + fileName?.substring(fileName.lastIndexOf("."))

                    mangerInstance?.put(file, key, token,
                        { _: String?, info: ResponseInfo, res: JSONObject? ->
                            if (res != null && info.isOK) {//res包含hash、key等信息，具体字段取决于上传策略的设置
                                try {
                                    val url = "https://" + Config.getQiNiuFile_URL + res.getString("key")
                                    finish.invoke(url, true, file?.name ?: "")
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    finish.invoke("",false, file?.name ?: "")
                                }
                            } else {
                                finish.invoke("",false, file?.name ?: "")
                            }
                        }, null)
                }
                else finish.invoke("",false, file?.name ?: "")
            };
        }


        /*
        * 上传多个文件
        * */
        fun startUpload(activity:FragmentActivity,files:List<File?>,pathName: String="avatar",finish:(Int,String,Boolean)->Unit) {
            apiService.upToken.observe(activity){ requestType ->
                if (requestType?.code == 0) {
                    val token = requestType.data?.toString()?:""
                    files.forEachIndexed {index,file->
                        if(file!=null){
                            val fileName = file.name
                            val key = pathName + "/" +
                                    System.currentTimeMillis() + fileName.substring(fileName.lastIndexOf("."))
                            mangerInstance?.put(file, key, token,
                            { key: String?, info: ResponseInfo, res: JSONObject? ->
                                if (res != null && info.isOK) {//res包含hash、key等信息，具体字段取决于上传策略的设置
                                try {
                                        val url = "https://" + Config.getQiNiuFile_URL + res.getString("key")
                                        finish.invoke(index,url, true)
                                    } catch (e: Exception) {
                                       e.printStackTrace()
                                       finish.invoke(index,"",false)
                                    }
                                } else {
                                finish.invoke(index,"",false)
                            }
                        }, null)
                        }
                    }
                }
                    else finish.invoke(-1,requestType?.message?:"token获取失败！",false)
            }
        }
        //类型转换工具
        @JvmStatic
        fun convertToLocalFile(localMedia: LocalMedia?): File? {
            if (localMedia != null) {
                val path: String = if (localMedia.isCompressed) {
                    // 如果媒体文件已经被压缩，使用压缩后的路径
                    localMedia.compressPath
                } else if (localMedia.path.isNotEmpty()) {
                    // 如果未压缩，使用原始路径
                    localMedia.realPath//仅适用于当前项目的pictureSelector
                } else {
                    return null
                }
                return File(path)
            }
            return null
        }

    }
    fun bitmapToByteArray(bitmap: Bitmap, format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG, quality: Int = 100): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(format, quality, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }
}
fun File?.getType(): Int {
    if (this == null || !exists())
        return 0
    val extension = extension.toLowerCase()
    return when (extension) {
        in arrayOf("jpg", "jpeg", "png", "gif", "bmp", "webp") -> 1 // 图片类型
        in arrayOf("mp4", "mov", "avi", "flv", "wmv", "mkv", "webm") -> 2 // 视频类型
        in arrayOf("txt", "doc", "docx", "pdf", "ppt", "pptx", "xls", "xlsx", "csv") -> 3 // 文档类型
        in arrayOf("mp3", "wav", "aac", "wma", "flac", "ogg") -> 4 // 音频类型
        in arrayOf("zip", "rar", "7z", "tar", "gz") -> 5 // 压缩文件类型
        else -> 0 // 未知类型
    }
}
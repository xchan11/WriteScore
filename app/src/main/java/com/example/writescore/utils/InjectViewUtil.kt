package tuorong.com.healthy.utils

import android.app.Activity
import android.os.Parcelable
import android.text.TextUtils
import android.view.View
import tuorong.healthy.annotation.InjectView
import tuorong.healthy.annotation.IntentParams
import java.util.Arrays

class InjectViewUtil {

    companion object {
        fun injectView(activity: Activity) {
            val cls = activity.javaClass
            val declaredFields = cls.declaredFields //获得类的所有成员
            for (field in declaredFields) {
                if (field.isAnnotationPresent(InjectView::class.java)) {
                    val injectView = field.getAnnotation(InjectView::class.java)
                    val id = injectView?.value ?: -1
                    val view = activity.findViewById<View>(id)
                    field.isAccessible = true
                    try {
                        field.set(activity, view)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }


        fun injectIntentParams(activity: Activity) {
            val cls = activity.javaClass
            val intent = activity.intent
            val extras = intent.extras
            extras ?: return

            val declaredFields = cls.declaredFields
            for (field in declaredFields) {
                val intentParams = field.getAnnotation(IntentParams::class.java)
                val value = intentParams?.value
                val key = if (TextUtils.isEmpty(value)) {
                    field.name
                } else value
                if (extras.containsKey(key)) {
                    try {
                        var obj = extras.get(key)
                        // Parcelable数组类型不能直接设置，其他的可以
                        // 获得数组的元素类型
                        val componentType = field.type.componentType
                        // 当前属性是数组并且是Parcelable数组
                        if (field.type.isArray && componentType != null && Parcelable::class.java.isAssignableFrom(
                                componentType
                            )
                        ) {
                            val objs = obj as Array<Parcelable>
                            obj = Arrays.copyOf(
                                objs,
                                objs.size,
                                field.type as Class<out Array<Parcelable>>
                            )
                        }
                        field.isAccessible = true
                        field.set(activity, obj)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}
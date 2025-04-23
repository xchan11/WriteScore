package tuorong.com.healthy.utils

import android.widget.TextView
import tuorong.com.healthy.R

fun Int?.getSexText():String{
    return when(this){
        0-> "男"
        1-> "女"
        else -> "无数据"
    }
}

/**
 * 设置角色
 * */
fun TextView.setRole(str:String?){
    val bg = when(str){
        LoginJumpingManager.ROLE_CONSUMER -> R.drawable.shape_role2
        LoginJumpingManager.ROLE_AGENT -> R.drawable.shape_stroke_orange
        LoginJumpingManager.ROLE_EXPERT -> R.drawable.shape_level_blue
        LoginJumpingManager.ROLE_ADMIN -> R.drawable.shape_stroke_purple
        LoginJumpingManager.ROLE_PROVIDER -> R.drawable.shape_role_grey
        else -> R.drawable.shape_role_grey
    }
    setBackgroundResource(bg)

    setText(getRoleInChinese(str))

    val color = when(str){
        LoginJumpingManager.ROLE_CONSUMER -> R.color.role_text2
        LoginJumpingManager.ROLE_AGENT -> R.color.devivor
        LoginJumpingManager.ROLE_EXPERT -> R.color.blue2
        LoginJumpingManager.ROLE_ADMIN -> R.color.purple2
        LoginJumpingManager.ROLE_PROVIDER -> R.color.black7
        else -> R.color.black7
    }
    setTextColor(context.resources.getColor(color))
}

fun getRoleInChinese(str:String?):Int{
    return when(str){
        LoginJumpingManager.ROLE_CONSUMER -> R.string.ROLE_USER
        LoginJumpingManager.ROLE_AGENT -> R.string.ROLE_SERVICE
        LoginJumpingManager.ROLE_EXPERT -> R.string.ROLE_EXPERT
        LoginJumpingManager.ROLE_ADMIN -> R.string.ROLE_ADMIN
        LoginJumpingManager.ROLE_PROVIDER -> R.string.ROLE_PROVIDER
        else -> R.string.UN_KNOW
    }
}

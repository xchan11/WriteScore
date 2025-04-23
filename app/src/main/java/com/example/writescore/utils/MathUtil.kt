package tuorong.com.healthy.utils

/**
 * 安全转换
 * */
fun String?.saveToDouble():Double{
    if(this.isNullOrEmpty())
        return 0.0
    else{
        try {
            return this.toDouble()
        }catch (e:Exception){
            return 0.0
        }
    }

}
fun Any?.anyNumToInt(): Int {//任意类型转换为Int
    return try {
        when(this){
            null->0
            is Int->this
            is String-> {
                if(this.isNullOrEmpty())
                    0
                if((this.outOfDot().toLong()) < Int.MAX_VALUE)
                    this.outOfDot().toInt()
                else Int.MAX_VALUE
            }
            is Double-> if((this) < Int.MAX_VALUE) this.toInt() else Int.MAX_VALUE
            is Long -> if((this) < Int.MAX_VALUE) this.toInt() else Int.MAX_VALUE//不建议
            is Float -> if((this) < Int.MAX_VALUE) this.toInt() else Int.MAX_VALUE
            else -> 0
        }
    }
    catch (e:Exception){
        0
    }
}
fun String?.outOfDot(): String {//不管double float的字符串，转换为整数
    if(this.isNullOrEmpty())
        return "0"
    if(!this.contains("."))
        return this
    val dotIndex = this.indexOf('.')
    // 如果找到小数点，则截取小数点前的部分，否则使用整个字符串
    val integerValue = if (dotIndex != -1) {
        this.substring(0, dotIndex)
    } else {
        this
    }
    return integerValue
}

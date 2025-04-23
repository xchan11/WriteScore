package tuorong.com.healthy.utils.message

//强行转为秒级时间戳
fun Long.toSecondTimeStamp():Long{
    return if(this>1000000000000L)//自动根据秒级和毫秒级换算
        this/1000
    else this
}
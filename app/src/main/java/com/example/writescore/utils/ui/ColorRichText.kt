package tuorong.com.healthy.utils.ui

import android.widget.TextView
    fun TextView.setRichText(textPairs:List<Pair<String,String>>){
        var builder = StringBuilder()
        textPairs.forEach {
            builder.append("<font color='${it.first}'${it.second}</font>")
        }
        this.text = builder.toString()
    }

package com.example.writescore.utils;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;

import tuorong.com.healthy.base.ColorTextPair;

public class SpannableUtil {
    public static SpannableStringBuilder createSpannable(ColorTextPair... colorTextPairs){

        SpannableStringBuilder spannable = new SpannableStringBuilder();
        for (ColorTextPair pair : colorTextPairs) {
            if(TextUtils.isEmpty(pair.text)||pair.color.isEmpty())
                continue;
            int staticEnd = spannable.length();
            spannable.append(pair.text);
            int color = parseColor(pair.color);
            spannable.setSpan(
                    new android.text.style.ForegroundColorSpan(color),
                    staticEnd,
                    spannable.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            spannable.setSpan(
                    new android.text.style.AbsoluteSizeSpan(pair.fontSize, true),
                    staticEnd,
                    spannable.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            if (pair.isBold) {
                spannable.setSpan(
                        new StyleSpan(android.graphics.Typeface.BOLD), // 粗体样式
                        staticEnd,
                        spannable.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }
        }
        return spannable;
    }

    //安全转换颜色，默认灰色
    public static int parseColor(String color){
        try{
            int colorInt = Color.parseColor(color);
            return colorInt;
        } catch (Exception e){
            return Color.parseColor("#323232");
        }
    }
}

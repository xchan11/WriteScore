package com.example.writescore.utils.cookie_tool;


import android.util.Log;

import com.example.writescore.utils.cookie_tool.cache.CookieCache;
import com.example.writescore.utils.cookie_tool.cache.SetCookieCache;
import com.example.writescore.utils.cookie_tool.persistence.CookiePersistor;
import com.example.writescore.utils.cookie_tool.persistence.SharedPrefsCookiePersistor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;


public class AppCookieJar implements CookieJar {

    private CookieCache cache;
    private CookiePersistor persistor;

    public AppCookieJar(SetCookieCache cache, SharedPrefsCookiePersistor persistor) {
        this.cache = cache;
        this.persistor = persistor;

        this.cache.addAll(persistor.loadAll());
    }

    //在请求后返回数据后会自动调用该方法，在该方法中
    @Override
    synchronized public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        Log.d("NetInfoCookie", "saveFromResponse: 返回数据");
        for (Cookie c : cookies)
            Log.d("NetInfoCookie", "saveFromResponse: 返回然后保存的cookie" + c);
        // Log.d("NetInfoCookie", "saveFromResponse: "+cookies.get(1));
        cache.addAll(cookies);
        persistor.saveAll(cookies);
    }

    @Override
    synchronized public List<Cookie> loadForRequest(HttpUrl url) {

        Log.d("NetInfoCookie", "loadForRequest: 发起请求");
        List<Cookie> removedCookies = new ArrayList<>();
        List<Cookie> validCookies = new ArrayList<>();

        for (Iterator<Cookie> it = cache.iterator(); it.hasNext(); ) {
            Cookie currentCookie = it.next();

            if (isCookieExpired(currentCookie)) {
                removedCookies.add(currentCookie);
                it.remove();

            } else if (currentCookie.matches(url)) {
                validCookies.add(currentCookie);
            }
        }

        persistor.removeAll(removedCookies);

        for (Cookie c : validCookies)
            Log.d("NetInfoCookie", "loadForRequest: 放在请求头的cookie" + c);
        for (Cookie c : removedCookies)
            Log.d("NetInfoCookie", "loadForRequest: 需要实时更新的cookie" + c);
        return validCookies;
    }

    public String getCurrentSession(){
        Log.d("NetInfoCookie", "开始获取当前session");
        List<Cookie> removedCookies = new ArrayList<>();
        List<Cookie> validCookies = new ArrayList<>();

        for (Iterator<Cookie> it = cache.iterator(); it.hasNext(); ) {
            Cookie currentCookie = it.next();

            if (isCookieExpired(currentCookie)) {
                removedCookies.add(currentCookie);
                it.remove();

            } else {
                validCookies.add(currentCookie);
            }
        }

        persistor.removeAll(removedCookies);

        for (Cookie c : validCookies)
            Log.d("NetInfoCookie", "loadForRequest: 放在请求头的cookie" + c);
        for (Cookie c : removedCookies)
            Log.d("NetInfoCookie", "loadForRequest: 需要实时更新的cookie" + c);
        if(validCookies.size()>0){
            String res = validCookies.get(0).value();
            Log.d("NetInfoCookie", "获取到了当前session为"+res);
            return res;
        };
        return "";
    }

    private static boolean isCookieExpired(Cookie cookie) {
        return cookie.expiresAt() < System.currentTimeMillis();
    }

    synchronized public void clear() {
        cache.clear();
        persistor.clear();
    }
}

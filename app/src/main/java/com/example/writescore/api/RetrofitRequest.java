package com.example.writescore.api;

import com.example.writescore.base.LiveDataCallAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tuorong.com.healthy.api.interceptor.LogPrinter;

import

/**
 * Retrofit统一配置类
 */
public class RetrofitRequest {
    private static RetrofitRequest mInstance;
    private static Retrofit retrofit;

    private OkHttpClient mOkHttpClient;

    private RetrofitRequest(OkHttpClient okHttpClient) {
        if (okHttpClient == null) {  // 如果传进来的okHttpClient为空，就new一个
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(message ->{
                LogPrinter.INSTANCE.printNetLog(message);
            }
            ).setLevel(HttpLoggingInterceptor.Level.BODY);
            mOkHttpClient = new OkHttpClient.Builder().addInterceptor(interceptor)
                    .build();
        } else {  // 有传实例，直接用
            mOkHttpClient = okHttpClient;
        }
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .client(mOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(LiveDataCallAdapterFactory.create())
                .build();
    }


    /**
     * 0->Base_url 默认服务器
     * 1->Base_url2 副服务器
     * */
    public static String getBaseUrl() {
        if(isUrlDebug)
            return BASE_URL2;
        else
            return BASE_URL;
        return BASE_URL;
    }

    /**
     * 获取自身实例
     *
     * @param okHttpClient
     * @return
     */
    public static RetrofitRequest initClient(OkHttpClient okHttpClient) {
        if (mInstance == null) {
            synchronized (RetrofitRequest.class) {
                if (mInstance == null) {
                    mInstance = new RetrofitRequest(okHttpClient);
                }
            }
        }
        return mInstance;
    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }

    public static RetrofitRequest getInstance() {
        return initClient(null);
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }


}

package com.example.writescore.base.livedata;

import android.util.Log;

import androidx.lifecycle.LiveData;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;
import tuorong.com.healthy.MyApplication;
import tuorong.com.healthy.manager.GlobalConfig;
import tuorong.com.healthy.model.RequestType;


/**
 * todo Howie：这方面知识不太熟，后面再整理
 */
public class LiveDataCallAdapter<T> implements CallAdapter<T, LiveData<T>> {

    private Type responseType;

    public LiveDataCallAdapter(Type responseType) {
        this.responseType = responseType;
    }

    @NotNull
    @Override
    public Type responseType() {
        return responseType;
    }

    @NotNull
    @Override
    public LiveData<T> adapt(@NotNull Call<T> call) {
        return new LiveData<T>() {
            AtomicBoolean stared = new AtomicBoolean(false);

            @Override
            protected void onActive() {
                super.onActive();
                if (stared.compareAndSet(false, true)) {
                    call.enqueue(new Callback<T>() {
                        @Override
                        public void onResponse(@NotNull Call<T> call, @NotNull Response<T> response) {
                            postValue(response.body());
                            MyApplication.lastIRoute = call.request().url().toString();
                            Log.d("123456", "onResponse: " + response);
                            Log.d("123456", "onResponse: " + response.body());

                        }

                        @Override
                        public void onFailure(Call<T> call, Throwable t) {
                            Log.d("123456初始化", "onFailure: " + t.getMessage());
                            Log.d("123456初始化", "onFailure: " + t.toString());
                            MyApplication.lastIRoute = call.request().url().toString();
                            if(GlobalConfig.isDebug()){
                                if(Objects.requireNonNull(t.getMessage()).contains("BEGIN_OBJECT") || Objects.requireNonNull(t.getMessage()).contains("BEGIN_ARRAY")){
                                    int lastDotIndex = t.getMessage().lastIndexOf(".");
                                    String errorField = lastDotIndex != -1 ? t.getMessage().substring(lastDotIndex + 1) : "No found";
                                    if(!errorField.equals("No found")){
                                        postValue((T) new RequestType<T>(500,
                                                "错误类型字段->" + errorField, null));
                                    }else{
                                        postValue((T) new RequestType<T>(500,
                                                "网络异常", null));
                                    }
                                }else if(Objects.requireNonNull(t.getMessage()).contains("No address associated with hostname")){
                                    postValue((T) new RequestType<T>(500,
                                            "网络异常", null));
                                }else{
                                    postValue((T) new RequestType<T>(500,
                                            "未知异常", null));
                                }
                            }else{
                                postValue((T) new RequestType<T>(500,
                                        "数据解析异常", null));
                            }
                        }
                    });
                }

            }
        };
    }

}

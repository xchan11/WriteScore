package com.example.writescore.base;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.writescore.base.livedata.LiveDataCallAdapter;
import com.example.writescore.model.RequestType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.CallAdapter;
import retrofit2.Retrofit;

/**
 * todo Howie：这方面知识不太熟，后面再整理
 */
public class LiveDataCallAdapterFactory extends CallAdapter.Factory {

    public static final String TAG = "LDCAFactory";

    public static LiveDataCallAdapterFactory create() {
        return new LiveDataCallAdapterFactory();
    }

    @SuppressWarnings("ClassGetClass")
    @Nullable
    @Override
    public CallAdapter<?, ?> get(@NotNull Type returnType, @NotNull Annotation[] annotations, @NotNull Retrofit retrofit) {
        //getRawType是提取的实例class，如果我们的User<T> 他得到的就是User.class
        if (getRawType(returnType) != LiveData.class) {
//            throw new IllegalArgumentException("type must be parameterized");
            return null;
        }

        final Type bodyType;

        // getParameterUpperBound方法，他是获取参数的type的，我觉得可以把它理解为获取的泛型，比如我们写了个User<T>,
        // 我们可以new User<Man>这个man就是我们实例化的type。
        // 有两个参数，一个是index，是指位置，比如我们的User<T,V>
        // 这个T就是0，这个V就是1
        // parameterizedType是个接口，继承type接口，从字面上来看是参数类型，所以我把它理解为我们定义的这个T的类型，比如man，
        Type observableType = getParameterUpperBound(0, (ParameterizedType) returnType);
        Class<?> rawType = getRawType(observableType);
        Log.d(TAG, "rawType: " + rawType.getClass().getSimpleName());
        if (rawType != RequestType.class) {
            if (!(observableType instanceof ParameterizedType)) {
                throw new IllegalArgumentException("type must be parameterized");
            }
            bodyType = getParameterUpperBound(0, (ParameterizedType) observableType);
        } else {
            bodyType = observableType;
        }

        Log.d(TAG, "bodyType: " + bodyType.toString());
        return new LiveDataCallAdapter<>(bodyType);
//        return new LiveDataCallAdapter2<>(bodyType);
    }
}

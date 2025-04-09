package com.example.writescore.model;

/**
 * 请求类型实体类
 */
public class RequestType<T> {
    public final int code;
    public final String message;
    public final T data;

    public RequestType(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    @Override
    public String toString() {
        return "RequestType{" +
                "status=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}

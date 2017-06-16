package com.retrofitclient.alex.alexretrofitclient.http.Interceptor;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Alex on 2017/6/16.
 * 通用的添加头部的拦截器
 * Alex
 */

public class MyHeadersBaseInterceptor implements Interceptor {
    private Map<String, String> headers;

    public MyHeadersBaseInterceptor(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request.Builder builder = chain.request()
                .newBuilder();
        if (headers != null && headers.size() > 0) {
            Set<String> keys = headers.keySet();
            for (String headerKey : keys) {
                builder.addHeader(headerKey, headers.get(headerKey)).build();
            }
        }
        return chain.proceed(builder.build());

    }
}

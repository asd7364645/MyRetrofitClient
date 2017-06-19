package com.retrofitclient.alex.alexretrofitclient.http;

import android.content.Context;
import android.util.Log;

import com.retrofitclient.alex.alexretrofitclient.http.Interceptor.MyCacheInterceptor;
import com.retrofitclient.alex.alexretrofitclient.http.Interceptor.MyHeadersBaseInterceptor;
import com.retrofitclient.alex.alexretrofitclient.http.converter.JsonConverterFactory;
import com.retrofitclient.alex.alexretrofitclient.http.cookies.CookiesManager;

import org.json.JSONObject;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Alex on 2017/6/15.
 * Alex
 */

public class RetrofitClient {

    private static final int TIME_OUT = 20;
    private Context context;
    private Converter.Factory converterFactory;
    private Retrofit.Builder builder;
    private OkHttpClient okHttpClient;
    //缓存的文件
    private File httpCacheFile;
    //okhttp缓存
    private Cache cache;
    private Retrofit retrofit;

    public static RetrofitClient instance(Context context, String baseUrl) {
        return new RetrofitClient(context, baseUrl, null);
    }

    public static RetrofitClient instance(Context context, String baseUrl, Map<String, String> headers) {
        return new RetrofitClient(context, baseUrl, headers);
    }

    private RetrofitClient(Context context, String baseUrl, Map<String, String> headers) {
        this.context = context;
        if (baseUrl == null || baseUrl.isEmpty())
            throw new IllegalStateException("baseUrl错误！");
        if (httpCacheFile == null)
            httpCacheFile = new File(context.getCacheDir(), "ht_cache");
        try {
            if (cache == null)
                cache = new Cache(httpCacheFile, 10 * 1024 * 1024);
        } catch (Exception e) {
            Log.e("OKHttp", "Could not create http cache", e);
        }

        okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(new MyCacheInterceptor(context))
                .addNetworkInterceptor(new MyCacheInterceptor(context))
                .addInterceptor(new MyHeadersBaseInterceptor(headers))
//                .cookieJar(new CookiesManager(context))
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                .cache(cache)
                .build();

        converterFactory = JsonConverterFactory.create();
        builder = new Retrofit.Builder().baseUrl(baseUrl);
    }

    public static void close(Call call) {
        if (call != null && !call.isCanceled()) {
            call.cancel();
            Log.d("retrofitclient", "关闭连接");
        }
    }

    public RetrofitClient addHeaders(Map<String, String> headers) {
        okHttpClient.newBuilder().addInterceptor(new MyHeadersBaseInterceptor(headers)).build();
        return this;
    }

    public RetrofitClient addCookies() {
        okHttpClient.newBuilder().cookieJar(new CookiesManager(context)).build();
        return this;
    }

    public BaseApiResponse buildToBase() {
        builder.addConverterFactory(converterFactory)
                .client(okHttpClient);
        retrofit = builder.build();
        return new BaseApiResponse(retrofit.create(RetrofitApi.class));
    }

    public <T> T buildToGsonFactory(Class<T> serviceClass) {
        converterFactory = GsonConverterFactory.create();
        builder.addConverterFactory(converterFactory)
                .client(okHttpClient);
        retrofit = builder.build();
        return retrofit.create(serviceClass);
    }

    public <T> T build(Converter.Factory factory, Class<T> serviceClass) {
        converterFactory = factory;
        builder.addConverterFactory(converterFactory)
                .client(okHttpClient);
        retrofit = builder.build();
        return retrofit.create(serviceClass);
    }

    public class BaseApiResponse {
        private RetrofitApi baseApi;

        public BaseApiResponse(RetrofitApi baseApi) {
            this.baseApi = baseApi;
        }

        public Call<JSONObject> baseGet(String url, Map<String, String> queryMap) {
            return baseApi.executeGet(url, queryMap);
        }

        public Call<JSONObject> basePost(String url, Map<String, String> queryMap) {
            return baseApi.executePost(url, queryMap);
        }

        public Call<JSONObject> baseJson(String url, String json) {
            return baseApi.executeJson(url, RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json));
        }

    }

}

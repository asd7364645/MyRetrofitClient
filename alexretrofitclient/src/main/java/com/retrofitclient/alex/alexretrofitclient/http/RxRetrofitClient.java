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

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Alex on 2017/6/16.
 * Alex
 */

public class RxRetrofitClient {
    private static final int TIME_OUT = 20;
    private Context context;
    private Converter.Factory converterFactory;
    private Retrofit.Builder builder;
    private OkHttpClient okHttpClient;
    //缓存的文件
    private File httpCacheFile;
    //okhttp缓存
    private Cache cache;
    private static Retrofit retrofit;

    public static RxRetrofitClient instance(Context context, String baseUrl) {
        return new RxRetrofitClient(context, baseUrl, null);
    }

    public static RxRetrofitClient instance(Context context, String baseUrl, Map<String, String> headers) {
        return new RxRetrofitClient(context, baseUrl, headers);
    }

    private RxRetrofitClient(Context context, String baseUrl, Map<String, String> headers) {
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

    public RxRetrofitClient addHeaders(Map<String, String> headers) {
        okHttpClient.newBuilder().addInterceptor(new MyHeadersBaseInterceptor(headers)).build();
        return this;
    }

    public RxRetrofitClient addCookies() {
        okHttpClient.newBuilder().cookieJar(new CookiesManager(context)).build();
        return this;
    }

    public BaseApiResponse buildToBase() {
        builder.addConverterFactory(converterFactory)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient);
        retrofit = builder.build();
        return new BaseApiResponse(retrofit.create(RxRetrofitApi.class));
    }

    public static <T> Observable<T> execute(Observable<T> observable){
        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public <T> T buildToGsonFactory(Class<T> serviceClass) {
        converterFactory = GsonConverterFactory.create();
        builder.addConverterFactory(converterFactory)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient);
        retrofit = builder.build();
        return retrofit.create(serviceClass);
    }

    public <T> T build(Converter.Factory factory, Class<T> serviceClass) {
        converterFactory = factory;
        builder.addConverterFactory(converterFactory)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient);
        retrofit = builder.build();
        return retrofit.create(serviceClass);
    }

    public class BaseApiResponse {
        private RxRetrofitApi baseApi;

        public BaseApiResponse(RxRetrofitApi baseApi) {
            this.baseApi = baseApi;
        }

        public Observable<JSONObject> baseGet(String url, Map<String, String> queryMap) {
            return baseApi.executeGet(url, queryMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }

        public Observable<JSONObject> basePost(String url, Map<String, String> queryMap) {
            return baseApi.executePost(url, queryMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }

        public Observable<JSONObject> baseJson(String url, String json) {
            return baseApi.executeJson(url, RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }

    }
}

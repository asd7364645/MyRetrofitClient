package com.retrofitclient.alex.alexretrofitclient.http.Interceptor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.retrofitclient.alex.alexretrofitclient.utils.MyNetWorkUtils;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 缓存拦截器
 */
public class MyCacheInterceptor implements Interceptor {

    private Context context;

    public MyCacheInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        if (MyNetWorkUtils.isNetworkConn(context)) {
            Response response = chain.proceed(request);
            // 最大读取缓存时间为60秒
            int maxAge = 60;
            String cacheControl = request.cacheControl().toString();
            Log.e("retrofit client", "60s load cahe" + cacheControl);
            return response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .build();
        } else {
//            ((Activity) context).runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(context, "当前无网络! 为你智能加载缓存", Toast.LENGTH_SHORT).show();
//                }
//            });
            Log.e("retrofit client", " 没有网络，读取缓存");
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
            Response response = chain.proceed(request);
            //设置缓存时间为3天
            int maxStale = 60 * 60 * 24 * 3;
            return response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .build();
        }
    }
}
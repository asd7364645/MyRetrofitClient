package com.retrofitclient.alex.alexretrofitclient.http.cookies;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class CookiesManager implements CookieJar {
    private Context context;
    private PersistentCookieStore cookieStore;

    public CookiesManager(Context context) {
        this.context = context;
        if (cookieStore == null) {
            cookieStore = new PersistentCookieStore(context);
        }
    }

    @Override
    public void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {
        if (cookies.size() > 0) {
            for (Cookie item : cookies) {
                cookieStore.add(url, item);
            }
        }
    }

    @Override
    public List<Cookie> loadForRequest(@NonNull HttpUrl url) {
        return cookieStore.get(url);
    }
}
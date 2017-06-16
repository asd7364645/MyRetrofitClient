package com.retrofitclient.alex.alexretrofitclient.http;

import android.support.annotation.NonNull;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Alex on 2017/6/16.
 * Alex
 */

public abstract class RetrofitCallBack<T> implements Callback<T> {
    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
        success(call, response);
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        if (call.isCanceled())
            Log.d("retrofitClient", "取消连接");
        else
            err(call, t);
    }

    public abstract void success(@NonNull Call<T> call, @NonNull Response<T> response);

    public abstract void err(@NonNull Call<T> call, @NonNull Throwable t);

}

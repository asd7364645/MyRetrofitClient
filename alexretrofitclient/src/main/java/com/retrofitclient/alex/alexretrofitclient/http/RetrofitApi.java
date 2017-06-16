package com.retrofitclient.alex.alexretrofitclient.http;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by Alex on 2017/6/15.
 * Alex
 */

public interface RetrofitApi {

    @GET("{getUrl}")
    Call<JSONObject> executeGet(@Path("getUrl") String url, @QueryMap Map<String, String> queryMap);
    @GET("{getUrl}")
    Call<JSONObject> executePost(@Path("getUrl") String url, @QueryMap Map<String, String> queryMap);
    @GET("{getUrl}")
    Call<JSONObject> executeJson(@Path("getUrl") String url, @Body RequestBody jsonStr);

}

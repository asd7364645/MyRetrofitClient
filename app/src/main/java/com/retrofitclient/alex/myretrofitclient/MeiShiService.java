package com.retrofitclient.alex.myretrofitclient;

import com.retrofitclient.alex.myretrofitclient.bean.CaiBean;
import com.retrofitclient.alex.myretrofitclient.constant.Constant;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Alex on 2017/6/16.
 * Alex
 */

public interface MeiShiService  {

    @GET("query?key=" + Constant.APPKEY)
    Call<CaiBean> getCaiFenLei();

    @GET("query?key=" + Constant.APPKEY)
    Observable<CaiBean> getCaiFenLeiRx();
}

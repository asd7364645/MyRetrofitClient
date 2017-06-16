package com.retrofitclient.alex.myretrofitclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.gson.Gson;
import com.retrofitclient.alex.alexretrofitclient.http.RetrofitCallBack;
import com.retrofitclient.alex.alexretrofitclient.http.RetrofitClient;
import com.retrofitclient.alex.alexretrofitclient.http.RxRetrofitClient;
import com.retrofitclient.alex.myretrofitclient.bean.CaiBean;
import com.retrofitclient.alex.myretrofitclient.constant.Constant;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    Call<CaiBean> caiFenLei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    private void lianJie() {

        caiFenLei = RetrofitClient.instance(this, Constant.URL)
                .buildToGsonFactory(MeiShiService.class)
                .getCaiFenLei();
        caiFenLei.enqueue(new RetrofitCallBack<CaiBean>() {
            @Override
            public void success(@android.support.annotation.NonNull Call<CaiBean> call, @android.support.annotation.NonNull Response<CaiBean> response) {
                System.out.println("response:" + new Gson().toJson(response.body()));
            }

            @Override
            public void err(@android.support.annotation.NonNull Call<CaiBean> call, @android.support.annotation.NonNull Throwable t) {

            }
        });

    }

    public void lianJieClick(View view) {
        lianJie();
        System.out.println("点击");
    }


    public void closeClick(View view) {
        RetrofitClient.close(caiFenLei);
    }

    public void rxLianJieClick(View view) {
        rxLianJie();
        System.out.println("rx点击");
    }

    private void rxLianJie() {
        HashMap<String, String> mMap = new HashMap<>();
        mMap.put("key", Constant.APPKEY);
        Observable<CaiBean> caiFenLeiRx = RxRetrofitClient.instance(this, Constant.URL)
                .buildToGsonFactory(MeiShiService.class)
                .getCaiFenLeiRx();

        RxRetrofitClient.execute(caiFenLeiRx).subscribe(new Consumer<CaiBean>() {
            @Override
            public void accept(@NonNull CaiBean caiBean) throws Exception {
                System.out.println("caiDan::" + new Gson().toJson(caiBean));
            }
        });
    }
}

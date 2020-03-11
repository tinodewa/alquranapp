package com.roma.android.sihmi.model.network;

import com.roma.android.sihmi.utils.Constant;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static volatile ApiClient instance = null;
    private static Retrofit retrofit;

    public ApiClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
        retrofit = new Retrofit.Builder().baseUrl(Constant.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static ApiClient getInstance(){
        if (instance == null){
            synchronized (ApiClient.class){
                instance = new ApiClient();
            }
        }
        return instance;
    }


    public MasterService getApi(){
        return retrofit.create(MasterService.class);
    }

}

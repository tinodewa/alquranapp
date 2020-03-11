package com.roma.android.sihmi.core;

import android.app.Application;

import androidx.room.Room;

import com.crashlytics.android.Crashlytics;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.utils.Constant;

import io.fabric.sdk.android.Fabric;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class CoreApplication extends Application {
    private static CoreApplication INSTANCE;
    private Constant constant;
    private AppDb appDb;
    private Scheduler scheduler;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        constant = new Constant(this);
    }

    public static CoreApplication get(){
        return INSTANCE;
    }

    public Constant getConstant() {
        return constant;
    }
}

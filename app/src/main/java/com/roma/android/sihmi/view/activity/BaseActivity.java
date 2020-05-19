package com.roma.android.sihmi.view.activity;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.core.CoreApplication;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;
import com.facebook.stetho.Stetho;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static io.fabric.sdk.android.Fabric.TAG;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());
        FirebaseMessaging.getInstance().setAutoInitEnabled(false);
        Stetho.initializeWithDefaults(this);
        Fabric.with(this, new Crashlytics());
        initFCM();
        initStetho();
        initFabric();
        initTheme();

        adjustFontScale(getResources().getConfiguration(), Constant.getFontSize());

        String fontPathStr;
        if (!Constant.getFontName().equalsIgnoreCase("Default")) {
            fontPathStr = "fonts/"+Constant.getFontName()+".ttf";
        }
        else {
            fontPathStr = null;
        }

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(fontPathStr)
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    private void initTheme(){
        try {
            if (Constant.loadNightModeState()) {
                setTheme(R.style.MainActivityThemeDark);
            } else {
                setTheme(R.style.AppTheme);
            }
        } catch (NullPointerException e){
            setTheme(R.style.AppTheme);
        }
    }

    private void initFabric(){
        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);
    }

    private void initFCM(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.d(TAG, "onComplete: onNewToken "+token);

                        Log.d(TAG, token);
                    }
                });
    }

    private void initStetho(){
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void location() {
        String[] listPermission = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requestPermission(listPermission)){
                requestLocation();
//                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                boolean isGpsProviderEnabled, isNetworkProviderEnabled;
//                assert locationManager != null;
//                isGpsProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//                isNetworkProviderEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//
//                if (!isGpsProviderEnabled && !isNetworkProviderEnabled) {
//                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                    builder.setTitle("Izin Lokasi");
//                    builder.setMessage("Aplikasi ini membutuhkan izin lokasi. Izinkan lokasi untuk terus menggunakan fitur aplikasi.");
//                    builder.setPositiveButton("IZINKAN", (dialogInterface, i) -> {
//                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                        Intent intentRedirectionGPSSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        intentRedirectionGPSSettings.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                        startActivityForResult(intentRedirectionGPSSettings, REQUEST_LOC);
//                    });
//                    builder.setNegativeButton("TOLAK", null);
//                    builder.show();
//                }
            }else{
                requestPermission(listPermission);
            }
        }else{
            requestPermission(listPermission);
        }
    }


    public boolean requestPermission(String[] permission){
        final boolean[] returned = {false};
        Dexter.withActivity(BaseActivity.this).withPermissions(permission)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        returned[0] = true;
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();

                        returned[0] = false;
                    }
                })
                .withErrorListener(error -> {
                    Tools.showToast(BaseActivity.this,"Go to Settings and Grant the permission to use this feature.");
                })
                .onSameThread()
                .check();
        return returned[0];
    }

    private void requestLocation(){
//        1 Create a LocationRequest as per your wish
        LocationRequest mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1 * 1000);

//        2 Create Location Builder
        LocationSettingsRequest.Builder settingsBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        settingsBuilder.setAlwaysShow(true);

//        3 Get LocationSettingsResponse Task using following code
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(this)
                .checkLocationSettings(settingsBuilder.build());

//        4 Add a OnCompleteListener to get the result from the Task.When the Task completes, the client can check the location settings by looking at the status code from the LocationSettingsResponse object.
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response =
                            task.getResult(ApiException.class);
                } catch (ApiException ex) {
                    switch (ex.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvableApiException =
                                        (ResolvableApiException) ex;
                                resolvableApiException
                                        .startResolutionForResult(BaseActivity.this,
                                                102);
                            } catch (IntentSender.SendIntentException e) {

                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                            break;
                    }
                }
            }
        });
    }

    private void overrideFonts(final Context context, final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child);
                }
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/buka_puasa_bersama_5.ttf"));
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public  void adjustFontScale(Configuration configuration, float scale) {
        configuration.fontScale = scale;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        getBaseContext().getResources().updateConfiguration(configuration, metrics);

    }
}

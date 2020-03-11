package com.roma.android.sihmi.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.core.CoreApplication;
import com.roma.android.sihmi.utils.Constant;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity {
    @BindView(R.id.tv_splash)
    TextView tvSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

//        CalligraphyUtils.applyFontToTextView(textView, TypefaceUtils.load(getAssets(), "fonts/my_font.ttf"));
        tvSplash.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/buka_puasa_bersama_5.ttf"));

        checkLanguage(Constant.getLanguage());

        new Handler().postDelayed(() -> {
            Log.d("Test", "runTOken: "+Constant.getToken());
            if (Constant.isLoggedIn()){
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            } else {
                if (Constant.getSizeAccount() > 0){
                    startActivity(new Intent(SplashActivity.this, SwitchAccountActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
                finish();
            }
        }, 3000);
    }


    private void checkLanguage(String code){
        Constant.setLanguage(code);
        Locale locale = new Locale(code);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config,
                getResources().getDisplayMetrics());
    }
}

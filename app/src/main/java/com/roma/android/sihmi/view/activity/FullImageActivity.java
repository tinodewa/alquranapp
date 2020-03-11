package com.roma.android.sihmi.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.roma.android.sihmi.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FullImageActivity extends BaseActivity {
    public static String URL_IMAGE = "url_image";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_fullscreen)
    ImageView ivFullScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        String url = getIntent().getStringExtra(URL_IMAGE);
        if (url != null && !url.isEmpty()){
            Glide.with(FullImageActivity.this)
                    .load(Uri.parse(url))
                    .into(ivFullScreen);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
}

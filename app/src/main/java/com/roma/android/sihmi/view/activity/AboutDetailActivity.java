package com.roma.android.sihmi.view.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Sejarah;
import com.roma.android.sihmi.model.database.interfaceDao.SejarahDao;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutDetailActivity extends BaseActivity {
    public static String ID_ABOUT = "id_about";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title)
    TextView tvTtitle;
    @BindView(R.id.tv_desc)
    TextView tvDesc;

    SejarahDao sejarahDao;
    String idSejarah;
    Sejarah sejarah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_detail);

        ButterKnife.bind(this);
        sejarahDao = AppDb.getInstance(this).sejarahDao();

        idSejarah = getIntent().getStringExtra(ID_ABOUT);
        sejarah = sejarahDao.getSejarahById(idSejarah);

        toolbar.setTitle(sejarah.getJudul().toUpperCase());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvTtitle.setVisibility(View.GONE);
        tvTtitle.setText(sejarah.getJudul());
        tvDesc.setText(sejarah.getDeskripsi());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){

        }

        return super.onOptionsItemSelected(item);
    }
}

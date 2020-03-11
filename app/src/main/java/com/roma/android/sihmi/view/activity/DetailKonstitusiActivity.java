package com.roma.android.sihmi.view.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;

import com.roma.android.sihmi.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailKonstitusiActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_konstitusi);
        ButterKnife.bind(this);

        toolbar.setTitle("Detail Konstitusi".toUpperCase());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat, menu);
        menu.findItem(R.id.action_1).setTitle("Update");
        menu.findItem(R.id.action_2).setTitle("Delete");
        menu.findItem(R.id.action_3).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_1:
                break;
            case R.id.action_2:
                break;
            case R.id.action_3:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}

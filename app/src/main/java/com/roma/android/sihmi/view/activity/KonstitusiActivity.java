package com.roma.android.sihmi.view.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.core.CoreApplication;
import com.roma.android.sihmi.model.database.entity.Konstituisi;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.KonstituisiResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.adapter.KonstituisiAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KonstitusiActivity extends BaseActivity {
    public static String TYPE_KONSTITUSI = "type_konstitusi";
    public static String NAMA_KONSTITUSI = "nama_konstitusi";

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.llPilihan)
    LinearLayout llPilihan;

    @BindView(R.id.cv_nasional)
    CardView cvNasional;
    @BindView(R.id.cv_cabang)
    CardView cvCabang;
    @BindView(R.id.cv_komisariat)
    CardView cvKomisariat;

//    @BindView(R.id.swipeRefresh)
//    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.fab_add)
    FloatingActionButton fab;

    MasterService service;
    int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konstitusi);
        ButterKnife.bind(this);

        toolbar.setTitle(getIntent().getStringExtra(NAMA_KONSTITUSI).toUpperCase());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        service = ApiClient.getClient().create(MasterService.class);

//        type = getIntent().getIntExtra(TYPE_KONSTITUSI, 0);
//        if (type == 0){
//            llPilihan.setVisibility(View.VISIBLE);
//            recyclerview.setVisibility(View.GONE);
//            fab.setVisibility(View.GONE);
//        } else {
//            if (Tools.isSecondAdmin() || Tools.isSuperAdmin()){
//                fab.setVisibility(View.VISIBLE);
//            } else {
//                fab.setVisibility(View.GONE);
//            }
//            getData(type);
//            llPilihan.setVisibility(View.GONE);
//            recyclerview.setVisibility(View.VISIBLE);
//        }
//
//
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivityForResult(new Intent(KonstitusiActivity.this, FileFormActivity.class).putExtra(TYPE_KONSTITUSI, type), Constant.REQUEST_KONSTITUSI);
//            }
//        });
    }

    @OnClick({R.id.cv_nasional, R.id.cv_cabang, R.id.cv_komisariat})
    public void menuClick(CardView button) {
        switch (button.getId()) {
            case R.id.cv_nasional:
                startActivity(new Intent(this, KonstitusiActivity.class).putExtra(TYPE_KONSTITUSI, 1).putExtra(KonstitusiActivity.NAMA_KONSTITUSI, "Konstitusi Nasional"));
                break;
            case R.id.cv_cabang:
                startActivity(new Intent(this, KonstitusiActivity.class).putExtra(TYPE_KONSTITUSI, 2).putExtra(KonstitusiActivity.NAMA_KONSTITUSI, "Konstitusi Cabang"));
                break;
            case R.id.cv_komisariat:
                startActivity(new Intent(this, KonstitusiActivity.class).putExtra(TYPE_KONSTITUSI, 3).putExtra(KonstitusiActivity.NAMA_KONSTITUSI, "Konstitusi Komisariat"));
                break;
        }
    }

//    private void getData(int type){
//        Call<KonstituisiResponse> call = service.getKonstitusi(String.valueOf(type));
//        if (Tools.isOnline(this)) {
//            Tools.showProgressDialog(this, "Load Data...");
//            call.enqueue(new Callback<KonstituisiResponse>() {
//                @Override
//                public void onResponse(Call<KonstituisiResponse> call, Response<KonstituisiResponse> response) {
//                    if (response.isSuccessful()) {
//                        if (response.body().getStatus().equalsIgnoreCase("ok")) {
//                            CoreApplication.get().getAppDb().interfaceDao().insertKonstituisi(response.body().getData());
//                            initAdapter(response.body().getData());
//                        } else {
//                            Toast.makeText(KonstitusiActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(KonstitusiActivity.this, "" + response.message(), Toast.LENGTH_SHORT).show();
//                    }
//                    Tools.dissmissProgressDialog();
//                }
//
//                @Override
//                public void onFailure(Call<KonstituisiResponse> call, Throwable t) {
//                    Toast.makeText(KonstitusiActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
//                    Tools.dissmissProgressDialog();
//                }
//            });
//        } else {
//            Toast.makeText(KonstitusiActivity.this, "Tidak Ada Internet!", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void initAdapter(List<Konstituisi> list){
//        KonstituisiAdapter adapter = new KonstituisiAdapter(this, list, new KonstituisiAdapter.itemClickListener() {
//            @Override
//            public void onItemClick(Konstituisi konstituisi, boolean isLongClick) {
//                if (isLongClick){
//
//                } else {
//                    startActivityForResult(new Intent(KonstitusiActivity.this, FileDetailActivity.class)
//                                    .putExtra("file", konstituisi.getFile())
//                                    .putExtra("_id", konstituisi.get_id())
//                                    .putExtra(TYPE_KONSTITUSI, type),
//                            Constant.REQUEST_KONSTITUSI);
//                }
//            }
//        }, 2);
//
//        recyclerview.setLayoutManager(new LinearLayoutManager(this));
//        recyclerview.setAdapter(adapter);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        checkPermission();
//    }
//
//    private void checkPermission(){
//        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE};
//
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
//                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
//
//        }else{
//            ActivityCompat.requestPermissions(this,
//                    permissions,
//                    LOCATION_PERMISSION_REQUEST_CODE);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
////        mLocationPermissionsGranted = false;
//
//        switch(requestCode){
//            case LOCATION_PERMISSION_REQUEST_CODE:{
//                if(grantResults.length > 0){
//                    for(int i = 0; i < grantResults.length; i++){
//                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
////                            mLocationPermissionsGranted = false;
//                            return;
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == Constant.REQUEST_KONSTITUSI && resultCode == Activity.RESULT_OK){
//            getData(type);
//        }
//    }
}

package com.roma.android.sihmi.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.core.CoreApplication;
import com.roma.android.sihmi.model.database.entity.Sejarah;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.SejarahResponse;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.adapter.TentangKamiAdapter;
import com.roma.android.sihmi.view.fragment.TentangFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AboutActivity extends BaseActivity {
    public static String TYPE_ABOUT = "type_about";
    public static String NAMA_ABOUT = "nama_about";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_tentang_kami)
    RecyclerView rvTentangKami;
    @BindView(R.id.frameContent)
    FrameLayout frameLayout;
    @BindView(R.id.llPilihan)
    LinearLayout llPilihan;
//    @BindView(R.id.card_view)
//    CardView cardView;

    @BindView(R.id.cv_nasional)
    CardView cvNasional;
    @BindView(R.id.cv_cabang)
    CardView cvCabang;
    @BindView(R.id.cv_komisariat)
    CardView cvKomisariat;

    @BindView(R.id.fab_add)
    FloatingActionButton fab;

    MasterService service;

    TentangKamiAdapter adapter;
    List<Sejarah> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        toolbar.setTitle(getIntent().getStringExtra(NAMA_ABOUT).toUpperCase());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int type = getIntent().getIntExtra(TYPE_ABOUT, 0);
        if (type == 0){
            llPilihan.setVisibility(View.VISIBLE);
            rvTentangKami.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
        } else {
            if (Tools.isSecondAdmin() || Tools.isSuperAdmin()) {
                fab.setVisibility(View.VISIBLE);
            } else {
                fab.setVisibility(View.GONE);
            }
            getData(type);
            llPilihan.setVisibility(View.GONE);
            rvTentangKami.setVisibility(View.VISIBLE);
        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutActivity.this, AboutFormActivity.class));
            }
        });

//        getViewUser();
        initAdapter();
//        initFrameLayout();


    }

    private void getViewUser(){
//        String idRoles = CoreApplication.get().getAppDb().interfaceDao().getUser().getId_roles();
//        String namaRoles = CoreApplication.get().getAppDb().interfaceDao().getNamaRoleById(idRoles);
//        if (!namaRoles.toLowerCase().contains("admin")){
            rvTentangKami.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.GONE);
//        } else {
//            rvTentangKami.setVisibility(View.GONE);
//            frameLayout.setVisibility(View.VISIBLE);
//        }
    }

    private void initAdapter(){
//        adapter = new TentangKamiAdapter(this, list, new TentangKamiAdapter.itemClickListener() {
//            @Override
//            public void onItemClick(String id) {
//                startActivity(new Intent(AboutActivity.this, AboutDetailActivity.class).putExtra(AboutDetailActivity.ID_ABOUT, id));
//            }
//
//            @Override
//            public void onLongItemClick(String id) {
//
//            }
//        });
//        rvTentangKami.setLayoutManager(new LinearLayoutManager(this));
//        rvTentangKami.setAdapter(adapter);
    }

    private void initFrameLayout(){
        getSupportFragmentManager().beginTransaction().replace(R.id.frameContent, new TentangFragment()).commit();
    }

    private void getData(int type){
//        Tools.showProgressDialog(this, "Load Data...");
//        MasterService service = ApiClient.getClient().create(MasterService.class);
//        Call<SejarahResponse> call = service.getSejarah(type);
//        call.enqueue(new Callback<SejarahResponse>() {
//            @Override
//            public void onResponse(Call<SejarahResponse> call, Response<SejarahResponse> response) {
//                if (response.isSuccessful()) {
//                    if (response.body().getStatus().equalsIgnoreCase("success")) {
//                        adapter.updateData(response.body().getData());
//                        CoreApplication.get().getAppDb().interfaceDao().insertSejarah(response.body().getData());
//                        Tools.dissmissProgressDialog();
//                    }
//                } else {
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<SejarahResponse> call, Throwable t) {
//
//            }
//        });
    }

    @OnClick({R.id.cv_nasional, R.id.cv_cabang, R.id.cv_komisariat})
    public void menuClick(CardView button) {
        switch (button.getId()) {
            case R.id.cv_nasional:
                startActivity(new Intent(this, AboutActivity.class).putExtra(TYPE_ABOUT, 1).putExtra(AboutActivity.NAMA_ABOUT, "HMI Nasional"));
                break;
            case R.id.cv_cabang:
                startActivity(new Intent(this, AboutActivity.class).putExtra(TYPE_ABOUT, 2).putExtra(AboutActivity.NAMA_ABOUT, "HMI Cabang"));
                break;
            case R.id.cv_komisariat:
                startActivity(new Intent(this, AboutActivity.class).putExtra(TYPE_ABOUT, 3).putExtra(AboutActivity.NAMA_ABOUT, "HMI Komisariat"));
                break;
        }
    }

}

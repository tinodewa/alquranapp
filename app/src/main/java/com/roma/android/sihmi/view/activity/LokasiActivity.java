package com.roma.android.sihmi.view.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.core.CoreApplication;
import com.roma.android.sihmi.model.database.entity.tempat.Kota;
import com.roma.android.sihmi.model.database.entity.tempat.Provinsi;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.tempat.KotaResponse;
import com.roma.android.sihmi.model.response.tempat.ProvinsiResponse;
import com.roma.android.sihmi.view.adapter.TempatAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LokasiActivity extends BaseActivity {
    public static int REQUEST_LOKASI = 1001;
    public static String NAMA_LOKASI = "NAMA_LOKASI";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.cv_provinsi)
    CardView cvProvinsi;
    @BindView(R.id.tv_provinsi)
    TextView tvProvinsi;
    @BindView(R.id.cv_kota)
    CardView cvKota;
    @BindView(R.id.tv_kota)
    TextView tvKota;
    @BindView(R.id.cv_wilayah)
    CardView cvWilayah;
    @BindView(R.id.tv_wilayah)
    TextView tvWilayah;
    @BindView(R.id.btn_simpan)
    Button btnSimpan;

    EditText etSearch;
    RecyclerView rvList;
    ProgressBar progressBar;
    AlertDialog dialog;

    MasterService service;

    int id_provinsi=0, id_kota=0;

    String namaProv="", namaKota="", namaWilayah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lokasi);
        ButterKnife.bind(this);

        toolbar.setTitle("Lokasi LK".toUpperCase());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        service = CoreApplication.get().getClient().create(MasterService.class);
//
//        cvProvinsi.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogLokasi(1);
//            }
//        });
//
//        cvKota.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogLokasi(2);
//            }
//        });
//
//        cvWilayah.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogLokasi(3);
//            }
//        });
//
//        checkData();
    }

    private void dialogLokasi(int type){
        View view=getLayoutInflater().inflate(R.layout.dialog_list_scroll, null);
        etSearch = view.findViewById(R.id.et_search);
        rvList = view.findViewById(R.id.rv_list);
        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        dialog = new AlertDialog.Builder(this)
                .setTitle("Dialog Provinsi")
                .setView(view)
                .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .create();
        dialog.show();

        switch (type){
            case 1:
                getProvinsi();
                break;
            case 2:
                getKota(id_provinsi);
                break;
            case 3:
                getWilayah(id_kota);
                break;
        }

    }

    private void getProvinsi(){
        final List<String> stringList = new ArrayList<>();
        Call<ProvinsiResponse> call = service.getProvinsi();
        call.enqueue(new Callback<ProvinsiResponse>() {
            @Override
            public void onResponse(Call<ProvinsiResponse> call, final Response<ProvinsiResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()){
                    if (response.body().getStatus().equalsIgnoreCase("ok")) {
                        if (response.body().getData().size() > 0) {
                            for (int i = 0; i < response.body().getData().size(); i++) {
                                stringList.add(response.body().getData().get(i).getNama());
                            }
                            final TempatAdapter adapter = new TempatAdapter(getApplicationContext(), stringList, new TempatAdapter.itemClickListener() {
                                @Override
                                public void onItemClick(String nama) {
                                    Toast.makeText(LokasiActivity.this, ""+nama, Toast.LENGTH_SHORT).show();
                                    namaProv = nama;
                                    tvProvinsi.setText(nama);
                                    id_provinsi = getIdProvinsi(nama, response.body().getData());
                                    dialog.dismiss();
                                    checkData();
                                }
                            });
                            rvList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            rvList.setAdapter(adapter);
                            etSearch.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    adapter.updateData(filter(s.toString(), stringList));
                                }
                            });
                        }
                    } else {
                        Toast.makeText(LokasiActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LokasiActivity.this, ""+response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProvinsiResponse> call, Throwable t) {
                Toast.makeText(LokasiActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getKota(int id_provinsi){
        final List<String> stringList = new ArrayList<>();
        Call<KotaResponse> call = service.getKota(id_provinsi);
        call.enqueue(new Callback<KotaResponse>() {
            @Override
            public void onResponse(Call<KotaResponse> call, final Response<KotaResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()){
                    if (response.body().getStatus().equalsIgnoreCase("ok")) {
                        if (response.body().getData().size() > 0) {
                            for (int i = 0; i < response.body().getData().size(); i++) {
                                stringList.add(response.body().getData().get(i).getNama());
                            }
                            final TempatAdapter adapter = new TempatAdapter(getApplicationContext(), stringList, new TempatAdapter.itemClickListener() {
                                @Override
                                public void onItemClick(String nama) {
                                    namaKota = nama;
                                    Toast.makeText(LokasiActivity.this, ""+nama, Toast.LENGTH_SHORT).show();
                                    tvKota.setText(nama);
                                    id_kota = getIdKota(nama, response.body().getData());
                                    dialog.dismiss();
                                    checkData();
                                }
                            });
                            rvList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            rvList.setAdapter(adapter);
                            etSearch.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    adapter.updateData(filter(s.toString(), stringList));
                                }
                            });
                        }
                    } else {
                        Toast.makeText(LokasiActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LokasiActivity.this, ""+response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KotaResponse> call, Throwable t) {
                Toast.makeText(LokasiActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getWilayah(int id_kota){
        namaWilayah = "Malangg";
        tvWilayah.setText("Kota dengan id "+id_kota);
        dialog.dismiss();
        checkData();
//        final List<String> stringList = new ArrayList<>();
//        Call<KotaResponse> call = service.getKota(id_provinsi);
//        call.enqueue(new Callback<KotaResponse>() {
//            @Override
//            public void onResponse(Call<KotaResponse> call, final Response<KotaResponse> response) {
//                progressBar.setVisibility(View.GONE);
//                if (response.isSuccessful()){
//                    if (response.body().getStatus().equalsIgnoreCase("ok")) {
//                        if (response.body().getData().size() > 0) {
//                            for (int i = 0; i < response.body().getData().size(); i++) {
//                                stringList.add(response.body().getData().get(i).getNama());
//                            }
//                            final TempatAdapter adapter = new TempatAdapter(getApplicationContext(), stringList, new TempatAdapter.itemClickListener() {
//                                @Override
//                                public void onItemClick(String nama) {
//                                    Toast.makeText(LokasiActivity.this, ""+nama, Toast.LENGTH_SHORT).show();
//                                    tvKota.setText(nama);
//                                }
//                            });
//                            rvList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//                            rvList.setAdapter(adapter);
//                            etSearch.addTextChangedListener(new TextWatcher() {
//                                @Override
//                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                                }
//
//                                @Override
//                                public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                                }
//
//                                @Override
//                                public void afterTextChanged(Editable s) {
//                                    adapter.updateData(filter(s.toString(), stringList));
//                                }
//                            });
//                        }
//                    } else {
//                        Toast.makeText(LokasiActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(LokasiActivity.this, ""+response.message(), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<KotaResponse> call, Throwable t) {
//                Toast.makeText(LokasiActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }


    private List<String> filter(String text, List<String> list){
        ArrayList<String> filStrings = new ArrayList<>();
        for (String item : list){
            if (item.toLowerCase().contains(text.toLowerCase())){
                filStrings.add(item);
            }
        }
        return filStrings;
    }

    private int getIdProvinsi(String text, List<Provinsi> list){
        int idProv = 0;
        for (Provinsi provinsi : list){
            if (provinsi.getNama().equalsIgnoreCase(text)){
                idProv = Integer.valueOf(provinsi.getId());
            }
        }
        return idProv;
    }

    private int getIdKota(String text, List<Kota> list){
        int idKota = 0;
        for (Kota kota : list){
            if (kota.getNama().equalsIgnoreCase(text)){
                idKota = Integer.valueOf(kota.getId());
            }
        }
        return idKota;
    }

    private void checkData(){
        if (tvProvinsi.getText().toString().equalsIgnoreCase("provinsi")){
            cvKota.setVisibility(View.GONE);
            cvWilayah.setVisibility(View.GONE);
        } else {
            if (tvKota.getText().toString().equalsIgnoreCase("kota")){
                cvKota.setVisibility(View.VISIBLE);
                cvWilayah.setVisibility(View.GONE);
            } else {
                cvKota.setVisibility(View.VISIBLE);
//                cvWilayah.setVisibility(View.VISIBLE);
            }
        }
    }

    @OnClick(R.id.btn_simpan)
    public void goSimpan(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra(NAMA_LOKASI,namaKota);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

}

package com.roma.android.sihmi.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.core.CoreApplication;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.User;
import com.roma.android.sihmi.model.database.interfaceDao.MasterDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.GeneralResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LK1Activity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_badko)
    EditText etBadko;
    @BindView(R.id.et_cabang)
    EditText etCabang;
    @BindView(R.id.et_korkom)
    EditText etKorkom;
    @BindView(R.id.et_komisariat)
    EditText etKomisariat;
    @BindView(R.id.et_tanggal)
    EditText etTanggal;
    @BindView(R.id.tv_ket)
    TextView tvKet;
    @BindView(R.id.checkbox)
    CheckBox checkBox;
    @BindView(R.id.btn_simpan)
    Button btnSimpan;

    MasterService service;
    User user;

    AppDb appDb;
    MasterDao masterDao;
    UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lk1);
        ButterKnife.bind(this);
        initToolbar();
        service = ApiClient.getInstance().getApi();
        appDb = AppDb.getInstance(this);
        masterDao = appDb.masterDao();
        userDao = appDb.userDao();
        user = userDao.getUser();
        initView();
    }

    private void initToolbar(){
        toolbar.setTitle(getString(R.string.telah_lk1_profil));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initView(){
        if (user.getBadko() != null && !user.getBadko().trim().isEmpty()) {
            Tools.setText(etBadko, user.getBadko());
            Tools.setText(etCabang, user.getCabang());
            Tools.setText(etKorkom, user.getKorkom());
            Tools.setText(etKomisariat, user.getKomisariat());
            Tools.setText(etTanggal, user.getTanggal_lk1());

            etBadko.setEnabled(false);
            etCabang.setEnabled(false);
            etKorkom.setEnabled(false);
            etKomisariat.setEnabled(false);
            etTanggal.setEnabled(false);

            if (Tools.isNonLK()) {
                tvKet.setVisibility(View.VISIBLE);
                checkBox.setVisibility(View.VISIBLE);
                btnSimpan.setVisibility(View.VISIBLE);
            } else {
                tvKet.setVisibility(View.GONE);
                checkBox.setVisibility(View.GONE);
                btnSimpan.setVisibility(View.GONE);
            }
        } else {
            etBadko.setEnabled(true);
            etCabang.setEnabled(true);
            etKorkom.setEnabled(true);
            etKomisariat.setEnabled(true);
            etTanggal.setEnabled(true);

            tvKet.setVisibility(View.VISIBLE);
            checkBox.setVisibility(View.VISIBLE);
            btnSimpan.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.et_badko, R.id.et_cabang, R.id.et_korkom, R.id.et_komisariat, R.id.et_tanggal})
    public void click(EditText editText){
        switch (editText.getId()){
            case R.id.et_badko:
                String[] badkoArray = masterDao.getMasterNameByType("1").toArray(new String[0]);
                Tools.showDialogLK1(this, badkoArray, ket -> {
                    etBadko.setText(ket);
                });
                break;
            case R.id.et_cabang:
                String[] cabangArray = masterDao.getMasterNameByType("2").toArray(new String[0]);
                Tools.showDialogLK1(this, cabangArray, ket -> {
                    etCabang.setText(ket);
                });
                break;
            case R.id.et_korkom:
                String[] korkomArray = masterDao.getMasterNameByType("3").toArray(new String[0]);
                Tools.showDialogLK1(this, korkomArray, ket -> {
                    etKorkom.setText(ket);
                });
                break;
            case R.id.et_komisariat:
                String[] komisariatArray = masterDao.getMasterNameByType("4").toArray(new String[0]);
                Tools.showDialogLK1(this, komisariatArray, ket -> {
                    etKomisariat.setText(ket);
                });
                break;
            case R.id.et_tanggal:
                Tools.showDateDialog(this, etTanggal);
                break;
        }
    }

    @OnClick(R.id.btn_simpan)
    public void click(){
        String badko = etBadko.getText().toString().trim();
        String cabang = etCabang.getText().toString().trim();
        String korkom = etKorkom.getText().toString().trim();
        String komisariat = etKomisariat.getText().toString().trim();
        String tanggal = etTanggal.getText().toString().trim();
        if (!badko.isEmpty() && !cabang.isEmpty() && !korkom.isEmpty() && !komisariat.isEmpty() && !tanggal.isEmpty() && checkBox.isChecked()) {
            if (Tools.isOnline(this)) {
                pegajuan(badko, cabang, korkom, komisariat, tanggal);
            } else {
                Tools.showToast(this, getString(R.string.tidak_ada_internet));
            }
        } else {
            Tools.showToast(this, getString(R.string.field_mandatory));
        }
    }

    private void pegajuan(String badko, String cabang, String korkom, String komisariat, String tgl){
        Tools.showProgressDialog(this, getString(R.string.pengajuan));
        Call<GeneralResponse> call = service.addPengajuanLK1(Constant.getToken(), badko, cabang, korkom, komisariat, tgl, "");
        call.enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                Tools.dissmissProgressDialog();
                if (response.isSuccessful()){
                    if (response.body().getStatus().equalsIgnoreCase("ok")){
                        Tools.showToast(LK1Activity.this, getString(R.string.pengajuan_berhasil));
                        finish();
                    } else {
                        Tools.showToast(LK1Activity.this, getString(R.string.pengajuan_gagal));
                    }
                } else {
                    Tools.showToast(LK1Activity.this, getString(R.string.pengajuan_gagal));
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Tools.dissmissProgressDialog();
                Tools.showToast(LK1Activity.this, getString(R.string.pengajuan_gagal));
            }
        });
    }
}

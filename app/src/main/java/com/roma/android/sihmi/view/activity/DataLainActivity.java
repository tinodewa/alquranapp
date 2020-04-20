package com.roma.android.sihmi.view.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.core.CoreApplication;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Pendidikan;
import com.roma.android.sihmi.model.database.entity.Training;
import com.roma.android.sihmi.model.database.entity.User;
import com.roma.android.sihmi.model.database.interfaceDao.MasterDao;
import com.roma.android.sihmi.model.database.interfaceDao.TrainingDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.GeneralResponse;
import com.roma.android.sihmi.model.response.PendidikanResponse;
import com.roma.android.sihmi.model.response.TrainingResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.adapter.PendidikanAdapter;
import com.roma.android.sihmi.view.adapter.TrainingAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataLainActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_tempat_lahir)
    EditText etTempatLahir;
    @BindView(R.id.et_tanggal_lahir)
    EditText etTglLahir;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_status)
    EditText etStatus;
    @BindView(R.id.et_akun)
    EditText etAkun;
    @BindView(R.id.rv_pelatihan)
    RecyclerView rvPelatihan;
    @BindView(R.id.rv_pendidikan)
    RecyclerView rvPendidikan;

    MasterService service;
    AppDb appDb;
    UserDao userDao;
    TrainingDao trainingDao;
    MasterDao masterDao;
    User user;

    TrainingAdapter trainingAdapter;
    PendidikanAdapter pendidikanAdapter;

    List<Training> trainings = new ArrayList();
    List<Pendidikan> pendidikans = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_lain);
        ButterKnife.bind(this);
        initToolbar();
        initModule();
        initView();
        initAdapter();
    }

    private void initToolbar(){
        toolbar.setTitle(getString(R.string.data_lain_profil));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initModule(){
        service = ApiClient.getInstance().getApi();
        appDb = AppDb.getInstance(this);
        trainingDao = appDb.trainingDao();
        masterDao = appDb.masterDao();
        userDao = appDb.userDao();
        user = userDao.getUser();

        getDataPendidikan();
        getDataTraining();

        trainingDao.getAllTrainingByUser(user.get_id()).observe(this, list -> trainingAdapter.updateData(list));
    }

    private void initView(){
        setText(etTglLahir, user.getTanggal_lahir());
        setText(etTempatLahir, user.getTempat_lahir());
        setText(etEmail, user.getEmail());
        setText(etStatus, user.getStatus_perkawinan());
        setText(etAkun, user.getAkun_sosmed());
    }

    private void initAdapter(){
        trainings = trainingDao.getListAllTrainingByUser(user.get_id());
        trainingAdapter = new TrainingAdapter(this, trainings, id -> {
            if (id.equalsIgnoreCase(Constant.FAB_ADD)){
                dialogAddDataTraining();
            }
        });
        rvPelatihan.setLayoutManager(new LinearLayoutManager(this));
        rvPelatihan.setAdapter(trainingAdapter);

        pendidikanAdapter = new PendidikanAdapter(this, pendidikans, id -> {
            if (id.equalsIgnoreCase(Constant.FAB_ADD)){
                dialogAddDataPendidikan();
            }
        });
        rvPendidikan.setLayoutManager(new LinearLayoutManager(this));
        rvPendidikan.setAdapter(pendidikanAdapter);

    }

    @OnClick(R.id.btn_simpan)
    public void click(){
        // halaman ini menyimpan tempat lahir, tanggal lahir, email, status perkawinan, Akun Sosmed
        if (!etTempatLahir.getText().toString().trim().isEmpty() && !etTglLahir.getText().toString().trim().isEmpty() &&  !etStatus.getText().toString().trim().isEmpty() && !etAkun.getText().toString().trim().isEmpty()){
            saveProfile(etTempatLahir.getText().toString(), etTglLahir.getText().toString(), etStatus.getText().toString(), etAkun.getText().toString());
        } else {
            Tools.showToast(this, getString(R.string.field_mandatory));
        }

    }

    @OnClick({R.id.et_tanggal_lahir, R.id.et_status})
    public void showDate(EditText editText){
        switch (editText.getId()){
            case R.id.et_tanggal_lahir:
                Tools.showDateDialog(this, etTglLahir);
                break;
            case R.id.et_status:
                Tools.showDialogStatus(this, ket -> {
                    etStatus.setText(ket);
                });
                break;
        }
    }

    private void saveProfile(String tempat_lhr, String tgl_lhr, String stts, String akun_sosmed){
        if (Tools.isOnline(this)) {
            Tools.showProgressDialog(this, getString(R.string.menyimpan_profile));

            Call<GeneralResponse> call = service.updateProfile(Constant.getToken(), user.getBadko(), user.getCabang(), user.getKorkom(), user.getKomisariat(), user.getId_roles(), user.getImage(), user.getNama_depan(), user.getNama_belakang(),
                    user.getNama_panggilan(), user.getJenis_kelamin(), user.getNomor_hp(), user.getAlamat(), user.getUsername(),
                    tempat_lhr, tgl_lhr, stts, "", user.getEmail(), akun_sosmed, user.getDomisili_cabang(), user.getPekerjaan(), user.getJabatan(), user.getAlamat_kerja(), user.getKontribusi());
            call.enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equalsIgnoreCase("ok")) {
                            Tools.showToast(DataLainActivity.this, getString(R.string.berhasil_update));
                            setResult(Activity.RESULT_OK);
                            finish();
                        } else {
                            Tools.showDialogAlert(DataLainActivity.this, response.body().getMessage());
                        }
                    } else {
                        Tools.showToast(DataLainActivity.this, getString(R.string.gagal_daftar));
                    }
                    Tools.dissmissProgressDialog();
                }

                @Override
                public void onFailure(Call<GeneralResponse> call, Throwable t) {
                    Tools.showToast(DataLainActivity.this, getString(R.string.gagal_daftar));
                    Tools.dissmissProgressDialog();
                }
            });
        } else {
            Tools.showToast(DataLainActivity.this, getString(R.string.tidak_ada_internet));
        }
    }

    private void setText(EditText editText, String text){
        if (text != null && !text.trim().isEmpty()){
            editText.setText(text);
        }
    }

    private void getDataPendidikan(){
        Call<PendidikanResponse> call = service.getPendidikan(Constant.getToken(), user.get_id());
        call.enqueue(new Callback<PendidikanResponse>() {
            @Override
            public void onResponse(Call<PendidikanResponse> call, Response<PendidikanResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        pendidikanAdapter.updateData(response.body().getData());
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<PendidikanResponse> call, Throwable t) {

            }
        });
    }

    private void dialogAddDataPendidikan(){
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_pendidikan, null);
        final EditText etTahun = dialogView.findViewById(R.id.et_thn_masuk);
        final EditText etJenjang = dialogView.findViewById(R.id.et_jenjang);
        final Spinner spJenjang = dialogView.findViewById(R.id.sp_jenjang);
        final EditText etNama = dialogView.findViewById(R.id.et_nama);
        final EditText etFakultas = dialogView.findViewById(R.id.et_fakultas);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton(getString(R.string.simpan), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!etNama.getText().toString().trim().isEmpty() && !etFakultas.getText().toString().trim().isEmpty()) {
                            addDataPendidikan(etTahun.getText().toString(), String.valueOf(spJenjang.getSelectedItem()), etNama.getText().toString(), etFakultas.getText().toString());
//                        addDataPendidikan(etTahun.getText().toString(), String.valueOf(spJenjang.getSelectedItem()), etNama.getText().toString(), etJurusan.getText().toString());
                        } else {
                            Tools.showToast(getApplicationContext(), getString(R.string.field_mandatory));
                        }
                    }
                })
                .setNegativeButton(getString(R.string.batal), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg) {
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorTextDark));
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });
        dialog.show();
    }

    private void addDataPendidikan(String tahun, String strata, String kampus, String jurusan){
        if (!tahun.isEmpty() && !strata.isEmpty() && !kampus.isEmpty() && !jurusan.isEmpty()) {
            user.setKomisariat(kampus);
            userDao.insertUser(user);
            Call<GeneralResponse> call = service.addPendidikan(Constant.getToken(), tahun, strata, kampus, jurusan);
            call.enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equalsIgnoreCase("success")) {
                            Tools.showToast(DataLainActivity.this, getString(R.string.berhasil_tambah));
                            getDataPendidikan();
                        } else {
                            Tools.showToast(DataLainActivity.this, response.body().getMessage());
                        }
                    } else {
                        Tools.showToast(DataLainActivity.this, getString(R.string.gagal_tambah));
                    }
                }

                @Override
                public void onFailure(Call<GeneralResponse> call, Throwable t) {
                    Tools.showToast(DataLainActivity.this, getString(R.string.gagal_tambah));
                }
            });
        } else {
            Tools.showToast(DataLainActivity.this, getString(R.string.gagal_tambah));
        }
    }

    private void getDataTraining(){
        Call<TrainingResponse> call = service.getTraining(Constant.getToken(), user.get_id());
        call.enqueue(new Callback<TrainingResponse>() {
            @Override
            public void onResponse(Call<TrainingResponse> call, Response<TrainingResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        List<Training> trainingList = response.body().getData();
                        for (int i = 0; i < trainingList.size(); i++) {
                            Training training = trainingList.get(i);
                            training.setId(training.getId_user()+training.getNama());
                            training.setId_user(training.getId_user());
                            training.setCabang(user.getCabang());
                            training.setKomisariat(user.getKomisariat());
                            training.setDomisili_cabang(user.getDomisili_cabang());
                            training.setJenis_kelamin(user.getJenis_kelamin());
                            trainingDao.insertTraining(training);
                        }
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<TrainingResponse> call, Throwable t) {

            }
        });
    }

    private void dialogAddDataTraining(){
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_training, null);
        final EditText etTipe = dialogView.findViewById(R.id.etTipe);
        final Spinner spTipe = dialogView.findViewById(R.id.sp_training);
        final EditText etTahun = dialogView.findViewById(R.id.etTahun);
        final EditText etLokasi = dialogView.findViewById(R.id.etLokasi);
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton(getString(R.string.simpan), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addDataTraining(String.valueOf(spTipe.getSelectedItem()), etTahun.getText().toString(), spTipe.getSelectedItem().toString(), etLokasi.getText().toString());
//                        trainings.add(new Training(trainings.size(), etTipe.getText().toString(), etTahun.getText().toString(), etNama.getText().toString(), etLokasi.getText().toString()));
//                        Log.d("tessss", "onClick: "+trainings.get(0).getNama());
//                        trainingAdapter.updateData(trainings);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.batal), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .create();

        dialog.setOnShowListener(arg -> {
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorTextDark));
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        });
        dialog.show();

        List<String> strings = masterDao.getMasterTraining();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, strings);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        spTipe.setAdapter(adapter);

    }

    private void addDataTraining(String tipe, String tahun, String nama, String lokasi){
        if (!tipe.isEmpty() && !tahun.isEmpty() && !nama.isEmpty() && !lokasi.isEmpty()) {
            Call<GeneralResponse> call = service.addTraining(Constant.getToken(), tipe, tahun, nama, lokasi);
            call.enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equalsIgnoreCase("success")) {
                            Tools.showToast(DataLainActivity.this, getString(R.string.berhasil_tambah));
                            getDataTraining();
                        } else {
                            Tools.showToast(DataLainActivity.this, response.body().getMessage());
                        }
                    } else {
                        Tools.showToast(DataLainActivity.this, getString(R.string.gagal_tambah));

                    }
                }

                @Override
                public void onFailure(Call<GeneralResponse> call, Throwable t) {
                    Tools.showToast(DataLainActivity.this, getString(R.string.gagal_tambah));

                }
            });
        } else {
            Tools.showToast(DataLainActivity.this, getString(R.string.field_mandatory));
        }
    }
}

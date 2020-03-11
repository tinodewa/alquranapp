package com.roma.android.sihmi.view.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.core.CoreApplication;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.User;
import com.roma.android.sihmi.model.database.interfaceDao.LevelDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.GeneralResponse;
import com.roma.android.sihmi.model.response.PengajuanAdminResponse;
import com.roma.android.sihmi.model.response.UploadFileResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.utils.UploadFile;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PermohonanAdminActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_type)
    EditText etType;
    @BindView(R.id.et_hak_akses)
    EditText etHakAkses;
    @BindView(R.id.et_unggah)
    EditText etUnggah;
    @BindView(R.id.img_attach)
    ImageView imgAttach;
    @BindView(R.id.iv_image)
    ImageView ivImage;
    @BindView(R.id.checkbox)
    CheckBox checkBox;
    @BindView(R.id.btn_simpan)
    Button btnSimpan;

    User user;
    String urlFile;
    MasterService service;
    AppDb appDb;
    UserDao userDao;
    LevelDao levelDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permohonan_admin);
        ButterKnife.bind(this);
        initToolbar();

        service = ApiClient.getInstance().getApi();
        appDb = AppDb.getInstance(this);
        userDao = appDb.userDao();
        levelDao = appDb.levelDao();
        user = userDao.getUser();
        etType.setText(user.getKomisariat());
        etType.setTextColor(getResources().getColor(R.color.colorBlack));
        etType.setBackground(getResources().getDrawable(R.color.colorTextLight));
        etType.setEnabled(false);

        getPengajuan();
    }

    private void initToolbar(){
        toolbar.setTitle(getString(R.string.permohonan_admin_profil));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @OnClick(R.id.et_hak_akses)
    public void clickHakAkses(){
        Tools.showDialogAdmin(this, ket -> {
            etHakAkses.setText(ket);
        });
    }

    @OnClick({R.id.img_attach, R.id.iv_image})
    public void click(ImageView imageView){
        switch (imageView.getId()){
            case R.id.img_attach:
                UploadFile.selectFile(PermohonanAdminActivity.this);
                break;
            case R.id.iv_image:
                UploadFile.selectFile(PermohonanAdminActivity.this);
                break;
        }
    }

    @OnClick(R.id.btn_simpan)
    public void click(){
        if (!etHakAkses.getText().toString().isEmpty() && !etUnggah.getText().toString().trim().isEmpty() && checkBox.isChecked()){
            addPengajuan(levelDao.getIdRoles(etHakAkses.getText().toString()), urlFile);
        } else {
            Toast.makeText(this, getString(R.string.field_kosong), Toast.LENGTH_SHORT).show();
        }
    }

    private void addPengajuan(String idRoles, String file){
        Call<GeneralResponse> call = service.addPengajuanAdmin(Constant.getToken(), idRoles, file);
        call.enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful()){
                    if (response.body().getStatus().equalsIgnoreCase("ok")){
                        Tools.showToast(PermohonanAdminActivity.this, getString(R.string.pengajuan_berhasil));
                    } else {
                        Tools.showToast(PermohonanAdminActivity.this, response.body().getMessage());
                    }
                } else {
                    Tools.showToast(PermohonanAdminActivity.this, getString(R.string.pengajuan_gagal));
                }
                finish();
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Tools.showToast(PermohonanAdminActivity.this, getString(R.string.pengajuan_gagal));
                finish();

            }
        });
    }

    private void getPengajuan(){
        Call<PengajuanAdminResponse> call = service.getPengajuanAdmin(Constant.getToken());
        call.enqueue(new Callback<PengajuanAdminResponse>() {
            @Override
            public void onResponse(Call<PengajuanAdminResponse> call, Response<PengajuanAdminResponse> response) {
                if (response.isSuccessful()){
                    if (response.body().getStatus().equalsIgnoreCase("success")){

                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<PengajuanAdminResponse> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_DOC_CODE && resultCode == Activity.RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            sendFile(Constant.DOCUMENT, filePath);
        }
    }

    private void sendFile(String type, String filePath){
        if (Tools.isOnline(this)) {
            String msg;
            if (type.equals(Constant.IMAGE)){
                msg = getString(R.string.mengunggah_gambar);
            } else {
                msg = getString(R.string.mengunggah_dok);
            }
            Tools.showProgressDialog(PermohonanAdminActivity.this, msg);
            UploadFile.uploadFileToServer(type, filePath, new Callback<UploadFileResponse>() {
                @Override
                public void onResponse(Call<UploadFileResponse> call, Response<UploadFileResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equalsIgnoreCase("ok")) {
                            if (response.body().getData().size() > 0) {
                                etUnggah.setText(response.body().getData().get(0).getNama_file());
                                urlFile = response.body().getData().get(0).getUrl();
                            }
                        }
                    }
                    Tools.dissmissProgressDialog();
                }

                @Override
                public void onFailure(Call<UploadFileResponse> call, Throwable t) {
                    Tools.dissmissProgressDialog();
                }
            });
        } else {
            Tools.showToast(PermohonanAdminActivity.this, getString(R.string.tidak_ada_internet));
        }
    }
}

package com.roma.android.sihmi.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.roma.android.sihmi.R;
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

public class RegisterActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_nama_lengkap)
    EditText etNamaLengkap;
    @BindView(R.id.et_nama_panggilan)
    EditText etNamaPanggilan;
    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_confirm_password)
    EditText etConfirmPassword;
    @BindView(R.id.et_nohp)
    EditText etHp;
    @BindView(R.id.et_jenis_kelamin)
    EditText etJenisKelamin;
    @BindView(R.id.checkbox)
    CheckBox checkBox;
    @BindView(R.id.tv_login)
    TextView tvLogin;
    @BindView(R.id.btn_register)
    Button btnRegister;

    MasterService service;
    ProgressDialog dialog;
    String jk = "404";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        initModule();
        initToolbar();
        tvLogin.setTypeface(tvLogin.getTypeface(), Typeface.BOLD);
    }

    private void initModule(){
        service = ApiClient.getInstance().getApi();
    }

    private void initToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.daftar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    @OnClick(R.id.btn_register)
    public void goRegister(){
        String namaLengkap = etNamaLengkap.getText().toString();
        String namaPanggilan = etNamaPanggilan.getText().toString();
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String confrimPassword = etConfirmPassword.getText().toString();
        String noHp = etHp.getText().toString();
        String jenisKelamin = etJenisKelamin.getText().toString();
        String roles = Constant.NON_LK;

        if (checkBox.isChecked() && (!namaLengkap.isEmpty() && !namaPanggilan.isEmpty() && !username.isEmpty() && !password.isEmpty() && !confrimPassword.isEmpty() && !noHp.isEmpty() && !jenisKelamin.isEmpty())){
            if (password.length() < 6){
                Tools.showToast(RegisterActivity.this, getString(R.string.pass_min_karakter));
            } else if (!password.equals(confrimPassword)){
                Tools.showToast(RegisterActivity.this, getString(R.string.pass_tidak_sesuai));
            }
//            else if (!Tools.isValidEmail(email)){
//                Tools.showToast(RegisterActivity.this, getString(R.string.email_not_valid));
//            }
            else {
                register(namaLengkap, namaPanggilan, noHp, jenisKelamin, username, password, roles);
            }
        } else {
            Tools.showToast(RegisterActivity.this, getString(R.string.field_mandatory));
        }
    }

    @OnClick(R.id.tv_login)
    public void goLogin(){
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }

    @OnClick(R.id.et_jenis_kelamin)
    public void goGender(){
        Tools.showDialogGender(RegisterActivity.this, ket -> {
            jk = ket;
            String gender;
            if (ket.equals("0")){
                gender = "Laki - laki";
            } else {
                gender = "Perempuan";
            }
            etJenisKelamin.setText(gender);
        });
    }

    private void register(String namaLengkap, String namaPanggilan, String nohp, String jenisKelamin, String username, String password, String roles){
        if (Tools.isOnline(this)) {
            dialog = new ProgressDialog(RegisterActivity.this);
            dialog.setMessage("Register...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();

            Call<GeneralResponse> call = service.register("", "", "", "", roles, "", namaLengkap, "", namaPanggilan, jk, nohp, "", username, password,
                    "", "", "", "");
            call.enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                    dialog.dismiss();
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equalsIgnoreCase("ok")) {
                            Tools.showToast(RegisterActivity.this, getString(R.string.berhasil_daftar));
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        } else {
                            Tools.showDialogAlert(RegisterActivity.this, response.body().getMessage());
                        }
                    } else {
                        Tools.showToast(RegisterActivity.this, getString(R.string.gagal_daftar));
                    }
                }

                @Override
                public void onFailure(Call<GeneralResponse> call, Throwable t) {
                    dialog.dismiss();
                    Tools.showToast(RegisterActivity.this, getString(R.string.gagal_daftar));
                }
            });
        } else {
            Tools.showToast(RegisterActivity.this, getString(R.string.tidak_ada_internet));
        }
    }
}
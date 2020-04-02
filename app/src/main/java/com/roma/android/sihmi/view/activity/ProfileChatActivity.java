package com.roma.android.sihmi.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.core.CoreApplication;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.entity.PengajuanHistory;
import com.roma.android.sihmi.model.database.entity.Training;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.HistoryPengajuanDao;
import com.roma.android.sihmi.model.database.interfaceDao.LevelDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.GeneralResponse;
import com.roma.android.sihmi.model.response.TrainingResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.adapter.PelatihanOtherUserAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileChatActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_photo)
    ImageView ivPhoto;
    @BindView(R.id.iv_initial)
    ImageView ivInitial;
    @BindView(R.id.et_fullname)
    EditText etFullname;
    @BindView(R.id.et_jenis_kelamin)
    EditText etJenisKelamin;
    @BindView(R.id.et_komisariat)
    EditText etKomisariat;
    @BindView(R.id.tv_pelatihan)
    TextView tvPelatihan;
    @BindView(R.id.rv_pelatihan)
    RecyclerView rvPelatihan;
    @BindView(R.id.iv_pdf)
    ImageView ivPdf;
    @BindView(R.id.tv_ket)
    TextView tvKet;

    Contact otherUser;
    MasterService service;

    List<Training> list;
    PelatihanOtherUserAdapter adapter;
    String idPengajuan = "", filePdf = "";

    AppDb appDb;
    ContactDao contactDao;
    UserDao userDao;
    HistoryPengajuanDao historyPengajuanDao;
    LevelDao levelDao;
    Boolean requestMode, acceptedMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_chat);
        ButterKnife.bind(this);

        service = ApiClient.getInstance().getApi();
        appDb = AppDb.getInstance(this);
        contactDao = appDb.contactDao();
        userDao = appDb.userDao();
        historyPengajuanDao = appDb.historyPengajuanDao();
        levelDao = appDb.levelDao();

        otherUser = contactDao.getContactById(getIntent().getStringExtra("iduser"));
        requestMode = getIntent().getBooleanExtra("MODE_REQUEST", false);
        acceptedMode = getIntent().getBooleanExtra("MODE_ACCEPTED", false);
        idPengajuan = getIntent().getStringExtra("idpengajuan");

        if (idPengajuan != null && !idPengajuan.isEmpty()){
            filePdf = historyPengajuanDao.getFilePengajuanHistory(idPengajuan);
            if (filePdf != null && !filePdf.isEmpty()){
                PengajuanHistory pengajuanHistory = historyPengajuanDao.getPengajuanHistoryById(idPengajuan);
                String pengajuan = levelDao.getNamaLevel(pengajuanHistory.getId_roles());
                ivPdf.setVisibility(View.VISIBLE);
                tvKet.setVisibility(View.VISIBLE);
                tvKet.setText(pengajuan+" "+getString(R.string.oleh)+" "+otherUser.getUsername());
            } else {
                ivPdf.setVisibility(View.GONE);
                tvKet.setVisibility(View.GONE);
            }
        } else {
            ivPdf.setVisibility(View.GONE);
            tvKet.setVisibility(View.GONE);
        }

        initToolbar();
        initView();
        initAdapter();
        getDataPelatihan();

        tvPelatihan.setVisibility(View.GONE);
    }

    @OnClick(R.id.iv_pdf)
    public void clickPdf(){
        startActivity(new Intent(ProfileChatActivity.this, FileDetailActivity.class)
                .putExtra("file", filePdf)
                .putExtra("_id", idPengajuan)
                .putExtra("judul", "SK USER"));
    }

    private void initToolbar(){
        toolbar.setTitle(otherUser.getFullName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initView() {
        etFullname.setText(otherUser.getFullName());
        etJenisKelamin.setText(otherUser.getGender());
        etKomisariat.setText(otherUser.getKomisariat());
        if (otherUser.getImage() != null && !otherUser.getImage().trim().isEmpty()) {
            ivPhoto.setVisibility(View.VISIBLE);
            ivInitial.setVisibility(View.GONE);
            Glide.with(this).load(otherUser.getImage()).into(ivPhoto);
        } else {
            ivPhoto.setVisibility(View.GONE);
            ivInitial.setVisibility(View.VISIBLE);
            Tools.initial(ivInitial, otherUser.getFullName());
        }
    }

    @OnClick(R.id.iv_photo)
    public void click(){
        startActivity(new Intent(ProfileChatActivity.this, ChatFileDetailActivity.class).putExtra(ChatFileDetailActivity.NAMA_FILE, otherUser.getImage()).putExtra(ChatFileDetailActivity.TYPE_FILE, Constant.IMAGE));
    }

    private void initAdapter(){
        list = new ArrayList<>();
        adapter = new PelatihanOtherUserAdapter(this, list);
        rvPelatihan.setLayoutManager(new LinearLayoutManager(this));
        rvPelatihan.setHasFixedSize(true);
        rvPelatihan.setAdapter(adapter);
    }

    private void getDataPelatihan(){
        Call<TrainingResponse> call = service.getTraining(CoreApplication.get().getConstant().getToken(), otherUser.get_id());
        call.enqueue(new Callback<TrainingResponse>() {
            @Override
            public void onResponse(Call<TrainingResponse> call, Response<TrainingResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        adapter.updateData(response.body().getData());
                        if (response.body().getData().size()>0){
                            tvPelatihan.setVisibility(View.VISIBLE);
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem;
        if (requestMode || acceptedMode) {
            getMenuInflater().inflate(R.menu.change_admin, menu);

            if (requestMode) {
                menuItem = menu.findItem(R.id.action_2);
            }
            else {
                menuItem = menu.findItem(R.id.action_1);
            }
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_1:
                Tools.showDialogCustom(this, "Batalkan Permintaan", "Apakah Anda yakin ingin membatalkan permintaan ini?", "batal", ket -> {
                    rejectRequest(otherUser.get_id(), idPengajuan);
                });
                break;
            case R.id.action_2:
                Tools.showDialogCustom(this, "Konfirmasi", getString(R.string.konfirm_akses_ket), Constant.LIHAT, ket -> {
                    showDialogPilihan();
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialogPilihan(){
        String[] grpName = {"Admin Komisariat", "Admin BPL", "Admin Alumni", "Admin Cabang", "Admin PBHMI", "Admin Nasional"};
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                .setSingleChoiceItems(grpName, -1, (dialog1, which) -> {
            int level;
            if (which == 0){
                level = Constant.USER_ADMIN_1;
            } else if (which == 1){
                level = Constant.USER_ADMIN_2;
            } else if (which == 2) {
                level = Constant.USER_ADMIN_3;
            } else if (which == 3) {
                level = Constant.USER_LOW_ADMIN_1;
            } else if (which == 4) {
                level = Constant.USER_LOW_ADMIN_2;
            } else {
                level = Constant.USER_SECOND_ADMIN;
            }
            changeRole(otherUser.get_id(), level, new Callback<GeneralResponse>() {
                @Override
                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                    if (response.isSuccessful()) {
                        otherUser.setId_level(level);
                        otherUser.setId_roles(levelDao.getIdRoles(level));
                        contactDao.insertContact(otherUser);
                        sendNotif(otherUser.get_id(), "-1");
                        Tools.showToast(ProfileChatActivity.this, "Berhasil menjadikan admin");
                        finish();
                    }
                    else {
                        Tools.showToast(ProfileChatActivity.this, "Gagal menjadikan admin");
                    }
                }

                @Override
                public void onFailure(Call<GeneralResponse> call, Throwable t) {
                    Tools.showToast(ProfileChatActivity.this, "Gagal menjadikan admin");
                }
            });
            dialog1.dismiss();
        }).setCancelable(true).create();
        dialog.show();
    }

    private void sendNotif(String user, String status) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("from", userDao.getUser().get_id());
        hashMap.put("to", user);
        hashMap.put("status", status.trim());
        hashMap.put("time", System.currentTimeMillis());
        hashMap.put("isshow", false);
        hashMap.put("type", "User");

        databaseReference.child("Notification").push().setValue(hashMap);
    }

    private void rejectRequest(String idUser, String id_pengajuan) {
        PengajuanHistory pengajuanHistory = historyPengajuanDao.getPengajuanHistoryById(id_pengajuan);
        Tools.showProgressDialog(ProfileChatActivity.this, getString(R.string.membatalkan_permintaan));
        int level = pengajuanHistory.getLevel();
        int levelBefore = levelDao.getLevel(otherUser.getId_roles());
        Call<GeneralResponse> call;
        if (level == 2) {
            // Pengajuan LK1
            call = service.updatePengajuanLK1(Constant.getToken(), id_pengajuan, "-1");
        }
        else {
            call = service.updatePengajuanAdmin(Constant.getToken(), id_pengajuan, "-1");
        }
        call.enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                Tools.dissmissProgressDialog();
                if (response.isSuccessful()) {
                    changeRole(idUser, levelBefore, new Callback<GeneralResponse>() {
                        @Override
                        public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                            if (response.isSuccessful()) {
                                PengajuanHistory pengajuanHistory = historyPengajuanDao.getPengajuanHistoryById(id_pengajuan);
                                pengajuanHistory.setStatus(-1);
                                historyPengajuanDao.insertPengajuanHistory(pengajuanHistory);
                                sendNotif(idUser, "-1");
                                Tools.showToast(ProfileChatActivity.this, "Berhasil membatalkan");
                                finish();
                            }
                            else {
                                Tools.showToast(ProfileChatActivity.this, getString(R.string.gagal_membatalkan));
                            }
                        }

                        @Override
                        public void onFailure(Call<GeneralResponse> call, Throwable t) {
                            Tools.showToast(ProfileChatActivity.this, getString(R.string.gagal_membatalkan));
                        }
                    });
                } else {
                    Log.d("PROFILE REJECT ERROR", "PROFILE REJECT ERROR" + response.message());
                    Tools.showToast(ProfileChatActivity.this, getString(R.string.gagal_membatalkan));
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Tools.dissmissProgressDialog();
                Log.d("PROFILE REJECT ERROR", "PROFILE REJECT ERROR");
                Tools.showToast(ProfileChatActivity.this, getString(R.string.gagal_membatalkan));
            }
        });
    }

    private void changeRole(String idUser, int level, Callback<GeneralResponse> callback) {
        Call<GeneralResponse> call = service.updateUserLevel(Constant.getToken(), idUser, level);
        call.enqueue(callback);
    }

}

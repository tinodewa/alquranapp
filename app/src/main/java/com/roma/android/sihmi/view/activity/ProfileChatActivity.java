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
    Boolean requestMode;

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
        if (requestMode) {
            getMenuInflater().inflate(R.menu.change_admin, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_1:
                Tools.showDialogCustom(ProfileChatActivity.this, "Batalkan Permintaan", "Apakah Anda yakin ingin membatalkan permintaan ini?", "batal", ket -> {
                    rejectRequest(otherUser.get_id(), idPengajuan);
                });
                break;
        }
        return super.onOptionsItemSelected(item);
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
        String idRoles = otherUser.getId_roles();
        int level = levelDao.getLevel(idRoles);
        Tools.showProgressDialog(ProfileChatActivity.this, getString(R.string.membatalkan_permintaan));
        Call<GeneralResponse> call = service.updateUserLevel(Constant.getToken(), idUser, level);
        call.enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                Tools.dissmissProgressDialog();
                if (response.isSuccessful()) {
                    Log.d("halloo", "onResponse: " + idRoles);
                    PengajuanHistory pengajuanHistory = new PengajuanHistory(id_pengajuan, idRoles, "", idUser, "", 0, 0, -1, "", level);
                    pengajuanHistory.setNama(contactDao.getContactById(idUser).getNama_depan());
                    historyPengajuanDao.insertPengajuanHistory(pengajuanHistory);
                    sendNotif(idUser, "-1");
                    historyPengajuanDao.updatePengajuanUser(id_pengajuan, idRoles);
                    finish();
                } else {
                    Tools.showToast(ProfileChatActivity.this, getString(R.string.gagal_membatalkan));
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Tools.dissmissProgressDialog();
                Tools.showToast(ProfileChatActivity.this, getString(R.string.gagal_membatalkan));
            }
        });
    }
}

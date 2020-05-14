package com.roma.android.sihmi.view.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.core.CoreApplication;
import com.roma.android.sihmi.helper.AgendaScheduler;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.InterfaceDao;
import com.roma.android.sihmi.model.database.interfaceDao.MasterDao;
import com.roma.android.sihmi.model.database.interfaceDao.TrainingDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.AgendaResponse;
import com.roma.android.sihmi.model.response.KonstituisiResponse;
import com.roma.android.sihmi.model.response.SejarahResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.adapter.LaporanUserAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.TEXT_ALIGNMENT_TEXT_END;

public class LaporanDetailActivity extends BaseActivity {
    public static String TYPE_LAPORAN = "type_laporan";
    public static String NAMA_LAPORAN = "nama_laporan";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_judul)
    TextView tvJudul;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.tv_col_1)
    TextView tvCol1;
    @BindView(R.id.tv_col_2)
    TextView tvCol2;
    @BindView(R.id.tv_col_3)
    TextView tvCol3;
    @BindView(R.id.tv_col_4)
    TextView tvCol4;
    @BindView(R.id.tv_col_5)
    TextView tvCol5;

    int type;
    String name_laporan="";
    String judul;
    LaporanUserAdapter adapter;

    MasterService service;
    List<Contact> list = new ArrayList<>();

    AppDb appDb;
    UserDao userDao;
    ContactDao contactDao;
    InterfaceDao interfaceDao;
    MasterDao masterDao;
    TrainingDao trainingDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_detail);
        ButterKnife.bind(this);

        service = ApiClient.getInstance().getApi();
        appDb = AppDb.getInstance(this);
        userDao = appDb.userDao();
        interfaceDao = appDb.interfaceDao();
        contactDao = appDb.contactDao();
        masterDao = appDb.masterDao();
        trainingDao = appDb.trainingDao();

        toolbar.setTitle("Laporan".toUpperCase());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        type = getIntent().getIntExtra(TYPE_LAPORAN, 0);
        name_laporan = getIntent().getStringExtra(NAMA_LAPORAN);

        Log.d("hallooooo", "onCreate: "+name_laporan);
        getViewMore(type);

    }

    private void getViewMore(int type){
        if (type == Constant.LAP_AKTIVITAS_PENGGUNA){
            tvCol1.setText("Nama");
            tvCol2.setText("Tgl Daftar");
            tvCol3.setText("Waktu Login");
            tvCol4.setVisibility(GONE);
            tvCol5.setVisibility(GONE);
            judul = getString(R.string.data_aktivitas);
            adapter = new LaporanUserAdapter(this, contactDao.getAllListContact(), Constant.LAP_AKTIVITAS_PENGGUNA, true);
        } else if (type == Constant.LAP_MEDIA_PENGGUNA){
            tvCol1.setText("Nama");
            tvCol2.setText("Device");
            tvCol3.setVisibility(GONE);
            tvCol4.setVisibility(GONE);
            tvCol5.setVisibility(GONE);
            judul = getString(R.string.data_media);
            adapter = new LaporanUserAdapter(this, contactDao.getAllListContact(), Constant.LAP_MEDIA_PENGGUNA, true);
        } else if (type == Constant.LAP_ADMIN_AKTIF){
            tvCol1.setText("Nama");
            tvCol2.setText("Admin");
            tvCol3.setText("Status");
            tvCol4.setVisibility(GONE);
            tvCol5.setVisibility(GONE);
            judul = getString(R.string.data_admin_aktif);
            adapter = new LaporanUserAdapter(this, contactDao.getListAdmin(), Constant.LAP_ADMIN_AKTIF, true);
        }
        else if (type == Constant.LAP_KONTEN_AGENDA){
            tvCol1.setText("Agenda");
            tvCol2.setText("Admin");
            tvCol2.setTextAlignment(TEXT_ALIGNMENT_TEXT_END);
            tvCol3.setVisibility(GONE);
            tvCol4.setVisibility(GONE);
            tvCol5.setVisibility(GONE);
            judul = getString(R.string.konten_agenda);
            adapter = new LaporanUserAdapter(this, list, Constant.LAP_KONTEN_AGENDA,true);
            getAgenda();
        } else if (type == Constant.LAP_KONTEN_SEJARAH){
            tvCol1.setText("Judul");
            tvCol2.setText("Copy Writer");
            tvCol2.setTextAlignment(TEXT_ALIGNMENT_TEXT_END);
            tvCol3.setVisibility(GONE);
            tvCol4.setVisibility(GONE);
            tvCol5.setVisibility(GONE);
            judul = getString(R.string.konten_sejarah);
            adapter = new LaporanUserAdapter(this, list, Constant.LAP_KONTEN_SEJARAH,true);
            getSejarah();
        } else if (type == Constant.LAP_KONTEN_KONSTITUSI){
            tvCol1.setText("Judul");
            tvCol2.setText("Copy Writer");
            tvCol2.setTextAlignment(TEXT_ALIGNMENT_TEXT_END);
            tvCol3.setVisibility(GONE);
            tvCol4.setVisibility(GONE);
            tvCol5.setVisibility(GONE);
            judul = getString(R.string.konten_konstitusi);
            adapter = new LaporanUserAdapter(this, list, Constant.LAP_KONTEN_KONSTITUSI,true);
            getKonstitusi();
        }
//        else if (type == Constant.LAP_KADER){
//            tvCol1.setText("Tahun");
//            tvCol2.setText("Jumlah");
//            tvCol3.setText("Kader");
//            tvCol4.setText("Non Kader");
//            tvCol2.setTextAlignment(TEXT_ALIGNMENT_TEXT_END);
//            tvCol3.setTextAlignment(TEXT_ALIGNMENT_TEXT_END);
//            tvCol4.setTextAlignment(TEXT_ALIGNMENT_TEXT_END);
//            tvCol5.setVisibility(GONE);
//            judul = getJudul();
//            adapter = new LaporanUserAdapter(this, list, Constant.LAP_KADER, true);
//        }

        tvJudul.setText(judul);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void getAgenda(){
        Tools.showProgressDialog(this, getString(R.string.load_data));
        List<Contact> list = new ArrayList<>();
        Call<AgendaResponse> call = service.getAgenda(Constant.getToken(), "0");
        call.enqueue(new Callback<AgendaResponse>() {
            @Override
            public void onResponse(Call<AgendaResponse> call, Response<AgendaResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("ok")) {
                        if (response.body().getData().size() > 0){
                            for (int i = 0; i < response.body().getData().size() ; i++) {
                                Contact contact = contactDao.getContactById(response.body().getData().get(i).getId_user());
                                contact.setKeterangan(response.body().getData().get(i).getNama());
                                list.add(contact);
                            }
                            adapter.updateData(list);

                            appDb.agendaDao().insertAgenda(response.body().getData());

                            AgendaScheduler.setupUpcomingAgendaNotifier(LaporanDetailActivity.this);
                        }
                    } else {

                    }
                } else {

                }
                Tools.dissmissProgressDialog();
            }

            @Override
            public void onFailure(Call<AgendaResponse> call, Throwable t) {
                Tools.dissmissProgressDialog();
            }
        });
    }

    private void getKonstitusi(){
        Tools.showProgressDialog(this, "Load Data...");
        List<Contact> list = new ArrayList<>();
        Call<KonstituisiResponse> call = service.getKonstitusi("0");
        call.enqueue(new Callback<KonstituisiResponse>() {
            @Override
            public void onResponse(Call<KonstituisiResponse> call, Response<KonstituisiResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("ok")) {
                        if (response.body().getData().size() > 0){
                            for (int i = 0; i < response.body().getData().size() ; i++) {
                                Contact contact = contactDao.getContactById(response.body().getData().get(i).getId_user());
                                contact.setKeterangan(response.body().getData().get(i).getNama());
                                list.add(contact);
                            }
                            adapter.updateData(list);
                        }
                    } else {

                    }
                } else {

                }
                Tools.dissmissProgressDialog();
            }

            @Override
            public void onFailure(Call<KonstituisiResponse> call, Throwable t) {
                Tools.dissmissProgressDialog();
            }
        });
    }

    private void getSejarah(){
        Tools.showProgressDialog(this, "Load Data...");
        List<Contact> list = new ArrayList<>();
        Call<SejarahResponse> call = service.getSejarah(0);
        call.enqueue(new Callback<SejarahResponse>() {
            @Override
            public void onResponse(Call<SejarahResponse> call, Response<SejarahResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        if (response.body().getData().size() > 0){
                            for (int i = 0; i < response.body().getData().size() ; i++) {
                                Contact contact = contactDao.getContactById(response.body().getData().get(i).getId_user());
                                contact.setKeterangan(response.body().getData().get(i).getJudul());
                                list.add(contact);
                            }
                            adapter.updateData(list);
                        }
                    }
                } else {

                }
                Tools.dissmissProgressDialog();
            }

            @Override
            public void onFailure(Call<SejarahResponse> call, Throwable t) {
                Tools.dissmissProgressDialog();
            }
        });
    }
}

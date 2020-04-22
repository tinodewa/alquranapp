package com.roma.android.sihmi.view.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.core.CoreApplication;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.entity.PengajuanAdmin;
import com.roma.android.sihmi.model.database.entity.PengajuanHistory;
import com.roma.android.sihmi.model.database.entity.PengajuanHistoryJoin;
import com.roma.android.sihmi.model.database.entity.PengajuanLK1;
import com.roma.android.sihmi.model.database.entity.Training;
import com.roma.android.sihmi.model.database.entity.notification.Data;
import com.roma.android.sihmi.model.database.entity.notification.NotifResponse;
import com.roma.android.sihmi.model.database.entity.notification.Sender;
import com.roma.android.sihmi.model.database.entity.notification.Token;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.HistoryPengajuanDao;
import com.roma.android.sihmi.model.database.interfaceDao.LevelDao;
import com.roma.android.sihmi.model.database.interfaceDao.PengajuanDao;
import com.roma.android.sihmi.model.database.interfaceDao.PengajuanLK1Dao;
import com.roma.android.sihmi.model.database.interfaceDao.TrainingDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.network.NotifClient;
import com.roma.android.sihmi.model.network.SendNotifService;
import com.roma.android.sihmi.model.response.ContactResponse;
import com.roma.android.sihmi.model.response.GeneralResponse;
import com.roma.android.sihmi.model.response.PengajuanAdminResponse;
import com.roma.android.sihmi.model.response.PengajuanLK1Response;
import com.roma.android.sihmi.model.response.TrainingResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.activity.ContactActivity;
import com.roma.android.sihmi.view.activity.ProfileChatActivity;
import com.roma.android.sihmi.view.adapter.PermintaanAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class PermintaanFragment extends Fragment implements Ifragment {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.refresh)
    SwipeRefreshLayout refreshLayout;

    MasterService service;

    List<PengajuanHistoryJoin> list;
    PermintaanAdapter adapter;

    LiveData<List<PengajuanHistoryJoin>> listLiveData;
    UserFragment userFragment;

    int page;

    AppDb appDb;
    HistoryPengajuanDao historyPengajuanDao;
    private PengajuanLK1Dao pengajuanLK1Dao;
    UserDao userDao;
    ContactDao contactDao;
    LevelDao levelDao;
    private TrainingDao trainingDao;

    public PermintaanFragment() {
        // Required empty public constructor
    }

    public PermintaanFragment(int page) {
        this.page = page;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, v);
        setHasOptionsMenu(true);

        userFragment = ((UserFragment) PermintaanFragment.this.getParentFragment());
        service = ApiClient.getInstance().getApi();
        appDb = AppDb.getInstance(getActivity());
        historyPengajuanDao = appDb.historyPengajuanDao();
        userDao = appDb.userDao();
        contactDao = appDb.contactDao();
        levelDao = appDb.levelDao();
        pengajuanLK1Dao = appDb.pengajuanLK1Dao();
        trainingDao = appDb.trainingDao();

        getContact();

        list = new ArrayList<>();
        initAdapter();

        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(true);
            getContact();
        });

        Log.d("hallo", "onCreateView: " + Tools.dateNow());

        if (Tools.isSuperAdmin() || Tools.isSecondAdmin()){
            listLiveData = historyPengajuanDao.getAllPengajuanHistory();
        } else if (Tools.isAdmin1()) {
            listLiveData = historyPengajuanDao.getAllPengajuanHistoryAdmin1(userDao.getUser().getKomisariat());
        } else if (Tools.isAdmin2()) {
            listLiveData = historyPengajuanDao.getAllPengajuanHistoryAdmin2(userDao.getUser().getCabang(), "%" + Tools.dateNow());
        } else if (Tools.isLA1()) {
            listLiveData = historyPengajuanDao.getAllPengajuanHistoryLA1(userDao.getUser().getCabang());
        } else if (Tools.isLA2()) {
            listLiveData = historyPengajuanDao.getAllPengajuanHistoryLA2();
        } else {
            listLiveData = historyPengajuanDao.getAllPengajuanHistory(levelDao.getLevel(userDao.getUser().getId_roles()));
        }
        listLiveData.observe(getActivity(), pengajuanHistories -> {
            userFragment.updateTab(page, pengajuanHistories.size());
            adapter.updateData(pengajuanHistories);
            Log.d("hallogesss", "onCreateView onchange: permintaanFragment " + pengajuanHistories.size());
        });
        return v;
    }

    private void getContact(){
        Call<ContactResponse> call = service.getContact(Constant.getToken());
        if (Tools.isOnline(getContext())) {
            call.enqueue(new Callback<ContactResponse>() {
                @Override
                public void onResponse(Call<ContactResponse> call, Response<ContactResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equalsIgnoreCase("success")) {
                            List<Contact> contacts = response.body().getData();
                            for (int i = 0; i < contacts.size() ; i++) {
                                Contact c = contacts.get(i);
                                c.setId_level(levelDao.getPengajuanLevel(c.getId_roles()));
                                c.setTahun_daftar(Tools.getYearFromMillis(Long.parseLong(c.getTanggal_daftar())));

                                contactDao.insertContact(c);
                            }

                            getPermintaanLK1();
                            getPermintaanAdmin();
                        } else {
                            Toast.makeText(getActivity(), "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "" + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ContactResponse> call, Throwable t) {
                    Toast.makeText(getActivity(), "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Tools.dissmissProgressDialog();
                }
            });
        } else{
            Toast.makeText(getActivity(), "Tidak Ada Internet!", Toast.LENGTH_SHORT).show();
        }
    }

    private void getPermintaanLK1() {
        if (Tools.isOnline(getActivity())) {
            Call<PengajuanLK1Response> call = service.getPengajuanLK1(Constant.getToken());
            call.enqueue(new Callback<PengajuanLK1Response>() {
                @Override
                public void onResponse(Call<PengajuanLK1Response> call, Response<PengajuanLK1Response> response) {
                    if (refreshLayout.isRefreshing()) {
                        refreshLayout.setRefreshing(false);
                    }
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equalsIgnoreCase("ok")) {
                            int size = response.body().getData().size();
                            if (size > 0) {
                                List<PengajuanHistory> listHistory = new ArrayList<>();
                                for (int i = 0; i < size; i++) {
                                    PengajuanLK1 pengajuanLK1 = response.body().getData().get(i);
                                    String idPengajuan = pengajuanLK1.get_id();
                                    String idRoles = "5da699c1a61aed0fe65ae31f";
                                    String tgl_lk1 = pengajuanLK1.getTanggal_lk1();
                                    String createdBy = pengajuanLK1.getCreated_by();
                                    String approvedBy = pengajuanLK1.getModified_by();
                                    long dateCreated = pengajuanLK1.getDate_created();
                                    long dateApproved = pengajuanLK1.getDate_modified();
                                    int status = pengajuanLK1.getStatus();

                                    int level = levelDao.getLevel(idRoles);

                                    PengajuanHistory pengajuanHistory = new PengajuanHistory(idPengajuan, idRoles, "", createdBy, approvedBy, dateCreated, dateApproved, status, tgl_lk1, level);
                                    pengajuanHistory.setNama(contactDao.getContactById(createdBy).getNama_depan());
                                    historyPengajuanDao.insertPengajuanHistory(pengajuanHistory);
                                    pengajuanLK1Dao.insertPengajuan(pengajuanLK1);

                                    if (pengajuanLK1.getStatus() == 0) {
                                        listHistory.add(pengajuanHistory);
                                    }
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<PengajuanLK1Response> call, Throwable t) {
                    if (refreshLayout.isRefreshing()) {
                        refreshLayout.setRefreshing(false);
                    }

                }
            });
        } else {
            if (refreshLayout.isRefreshing()) {
                refreshLayout.setRefreshing(false);
            }
            Tools.showToast(getActivity(), getString(R.string.tidak_ada_internet));
        }
    }

    private boolean availableUser(Contact contact, List<PengajuanHistory> list) {
        boolean available = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getCreated_by().equals(contact.get_id())) {
                available = true;
            }
        }
        return available;
    }

    private void getPermintaanAdmin() {
        if (Tools.isOnline(getActivity())) {
            Call<PengajuanAdminResponse> call = service.getPengajuanAdmin(Constant.getToken());
            call.enqueue(new Callback<PengajuanAdminResponse>() {
                @Override
                public void onResponse(Call<PengajuanAdminResponse> call, Response<PengajuanAdminResponse> response) {
                    if (refreshLayout.isRefreshing()){
                        refreshLayout.setRefreshing(false);
                    }
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equalsIgnoreCase("success")) {
                            int size = response.body().getData().size();
                            if (size > 0) {
                                for (int i = 0; i < size; i++) {
                                    PengajuanAdmin pengajuanAdmin = response.body().getData().get(i);
                                    String statusString = pengajuanAdmin.getStatus() != null ? pengajuanAdmin.getStatus() : "3";
                                    String idPengajuan = pengajuanAdmin.get_id();
                                    String idRoles = pengajuanAdmin.getId_roles();
                                    String file = pengajuanAdmin.getFile();
                                    String createdBy = pengajuanAdmin.getCreated_by();
                                    String approvedBy = pengajuanAdmin.getApproved_by();
                                    long dateCreated = pengajuanAdmin.getDate_created();
                                    long dateApproved = pengajuanAdmin.getDate_modified();
                                    int status = Integer.valueOf(statusString);
                                    Log.d("halllo", "onResponse: " + idPengajuan);

                                    // begin log
                                    if (contactDao.getContactById(createdBy).getNama_depan().equalsIgnoreCase("Rofiudin")) {
                                        Log.d("REQUEST LOG", "TOKEN " + Constant.getToken());
                                        Log.d("REQUEST LOG", "REQUEST LOG Rofiudin has status " + statusString + " has id_pengajuan " + idPengajuan);
                                    }
                                    // end log

                                    int level = levelDao.getLevel(idRoles);

                                    PengajuanHistory pengajuanHistory = new PengajuanHistory(idPengajuan, idRoles, file, createdBy, approvedBy, dateCreated, dateApproved, status, "", level);
                                    pengajuanHistory.setNama(contactDao.getContactById(createdBy).getNama_depan());
                                    historyPengajuanDao.insertPengajuanHistory(pengajuanHistory);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<PengajuanAdminResponse> call, Throwable t) {
                    if (refreshLayout.isRefreshing()){
                        refreshLayout.setRefreshing(false);
                    }

                }
            });
        } else {
            Tools.showToast(getActivity(), getString(R.string.tidak_ada_internet));
            if (refreshLayout.isRefreshing()){
                refreshLayout.setRefreshing(false);
            }
        }
    }

    private void initAdapter() {
        adapter = new PermintaanAdapter(getActivity(), list, (type, id_pengajuan, contact, pengajuanHistory) -> {
            if (type.equals(Constant.LIHAT)) {
                Intent profileChatIntent = new Intent(getActivity(), ProfileChatActivity.class)
                        .putExtra("iduser", contact.get_id())
                        .putExtra("idpengajuan", id_pengajuan)
                        .putExtra("MODE_REQUEST", true);
                if (!pengajuanHistory.getTanggal_lk1().trim().isEmpty()) {
                    profileChatIntent.putExtra(ProfileChatActivity.requestKader, true);
                }
                startActivity(profileChatIntent);
            } else if (type.equals(Constant.UBAH)) {
                approveUser(contact, id_pengajuan, "1");
            } else if (type.equals(Constant.DOCUMENT)) {
//                String[] file = id_pengajuan.split("-");
//                startActivityForResult(new Intent(getActivity(), FileDetailActivity.class)
//                                .putExtra("file", file[1])
//                                .putExtra("_id", file[0])
//                                .putExtra("judul", contact.getNama_depan()),
//                        Constant.REQUEST_KONSTITUSI);
            } else if (type.equals(Constant.HAPUS)) {
                Log.d("halloo", "initAdapter: " + id_pengajuan);
                changeRoles(contact.get_id(), id_pengajuan);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    private void approveUser(Contact contact, String id_pengajuan, String status) {
        if (Tools.isOnline(getActivity())) {
            Tools.showProgressDialog(getActivity(), "Meyetujui Permintaan...");

            int level = historyPengajuanDao.getLevelByIdPengajuanHistory(id_pengajuan);
            Call<GeneralResponse> call;
            if (level == 2) {
                call = service.updatePengajuanLK1(Constant.getToken(), id_pengajuan, status);
            } else {
                call = service.updatePengajuanAdmin(Constant.getToken(), id_pengajuan, status);
            }
            call.enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                    Tools.dissmissProgressDialog();
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equalsIgnoreCase("ok")) {
                            if (level != 2) {
                                sendNotif(contact.get_id(), status);
                            } else {
                                sendNotif(contact.get_id(), "4");
                            }

                            String idRoles = historyPengajuanDao.getPengajuanHistoryById(id_pengajuan).getId_roles();
                            int id_level = levelDao.getIdLevelByIdRole(idRoles);
                            contactDao.updateRolesUser(contact.get_id(), idRoles, id_level);
                            historyPengajuanDao.updateApprovePengajuan(id_pengajuan);
                        }
                    }
                }

                @Override
                public void onFailure(Call<GeneralResponse> call, Throwable t) {
                    Tools.dissmissProgressDialog();
                }
            });
        } else {
            Toast.makeText(getActivity(), "Tidak Ada Internet!", Toast.LENGTH_SHORT).show();
        }
    }

    private void changeRoles(String idUser, String id_pengajuan) {
        Call<GeneralResponse> call = service.updateUserLevel(Constant.getToken(), idUser, 1);
        call.enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful()) {
                    String idRoles = levelDao.getIdRoles(1);
                    Log.d("halloo", "onResponse: " + idRoles);
                    int level = levelDao.getLevel(idRoles);
                    PengajuanHistory pengajuanHistory = new PengajuanHistory(id_pengajuan, idRoles, "", idUser, "", 0, 0, -1, "", level);
                    pengajuanHistory.setNama(contactDao.getContactById(idUser).getNama_depan());
                    historyPengajuanDao.insertPengajuanHistory(pengajuanHistory);
                    sendNotif(idUser, "2");
                    historyPengajuanDao.updatePengajuanUser(id_pengajuan, idRoles);
//                    sendNotif(idUser, "2");
//                    CoreApplication.get().getAppDb().interfaceDao().updatePengajuanUser(id_pengajuan, idRoles);
                } else {
                    Tools.showToast(getActivity(), getString(R.string.gagal_ganti_admin));
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Tools.showToast(getActivity(), getString(R.string.gagal_ganti_admin));
            }
        });
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


    public String getMonthByMillis(long currentDateTime) {
        Date currentDate = new Date(currentDateTime);
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("MM-yyyy");
        return df.format(currentDate);
    }

    private void sendNotifiaction(String receiver, final String username, final String message) {
        SendNotifService sendNotifService = NotifClient.getInstance("https://fcm.googleapis.com/").getNotifService();
        Log.d("Message", "sendNotifiaction: ");
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("roma", "onDataChange: permintaanfragment 347");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(userDao.getUser().get_id(), R.mipmap.ic_launcher, message, "Pemberitahuan",
                            receiver);

                    Sender sender = new Sender(data, token.getToken());

                    sendNotifService.sendNotification(sender)
                            .enqueue(new Callback<NotifResponse>() {
                                @Override
                                public void onResponse(Call<NotifResponse> call, Response<NotifResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<NotifResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            SearchView searchView = (SearchView) item.getActionView();
            searchView.setQueryHint(getString(R.string.cari));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    List<PengajuanHistoryJoin> list;
                    if (newText.isEmpty()) {
                        if (Tools.isSuperAdmin() || Tools.isSecondAdmin()){
                            list = historyPengajuanDao.getAllListPengajuanHistory();
                        } else if (Tools.isLA1()){
                            list = historyPengajuanDao.getAllListPengajuanHistoryLA1(userDao.getUser().getCabang());
                        } else if (Tools.isLA2()){
                            list = historyPengajuanDao.getAllListPengajuanHistoryLA2();
                        } else if (Tools.isAdmin1()) {
                            list = historyPengajuanDao.getAllListPengajuanHistoryAdmin1(userDao.getUser().getKomisariat());
                        } else if (Tools.isAdmin2()) {
                            list = historyPengajuanDao.getAllListPengajuanHistoryAdmin2(userDao.getUser().getCabang(), "%" + Tools.dateNow());
                        } else {
                            list = historyPengajuanDao.getAllListPengajuanHistory(levelDao.getLevel(userDao.getUser().getId_roles()));
                        }
                    } else {
                        if (Tools.isSuperAdmin() || Tools.isSecondAdmin()){
                            list = historyPengajuanDao.getSearchAllPengajuanHistory("%" + newText + "%");
                        } else if (Tools.isLA1()){
                            list = historyPengajuanDao.getSearchAllPengajuanHistoryLA1(userDao.getUser().getCabang(), "%" + newText + "%");
                        } else if (Tools.isLA2()){
                            list = historyPengajuanDao.getSearchAllPengajuanHistoryLA2("%" + newText + "%");
                        } else if (Tools.isAdmin1()) {
                            list = historyPengajuanDao.getSearchAllPengajuanHistoryAdmin1(userDao.getUser().getKomisariat(), "%" + newText + "%");
                        } else if (Tools.isAdmin2()) {
                            list = historyPengajuanDao.getSearchAllPengajuanHistoryAdmin2(userDao.getUser().getCabang(), "%" + Tools.dateNow(), "%" + newText + "%");
                        } else {
                            list = historyPengajuanDao.getSearchAllPengajuanHistory(levelDao.getLevel(userDao.getUser().getId_roles()), "%" + newText + "%");
                        }
                    }
                    adapter.updateData(list);
                    return true;
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void cobaInterface(String info) {
        Log.d("halloo", "cobaInterface: PermintaanFragment " + info);
    }
}

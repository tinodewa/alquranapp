package com.roma.android.sihmi.view.fragment;


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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.core.CoreApplication;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.entity.Training;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.LevelDao;
import com.roma.android.sihmi.model.database.interfaceDao.TrainingDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.ContactResponse;
import com.roma.android.sihmi.model.response.GeneralResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.adapter.UserAdapter;

import java.util.ArrayList;
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
public class AdminFragment extends Fragment {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.refresh)
    SwipeRefreshLayout refreshLayout;

    UserAdapter adapter;
    List<Contact> contacts;

    MasterService service;
    UserFragment userFragment;

    int page;

    AppDb appDb;
    ContactDao contactDao;
    TrainingDao trainingDao;
    UserDao userDao;
    LevelDao levelDao;

    public AdminFragment() {
        // Required empty public constructor
    }

    public AdminFragment(int page) {
        this.page = page;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("hallogesss", "onCreateView: adminFragment");
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, v);
        setHasOptionsMenu(true);

        appDb = AppDb.getInstance(getContext());
        contactDao = appDb.contactDao();
        trainingDao = appDb.trainingDao();
        userDao = appDb.userDao();
        levelDao = appDb.levelDao();

        userFragment = ((UserFragment)AdminFragment.this.getParentFragment());
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(true);
            getContact();
        });

        contactDao.getLiveDataListAdmin().observe(getActivity(), contacts -> {
//            getContact();
            adapter.updateData(contacts);
            userFragment.updateTab(page, contacts.size());
            Log.d("hallogesss", "onCreateView onchange: adminFragment "+contacts.size());
        });
        contacts = new ArrayList<>();
        initAdapter();

        service = ApiClient.getInstance().getApi();

        return v;
    }

    private void initAdapter(){
//        contacts = CoreApplication.get().getAppDb().interfaceDao().getListAdmin();
        adapter = new UserAdapter(getContext().getApplicationContext(), contacts, 2, new UserAdapter.itemClickListener() {
            @Override
            public void onItemClick(Contact contact) {
                Log.d("halloo", "onItemClick: ");
                changeRoles(contact.get_id());
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext()));
        recyclerView.setAdapter(adapter);
    }

    private void changeRoles(String idUser){
        Call<GeneralResponse> call = service.updateUserLevel(Constant.getToken(), idUser, 1);
        call.enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful()){
                    String idRoles = levelDao.getIdRoles(1);
                    sendNotif(idUser, "2");
                    contactDao.updateRolesUser(idUser, idRoles);
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

    private void sendNotif(String user, String status){
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

    @Override
    public void onResume() {
        super.onResume();
//        getContact();
    }

    private void getContact(){
        Call<ContactResponse> call = service.getContact(Constant.getToken());
        if (Tools.isOnline(getActivity())) {
//            Tools.showProgressDialog(getActivity(), "Load Data Admin...");
            call.enqueue(new Callback<ContactResponse>() {
                @Override
                public void onResponse(Call<ContactResponse> call, Response<ContactResponse> response) {
                    if (refreshLayout.isRefreshing()){
                        refreshLayout.setRefreshing(false);
                    }
//                    Tools.dissmissProgressDialog();
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equalsIgnoreCase("success")) {
                            if (refreshLayout.isRefreshing()){
                                refreshLayout.setRefreshing(false);
                            }
                            List<Contact> contacts = response.body().getData();
                            for (int i = 0; i < contacts.size() ; i++) {
                                Contact c = contacts.get(i);
                                c.setId_level(levelDao.getPengajuanLevel(c.getId_roles()));
                                c.setTahun_daftar(Tools.getYearFromMillis(Long.parseLong(c.getTanggal_daftar())));
                                if (c.getTanggal_lk1() != null && !c.getTanggal_lk1().trim().isEmpty()){
                                    String[] lk1 = c.getTanggal_lk1().split("-");
                                    c.setTahun_lk1(lk1[2]);
                                    Training training = new Training();
                                    training.setId(c.get_id()+"-LK1 (Basic Training)");
                                    training.setId_user(c.get_id());
                                    training.setId_level(c.getId_level());
                                    training.setTipe("LK1 (Basic Training)");
                                    training.setTahun(lk1[2]);
                                    training.setCabang(c.getCabang());
                                    training.setKomisariat(c.getKomisariat());
                                    training.setDomisili_cabang(c.getDomisili_cabang());
                                    training.setJenis_kelamin(c.getJenis_kelamin());
                                    if (trainingDao.checkTrainingAvailable(c.get_id(), "LK1 (Basic Training)", lk1[2]) == null){
                                        trainingDao.insertTraining(training);
                                    }
                                }
                                contactDao.insertContact(c);
                            }
//                            initAdapter();
                        } else {
                            Toast.makeText(getContext().getApplicationContext(), "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext().getApplicationContext(), "" + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ContactResponse> call, Throwable t) {
                    if (refreshLayout.isRefreshing()){
                        refreshLayout.setRefreshing(false);
                    }
                    Toast.makeText(getContext().getApplicationContext(), "" + t.getMessage(), Toast.LENGTH_SHORT).show();
//                    Tools.dissmissProgressDialog();
                }
            });
        } else{
            if (refreshLayout.isRefreshing()){
                refreshLayout.setRefreshing(false);
            }
            Toast.makeText(getActivity(), "Tidak Ada Internet!", Toast.LENGTH_SHORT).show();
        }
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
                    if (newText.isEmpty()){
                        contacts = contactDao.getListAdmin();
                    } else {
                        contacts = contactDao.getSearchListAdmin("%" + newText + "%");
                    }
                    adapter.updateData(contacts);
                    return true;
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }
}

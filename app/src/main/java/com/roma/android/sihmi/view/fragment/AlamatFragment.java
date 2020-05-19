package com.roma.android.sihmi.view.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Alamat;
import com.roma.android.sihmi.model.database.interfaceDao.AlamatDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.AlamatResponse;
import com.roma.android.sihmi.model.response.GeneralResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.activity.AlamatFormActivity;
import com.roma.android.sihmi.view.activity.MainActivity;
import com.roma.android.sihmi.view.adapter.AlamatAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlamatFragment extends Fragment {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.fab_add)
    FloatingActionButton fabAdd;

    MasterService service;
    List<Alamat> list= new ArrayList<>();
    AlamatAdapter adapter;

    AppDb appDb;
    AlamatDao alamatDao;
    UserDao userDao;
    public AlamatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for getActivity() fragment
        View v = inflater.inflate(R.layout.fragment_alamat, container, false);
        ButterKnife.bind(this, v);
        setHasOptionsMenu(true);

        service = ApiClient.getInstance().getApi();
        appDb = AppDb.getInstance(getContext());
        userDao = appDb.userDao();
        alamatDao = appDb.alamatDao();

        ((MainActivity) getActivity()).setToolBar(getString(R.string.judul_alamat));
        ((MainActivity) getActivity()).location();

        Tools.visibilityFab(fabAdd);
        recyclerView.setVisibility(View.VISIBLE);
        initAdapter();
        getData(0);

        alamatDao.getAllAlamat().observe(getActivity(), alamats -> adapter.updateData(alamats));

        fabAdd.setOnClickListener(v1 -> startActivityForResult(new Intent(getActivity(), AlamatFormActivity.class), Constant.REQUEST_ALAMAT));
        return v;
    }

    private void getData(int type){
        Call<AlamatResponse> call = service.getAddress(Constant.getToken(), String.valueOf(type));
        if (Tools.isOnline(getActivity())) {
            Tools.showProgressDialog(getActivity(), getString(R.string.load_data));
            call.enqueue(new Callback<AlamatResponse>() {
                @Override
                public void onResponse(Call<AlamatResponse> call, Response<AlamatResponse> response) {
                    if (response.isSuccessful()) {
                        alamatDao.deleteAlamat();
                        if (response.body().getStatus().equalsIgnoreCase("ok")) {
                            alamatDao.insertAlamat(response.body().getData());
                        } else {
                            Tools.showToast(getActivity(), response.body().getMessage());
                        }
                    } else {
                        Tools.showToast(getActivity(), response.message());
                    }
                    Tools.dissmissProgressDialog();
                }

                @Override
                public void onFailure(Call<AlamatResponse> call, Throwable t) {
                    Tools.showToast(getActivity(), t.getMessage());
                    Tools.dissmissProgressDialog();
                }
            });
        } else {
            Tools.showToast(getActivity(), getString(R.string.tidak_ada_internet));
        }
    }

    private void initAdapter(){
        adapter = new AlamatAdapter(getActivity(), list, (alamat, isLongClick) -> {
            if (isLongClick){
                if (allowLongClick()) {
                    Tools.showDialogTindakan(getActivity(), ket -> {
                        if (ket.equals(Constant.UBAH)) {
                            if (allowUpdate(alamat)) {
                                startActivityForResult(new Intent(getActivity(), AlamatFormActivity.class)
                                        .putExtra("id", alamat.get_id()).putExtra(AlamatFormActivity.IS_EDIT, true), Constant.REQUEST_ALAMAT);
                            } else {
                                Tools.showToast(getActivity(), getString(R.string.hak_akses_perbarui));
                            }
                        } else if (ket.equals(Constant.HAPUS)) {
//                            if (Tools.isSuperAdmin()) {
                            // perbaikan lagi tanggal 27-12-2020, second admin juga bisa  :)
                            if (allowDelete()) {
                                Tools.confirmDelete(getActivity(), ket1 -> {
                                    if (ket1.equals(Constant.HAPUS)) {
                                        deleteData(alamat.get_id());
                                    }
                                });
                            } else {
                                Tools.showToast(getActivity(), getString(R.string.hak_akses_hapus));
                            }
                        }
                    }, true, allowDelete());
                }
            } else {
//                startActivity(new Intent(getContext(), AlamatMapsActivity.class));
                startActivityForResult(new Intent(getActivity(), AlamatFormActivity.class)
                        .putExtra("id", alamat.get_id()).putExtra(AlamatFormActivity.IS_EDIT, false), Constant.REQUEST_ALAMAT);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    private boolean allowLongClick(){
        if (Tools.isAdmin1() || Tools.isLA1() || Tools.isLA2() || Tools.isSecondAdmin() || Tools.isSuperAdmin()){
            return true;
        } else {
            return false;
        }
    }

    private boolean allowUpdate(Alamat alamat){
//        boolean allow;
//        if (Tools.isAdmin1() && alamat.getType().contains("4-"+Constant.getUser().getKomisariat())){
//            allow = true;
//        } else if (Tools.isLA1() && alamat.getType().contains("2-"+Constant.getUser().getCabang())){
//            allow = true;
//        } else if (Tools.isLA2() && alamat.getType().toLowerCase().contains("0-PB HMI")){
//            allow = true;
//        } else if (Tools.isSecondAdmin() || Tools.isSuperAdmin()){
//            allow = true;
//        } else {
//            allow = false;
//        }
//        return allow;

//        return true;

        // ganti lagi 27-12-2019
        if (Tools.isAdmin1()) {
            if (alamat.getType().contains("4-"+userDao.getUser().getKomisariat())){
                return true;
            } else {
                return false;
            }
        } else if (Tools.isLA1()){
            if (alamat.getType().contains("2-"+userDao.getUser().getCabang())){
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private boolean allowDelete(){
        boolean allow;
        if (Tools.isSecondAdmin() || Tools.isSuperAdmin()){
            allow = true;
        } else {
            allow = false;
        }
        return allow;
    }

    private void deleteData(String id){
        Call<GeneralResponse> call = service.deleteAddress(Constant.getToken(), id);
        if (Tools.isOnline(getActivity())) {
            Tools.showProgressDialog(getActivity(), getString(R.string.hapus_alamat));
            call.enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equalsIgnoreCase("ok")) {
                            alamatDao.deleteAlamatById(id);
                            Tools.showToast(getActivity(), getString(R.string.berhasil_hapus));
                        } else {
                            Tools.showToast(getActivity(), getString(R.string.gagal_hapus));
                        }
                    } else {
                        Tools.showToast(getActivity(), getString(R.string.gagal_hapus));
                    }
                    Tools.dissmissProgressDialog();
                }

                @Override
                public void onFailure(Call<GeneralResponse> call, Throwable t) {
                    Tools.showToast(getActivity(), getString(R.string.gagal_hapus));
                    Tools.dissmissProgressDialog();
                }
            });
        } else {
            Tools.showToast(getActivity(), getString(R.string.tidak_ada_internet));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_ALAMAT && resultCode == Activity.RESULT_OK){
            getData(0);
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
                    Log.d("checckk", "onQueryTextSubmit: fragment");
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    alamatDao.getSearchAlamat("%"+newText+"%");
                    adapter.updateData(list);
                    return true;
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

}

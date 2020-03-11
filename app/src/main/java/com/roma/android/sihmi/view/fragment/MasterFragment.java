package com.roma.android.sihmi.view.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Master;
import com.roma.android.sihmi.model.database.interfaceDao.MasterDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.GeneralResponse;
import com.roma.android.sihmi.model.response.MasterResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.activity.MainActivity;
import com.roma.android.sihmi.view.adapter.MasterAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MasterFragment extends Fragment {
    @BindView(R.id.et_badko)
    EditText etBadko;
    @BindView(R.id.et_cabang)
    EditText etCabang;
    @BindView(R.id.et_korkom)
    EditText etKorkom;
    @BindView(R.id.et_komisariat)
    EditText etKomisariat;
    @BindView(R.id.et_pelatihan)
    EditText etPelatihan;

    MasterService service;
    List<Master> masters;
    MasterAdapter adapter;

    List<Master> list = new ArrayList<>();

    AppDb appDb;
    MasterDao masterDao;



    public MasterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_master, container, false);
        ButterKnife.bind(this, v);
        setHasOptionsMenu(true);
        ((MainActivity) getActivity()).setToolBar("Master HMI");

        service = ApiClient.getInstance().getApi();
        appDb = AppDb.getInstance(getContext());
        masterDao = appDb.masterDao();

        clickDrawable(etBadko);
        clickDrawable(etCabang);
        clickDrawable(etKomisariat);
        clickDrawable(etKorkom);
        clickDrawable(etPelatihan);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getData();
    }

    @OnClick({R.id.et_badko, R.id.et_cabang, R.id.et_komisariat, R.id.et_korkom, R.id.et_pelatihan})
    public void click(EditText editText){
        int id = editText.getId();
        switch (id){
            case R.id.et_badko:
                masterDao.getMasterByType("1").observe(this, list -> {
                    adapter.updateData(list);
                });
                listDialog(masterDao.getListMasterByType("1"));
                break;
            case R.id.et_cabang:
                masterDao.getMasterByType("2").observe(this, list -> {
                    adapter.updateData(list);
                });
                listDialog(masterDao.getListMasterByType("2"));
                break;
            case R.id.et_korkom:
                masterDao.getMasterByType("3").observe(this, list -> {
                    adapter.updateData(list);
                });
                listDialog(masterDao.getListMasterByType("3"));
                break;
            case R.id.et_komisariat:
                masterDao.getMasterByType("4").observe(this, list -> {
                    adapter.updateData(list);
                });
                listDialog(masterDao.getListMasterByType("4"));
                break;
            case R.id.et_pelatihan:
                masterDao.getMasterByType("5").observe(this, list -> {
                    adapter.updateData(list);
                });
                listDialog(masterDao.getListMasterByType("5"));
                break;
        }
    }

    private void getData (){
        Call<MasterResponse> call = service.getMaster(Constant.getToken(), "0");
        call.enqueue(new Callback<MasterResponse>() {
            @Override
            public void onResponse(Call<MasterResponse> call, Response<MasterResponse> response) {
                if (response.isSuccessful()){
                    if (response.body().getStatus().equalsIgnoreCase("ok")){
                        masterDao.insertMaster(response.body().getData());
                    }
                }
            }

            @Override
            public void onFailure(Call<MasterResponse> call, Throwable t) {

            }
        });
    }


    private void clickDrawable(EditText editText){
        editText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP){
                if (event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[2].getBounds().width())) {
                    addDialog(editText.getText().toString());
                    return true;
                }
            }
            return false;
        });
    }

    private void listDialog(List<Master> list){
        if (list.size() > 0) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View dialogView = inflater.inflate(R.layout.dialog_master, null);
            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setView(dialogView)
                    .create();
            dialog.show();

            adapter = new MasterAdapter(getActivity(), list, (type1, master) -> {
                if (type1.equalsIgnoreCase(Constant.UBAH)) {
                    // update
                    updateDialog(master);
                    dialog.dismiss();
                } else {
                    // hapus
                    Tools.deleteDialog(getActivity(), getString(R.string.konfirmasi), ket -> {
                        hapus(master);
                    });
                    dialog.dismiss();
                }
            });
            RecyclerView recyclerView = dialogView.findViewById(R.id.rv_list_master);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
        } else {
            Tools.showToast(getActivity(), getString(R.string.data_kosong));
        }

    }

    private void addDialog(String nama){
        final EditText editText = new EditText(getActivity());
        editText.setSingleLine();
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        editText.setHint(nama);
        FrameLayout conrtainer = new FrameLayout(getActivity());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        editText.setLayoutParams(params);
        conrtainer.addView(editText);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.tambah)+" "+nama)
                .setView(conrtainer)
                .setPositiveButton(getString(R.string.simpan), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Tools.showToast(getActivity(), editText.getText().toString());
                        simpan(typeMaster(nama), editText.getText().toString());
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.batal), (dialog1, which) -> dialog1.dismiss())
                .create();
        dialog.show();
    }

    private void updateDialog(Master master){
        final EditText editText = new EditText(getActivity());
        editText.setSingleLine();
        editText.setText(master.getValue());
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        FrameLayout conrtainer = new FrameLayout(getActivity());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        editText.setLayoutParams(params);
        conrtainer.addView(editText);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.update)+" "+nameMaster(master.getType()))
                .setView(conrtainer)
                .setPositiveButton(getString(R.string.simpan), (dialog12, which) -> {
                    edit(master, editText.getText().toString());
                    dialog12.dismiss();
                })
                .setNegativeButton(getString(R.string.batal), (dialog1, which) -> dialog1.dismiss())
                .create();
        dialog.show();
    }

    private String typeMaster(String nama){
        String type;
        if (nama.toLowerCase().contains("badko")){
            type = "1";
        } else if (nama.toLowerCase().contains("cabang")){
            type = "2";
        } else if (nama.toLowerCase().contains("korkom")){
            type = "3";
        } else if (nama.toLowerCase().contains("komisariat")){
            type = "4";
        } else if (nama.toLowerCase().contains("pelatihan")) {
            type = "5";
        } else {
            type = "6";
        }
        return type;
    }

    private String nameMaster(String type){
        String name;
        if (type.toLowerCase().contains("1")){
            name = "Badko";
        } else if (type.toLowerCase().contains("2")){
            name = "Cabang";
        } else if (type.toLowerCase().contains("3")){
            name = "Korkom";
        } else if (type.toLowerCase().contains("4")){
            name = "Komisariat";
        } else if (type.toLowerCase().contains("5")) {
            name = "Pelatihan";
        } else {
            name = "Tidak Ada";
        }
        return name;
    }

    private void simpan(String type, String value){
        if (Tools.isOnline(getActivity())){
            if (!value.trim().isEmpty()) {
                Call<GeneralResponse> call = service.addMaster(Constant.getToken(), type, value);
                call.enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                        if (response.isSuccessful()) {
                            if (response.body().getStatus().equalsIgnoreCase("ok")) {
                                Tools.showToast(getActivity(), getString(R.string.berhasil_tambah));
                                reloadData();
                            } else {
                                Tools.showToast(getActivity(), getString(R.string.gagal_tambah));
                            }
                        } else {
                            Tools.showToast(getActivity(), getString(R.string.gagal_tambah));
                        }
                    }

                    @Override
                    public void onFailure(Call<GeneralResponse> call, Throwable t) {
                        Tools.showToast(getActivity(),getString(R.string.gagal_tambah));

                    }
                });
            } else {
                Tools.showToast(getActivity(), getString(R.string.field_mandatory));
            }
        } else {
            Tools.showToast(getActivity(), getString(R.string.tidak_ada_internet));
        }
    }

    private void edit(Master master, String value){
        if (Tools.isOnline(getActivity())){
            Call<GeneralResponse> call = service.updateMaster(Constant.getToken(), master.get_id(), master.getType(), value);
            call.enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                    if (response.isSuccessful()){
                        if (response.body().getStatus().equalsIgnoreCase("ok")){
                            Tools.showToast(getActivity(), getString(R.string.berhasil_update));
                            reloadData();
                        } else {
                            Tools.showToast(getActivity(), response.body().getMessage());
                        }
                    } else {
                        Tools.showToast(getActivity(), response.message());
                    }
                }

                @Override
                public void onFailure(Call<GeneralResponse> call, Throwable t) {
                    Tools.showToast(getActivity(), t.getMessage());

                }
            });
        } else {
            Tools.showToast(getActivity(), getString(R.string.tidak_ada_internet));
        }
    }

    private void hapus(Master master){
        if (Tools.isOnline(getActivity())){
            Call<GeneralResponse> call = service.deleteMaster(Constant.getToken(), master.get_id());
            call.enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                    if (response.isSuccessful()){
                        if (response.body().getStatus().equalsIgnoreCase("ok")){
                            Tools.showToast(getActivity(), getString(R.string.berhasil_hapus));
                            masterDao.deleteMasterById(master.get_id());
                            reloadData();
                        } else {
                            Tools.showToast(getActivity(), response.body().getMessage());
                        }
                    } else {
                        Tools.showToast(getActivity(), response.message());
                    }
                }

                @Override
                public void onFailure(Call<GeneralResponse> call, Throwable t) {
                    Tools.showToast(getActivity(), t.getMessage());

                }
            });
        } else {
            Tools.showToast(getActivity(), getString(R.string.tidak_ada_internet));
        }
    }

    private void reloadData(){
        new Handler().postDelayed(() -> getData(), 500);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(false);
        MenuItem gridItem = menu.findItem(R.id.action_list);
        gridItem.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

}

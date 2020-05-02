package com.roma.android.sihmi.view.activity;

import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.core.CoreApplication;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Sejarah;
import com.roma.android.sihmi.model.database.interfaceDao.MasterDao;
import com.roma.android.sihmi.model.database.interfaceDao.SejarahDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.GeneralResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AboutFormActivity extends BaseActivity {
    public static String IS_NEW = "IS_NEW";
    public static String ID_SEJ = "ID_SEJ";
    public static String TYPE = "TYPE";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_type)
    EditText etType;
    @BindView(R.id.et_nama_type)
    EditText etNamaType;
    @BindView(R.id.et_desc)
    EditText etDesc;
    @BindView(R.id.btn_simpan)
    Button btnSimpan;

    private int type = 0;

    MasterService service;
    AppDb appDb;
    SejarahDao sejarahDao;
    UserDao userDao;
    MasterDao masterDao;

    Boolean isNew;
    String idSej="";
    Sejarah sejarah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_form);
        ButterKnife.bind(this);

        service = ApiClient.getInstance().getApi();
        appDb = AppDb.getInstance(this);
        sejarahDao = appDb.sejarahDao();
        userDao = appDb.userDao();
        masterDao = appDb.masterDao();
//
        isNew = getIntent().getBooleanExtra(IS_NEW, false);
        idSej = getIntent().getStringExtra(ID_SEJ);
        if (idSej != null && !idSej.isEmpty()){
            sejarah = sejarahDao.getSejarahById(idSej);
            Log.e("halllooooo", "onCreate: "+sejarah.get_id()+" --- "+sejarah.getType());
            String[] sej = sejarah.getType().split("-");
            if (sej.length>1) {
                String typeString;
                if (sej[0].equals("0")) {
                    typeString = "Nasional";
                } else if (sej[0].equals("1")) {
                    typeString = getString(R.string.cabang);
                } else {
                    typeString = getString(R.string.komisariat);
                }
                type = Integer.valueOf(sej[0]);
                etType.setText(typeString);
                etNamaType.setText(sej[1]);
                etDesc.setText(sejarah.getDeskripsi());
            }
        }
        String title;
        if (isNew){
            title = getString(R.string.tambah_tentang_kami);type = getIntent().getIntExtra(TYPE, 0);
            if (Tools.isSuperAdmin()) {
                String tipe = "";
                String[] namaType;
                if (type == 1) {
                    tipe = "0";
                    namaType = new String[1];
                    namaType[0] = "PB HMI";
                    Constant.setTypeData(0);
                } else if (type == 2) {
                    tipe = "2";
                    Constant.setTypeData(1);
                } else {
                    tipe = "4";
                    Constant.setTypeData(2);
                }
                etType.setText(Tools.getStringType(tipe));
                if(type != 1) {
                    etNamaType.setText(masterDao.getFirstDataMasterByType(Integer.valueOf(tipe)));
                }
            }
        } else {
            title = getString(R.string.perbarui_tentang_kami);
        }
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etType.setFocusable(false);
        etNamaType.setFocusable(false);

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSejarah(etNamaType.getText().toString(), etDesc.getText().toString());
            }
        });

        etDesc.setOnTouchListener((v, event) -> {
            if (etDesc.hasFocus()) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_SCROLL:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        return true;
                }
            }
            return false;
        });

        notSuperAdmin();
    }

    private void notSuperAdmin(){
        if (!Tools.isSuperAdmin()){
            etType.setVisibility(View.GONE);
            etNamaType.setEnabled(false);
//            etNamaType.setText(userDao.getUser().getKomisariat());
            etNamaType.setBackgroundColor(getResources().getColor(R.color.colorTextLight));
        }
    }

    @OnClick({R.id.et_type, R.id.et_nama_type})
    public void click(EditText editText){
        switch (editText.getId()){
            case R.id.et_type:
                if (Tools.isSuperAdmin() || Tools.isSecondAdmin()) {
                    Tools.showDialogType(this, res -> {
                        etType.setText(res);

                        if (res.equalsIgnoreCase("nasional")) {
                            etNamaType.setText("PB HMI");
                        }
                        else if (res.equalsIgnoreCase("cabang")) {
                            List<String> listCabang = masterDao.getMasterCabang();
                            if (listCabang.size() > 0)
                                etNamaType.setText(listCabang.get(0));
                        }
                        else if (res.equalsIgnoreCase("komisariat")) {
                            List<String> listKomisariat = masterDao.getMasterKomisariat();
                            if (listKomisariat.size() > 0)
                                etNamaType.setText(listKomisariat.get(0));
                        }
                        else {
                            etNamaType.setText(null);
                        }
                    });
                }
                break;
            case R.id.et_nama_type:
                if (Tools.isSuperAdmin() || Tools.isSecondAdmin()) {
                    String[] array;
                    List<String> list;
                    if (etType.getText().toString().equalsIgnoreCase("cabang")) {
                        list = masterDao.getMasterCabang();
                        array = new String[list.size()];
                        list.toArray(array);
                    }
                    else if (etType.getText().toString().equalsIgnoreCase("komisariat")) {
                        list = masterDao.getMasterKomisariat();
                        array = new String[list.size()];
                        list.toArray(array);
                    }
                    else {
                        array = new String[1];
                        array[0] = "PB HMI";
                    }
                    Tools.showDialogNamaType(this, array, res -> {
                        etNamaType.setText(res);
                    });
                }
                break;
        }
    }

    private void addSejarah(String title, String desc){
        if (isNew){
            addData(title, desc);
        } else {
            updateData(title, desc);
        }
    }

    private int getType(String text){
        int angka = 404;
        if (text.toLowerCase().contains("nasional") || text.toLowerCase().contains("hmi")){
            angka = 0;
        } else if (text.toLowerCase().contains("cabang")){
            angka = 1;
        } else if (text.toLowerCase().contains("komisariat")){
            angka = 2;
        }
        return angka;
    }

    private void addData(String title, String desc){
        if (!title.isEmpty() && !desc.isEmpty()){
            Tools.showProgressDialog(this, getString(R.string.menambah_tentang_kami));
            String type;
            if (etNamaType.getText().toString().equals("PB HMI")) {
                type = "0-PB HMI";
            } else {
                type = masterDao.getTypeMasterByValue(etNamaType.getText().toString()) + "-" + etNamaType.getText().toString();
            }
            Call<GeneralResponse> call = service.addSejarah(Constant.getToken(), title, desc, type);
            call.enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                    if (response.isSuccessful()) {
                        Log.d("test", "onResponse: addSejarah "+response.body().getMessage()+" - "+response.body().getStatus());
                        if (response.body().getStatus().equalsIgnoreCase("success")) {
                            Tools.showToast(AboutFormActivity.this, getResources().getString(R.string.berhasil_tambah));
                            Tools.dissmissProgressDialog();
                            setResult(Activity.RESULT_OK);
                            finish();
                        } else {
                            Tools.dissmissProgressDialog();
                            Tools.showToast(AboutFormActivity.this, getResources().getString(R.string.gagal_tambah));
                            Log.d("test", "onResponse: !succes addSejarah "+response.body().getMessage());
                        }
                    } else {
                        Tools.dissmissProgressDialog();
                        Tools.showToast(AboutFormActivity.this, getResources().getString(R.string.gagal_tambah));
                        Log.d("test", "onResponse: !successfull addSejarah "+response.message());

                    }
                }

                @Override
                public void onFailure(Call<GeneralResponse> call, Throwable t) {
                    Tools.dissmissProgressDialog();
                    Tools.showToast(AboutFormActivity.this, getResources().getString(R.string.gagal_tambah));
                    Log.d("test", "onResponse: failure addSejarah "+t.getMessage());
                }
            });
        }  else {
            Tools.showToast(AboutFormActivity.this, getResources().getString(R.string.field_mandatory));
        }
    }

    private void updateData(String title, String desc){
        if (!title.isEmpty() && !desc.isEmpty()){
            Tools.showProgressDialog(this, getString(R.string.memperbarui_tentang_kami));
            String type;
            if (etNamaType.getText().toString().equals("PB HMI")) {
                type = "0-PB HMI";
            } else {
                type = masterDao.getTypeMasterByValue(etNamaType.getText().toString()) + "-" + etNamaType.getText().toString();
            }
            Call<GeneralResponse> call = service.updateSejarah(Constant.getToken(), idSej, title, desc, type);
            call.enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                    if (response.isSuccessful()) {
                        Log.d("test", "onResponse: addSejarah "+response.body().getMessage()+" - "+response.body().getStatus());
                        if (response.body().getStatus().equalsIgnoreCase("success")) {
                            Tools.showToast(AboutFormActivity.this, getResources().getString(R.string.berhasil_update));
                            Tools.dissmissProgressDialog();
                            setResult(Activity.RESULT_OK);
                            finish();
                        } else {
                            Tools.dissmissProgressDialog();
                            Tools.showToast(AboutFormActivity.this, getResources().getString(R.string.gagal_update));
                            Log.d("test", "onResponse: !succes addSejarah "+response.body().getMessage());
                        }
                    } else {
                        Tools.dissmissProgressDialog();
                        Tools.showToast(AboutFormActivity.this, getResources().getString(R.string.gagal_update));
                        Log.d("test", "onResponse: !successfull addSejarah "+response.message());

                    }
                }

                @Override
                public void onFailure(Call<GeneralResponse> call, Throwable t) {
                    Tools.dissmissProgressDialog();
                    Tools.showToast(AboutFormActivity.this, getResources().getString(R.string.gagal_update));
                    Log.d("test", "onResponse: failure addSejarah "+t.getMessage());
                }
            });
        }  else {
            Tools.showToast(AboutFormActivity.this, getResources().getString(R.string.field_mandatory));
        }
    }
}

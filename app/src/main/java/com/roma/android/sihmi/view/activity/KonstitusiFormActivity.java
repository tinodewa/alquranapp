package com.roma.android.sihmi.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.core.CoreApplication;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Konstituisi;
import com.roma.android.sihmi.model.database.interfaceDao.KonstitusiDao;
import com.roma.android.sihmi.model.database.interfaceDao.MasterDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.GeneralResponse;
import com.roma.android.sihmi.model.response.UploadFileResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KonstitusiFormActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_type)
    EditText etType;
    @BindView(R.id.et_nama_type)
    EditText etNamaType;
    @BindView(R.id.et_title)
    EditText etTitle;
    @BindView(R.id.et_unggah)
    EditText etUnggah;
    @BindView(R.id.img_attach)
    ImageView imgAttach;
    @BindView(R.id.iv_image)
    ImageView ivImage;
    @BindView(R.id.btn_simpan)
    Button btnSimpan;


    String id_konstitusi="", judul;
    Konstituisi konstitusi;
    boolean isNew = true;
    String pdfFileName, pdfPath="";
    String urlFile;
    int type;
    MasterService service;

    AppDb appDb;
    UserDao userDao;
    KonstitusiDao konstitusiDao;
    MasterDao masterDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konstitusi_form);
        ButterKnife.bind(this);
        initModule();
        initToolbar();

    }

    private void initToolbar(){
        toolbar.setTitle(judul);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initModule(){
        service = ApiClient.getInstance().getApi();
        appDb = AppDb.getInstance(this);
        userDao = appDb.userDao();
        konstitusiDao = appDb.konstitusiDao();
        masterDao = appDb.masterDao();

        id_konstitusi = getIntent().getStringExtra(Constant.ID_KONSTITUSI);
        if (id_konstitusi != null && !id_konstitusi.isEmpty()){
            judul = getString(R.string.perbarui_konstitusi);
            konstitusi = konstitusiDao.getKonstituisibyId(id_konstitusi);
            urlFile = konstitusi.getFile();
            isNew = false;
            initView();
        } else {
            judul = getString(R.string.tambah_konstitusi);
            admin1();
        }
    }

    private void initView(){
        etTitle.setText(konstitusi.getNama());
        etUnggah.setText(konstitusi.getNama_file());
        etType.setFocusable(false);
        etNamaType.setFocusable(false);
        etUnggah.setFocusable(false);
        String[] type = konstitusi.getType().split("-");
        etType.setText(Tools.getStringType(type[0]));
        etNamaType.setText(type[1]);
        admin1();
    }

    private void admin1(){
        if (Tools.isAdmin1()){
            etType.setVisibility(View.GONE);
            etNamaType.setEnabled(false);
            etNamaType.setText(userDao.getUser().getKomisariat());
            etNamaType.setBackgroundColor(getResources().getColor(R.color.colorTextLight));
        }
    }

    @OnClick(R.id.iv_image)
    public void upload(){

    }

    @OnClick(R.id.btn_simpan)
    public void simpan(){
        if (isNew){
            addKonstitusi();
        } else {
            updateKonstitusi();
        }
    }

    private void addKonstitusi(){
        if (Tools.isOnline(KonstitusiFormActivity.this)) {
            Tools.showProgressDialog(KonstitusiFormActivity.this, getString(R.string.add_konstitusi));
            String type;
            if (etNamaType.getText().toString().equals("PB HMI")) {
                type = "0-PB HMI";
            } else {
                type = masterDao.getTypeMasterByValue(etNamaType.getText().toString()) + "-" + etNamaType.getText().toString();
            }
            Call<GeneralResponse> call = service.addKonstitusi(Constant.getToken(), etTitle.getText().toString(), etUnggah.getText().toString(), "", urlFile , "", type);
            call.enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equalsIgnoreCase("ok")) {
                            Tools.showToast(KonstitusiFormActivity.this, getString(R.string.berhasil_tambah));
                        } else {
                            Tools.showToast(KonstitusiFormActivity.this, getString(R.string.gagal_tambah));
                        }
                    } else {
                        Tools.showToast(KonstitusiFormActivity.this, getString(R.string.gagal_tambah));
                    }
                    setResult(Activity.RESULT_OK);
                    finish();
                    Tools.dissmissProgressDialog();
                }

                @Override
                public void onFailure(Call<GeneralResponse> call, Throwable t) {
                    Tools.showToast(KonstitusiFormActivity.this, getString(R.string.gagal_tambah));
                    Tools.dissmissProgressDialog();
                    finish();
                }
            });
        } else {
            Tools.showToast(KonstitusiFormActivity.this, getString(R.string.tidak_ada_internet));
        }
    }

    private void updateKonstitusi(){
        if (Tools.isOnline(this)) {
            Tools.showProgressDialog(KonstitusiFormActivity.this, getString(R.string.update_konstitusi));
            String type;
            if (etNamaType.getText().toString().equals("PB HMI")) {
                type = "0-PB HMI";
            } else {
                type = masterDao.getTypeMasterByValue(etNamaType.getText().toString()) + "-" + etNamaType.getText().toString();
            }
            Call<GeneralResponse> call = service.updateKonstitusi(Constant.getToken(), id_konstitusi, etTitle.getText().toString(), etUnggah.getText().toString(), "", urlFile, "", type);
            call.enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equalsIgnoreCase("ok")) {
                            Tools.showToast(KonstitusiFormActivity.this, getString(R.string.berhasil_update));
                        } else {
                            Tools.showToast(KonstitusiFormActivity.this, getString(R.string.gagal_update));
                        }
                    } else {
                        Tools.showToast(KonstitusiFormActivity.this, getString(R.string.gagal_update));
                    }
                    setResult(Activity.RESULT_OK);
                    finish();
                    Tools.dissmissProgressDialog();
                }

                @Override
                public void onFailure(Call<GeneralResponse> call, Throwable t) {
                    Tools.showToast(KonstitusiFormActivity.this, getString(R.string.gagal_update));
                    Tools.dissmissProgressDialog();
                    finish();
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.tidak_ada_internet), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick({R.id.et_type, R.id.et_nama_type, R.id.et_unggah})
    public void click(EditText editText){
        switch (editText.getId()){
            case R.id.et_type:
                if (Tools.isSuperAdmin()) {
                    Tools.showDialogType(this, res -> {
                        etType.setText(res);

                        if (res.equalsIgnoreCase("nasional")) {
                            etNamaType.setText("PB HMI");
                        }
                        else {
                            etNamaType.setText(null);
                        }
                    });
                }
                break;
            case R.id.et_nama_type:
                if (Tools.isSuperAdmin()) {
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
            case R.id.et_unggah:
                selectFile();
                break;
        }
    }

    private void selectFile(){
        new MaterialFilePicker()
                .withActivity(this)
                .withRequestCode(Constant.REQUEST_DOC_CODE)
                .withHiddenFiles(true)
                .withFilter(Pattern.compile(".*\\.pdf$"))
                .withTitle("Select PDF file")
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case Constant.REQUEST_DOC_CODE:
                if (resultCode == RESULT_OK) {
                    String path = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);

                    File file = new File(path);
//                    Uri uri = Uri.fromFile(new File(file.getAbsolutePath()));
//                    etUnggah.setText(getFileName(uri));

                    if (path != null && !path.isEmpty()) {
                        pdfPath = path;
                        uploadFile();
                    }

                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    private void uploadFile() {
        if (Tools.isOnline(this)) {
            Tools.showProgressDialog(KonstitusiFormActivity.this, getString(R.string.mengunggah_dok));

            File file = new File(pdfPath);
            // Parsing any Media type file
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/pdf"), file);

            Map<String, RequestBody> map = new HashMap<>();
            map.put("files\"; filename=\"" + file.getName() + "\"", requestBody);

            // finally, execute the request
            Call<UploadFileResponse> call = service.uploadFile(Constant.getToken(), map);
            call.enqueue(new Callback<UploadFileResponse>() {
                @Override
                public void onResponse(Call<UploadFileResponse> call, Response<UploadFileResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equalsIgnoreCase("ok")) {
                            Tools.showToast(KonstitusiFormActivity.this, getString(R.string.berhasil_unggah_dokumen));
                            urlFile = response.body().getData().get(0).getUrl();
                            pdfFileName = response.body().getData().get(0).getNama_file();
                            etUnggah.setText(pdfFileName+".pdf");
                        } else {
                            Tools.showToast(KonstitusiFormActivity.this, getString(R.string.gagal_unggah_dokumen));
                        }
                    } else {
                        Tools.showToast(KonstitusiFormActivity.this, getString(R.string.gagal_unggah_dokumen));
                    }
                    Tools.dissmissProgressDialog();
                }

                @Override
                public void onFailure(Call<UploadFileResponse> call, Throwable t) {
                    Tools.showToast(KonstitusiFormActivity.this, getString(R.string.gagal_unggah_dokumen));
                    Tools.dissmissProgressDialog();
                }
            });
        } else {
            Tools.showToast(KonstitusiFormActivity.this, getString(R.string.tidak_ada_internet));
        }
    }
}

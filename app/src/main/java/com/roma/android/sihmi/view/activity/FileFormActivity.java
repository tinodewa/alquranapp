package com.roma.android.sihmi.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.core.CoreApplication;
import com.roma.android.sihmi.model.database.entity.Konstituisi;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.GeneralResponse;
import com.roma.android.sihmi.model.response.UploadFileResponse;
import com.roma.android.sihmi.utils.Tools;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.shockwave.pdfium.PdfDocument;

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

public class FileFormActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_title)
    EditText etTitle;
    @BindView(R.id.et_desc)
    EditText etDesc;
    @BindView(R.id.btn_upload)
    Button btnUpload;
    @BindView(R.id.tv_nama_file)
    TextView tvNamaFile;
    @BindView(R.id.pdfView)
    PDFView pdfView;
    @BindView(R.id.btn_batal)
    Button btnBatal;
    @BindView(R.id.btn_simpan)
    Button btnSimpan;
//    @BindView(R.id.webView)
//    WebView webView;

    private Uri uri;

    String data="", _id="", nama_file;
    MasterService service;

    private String pdfFileName, pdfPath="";
    private int pageNumber = 0;

    Konstituisi konstituisi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_form);
        ButterKnife.bind(this);

//        service = CoreApplication.get().getClient().create(MasterService.class);

        pdfView = (PDFView) findViewById(R.id.pdfView);
//        webView = (WebView) findViewById(R.id.webView);

        toolbar.setTitle("Upload File".toUpperCase());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        if (getIntent().getStringExtra("_id") != null) {
//            _id = getIntent().getStringExtra("_id");
//            data = getIntent().getStringExtra("file");
//        }
//        konstituisi = CoreApplication.get().getAppDb().interfaceDao().getKonstituisibyId(_id);
//        if (konstituisi != null) {
//            etTitle.setText(konstituisi.getNama());
//            etDesc.setText(konstituisi.getDeskripsi());
//            tvNamaFile.setText(konstituisi.getNama_file());
//            btnSimpan.setText("Update");
//        }

//        String pdfurl = "http://192.168.137.177:8765/view/5caf02d8bbee3475a8afd0d6";
////        String pdfurl = "http://www.africau.edu/images/default/sample.pdf";
//
//        WebView webview = new WebView(this);
//        webview.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setLoadWithOverviewMode(true);
//        webView.getSettings().setUseWideViewPort(true);
//        try {
//            String urlEncoded = URLEncoder.encode(pdfurl, "UTF-8");
////            url = "http://docs.google.com/viewer?url=" + urlEncoded;
//            webview.loadUrl(urlEncoded);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
////        webview.getSettings().setPluginsEnabled(true);
////        webview.loadUrl(pdfurl);

//        pdfView.fromUri(Uri.parse(pdfurl))
//                .defaultPage(pageNumber)
//                .onPageChange(new OnPageChangeListener() {
//                    @Override
//                    public void onPageChanged(int page, int pageCount) {
//                        pageNumber = page;
//                        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
//                    }
//                })
//                .enableAnnotationRendering(true)
//                .onLoad(new OnLoadCompleteListener() {
//                    @Override
//                    public void loadComplete(int nbPages) {
//                        PdfDocument.Meta meta = pdfView.getDocumentMeta();
//
//                        printBookmarksTree(pdfView.getTableOfContents(), "-");
//                    }
//                })
//                .scrollHandle(new DefaultScrollHandle(this))
//                .spacing(10) // in dp
//                .onPageError(new OnPageErrorListener() {
//                    @Override
//                    public void onPageError(int page, Throwable t) {
//
//                    }
//                })
//                .load();
    }

    @OnClick(R.id.btn_upload)
    public void goUpload(){
        new MaterialFilePicker()
                .withActivity(this)
                .withRequestCode(1212)
                .withHiddenFiles(true)
                .withFilter(Pattern.compile(".*\\.pdf$"))
                .withTitle("Select PDF file")
                .start();

//        Intent intent = new Intent();
//        intent.setType("application/pdf");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select PDF"), 1212);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 1212:
                if (resultCode == RESULT_OK) {
                    String path = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);

                    File file = new File(path);
                    displayFromFile(file);

                    if (path != null) {
                        Log.d("Path: ", path);
                        pdfPath = path;
//                        uploadFile();
                    }

                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void displayFromFile(File file) {

        Uri uri = Uri.fromFile(new File(file.getAbsolutePath()));
        pdfFileName = getFileName(uri);

        tvNamaFile.setText(pdfFileName);

        pdfView.fromFile(file)
                .defaultPage(pageNumber)
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        pageNumber = page;
                        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
                    }
                })
                .enableAnnotationRendering(true)
                .onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int nbPages) {
                        PdfDocument.Meta meta = pdfView.getDocumentMeta();

                        printBookmarksTree(pdfView.getTableOfContents(), "-");
                    }
                })
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                .onPageError(new OnPageErrorListener() {
                    @Override
                    public void onPageError(int page, Throwable t) {

                    }
                })
                .load();
    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            //Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
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

//    private void uploadFile(){
//        if (!pdfPath.isEmpty()) {
//            if (Tools.isOnline(this)) {
//                Tools.showProgressDialog(FileFormActivity.this, "Proses Upload File...");
//
//                File file = new File(pdfPath);
//                // Parsing any Media type file
//                RequestBody requestBody = RequestBody.create(MediaType.parse("application/pdf"), file);
//
//                Map<String, RequestBody> map = new HashMap<>();
//                map.put("files\"; filename=\"" + file.getName() + "\"", requestBody);
//
//                // finally, execute the request
//                Call<UploadFileResponse> call = service.uploadFile(CoreApplication.get().getConstant().getToken(), map);
//                call.enqueue(new Callback<UploadFileResponse>() {
//                    @Override
//                    public void onResponse(Call<UploadFileResponse> call, Response<UploadFileResponse> response) {
////                    Tools.dissmissProgressDialog();
//                        if (response.isSuccessful()) {
//                            Log.d("uploadFile", "onResponse: sukses");
//                            if (response.body().getStatus().equalsIgnoreCase("ok")) {
//                                Log.d("uploadFile", "onResponse: ok");
//                                Toast.makeText(FileFormActivity.this, "Sukses Upload File", Toast.LENGTH_SHORT).show();
//                                data = response.body().getData().get(0).getUrl();
//                                nama_file = response.body().getData().get(0).getNama_file();
//                                Log.d("uploadFile", "onResponse: " + response.body().getData().get(0).getNama_file());
//
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Tools.dissmissProgressDialog();
//                                        if (_id.isEmpty()) {
//                                            addKonstitusi();
//                                        } else {
//                                            updateKonstituisi();
//                                        }
//                                    }
//                                }, 2000);
//                            } else {
//                                Tools.dissmissProgressDialog();
//                                Log.d("uploadFile", "onResponse: !ok " + response.body().getMessage());
//                                Toast.makeText(FileFormActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            Log.d("uploadFile", "onResponse: !sukses " + response.message());
//                            Toast.makeText(FileFormActivity.this, "" + response.message(), Toast.LENGTH_SHORT).show();
//                            Tools.dissmissProgressDialog();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<UploadFileResponse> call, Throwable t) {
//                        Log.d("uploadFile", "onResponse: failure " + t.getMessage());
//                        Toast.makeText(FileFormActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
//                        Tools.dissmissProgressDialog();
//                    }
//                });
//            } else {
//                if (_id.isEmpty()) {
//                    Toast.makeText(getApplicationContext(), "File belum diupload!", Toast.LENGTH_SHORT).show();
//                } else {
//                    updateKonstituisi();
//                }
//            }
//        } else {
//            Toast.makeText(this, "Dokumen Belum di Upload!", Toast.LENGTH_SHORT).show();
//        }
//    }

//    @OnClick(R.id.btn_batal)
//    public void goBatal(){
//        finish();
//    }
//
//    @OnClick(R.id.btn_simpan)
//    public void goSimpan(){
//        if (!etTitle.getText().toString().isEmpty() && !etDesc.getText().toString().isEmpty()) {
//            uploadFile();
//        }
//         else {
//            Toast.makeText(getApplicationContext(), "Masih ada field kosong!", Toast.LENGTH_SHORT).show();
//        }
//    }

    private void addKonstitusi(){
//        if (Tools.isOnline(this)) {
//            Tools.showProgressDialog(FileFormActivity.this, "Tambah Konstitusi...");
//
//            Call<GeneralResponse> call = service.addKonstitusi(CoreApplication.get().getConstant().getToken(), etTitle.getText().toString(), nama_file, etDesc.getText().toString(), data, "", String.valueOf(getIntent().getIntExtra(KonstitusiActivity.TYPE_KONSTITUSI, 0)));
//            call.enqueue(new Callback<GeneralResponse>() {
//                @Override
//                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
//                    if (response.isSuccessful()) {
//                        Log.d("uploadFile", "onResponse: addKonstitusi sukses");
//                        if (response.body().getStatus().equalsIgnoreCase("ok")) {
//                            Toast.makeText(FileFormActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                            Log.d("uploadFile", "onResponse: addKonstitusi ok");
//                        } else {
//                            Log.d("uploadFile", "onResponse: addKonstitusi !ok " + response.body().getMessage());
//                            Toast.makeText(FileFormActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Log.d("uploadFile", "onResponse: addKonstitusi !sukses " + response.message());
//                        Toast.makeText(FileFormActivity.this, "" + response.message(), Toast.LENGTH_SHORT).show();
//                    }
//                    setResult(Activity.RESULT_OK);
//                    finish();
//                    Tools.dissmissProgressDialog();
//                }
//
//                @Override
//                public void onFailure(Call<GeneralResponse> call, Throwable t) {
//                    Log.d("uploadFile", "onResponse: addKonstitusi failure " + t.getMessage());
//                    Toast.makeText(FileFormActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
//                    Tools.dissmissProgressDialog();
//                    finish();
//                }
//            });
//        } else {
//            Toast.makeText(this, "Tidak Ada Internet!", Toast.LENGTH_SHORT).show();
//        }
    }

    private void updateKonstituisi(){
//        if (Tools.isOnline(this)) {
//            Tools.showProgressDialog(FileFormActivity.this, "Update Konstitusi...");
//
//            Call<GeneralResponse> call = service.updateKonstitusi(CoreApplication.get().getConstant().getToken(), _id, etTitle.getText().toString(), nama_file, etDesc.getText().toString(), data, "", konstituisi.getType());
//            call.enqueue(new Callback<GeneralResponse>() {
//                @Override
//                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
//                    if (response.isSuccessful()) {
//                        if (response.body().getStatus().equalsIgnoreCase("ok")) {
//                            Toast.makeText(FileFormActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(FileFormActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(FileFormActivity.this, "" + response.message(), Toast.LENGTH_SHORT).show();
//                    }
//                    setResult(Activity.RESULT_OK);
//                    finish();
//                    Tools.dissmissProgressDialog();
//                }
//
//                @Override
//                public void onFailure(Call<GeneralResponse> call, Throwable t) {
//                    Toast.makeText(FileFormActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
//                    Tools.dissmissProgressDialog();
//                    finish();
//                }
//            });
//        } else {
//            Toast.makeText(this, "Tidak Ada Internet!", Toast.LENGTH_SHORT).show();
//        }
    }

}

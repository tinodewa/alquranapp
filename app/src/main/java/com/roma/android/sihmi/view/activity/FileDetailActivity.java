package com.roma.android.sihmi.view.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.core.CoreApplication;
import com.roma.android.sihmi.helper.FileDownloader;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.GeneralResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FileDetailActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.pdfView)
    PDFView pdfView;

    String _id, file;
    MasterService service;

    int type;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_detail);
        ButterKnife.bind(this);


        service = ApiClient.getInstance().getApi();
        _id = getIntent().getStringExtra("_id");
        file = getIntent().getStringExtra("file");
        title = getIntent().getStringExtra("judul");
        String judul;
        if (title != null && !title.trim().isEmpty()){
            judul = title;
        } else {
            judul = "Konstitusional HMI";
        }
        toolbar.setTitle(judul.toLowerCase());
//        toolbar.setTitle("".toUpperCase());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        pdfView = (PDFView) findViewById(R.id.pdfView);


        type = getIntent().getIntExtra(KonstitusiActivity.TYPE_KONSTITUSI, 0);

        Log.d("check", "onCreate: id "+_id);
        Log.d("check", "onCreate: file "+file);
        Log.d("check", "onCreate: judul "+title);


//        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/filepdf/" + _id+".pdf");  // -> filename = maven.pdf
//        if (pdfFile.exists()){
//            Toast.makeText(this, "File Ada", Toast.LENGTH_SHORT).show();
//            displayFromFile(pdfFile);
//        } else {
//            Toast.makeText(this, "Menunggu Donwload", Toast.LENGTH_SHORT).show();
//            new DownloadFile().execute("https://www.adobe.com/support/products/enterprise/knowledgecenter/media/c4611_sample_explain.pdf", "sample_explain.pdf");
        new DownloadFile(this).execute(file, _id+".pdf");
//        }

//        if (mimetype.toLowerCase().contains("image")){
//            Log.d("check", "onCreate: image");
//            webView.setVisibility(View.GONE);
//            Glide.with(this).load(_id).into(imageView);
//        } else {
//            Log.d("check", "onCreate: else ");
//
//            imageView.setVisibility(View.GONE);
//            webView.getSettings().setJavaScriptEnabled(true);
//            webView.loadUrl(_id);
//            Toast.makeText(this, "INi Bukan JPEG", Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.file, menu);
        MenuItem update = menu.findItem(R.id.action_update);
        MenuItem delete = menu.findItem(R.id.action_delete);

        if (Tools.isSuperAdmin()) {
//            return true;
        } else if (Tools.isLA2()){
            delete.setVisible(false);
//            return true;
        } else if (Tools.isLA1() && type == 3){
            delete.setVisible(false);
//            return true;
        } else {
            menu.clear();
//            return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_update:
                Bundle bundle = new Bundle();
                bundle.putString("_id", _id);
                bundle.putString("file", file);
                startActivity(new Intent(FileDetailActivity.this, FileFormActivity.class).putExtra(KonstitusiActivity.TYPE_KONSTITUSI, type).putExtras(bundle));
                setResult(Activity.RESULT_OK);
                finish();
//                update();
                break;
            case R.id.action_delete:
                deleteDialog();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void delete(){
        Call<GeneralResponse> call = service.deleteKonstitusi(Constant.getToken(), _id);

        if (Tools.isOnline(this)) {
            Tools.showProgressDialog(this, "Hapus Konstitusi...");
            call.enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equalsIgnoreCase("ok")) {
                            Toast.makeText(FileDetailActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            setResult(Activity.RESULT_OK);
                            onBackPressed();
                        } else {
                            Toast.makeText(FileDetailActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(FileDetailActivity.this, "" + response.message(), Toast.LENGTH_SHORT).show();
                    }
                    Tools.dissmissProgressDialog();
                }

                @Override
                public void onFailure(Call<GeneralResponse> call, Throwable t) {
                    Toast.makeText(FileDetailActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Tools.dissmissProgressDialog();
                }
            });
        } else {
            Toast.makeText(this, "Tidak Ada Internet!", Toast.LENGTH_SHORT).show();
        }
    }

    private class DownloadFile extends AsyncTask<String, Void, Void> {
        File pdfFile;

        public Activity activity;

        public DownloadFile(Activity a) {
            this.activity = a;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Tools.showProgressDialog(activity, "Tunggu Sebentar...");
        }

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];   // -> http://maven.apache.org/maven-1.x/maven.pdf
            String fileName = strings[1];  // -> maven.pdf
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "filepdf");
            folder.mkdir();

            pdfFile = new File(folder, fileName);

            try{
                pdfFile.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            FileDownloader.downloadFile(fileUrl, pdfFile);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            viewPdf(pdfFile);
            Tools.dissmissProgressDialog();
//            Toast.makeText(FileDetailActivity.this, "Selesai download dan tampilkan", Toast.LENGTH_SHORT).show();
        }
    }

    private void viewPdf(File pdfFile){
        displayFromFile(pdfFile);
//        Uri path = Uri.fromFile(pdfFile);
//        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
//        pdfIntent.setDataAndType(path, "application/pdf");
//        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        try{
//            startActivity(pdfIntent);
//        }catch(ActivityNotFoundException e){
//            Toast.makeText(FileDetailActivity.this, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
//        }
    }

    private void displayFromFile(File file) {

        Uri uri = Uri.fromFile(new File(file.getAbsolutePath()));
        final String pdfFileName = getFileName(uri);

//        tvNamaFile.setText(pdfFileName);

        pdfView.fromFile(file)
//                .defaultPage(pageNumber)
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
//                        pageNumber = page;
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

    private void deleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Konfirmasi")
                .setMessage("Apakah anda yakin ingin menghapus ?")
                .setCancelable(true)
                .setPositiveButton("YA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete();
                    }
                }).setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
}

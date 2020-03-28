package com.roma.android.sihmi.view.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.sqlite.db.SimpleSQLiteQuery;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.entity.DataGrafik;
import com.roma.android.sihmi.model.database.entity.DataKader;
import com.roma.android.sihmi.model.database.entity.Training;
import com.roma.android.sihmi.model.database.entity.User;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.InterfaceDao;
import com.roma.android.sihmi.model.database.interfaceDao.MasterDao;
import com.roma.android.sihmi.model.database.interfaceDao.TrainingDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Query;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.adapter.LaporanDataKaderAdapter;
import com.roma.android.sihmi.view.adapter.LaporanDataKaderPelatihanAdapter;
import com.roma.android.sihmi.view.adapter.LaporanGrafikAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.roma.android.sihmi.view.activity.DetailReportPelatihanActivity.VALUE_YEAR;

public class DetailReportActivity extends BaseActivity {
    public static final String TYPE_DETAIL = "type_detail";
    public static final String VALUE_DETAIL = "value_detail";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title_chart)
    TextView tvTitleChart;
    @BindView(R.id.kader_chart)
    LineChart kaderChart;
    @BindView(R.id.pelatihan_chart)
    BarChart pelatihanChart;
    @BindView(R.id.tv_title_data)
    TextView tvTitleData;
    @BindView(R.id.ll_data_kader)
    LinearLayout llDataKader;
    @BindView(R.id.ll_data_pelatihan)
    LinearLayout llDataPelatihan;
    @BindView(R.id.rv_data)
    RecyclerView rvData;

    AppDb appDb;
    UserDao userDao;
    ContactDao contactDao;
    InterfaceDao interfaceDao;
    MasterDao masterDao;
    TrainingDao trainingDao;

    User user;
    String type, value = "";
    int now, batas_tahun;


    LaporanGrafikAdapter kaderAdapter;
    LaporanDataKaderPelatihanAdapter pelatihanAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_report);
        ButterKnife.bind(this);
        type = getIntent().getStringExtra(TYPE_DETAIL);
        value = getIntent().getStringExtra(VALUE_DETAIL);
        initToolbar();
        initModule();
        initView(type);
    }

    void initModule() {
        appDb = AppDb.getInstance(this);
        userDao = appDb.userDao();
        interfaceDao = appDb.interfaceDao();
        contactDao = appDb.contactDao();
        masterDao = appDb.masterDao();
        trainingDao = appDb.trainingDao();

        user = userDao.getUser();
        now = Integer.valueOf(Tools.getYearFromMillis(System.currentTimeMillis()));
        batas_tahun = now - 4;
    }


    private void initToolbar() {
        if (type.equals(Constant.KADERISASI)) {
            toolbar.setTitle(getString(R.string.data_kaderisasi));
        } else if (type.equals(Constant.ALUMNI)) {
            toolbar.setTitle(getString(R.string.data_alumni));
        } else {
            toolbar.setTitle(getString(R.string.data_pelatihan));
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    void initView(String type) {
        if (type.equals(Constant.KADERISASI) || type.equals(Constant.ALUMNI)) {
            if (type.equals(Constant.KADERISASI)) {
                tvTitleChart.setText(getString(R.string.grafik_kaderisasi));
                tvTitleData.setText(R.string.data_kaderisasi);
            } else {
                tvTitleChart.setText(getString(R.string.grafik_alumni));
                tvTitleData.setText(R.string.data_alumni);
            }
            kaderChart.setVisibility(View.VISIBLE);
            pelatihanChart.setVisibility(View.GONE);
            llDataKader.setVisibility(View.VISIBLE);
            llDataPelatihan.setVisibility(View.GONE);
            initKaderChart();
            // Adapter untuk kaderisasi
            initKaderAdapter();
        } else {
            tvTitleChart.setText(getString(R.string.grafik_pelatihan));
            pelatihanChart.setVisibility(View.VISIBLE);
            kaderChart.setVisibility(View.GONE);
            tvTitleData.setText(R.string.data_pelatihan);
            llDataPelatihan.setVisibility(View.VISIBLE);
            llDataKader.setVisibility(View.GONE);
            initPelatihanChart();
            // Adapter untuk pelatihan
            initPelatihanAdapter();

        }
    }

    private void initKaderChart(){
        String query;
        if (Tools.isAdmin1()){
            query = Query.countReportKaderAdmin1(user.getKomisariat());
        } else if (Tools.isAdmin2() || Tools.isLA1()) {
            query = Query.countReportKaderAdmin2(user.getCabang());
        } else if (Tools.isAdmin3()){
            query = Query.countReportAlumniAdmin3(user.getDomisili_cabang());
        } else {
            query = Query.countReportKaderLA2();
        }

        ArrayList member = new ArrayList();
        for (int i = batas_tahun; i <= now; i++) {
            String query2 = query+" AND tahun_daftar ='"+i+"';";
            int count = contactDao.countRawQueryContact(new SimpleSQLiteQuery(query2));
            member.add(new Entry(i, (float) count));
        }

        LineDataSet dataSet = new LineDataSet(member, "");
        dataSet.setColors(new int[]{getResources().getColor(R.color.colorPrimary)});
        LineData data = new LineData(dataSet);

        // the labels that should be drawn on the XAxis
        final String[] quarters = new String[] {  };

        YAxis yAxis = kaderChart.getAxisLeft();
        yAxis.setLabelCount(5, false);
        yAxis.setAxisMaximum(100f);
        yAxis.setAxisMinimum(0f);

        kaderChart.getAxisRight().setEnabled(false);

        XAxis xAxis = kaderChart.getXAxis();
        xAxis.setSpaceMax(0.1f);
        xAxis.setSpaceMin(0.1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(quarters));

        kaderChart.setData(data);
        kaderChart.getDescription().setEnabled(false);
        kaderChart.getLegend().setEnabled(false);
        kaderChart.invalidate();
    }

    private void initPelatihanChart(){
        String query = getQueryPelatihan();

        ArrayList member = new ArrayList();
        for (int i = batas_tahun; i <= now; i++) {
            int lk1 = trainingDao.countRawQueryTraining(
                    new SimpleSQLiteQuery(query + " AND tipe = '" + Constant.TRAINING_LK1 + "' AND tahun = '" + i + "';"));
            int lk2 = trainingDao.countRawQueryTraining(
                    new SimpleSQLiteQuery(query + " AND tipe = '" + Constant.TRAINING_LK2 + "' AND tahun = '" + i + "';"));
            int lk3 = trainingDao.countRawQueryTraining(
                    new SimpleSQLiteQuery(query + " AND tipe = '" + Constant.TRAINING_LK3 + "' AND tahun = '" + i + "';"));
            int sc = trainingDao.countRawQueryTraining(
                    new SimpleSQLiteQuery(query + " AND tipe = '" + Constant.TRAINING_SC + "' AND tahun = '" + i + "';"));
            int tid = trainingDao.countRawQueryTraining(
                    new SimpleSQLiteQuery(query + " AND tipe = '" + Constant.TRAINING_TID + "' AND tahun = '" + i + "';"));
            int count = lk1+lk2+lk3+sc+tid;
//            String query2 = query+ " AND tipe = '" + Constant.TRAINING_LK1 + "' AND tahun = '" + i + "';");
//            int count = trainingDao.countRawQueryTraining(new SimpleSQLiteQuery(query2));
            member.add(new BarEntry(i, (float) count));
        }

        BarDataSet dataSet = new BarDataSet(member, "");
        dataSet.setColors(new int[]{getResources().getColor(R.color.colorPrimary)});
        BarData data = new BarData(dataSet);

        // the labels that should be drawn on the XAxis
        final String[] quarters = new String[] {  };

        YAxis yAxis = pelatihanChart.getAxisLeft();
        yAxis.setLabelCount(5, false);
        yAxis.setAxisMaximum(100f);
        yAxis.setAxisMinimum(0f);

        pelatihanChart.getAxisRight().setEnabled(false);

        XAxis xAxis = pelatihanChart.getXAxis();
        xAxis.setSpaceMax(0.1f);
        xAxis.setSpaceMin(0.1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(quarters));

        pelatihanChart.setData(data);
        pelatihanChart.getDescription().setEnabled(false);
        pelatihanChart.getLegend().setEnabled(false);
        pelatihanChart.invalidate();

    }

    // Kader
    private List<DataGrafik> getListKaderGender(){
        String query = getQuery();

        List<DataGrafik> list = new ArrayList<>();
        for (int i = now; i >= 1947 ; i--) {
            int tahun = i;
            int l = contactDao.countRawQueryContact(
                    new SimpleSQLiteQuery(query+" AND jenis_kelamin = '0' AND tahun_daftar = '"+i+"';"));
            int p = contactDao.countRawQueryContact(
                    new SimpleSQLiteQuery(query+" AND jenis_kelamin = '1' AND tahun_daftar = '"+i+"';"));
            int total = l+p;
            list.add(new DataGrafik(tahun, total, p, l));
        }
        return list;
    }

    private void initKaderAdapter(){
        kaderAdapter = new LaporanGrafikAdapter(this, getListKaderGender(), dataGrafik -> {
            if (dataGrafik.getJumlah() > 0) {
                Log.d("GET TAHUN", "GET TAHUN "+dataGrafik.getTahun());
                showDialogUser(getObjectQuery() + " AND tahun_daftar = '" + dataGrafik.getTahun() + "'");
            } else {
                Toast.makeText(this, getString(R.string.data_tidak_tersedia), Toast.LENGTH_SHORT).show();
            }
        });
        rvData.setLayoutManager(new LinearLayoutManager(this));
        rvData.setAdapter(kaderAdapter);
    }

    // Pelatihan
    private List<DataKader> getListPelatihan(){
        String query = getQueryPelatihan();
        List<DataKader> list = new ArrayList<>();
        for (int i = now; i >= 1947 ; i--) {
            int lk1 = trainingDao.countRawQueryTraining(
                    new SimpleSQLiteQuery(query + " AND tipe = '" + Constant.TRAINING_LK1 + "' AND tahun = '" + i + "';"));
            int lk2 = trainingDao.countRawQueryTraining(
                    new SimpleSQLiteQuery(query + " AND tipe = '" + Constant.TRAINING_LK2 + "' AND tahun = '" + i + "';"));
            int lk3 = trainingDao.countRawQueryTraining(
                    new SimpleSQLiteQuery(query + " AND tipe = '" + Constant.TRAINING_LK3 + "' AND tahun = '" + i + "';"));
            int sc = trainingDao.countRawQueryTraining(
                    new SimpleSQLiteQuery(query + " AND tipe = '" + Constant.TRAINING_SC + "' AND tahun = '" + i + "';"));
            int tid = trainingDao.countRawQueryTraining(
                    new SimpleSQLiteQuery(query + " AND tipe = '" + Constant.TRAINING_TID + "' AND tahun = '" + i + "';"));

            list.add(new DataKader(i, lk1, lk2, lk3, sc, tid));
        }
        return list;
    }

    private void initPelatihanAdapter(){
        pelatihanAdapter = new LaporanDataKaderPelatihanAdapter(this, getListPelatihan(), dataKader -> {
            int total = dataKader.getLk1()+dataKader.getLk2()+dataKader.getLk3()+dataKader.getSc()+dataKader.getTid();
            if (total > 0) {
                startActivity(new Intent(DetailReportActivity.this, DetailReportPelatihanActivity.class).putExtra(VALUE_YEAR, dataKader.getTahun()));
            } else {
                Toast.makeText(this, getString(R.string.data_tidak_tersedia), Toast.LENGTH_SHORT).show();
            }
        });
        rvData.setLayoutManager(new LinearLayoutManager(this));
        rvData.setAdapter(pelatihanAdapter);

    }

    private String getQuery(){
        String query;
        if (Tools.isAdmin1()){
            query = Query.countReportKaderAdmin1(user.getKomisariat());
        } else if (Tools.isAdmin2() || Tools.isLA1()){
            query = Query.countReportKaderAdmin2(user.getCabang());
        } else if (Tools.isAdmin3()){
            query = Query.countReportAlumniAdmin3(user.getDomisili_cabang());
        } else {
            query = Query.countReportKaderLA2();
        }
        return query;
    }

    private String getQueryPelatihan(){
        String query;
        if (Tools.isAdmin1()){
            query = Query.countPelatihanAdmin1(user.getKomisariat());
        } else if (Tools.isAdmin2() || Tools.isLA1()){
            query = Query.countPelatihanAdmin2(user.getCabang());
        } else {
            query = Query.countPelatihanLA2();
        }
        return query;
    }

    private String getObjectQuery(){
        String query;
        if (Tools.isAdmin1()){
            query = Query.ReportKaderAdmin1(user.getKomisariat());
        } else if (Tools.isAdmin2() || Tools.isLA1()){
            query = Query.ReportKaderAdmin2(user.getCabang());
        } else if (Tools.isAdmin3()){
            query = Query.ReportAlumniAdmin3(user.getDomisili_cabang());
        } else {
            query = Query.ReportKaderLA2();
        }
        return query;
    }

    private List<String> listName(List<Contact> list){
        List<String> listName = new ArrayList<>();
        for (Contact t : list){
            listName.add(contactDao.getContactById(t.get_id()).getNama_depan());
        }
        return listName;
    }

    private void showDialogUser(String query){
        List<Contact> contacts = contactDao.rawQueryContact(new SimpleSQLiteQuery(query));
        String[] user = listName(contacts).toArray(new String[listName(contacts).size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setItems(user, (dialog1, which) -> {
                    String id_other_user = contactDao.getContactIdByName(user[which]);
                    startActivity(new Intent(DetailReportActivity.this, ProfileChatActivity.class).putExtra("iduser", id_other_user));
                    dialog1.dismiss();
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}

package com.roma.android.sihmi.view.activity;

import androidx.annotation.ColorInt;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.sqlite.db.SimpleSQLiteQuery;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.core.CoreApplication;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.entity.DataGrafik;
import com.roma.android.sihmi.model.database.entity.DataKader;
import com.roma.android.sihmi.model.database.entity.User;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.InterfaceDao;
import com.roma.android.sihmi.model.database.interfaceDao.MasterDao;
import com.roma.android.sihmi.model.database.interfaceDao.TrainingDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Query;
import com.roma.android.sihmi.utils.QueryRoomDao;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.adapter.LaporanDataKaderPelatihanAdapter;
import com.roma.android.sihmi.view.adapter.LaporanGrafikAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.roma.android.sihmi.view.activity.DetailReportPelatihanActivity.VALUE_YEAR;

public class LaporanGrafikActivity extends BaseActivity {
    public static final String TYPE = "type";
    public static final String VALUE = "value";
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.pie_chart)
    PieChart pieChart;
    @BindView(R.id.rv_data)
    RecyclerView rvData;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_kader)
    TextView tvKader;
    @BindView(R.id.tv_non_kader)
    TextView tvNonKader;
    @BindView(R.id.cv_table)
    CardView cvTable;

    @BindView(R.id.pelatihan_chart)
    BarChart pelatihanChart;
    @BindView(R.id.rv_data_pelatihan)
    RecyclerView rvDataPelatihan;

    @BindView(R.id.ll_kader)
    LinearLayout llKader;
    @BindView(R.id.ll_pelatihan)
    LinearLayout llPelatihan;


    User user;
    List<DataGrafik> grafikList = new ArrayList<>();
    int totNonLk, totLk;

    int now, batas_tahun;
    String type="", value="";

    AppDb appDb;
    UserDao userDao;
    ContactDao contactDao;
    InterfaceDao interfaceDao;
    MasterDao masterDao;
    TrainingDao trainingDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_grafik);
        ButterKnife.bind(this);

        initModule();
        initToolbar();
        initAdapter();
        getData();
    }

    private void initModule() {
        appDb = AppDb.getInstance(this);
        userDao = appDb.userDao();
        interfaceDao = appDb.interfaceDao();
        contactDao = appDb.contactDao();
        masterDao = appDb.masterDao();
        trainingDao = appDb.trainingDao();

        user = userDao.getUser();
        now = Integer.valueOf(Tools.getYearFromMillis(System.currentTimeMillis()));
        batas_tahun = now - 4;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getString(TYPE);
            value = bundle.getString(VALUE);
            Log.e("CEK DETAIL", "initModule: "+type+", value "+value);
            tvTitle.setText(value);
            if (type.equals(Constant.M_TRAINING)){
                llKader.setVisibility(View.GONE);
                llPelatihan.setVisibility(View.VISIBLE);
            } else{
                llKader.setVisibility(View.VISIBLE);
                llPelatihan.setVisibility(View.GONE);
            }
        }
    }

    private void initToolbar() {
        toolbar.setTitle(getString(R.string.pengguna_sihmi));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void getData() {
        String queryNonLk = Query.countReportSuperAdminNonLK();
        String queryLk = Query.countReportSuperAdmin();

        if (type.equals(Constant.M_CABANG)) {
            if (!value.contains(getString(R.string.semua))) {
                queryNonLk += "AND cabang = '" + value + "' ";
                queryLk += "AND cabang = '" + value + "' ";
            } else {
                queryNonLk += "AND cabang != '' ";
                queryLk += "AND cabang != '' ";
            }
        } else if (type.equals(Constant.M_KOMISARIAT)) {
            if (!value.contains(getString(R.string.semua))) {
                queryNonLk += "AND komisariat = '" + value + "' ";
                queryLk += "AND komisariat = '" + value + "' ";
            } else {
                queryNonLk += "AND komisariat != '' ";
                queryLk += "AND komisariat != '' ";
            }
        } else if (type.equals(Constant.M_ALUMNI)) {
            if (!value.contains(getString(R.string.semua))) {
                queryNonLk += "AND domisili_cabang = '" + value + "' ";
                queryLk += "AND domisili_cabang = '" + value + "' ";
            } else {
                queryNonLk += "AND domisili_cabang != '' ";
                queryLk += "AND domisili_cabang != '' ";
            }
        }

        Log.e("hahahaha", "getData: "+queryLk );

//        SimpleSQLiteQuery queryNonLk = new SimpleSQLiteQuery(Query.countReportSuperAdminNonLK());
//        SimpleSQLiteQuery queryLk = new SimpleSQLiteQuery(Query.countReportSuperAdmin());

        totNonLk = contactDao.countRawQueryContact(new SimpleSQLiteQuery(queryNonLk));
        totLk = contactDao.countRawQueryContact(new SimpleSQLiteQuery(queryLk));

        initPieChart(totLk, totNonLk, "Kader", "Non Kader");
        initPelatihanChart();
        initPelatihanAdapter();

        int now = Integer.valueOf(Tools.getYearFromMillis(System.currentTimeMillis()));
        int start = now - 4;

        for (int i = now; i >= 1947; i--) {
            int tahun = i;

            String queryNonLkTahun = Query.countReportSuperAdminNonLK(i);
            String queryLkTahun = Query.countReportSuperAdmin(i);

//            if (type.equals(Constant.M_CABANG)) {
//                if (!value.contains(getString(R.string.semua))) {
//                    queryNonLk += "AND cabang = '" + value + "' ";
//                    queryLk += "AND cabang = '" + value + "' ";
//                } else {
//                    queryNonLk += "AND cabang IS NOT NULL   AND   cabang NOT LIKE ''   AND   cabang NOT LIKE ' ' ";
//                    queryLk += "AND cabang IS NOT NULL   AND   cabang NOT LIKE ''   AND   cabang NOT LIKE ' ' ";
//                }
//            } else if (type.equals(Constant.M_KOMISARIAT)) {
//                if (!value.contains(getString(R.string.semua))) {
//                    queryNonLk += "AND komisariat = '" + value + "' ";
//                    queryLk += "AND komisariat = '" + value + "' ";
//                } else {
//                    queryNonLk += "AND komisariat IS NOT NULL   AND   komisariat NOT LIKE ''   AND   komisariat NOT LIKE ' ' ";
//                    queryLk += "AND komisariat IS NOT NULL   AND   komisariat NOT LIKE ''   AND   komisariat NOT LIKE ' ' ";
//                }
//            } else if (type.equals(Constant.M_ALUMNI)) {
//                if (!value.contains(getString(R.string.semua))) {
//                    queryNonLk += "AND domisili_cabang = '" + value + "' ";
//                    queryLk += "AND domisili_cabang = '" + value + "' ";
//                } else {
//                    queryNonLk += "AND domisili_cabang IS NOT NULL   AND   domisili_cabang NOT LIKE ''   AND   domisili_cabang NOT LIKE ' ' ";
//                    queryLk += "AND domisili_cabang IS NOT NULL   AND   domisili_cabang NOT LIKE ''   AND   domisili_cabang NOT LIKE ' '";
//                }
//            }

            if (!value.contains(getString(R.string.semua))) {
                if (type.equals(Constant.M_CABANG)) {
                    queryNonLkTahun += "AND cabang = '" + value + "' ";
                    queryLkTahun += "AND cabang = '" + value + "' ";
                } else if (type.equals(Constant.M_KOMISARIAT)) {
                    queryNonLkTahun += "AND komisariat = '" + value + "' ";
                    queryLkTahun += "AND komisariat = '" + value + "' ";
                } else if (type.equals(Constant.M_ALUMNI)) {
                    queryNonLkTahun += "AND domisili_cabang = '" + value + "' ";
                    queryLkTahun += "AND domisili_cabang = '" + value + "' ";
                }
            } else {

            }

            int NonLK = contactDao.countRawQueryContact(new SimpleSQLiteQuery(queryNonLkTahun));
            int LK = contactDao.countRawQueryContact(new SimpleSQLiteQuery(queryLkTahun));
            int total = NonLK + LK;
            grafikList.add(new DataGrafik(tahun, total, NonLK, LK));
        }
    }

    private void initAdapter() {
        LaporanGrafikAdapter adapter;// SUper Admin
        adapter = new LaporanGrafikAdapter(this, grafikList, dataGrafik -> {
            if (dataGrafik.getJumlah() > 0) {
                startActivity(new Intent(LaporanGrafikActivity.this, DataKaderActivity.class).putExtra(DataKaderActivity.TAHUN_KADER, dataGrafik.getTahun()));
            } else {
                Tools.showToast(this, getString(R.string.data_tidak_tersedia));
            }
        });
        rvData.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvData.setHasFixedSize(true);
        rvData.setAdapter(adapter);
    }

    private void dialogUser(String query) {
        List<Contact> contactList = contactDao.rawQueryContact(new SimpleSQLiteQuery(query));
        String[] user = listName(contactList).toArray(new String[listName(contactList).size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setItems(user, (dialog1, which) -> {
                    String id_other_user = contactDao.getContactIdByName(user[which]);
                    startActivity(new Intent(LaporanGrafikActivity.this, ProfileChatActivity.class).putExtra("iduser", id_other_user));
                    dialog1.dismiss();
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private List<String> listName(List<Contact> list) {
        List<String> listName = new ArrayList<>();
        for (Contact c : list) {
            listName.add(c.getNama_depan());
        }
        return listName;
    }

    private void initPieChart(int total1, int total2, String ket1, String ket2) {

//        double total =  totLk+totNonLk;
//        double percentNon = ((double) totNonLk / total) * 100;
//        double percentLk = 100 - percentNon;
        double total = total1 + total2;
        double percent2 = ((double) total2 / total) * 100;
        double percent1 = 100 - percent2;

        ArrayList numberMember = new ArrayList();
        numberMember.add(new PieEntry((float) percent1, ket1));
        numberMember.add(new PieEntry((float) percent2, ket2));
        PieDataSet dataSet = new PieDataSet(numberMember, "");
        dataSet.setColors(new int[]{getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryLight)});
        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawSliceText(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.invalidate();
    }

    private void initPelatihanChart(){
        String query = Query.countPelatihanLA2();

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

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = this.getTheme();
        theme.resolveAttribute(R.attr.textcolor, typedValue, true);
        @ColorInt int textColor = typedValue.data;
        yAxis.setTextColor(textColor);
        xAxis.setTextColor(textColor);
        data.setValueTextColor(textColor);

        pelatihanChart.setData(data);
        pelatihanChart.getDescription().setEnabled(false);
        pelatihanChart.getLegend().setEnabled(false);
        pelatihanChart.invalidate();
    }

    // Pelatihan
    private List<DataKader> getListPelatihan(){
        String query = Query.countPelatihanLA2();
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
        LaporanDataKaderPelatihanAdapter pelatihanAdapter = new LaporanDataKaderPelatihanAdapter(this, getListPelatihan(), dataKader -> {
            int total = dataKader.getLk1()+dataKader.getLk2()+dataKader.getLk3()+dataKader.getSc()+dataKader.getTid();
            if (total > 0) {
                startActivity(new Intent(LaporanGrafikActivity.this, DetailReportPelatihanActivity.class).putExtra(VALUE_YEAR, dataKader.getTahun()));
            } else {
                Toast.makeText(this, getString(R.string.data_tidak_tersedia), Toast.LENGTH_SHORT).show();
            }
        });
        rvDataPelatihan.setLayoutManager(new LinearLayoutManager(this));
        rvDataPelatihan.setAdapter(pelatihanAdapter);

    }

    private List<DataGrafik> getListGrafik(int type) {
        grafikList = new ArrayList<>();
        String query = "SELECT COUNT (*) FROM Contact WHERE (id_level != 19 AND id_level !=20) ";
        if (type == 1) { // Admin Alumni
//            query += " AND domisili_cabang = '"+user.getDomisili_cabang()+"' ";
            query += " AND domisili_cabang != '' ";
        } else if (type == 2) {  // Admin Komisariat
            query += " AND komisariat = '" + user.getKomisariat() + "' ";
        } else if (type == 3) { // Admin Cabang -> LA1
            query += " AND cabang = '" + user.getCabang() + "' ";
        }

        for (int i = now; i >= batas_tahun; i--) {
            int tahun = i;
            int l = contactDao.countRawQueryContact(
                    new SimpleSQLiteQuery(query + " AND jenis_kelamin = '0' AND tahun_daftar = '" + i + "';"));
            int p = contactDao.countRawQueryContact(
                    new SimpleSQLiteQuery(query + " AND jenis_kelamin = '1' AND tahun_daftar = '" + i + "';"));
            int total = l + p;
            grafikList.add(new DataGrafik(tahun, total, p, l));
        }
        return grafikList;
    }

    private List<DataGrafik> getListPelatihanGender() {
        List<DataGrafik> list = new ArrayList<>();
        String query = "SELECT COUNT (*) FROM Contact WHERE (id_level != 19 AND id_level !=20) AND (lk1 != '' OR lk2 != '' OR lk3 != '' OR sc != '' OR tid != '') ";
        for (int i = now; i >= batas_tahun; i--) {
            int tahun = i;
            int l = contactDao.countRawQueryContact(
                    new SimpleSQLiteQuery(query + " AND jenis_kelamin = '0' AND tahun_daftar = '" + i + "';"));
            int p = contactDao.countRawQueryContact(
                    new SimpleSQLiteQuery(query + " AND jenis_kelamin = '1' AND tahun_daftar = '" + i + "';"));
            int total = l + p;
            list.add(new DataGrafik(tahun, total, p, l));
        }
        return list;
    }

}

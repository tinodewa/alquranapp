package com.roma.android.sihmi.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.entity.DataKader;
import com.roma.android.sihmi.model.database.entity.Training;
import com.roma.android.sihmi.model.database.entity.User;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.MasterDao;
import com.roma.android.sihmi.model.database.interfaceDao.TrainingDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.TrainingResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Query;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.activity.DetailReportActivity;
import com.roma.android.sihmi.view.activity.MainActivity;
import com.roma.android.sihmi.view.adapter.LaporanDataKaderPelatihanAdapter;
import com.roma.android.sihmi.view.adapter.LaporanDataPelatihanAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.roma.android.sihmi.view.activity.DetailReportActivity.TYPE_DETAIL;
import static com.roma.android.sihmi.view.activity.DetailReportActivity.VALUE_DETAIL;

public class ReportFragment extends Fragment {
    @BindView(R.id.ll_grafik_alumni)
    LinearLayout llGrafikAlumni;
    @BindView(R.id.tv_detail_alumni)
    TextView tvDetailAlumni;
    @BindView(R.id.chart_alumni)
    LineChart chartAlumni;

    @BindView(R.id.ll_grafik_kaderisasi)
    LinearLayout llGrafikKaderisasi;
    @BindView(R.id.tv_detail_kaderisasi)
    TextView tvDetailKaderisasi;
    @BindView(R.id.chart_kaderisasi)
    LineChart chartKaderisasi;

    @BindView(R.id.ll_pelatihan)
    LinearLayout llPelatihan;
    @BindView(R.id.rv_data_pelatihan)
    RecyclerView rvDataPelatihan;

    @BindView(R.id.ll_gender)
    LinearLayout llGender;
    @BindView(R.id.pie_chart)
    PieChart pieChart;
    @BindView(R.id.btn_detail)
    Button btnDetail;

    @BindView(R.id.ll_data_kader)
    LinearLayout llDataKader;
    @BindView(R.id.rv_kaderisasi)
    RecyclerView rvKaderisasi;

    MasterService service;
    AppDb appDb;
    UserDao userDao;
    ContactDao contactDao;
    MasterDao masterDao;
    TrainingDao trainingDao;

    User user;
    int now, batas_tahun;

    public ReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_report, container, false);
        ButterKnife.bind(this, v);
        setHasOptionsMenu(true);
        ((MainActivity) getActivity()).setToolBar(getString(R.string.laporan));
        initModule();
        initView();
        return v;
    }

    void initModule() {
        service = ApiClient.getInstance().getApi();
        appDb = AppDb.getInstance(getContext());
        userDao = appDb.userDao();
        contactDao = appDb.contactDao();
        masterDao = appDb.masterDao();
        trainingDao = appDb.trainingDao();

        user = userDao.getUser();
        now = Integer.valueOf(Tools.getYearFromMillis(System.currentTimeMillis()));
        batas_tahun = now - 4;
    }

    void initView() {
        boolean isSlider;
        if (Tools.isAdmin1()) {
            visibilityView(View.GONE, View.VISIBLE, View.VISIBLE, View.VISIBLE, View.GONE);
            isSlider = true;
        } else if (Tools.isAdmin2()) {
            visibilityView(View.GONE, View.VISIBLE, View.VISIBLE, View.GONE, View.GONE);
            isSlider = false;
        } else if (Tools.isAdmin3()) {
            visibilityView(View.VISIBLE, View.VISIBLE, View.GONE, View.GONE, View.VISIBLE);
            tvDetailKaderisasi.setVisibility(View.GONE);
            isSlider = true;
        } else if (Tools.isLA1()) {
            visibilityView(View.GONE, View.VISIBLE, View.VISIBLE, View.VISIBLE, View.GONE);
            isSlider = true;
        } else if (Tools.isLA2()) {
            visibilityView(View.GONE, View.VISIBLE, View.VISIBLE, View.VISIBLE, View.GONE);
            isSlider = true;
        } else {
            visibilityView(View.GONE, View.VISIBLE, View.VISIBLE, View.VISIBLE, View.GONE);
            isSlider = true;
        }
        getTraining(isSlider);
    }


    private void visibilityView(int grafikAlumni, int grafikKaderisasi, int pelatihan, int gender, int dataKader) {
        llGrafikAlumni.setVisibility(grafikAlumni);
        llGrafikKaderisasi.setVisibility(grafikKaderisasi);
        llPelatihan.setVisibility(pelatihan);
        llGender.setVisibility(gender);
        llDataKader.setVisibility(dataKader);

        if (grafikAlumni == View.VISIBLE)
            grafikAlumni();

        if (grafikKaderisasi == View.VISIBLE)
            grafikKaderisasi();

        if (gender == View.VISIBLE)
            gender();
    }

    private void getTraining(boolean isSlider) {
        List<Training> list = new ArrayList<>();
        Call<TrainingResponse> call = service.getTraining(Constant.getToken(), "0");
        call.enqueue(new Callback<TrainingResponse>() {
            @Override
            public void onResponse(Call<TrainingResponse> call, Response<TrainingResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        int size = response.body().getData().size();
                        if (size > 0) {
                            for (int i = 0; i < size; i++) {
                                Training training = response.body().getData().get(i);
                                Contact contact = contactDao.getContactById(training.getId_user());
                                if (training.getNama_training().contains("LK1")) {
                                    contact.setLk1(training.getNama_training());
                                    contact.setTahun_lk1(training.getTahun());
                                } else if (training.getNama_training().contains("LK2")) {
                                    contact.setLk2(training.getNama_training());
                                } else if (training.getNama_training().contains("LK3")) {
                                    contact.setLk3(training.getNama_training());
                                } else if (training.getNama_training().contains("SC")) {
                                    contact.setSc(training.getNama_training());
                                } else if (training.getNama_training().contains("TID")) {
                                    contact.setTid(training.getNama_training());
                                }

                                contactDao.insertContact(contact);


                                training.setId(training.getId_user() + "-" + training.getTipe());
                                training.setId_user(training.getId_user());
                                training.setId_level(contact.getId_level());
                                training.setCabang(contact.getCabang());
                                training.setKomisariat(contact.getKomisariat());
                                training.setDomisili_cabang(contact.getDomisili_cabang());
                                training.setJenis_kelamin(contact.getJenis_kelamin());

                                if (trainingDao.checkTrainingAvailable(training.getId_user(), training.getTipe(), training.getTahun()) == null) {
                                    trainingDao.insertTraining(training);
                                }

                            }
                            pelatihanKader(isSlider);
                            dataKader();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<TrainingResponse> call, Throwable t) {

            }
        });
    }

    private void grafikAlumni() {
        String domisiliCabang = user.getDomisili_cabang() != null ? user.getDomisili_cabang() : "";
        ArrayList alumni = new ArrayList();
        for (int i = batas_tahun; i <= now; i++) {
            String query = Query.countReportAlumniAdmin3(domisiliCabang) + " AND tahun_daftar ='" + i + "'";
            int count = contactDao.countRawQueryContact(new SimpleSQLiteQuery(query));
            alumni.add(new Entry(i, (float) count));
        }

        LineDataSet dataSet = new LineDataSet(alumni, "");
        dataSet.setColors(new int[]{getResources().getColor(R.color.colorPrimary)});
        LineData data = new LineData(dataSet);

        final String[] quarters = new String[]{};

        YAxis yAxis = chartAlumni.getAxisLeft();
        yAxis.setLabelCount(5, false);
        yAxis.setAxisMaximum(100f);
        yAxis.setAxisMinimum(0f);

        chartAlumni.getAxisRight().setEnabled(false);

        XAxis xAxis = chartAlumni.getXAxis();
        xAxis.setSpaceMax(0.1f);
        xAxis.setSpaceMin(0.1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(quarters));

        chartAlumni.setData(data);
        chartAlumni.getDescription().setEnabled(false);
        chartAlumni.getLegend().setEnabled(false);
        chartAlumni.invalidate();
    }

    private void grafikKaderisasi() {
        String query;

        if (Tools.isAdmin1()) {
            query = Query.countReportKaderAdmin1(user.getKomisariat());
        } else if (Tools.isAdmin2() || Tools.isLA1()) {
            query = Query.countReportKaderAdmin2(user.getCabang());
        } else if (Tools.isAdmin3()) {
            query = Query.countReportAlumniAdmin3(user.getDomisili_cabang());
        } else {
            query = Query.countReportKaderLA2();
        }

        ArrayList meber = new ArrayList();
        for (int i = batas_tahun; i <= now; i++) {
            int count = contactDao.countRawQueryContact(new SimpleSQLiteQuery(query + " AND tahun_daftar = '" + i + "';"));
            meber.add(new Entry((float) i, (float) count));
        }

        // the labels that should be drawn on the XAxis
//        final String[] year = new String[] { ""+(now-4),""+(now-3), ""+(now-2), ""+(now-1), ""+now };
        final String[] quarters = new String[]{};

        LineDataSet dataSet = new LineDataSet(meber, "");
        dataSet.setColors(new int[]{getResources().getColor(R.color.colorPrimary)});
        LineData data = new LineData(dataSet);

        // the labels that should be drawn on the XAxis

        YAxis yAxis = chartKaderisasi.getAxisLeft();
        yAxis.setLabelCount(5, false);
        yAxis.setAxisMaximum(100f);
        yAxis.setAxisMinimum(0f);

        chartKaderisasi.getAxisRight().setEnabled(false);

        XAxis xAxis = chartKaderisasi.getXAxis();
        xAxis.setSpaceMax(0.1f);
        xAxis.setSpaceMin(0.1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(quarters));

        chartKaderisasi.setData(data);
        chartKaderisasi.getDescription().setEnabled(false);
        chartKaderisasi.getLegend().setEnabled(false);
        chartKaderisasi.invalidate();
    }

    private void pelatihanKader(boolean slider) {
        List<DataKader> list = new ArrayList<>();

        String query = "SELECT COUNT (*) FROM Training WHERE (id_level != 19 AND id_level !=20) AND (tahun != 8 AND tahun != 200 AND tahun != 255 AND tahun != 999 AND tahun != 1875) ";
        if (Tools.isAdmin1()) {
            query += " AND komisariat = '" + user.getKomisariat() + "' ";
        } else if (Tools.isAdmin2() || Tools.isLA1()) {
            query += " AND cabang = '" + user.getCabang() + "' ";
        } else if (Tools.isAdmin3()) {
            query += " AND cabang = '" + user.getDomisili_cabang() + "' ";
        }

        int lk1 = trainingDao.countRawQueryTraining(
                new SimpleSQLiteQuery(query + " AND tipe = '" + Constant.TRAINING_LK1 + "';"));
        int lk2 = trainingDao.countRawQueryTraining(
                new SimpleSQLiteQuery(query + " AND tipe = '" + Constant.TRAINING_LK2 + "';"));
        int lk3 = trainingDao.countRawQueryTraining(
                new SimpleSQLiteQuery(query + " AND tipe = '" + Constant.TRAINING_LK3 + "';"));
        int sc = trainingDao.countRawQueryTraining(
                new SimpleSQLiteQuery(query + " AND tipe = '" + Constant.TRAINING_SC + "';"));
        int tid = trainingDao.countRawQueryTraining(
                new SimpleSQLiteQuery(query + " AND tipe = '" + Constant.TRAINING_TID + "';"));

        int totalAll = lk1 + lk2 + lk3 + sc + tid;

        list.add(new DataKader(getString(R.string.semua), totalAll));
        list.add(new DataKader("LK1 (Basic Training)", lk1));
        list.add(new DataKader("LK2 (Intermediate Training)", lk2));
        list.add(new DataKader("LK3 (Advance Training)", lk3));
        list.add(new DataKader("SC (Senior Course)", sc));
        list.add(new DataKader("TID (Training Instruktur Dasar)", tid));
        LaporanDataPelatihanAdapter adapter = new LaporanDataPelatihanAdapter(getActivity(), slider, list, dataKader -> {
            Log.d("hallo", "pelatihanKader: " + dataKader.getNama());
            startActivity(new Intent(getActivity(), DetailReportActivity.class).putExtra(TYPE_DETAIL, Constant.PELATIHAN).putExtra(VALUE_DETAIL, dataKader.getNama()));
        });
        RecyclerView.LayoutManager llm;
        if (slider) {
            llm = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        } else {
            llm = new GridLayoutManager(getActivity(), 2);
        }
        rvDataPelatihan.setLayoutManager(llm);
        rvDataPelatihan.setAdapter(adapter);
    }

    private void gender() {
        String queryL;
        String queryP;

        if (Tools.isAdmin1()) {
            queryL = Query.countReportKaderAdmin1L(user.getKomisariat());
            queryP = Query.countReportKaderAdmin1P(user.getKomisariat());
        } else if (Tools.isLA1()) {
            queryL = Query.countReportKaderAdmin2L(user.getCabang());
            queryP = Query.countReportKaderAdmin2P(user.getCabang());
        } else {
            queryL = Query.countReportKaderLA2L();
            queryP = Query.countReportKaderLA2P();
        }

        int totalL = contactDao.countRawQueryContact(new SimpleSQLiteQuery(queryL));
        int totalP = contactDao.countRawQueryContact(new SimpleSQLiteQuery(queryP));
        Log.d("report_sihmi", "gender: l:" + totalL + ", p:" + totalP);

        double total = totalL + totalP;
        double percentP = ((double) totalP / total) * 100;
        double percentL = 100 - percentP;

        ArrayList numberMember = new ArrayList();
        numberMember.add(new PieEntry((float) percentL, "Laki-laki"));
        numberMember.add(new PieEntry((float) percentP, "Perempuan"));
        PieDataSet dataSet = new PieDataSet(numberMember, "");
        dataSet.setColors(new int[]{getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryLight)});
        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawSliceText(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.invalidate();
    }

    private void dataKader() {
//        String query = Query.countReportAlumniAdmin3(user.getDomisili_cabang());
//        String query = "SELECT COUNT (*) FROM Training WHERE (id_level != 19 AND id_level !=20) AND cabang = '" + user.getDomisili_cabang() + "' ";
        String query = "SELECT COUNT (*) FROM Training WHERE (id_level != 19 AND id_level !=20) AND domisili_cabang = '" + user.getDomisili_cabang() + "' AND " +
                "(tahun != 8 AND tahun != 200 AND tahun != 255 AND tahun != 999 AND tahun != 1875) ";

        List<DataKader> list = new ArrayList<>();
        for (int i = now; i >= 1947; i--) {
            int tahun = i;
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

            list.add(new DataKader(tahun, lk1, lk2, lk3, sc, tid));
        }

        LaporanDataKaderPelatihanAdapter adapter = new LaporanDataKaderPelatihanAdapter(getActivity(), list, dataKader -> {

        });

        rvKaderisasi.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvKaderisasi.setHasFixedSize(true);
        rvKaderisasi.setAdapter(adapter);
    }

    @OnClick({R.id.tv_detail_kaderisasi, R.id.tv_detail_alumni})
    public void click(TextView textView) {
        String type;
        if (textView.getId() == R.id.tv_detail_kaderisasi) {
            type = Constant.KADERISASI;
        } else if (textView.getId() == R.id.tv_detail_alumni) {
            type = Constant.ALUMNI;
        } else {
            type = Constant.PELATIHAN;
        }
        startActivity(new Intent(getActivity(), DetailReportActivity.class).putExtra(TYPE_DETAIL, type));
    }

    @OnClick(R.id.btn_detail)
    public void click() {
        startActivity(new Intent(getActivity(), DetailReportActivity.class).putExtra(TYPE_DETAIL, Constant.KADERISASI));
    }

}
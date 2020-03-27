package com.roma.android.sihmi.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.sqlite.db.SimpleSQLiteQuery;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.entity.Training;
import com.roma.android.sihmi.model.database.entity.User;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.InterfaceDao;
import com.roma.android.sihmi.model.database.interfaceDao.MasterDao;
import com.roma.android.sihmi.model.database.interfaceDao.TrainingDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.utils.Query;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.adapter.PelatihanAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailReportPelatihanActivity extends BaseActivity {
    public static final String VALUE_YEAR = "value_year";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_lk1)
    RecyclerView rvLk1;
    @BindView(R.id.rv_lk2)
    RecyclerView rvLk2;
    @BindView(R.id.rv_lk3)
    RecyclerView rvLk3;
    @BindView(R.id.rv_sc)
    RecyclerView rvSc;
    @BindView(R.id.rv_tid)
    RecyclerView rvTid;
    @BindView(R.id.no_lk1)
    TextView tvNoLk1;
    @BindView(R.id.no_lk2)
    TextView tvNoLk2;
    @BindView(R.id.no_lk3)
    TextView tvNoLk3;
    @BindView(R.id.no_sc)
    TextView tvNoSc;
    @BindView(R.id.no_tid)
    TextView tvNoTid;

    AppDb appDb;
    UserDao userDao;
    ContactDao contactDao;
    InterfaceDao interfaceDao;
    TrainingDao trainingDao;

    User user;
    int tahun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_report_pelatihan);
        ButterKnife.bind(this);
        initModule();
        initToolbar();
        initView();
    }

    private void initModule(){
        appDb = AppDb.getInstance(this);
        userDao = appDb.userDao();
        interfaceDao = appDb.interfaceDao();
        contactDao = appDb.contactDao();
        trainingDao = appDb.trainingDao();

        user = userDao.getUser();
        tahun = getIntent().getIntExtra(VALUE_YEAR, 0);
    }
    private void initToolbar() {
        toolbar.setTitle(getString(R.string.data_pelatihan));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initView(){
        initAdapterLK1();
        initAdapterLK2();
        initAdapterLK3();
        initAdapterSC();
        initAdapterTID();
    }

    private String getQuery(){
        String query = "";
        if (Tools.isAdmin1()){
            query = Query.reportTrainingAdmin1(user.getKomisariat());
        } else if (Tools.isAdmin2() || Tools.isLA1()){
            query = Query.ReportKaderAdmin2(user.getCabang());
        } else {
            query = Query.ReportKaderLA2();
        }
        return query;
    }

    private List<Contact> getListUsers(List<Training> trainingList){
        List<Contact> contactList = new ArrayList<>();
        for (int i = 0; i < trainingList.size() ; i++) {
            Contact c = contactDao.getContactById(trainingList.get(i).getId_user());
            contactList.add(c);
        }
        return contactList;
    }

    private void initAdapterLK1(){
        String query = getQuery()+" AND tipe LIKE 'LK1%' AND tahun = '"+tahun+"'";
        Log.d("hahahaha", "initAdapterLK1: "+query);
        List<Training> trainings = trainingDao.rawQueryTraining(new SimpleSQLiteQuery(query));
        PelatihanAdapter adapter = new PelatihanAdapter(this, getListUsers(trainings), contact -> startActivity(new Intent(DetailReportPelatihanActivity.this, ProfileChatActivity.class).putExtra("iduser", contact.get_id())));
        rvLk1.setLayoutManager(new LinearLayoutManager(this));
        rvLk1.setAdapter(adapter);
        isVisible(tvNoLk1, getListUsers(trainings));
    };

    private void initAdapterLK2(){
        String query = getQuery()+" AND tipe LIKE 'LK2%' AND tahun = '"+tahun+"'";
        Log.d("hahahaha", "initAdapterLK2: "+query);
        List<Training> trainings = trainingDao.rawQueryTraining(new SimpleSQLiteQuery(query));
        PelatihanAdapter adapter = new PelatihanAdapter(this, getListUsers(trainings), contact -> startActivity(new Intent(DetailReportPelatihanActivity.this, ProfileChatActivity.class).putExtra("iduser", contact.get_id())));
        rvLk2.setLayoutManager(new LinearLayoutManager(this));
        rvLk2.setAdapter(adapter);
        isVisible(tvNoLk2, getListUsers(trainings));
    };

    private void initAdapterLK3(){
        String query = getQuery()+" AND tipe LIKE 'LK3%' AND tahun = '"+tahun+"'";
        Log.d("hahahaha", "initAdapterLK3: "+query);
        List<Training> trainings = trainingDao.rawQueryTraining(new SimpleSQLiteQuery(query));
        PelatihanAdapter adapter = new PelatihanAdapter(this, getListUsers(trainings), contact -> startActivity(new Intent(DetailReportPelatihanActivity.this, ProfileChatActivity.class).putExtra("iduser", contact.get_id())));
        rvLk3.setLayoutManager(new LinearLayoutManager(this));
        rvLk3.setAdapter(adapter);
        isVisible(tvNoLk3, getListUsers(trainings));
    };

    private void initAdapterSC(){
        String query = getQuery()+" AND tipe LIKE 'SC%' AND tahun = '"+tahun+"'";
        Log.d("hahahaha", "initAdapterSC: "+query);
        List<Training> trainings = trainingDao.rawQueryTraining(new SimpleSQLiteQuery(query));
        PelatihanAdapter adapter = new PelatihanAdapter(this, getListUsers(trainings), contact -> startActivity(new Intent(DetailReportPelatihanActivity.this, ProfileChatActivity.class).putExtra("iduser", contact.get_id())));
        rvSc.setLayoutManager(new LinearLayoutManager(this));
        rvSc.setAdapter(adapter);
        isVisible(tvNoSc, getListUsers(trainings));
    };

    private void initAdapterTID(){
        String query = getQuery()+" AND tipe LIKE 'TID%' AND tahun = '"+tahun+"'";
        Log.d("hahahaha", "initAdapterTID: "+query);
        List<Training> trainings = trainingDao.rawQueryTraining(new SimpleSQLiteQuery(query));
        PelatihanAdapter adapter = new PelatihanAdapter(this, getListUsers(trainings), contact -> startActivity(new Intent(DetailReportPelatihanActivity.this, ProfileChatActivity.class).putExtra("iduser", contact.get_id())));
        rvTid.setLayoutManager(new LinearLayoutManager(this));
        rvTid.setAdapter(adapter);
        isVisible(tvNoTid, getListUsers(trainings));
    };

    private void isVisible(TextView textView, List<Contact> list){
        if (list.size() > 0){
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
        }
    }
}

package com.roma.android.sihmi.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.core.CoreApplication;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.entity.Training;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.LevelDao;
import com.roma.android.sihmi.model.database.interfaceDao.TrainingDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.ContactResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.adapter.ContactAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    MasterService service;
    ContactAdapter adapter;

    AppDb appDb;
    UserDao userDao;
    LevelDao levelDao;
    ContactDao contactDao;
    TrainingDao trainingDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        ButterKnife.bind(this);

        toolbar.setTitle(getString(R.string.cari_kontak).toUpperCase());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        service = ApiClient.getInstance().getApi();
        appDb = AppDb.getInstance(this);
        userDao = appDb.userDao();
        levelDao = appDb.levelDao();
        contactDao = appDb.contactDao();
        trainingDao = appDb.trainingDao();
        initAdapter(contactDao.getAllListContact("%"+userDao.getUser().get_id()+"%"));
        getContact();
    }

    private void getContact(){
        Call<ContactResponse> call = service.getContact(Constant.getToken());
        if (Tools.isOnline(this)) {
            Tools.showProgressDialog(this, "Load Data...");
            call.enqueue(new Callback<ContactResponse>() {
                @Override
                public void onResponse(Call<ContactResponse> call, Response<ContactResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equalsIgnoreCase("success")) {
                            List<Contact> contacts = response.body().getData();
                            for (int i = 0; i < contacts.size() ; i++) {
                                Contact c = contacts.get(i);
                                c.setId_level(levelDao.getPengajuanLevel(c.getId_roles()));
                                c.setTahun_daftar(Tools.getYearFromMillis(Long.parseLong(c.getTanggal_daftar())));
                                if (c.getTanggal_lk1() != null && !c.getTanggal_lk1().trim().isEmpty()){
                                    String[] lk1 = c.getTanggal_lk1().split("-");
                                    if (c.getTahun_lk1() == null || c.getTahun_lk1().trim().isEmpty()) {
                                        c.setTahun_lk1(lk1[2]);
                                    }
                                    Training training = new Training();
                                    training.setId(c.get_id()+"-LK1 (Basic Training)");
                                    training.setId_user(c.get_id());
                                    training.setId_level(c.getId_level());
                                    training.setTipe("LK1 (Basic Training)");
                                    training.setTahun(lk1[2]);
                                    training.setCabang(c.getCabang());
                                    training.setKomisariat(c.getKomisariat());
                                    training.setDomisili_cabang(c.getDomisili_cabang());
                                    training.setJenis_kelamin(c.getJenis_kelamin());
                                    if (trainingDao.checkTrainingAvailable(c.get_id(), "LK1 (Basic Training)", lk1[2]) == null){
                                        trainingDao.insertTraining(training);
                                    }
                                }
                                contactDao.insertContact(c);
                            }
                        } else {
                            Toast.makeText(ContactActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ContactActivity.this, "" + response.message(), Toast.LENGTH_SHORT).show();
                    }
                    Tools.dissmissProgressDialog();
                }

                @Override
                public void onFailure(Call<ContactResponse> call, Throwable t) {
                    Toast.makeText(ContactActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Tools.dissmissProgressDialog();
                }
            });
        } else{
            Toast.makeText(this, "Tidak Ada Internet!", Toast.LENGTH_SHORT).show();
        }
    }

    private void initAdapter(List<Contact> list){
        adapter = new ContactAdapter(this, list, new ContactAdapter.itemClickListener() {
            @Override
            public void onItemClick(Contact contact) {
                startActivity(new Intent(ContactActivity.this, ChatActivity.class)
                        .putExtra("nama", contact.getFullName()).putExtra("iduser", contact.get_id()));
                finish();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.cari));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                Toast.makeText(ContactActivity.this, query, Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.updateData(contactDao.getSearchListContact("%"+userDao.getUser().get_id()+"%", "%"+newText+"%"));
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

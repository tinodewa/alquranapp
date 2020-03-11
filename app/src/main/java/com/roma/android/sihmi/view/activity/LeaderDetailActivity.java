package com.roma.android.sihmi.view.activity;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.roma.android.sihmi.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LeaderDetailActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_leader_detail)
    RecyclerView rvLeaderDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_detail);
        ButterKnife.bind(this);

        String title = getIntent().getStringExtra("leader");
        toolbar.setTitle(title.toUpperCase());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        List<Contact> list = new ArrayList<>();
//        if (title.toLowerCase().contains("nasional")){
//            list = CoreApplication.get().getAppDb().interfaceDao().getListPBHMI();
//        } else if (title.toLowerCase().contains("cabang")){
//            list = CoreApplication.get().getAppDb().interfaceDao().getListCabang();
//        } else if (title.toLowerCase().contains("komisariat")){
//            list = CoreApplication.get().getAppDb().interfaceDao().getListKomisariat();
//        }
//
//        LeaderDetailAdapter adapter = new LeaderDetailAdapter(this, list, new LeaderDetailAdapter.itemClickListener() {
//            @Override
//            public void onItemClick(Contact contact, int type) {
//                if (type == Constant.READ){
////                    startActivity(new Intent(LeaderDetailActivity.this, ProfilActivity.class).putExtra(ProfilActivity.ID_ROLES, contact.get_id()));
//                }
//            }
//        });
//        rvLeaderDetail.setLayoutManager(new LinearLayoutManager(this));
//        rvLeaderDetail.setAdapter(adapter);
    }
}

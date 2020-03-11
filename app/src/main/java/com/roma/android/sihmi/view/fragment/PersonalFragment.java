package com.roma.android.sihmi.view.fragment;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.core.CoreApplication;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Chat;
import com.roma.android.sihmi.model.database.entity.Chating;
import com.roma.android.sihmi.model.database.entity.Chatlist;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.entity.notification.Token;
import com.roma.android.sihmi.model.database.interfaceDao.ChatingDao;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.activity.ChatActivity;
import com.roma.android.sihmi.view.activity.ContactActivity;
import com.roma.android.sihmi.view.adapter.ChatAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalFragment extends Fragment {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout refreshLayout;

    DatabaseReference reference;
    List<Chatlist> list;
    List<Contact> contactList;
    List<Chating> chatings = new ArrayList<>();

    ChatAdapter adapter;
    private String searchText="";

    DatabaseReference referenceChats;
    ValueEventListener eventListenerChats;

    AppDb appDb;
    UserDao userDao;
    ContactDao contactDao;
    ChatingDao chatingDao;


    public PersonalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_konstitusi, container, false);
        ButterKnife.bind(this, v);
        setHasOptionsMenu(true);

        appDb = AppDb.getInstance(getContext());
        userDao = appDb.userDao();
        contactDao = appDb.contactDao();
        chatingDao = appDb.chatingDao();

        list = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(userDao.getUser().get_id());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("roma", "onDataChange: personalfragment 86");
                list.clear();
                new ListChatAsync().execute(dataSnapshot);

//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    Chatlist chatlist = snapshot.getValue(Chatlist.class);
//                    list.add(chatlist);
//                }
//                //initAdapter(list.size());
//                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        initAdapter();

        fab.setOnClickListener(v1 -> startActivity(new Intent(getActivity().getApplicationContext(), ContactActivity.class)));

        refreshLayout.setRefreshing(false);
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
//                initAdapter();
        });

        chatingDao.getListChating().observe(getActivity(), chatings -> adapter.updateData(chatings));

        updateToken(FirebaseInstanceId.getInstance().getToken());

        return v;
    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(userDao.getUser().get_id()).setValue(token1);
    }

    private void initAdapter(){
        adapter = new ChatAdapter(getContext().getApplicationContext(), chatings, (chating, isLongCLick) -> {
            Contact contact = contactDao.getContactById(chating.get_id());
            if (!isLongCLick){
                startActivity(new Intent(getContext().getApplicationContext(), ChatActivity.class)
                        .putExtra("nama", contact.getFullName()).putExtra("iduser", contact.get_id()));

            } else {
                Tools.showDialogRb(getActivity(), chating.get_id());
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext()));
        recyclerView.setAdapter(adapter);
        refreshLayout.setRefreshing(false);
    }

    private void chatList(){
        contactList = new ArrayList<>();
        List<Contact> contactList1 = contactDao.getAllListContact("%"+userDao.getUser().get_id()+"%");
//        for (Chatlist chatlist : list){
//            for (int i = 0; i < contactList.size(); i++) {
//                if (chatlist.get_id().equals(contactList.get(i))){
//
//                }
//            }
//        }
        if (contactList1.size()>0) {
            for (Contact contact : contactList1) {
                for (Chatlist chatlist : list) {
                    if (contact.get_id().equals(chatlist.get_id())) {
                        contactList.add(contact);
                    }
                }
            }
        }
        getListChating(contactList);
    }

    private void getListChating(List<Contact> contactList) {
        Log.e("roma", "onDataChange getListChating: Chats_v2");
        chatingDao.deleteAllChating();
        referenceChats = FirebaseDatabase.getInstance().getReference("Chats_v2");
        eventListenerChats = referenceChats.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("roma", "onDataChange: personalfragment 187");
                new getListChatingAsync(contactList).execute(dataSnapshot);

//                for (int i = 0; i < contactList.size(); i++) {
//                    String myuserid = userDao.getUser().get_id();
//                    int unReadCount = 0;
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        Chat chat = snapshot.getValue(Chat.class);
//                        String userid = contactList.get(i).get_id();
//                        if (chat.getReceiver().equals(myuserid) && chat.getSender().equals(userid) ||
//                                chat.getReceiver().equals(userid) && chat.getSender().equals(myuserid)) {
//                            if (!chat.isIsseen()){
//                                unReadCount++;
//                            }
//                            String lastMsg;
//                            if (chat.getType().equalsIgnoreCase(Constant.IMAGE)){
//                                lastMsg = Constant.IMAGE;
//                            } else if (chat.getType().equalsIgnoreCase(Constant.DOCUMENT)){
//                                lastMsg = Constant.DOCUMENT;
//                            } else {
//                                lastMsg = chat.getMessage();
//                            }
//                            String name = contactDao.getContactById(userid).getFullName();
//                            chatingDao.insertChating(new Chating(userid, name, chat.getReceiver()+"split100x"+lastMsg, chat.getTime(), unReadCount));
//                        }
//                    }
//
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            SearchView searchView = (SearchView) item.getActionView();
            searchView.setQueryHint(getString(R.string.cari));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    chatings = chatingDao.getSearchChating("%"+newText+"%");
                    adapter.updateData(chatings);
                    return false;
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        referenceChats.removeEventListener(eventListenerChats);
    }

    @Override
    public void onPause() {
        super.onPause();
//        referenceChats.removeEventListener(eventListenerChats);
    }

    private class ListChatAsync extends AsyncTask<DataSnapshot, Void, Boolean> {
        @Override
        protected Boolean doInBackground(DataSnapshot... dataSnapshots) {
            list.clear();
            for (DataSnapshot snapshot : dataSnapshots[0].getChildren()){
                Chatlist chatlist = snapshot.getValue(Chatlist.class);
                list.add(chatlist);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            chatList();
        }
    }

    private class getListChatingAsync extends AsyncTask<DataSnapshot, Void, Boolean> {
        List<Contact> contactList;

        public getListChatingAsync(List<Contact> contactList) {
            this.contactList = contactList;
        }

        @Override
        protected Boolean doInBackground(DataSnapshot... dataSnapshots) {
            try {
                for (int i = 0; i < contactList.size(); i++) {
                    String myuserid = userDao.getUser().get_id();
                    int unReadCount = 0;
                    for (DataSnapshot snapshot : dataSnapshots[0].getChildren()) {
                        Chat chat = snapshot.getValue(Chat.class);
                        String userid = list.get(i).get_id();
                        if (chat.getReceiver().equals(myuserid) && chat.getSender().equals(userid) ||
                                chat.getReceiver().equals(userid) && chat.getSender().equals(myuserid)) {
                            if (!chat.isIsseen()) {
                                unReadCount++;
                            }
                            String lastMsg;
                            if (chat.getType().equalsIgnoreCase(Constant.IMAGE)) {
                                lastMsg = Constant.IMAGE;
                            } else if (chat.getType().equalsIgnoreCase(Constant.DOCUMENT)) {
                                lastMsg = Constant.DOCUMENT;
                            } else {
                                lastMsg = chat.getMessage();
                            }
                            String name = contactDao.getContactById(userid).getFullName();
                            chatingDao.insertChating(new Chating(userid, name, chat.getReceiver() + "split100x" + lastMsg, chat.getTime(), unReadCount));
                        }
                    }

                }
            } catch (IndexOutOfBoundsException e){
                return true;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }

}

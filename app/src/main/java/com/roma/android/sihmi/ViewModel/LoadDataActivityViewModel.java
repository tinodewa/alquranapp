package com.roma.android.sihmi.ViewModel;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Chat;
import com.roma.android.sihmi.model.database.entity.Chating;
import com.roma.android.sihmi.model.database.entity.Chatlist;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.entity.GroupChat;
import com.roma.android.sihmi.model.database.entity.GroupChatFirebase;
import com.roma.android.sihmi.model.database.entity.GroupChatSeen;
import com.roma.android.sihmi.model.database.entity.Level;
import com.roma.android.sihmi.model.database.entity.LoadDataState;
import com.roma.android.sihmi.model.database.entity.Training;
import com.roma.android.sihmi.model.database.entity.User;
import com.roma.android.sihmi.model.database.interfaceDao.ChatingDao;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.GroupChatDao;
import com.roma.android.sihmi.model.database.interfaceDao.LevelDao;
import com.roma.android.sihmi.model.database.interfaceDao.LoadDataStateDao;
import com.roma.android.sihmi.model.database.interfaceDao.MasterDao;
import com.roma.android.sihmi.model.database.interfaceDao.TrainingDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.ContactResponse;
import com.roma.android.sihmi.model.response.GeneralResponse;
import com.roma.android.sihmi.model.response.LevelResponse;
import com.roma.android.sihmi.model.response.MasterResponse;
import com.roma.android.sihmi.model.response.TrainingResponse;
import com.roma.android.sihmi.service.AgendaWorkManager;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class LoadDataActivityViewModel extends ViewModel {
    private AppDb appDb;
    private LoadDataStateDao loadDataStateDao;
    private MasterDao masterDao;
    private MasterService service;
    private UserDao userDao;
    private LevelDao levelDao;
    private ContactDao contactDao;
    private TrainingDao trainingDao;
    private GroupChatDao groupChatDao;
    private ChatingDao chatingDao;
    private String token, errorMessage;
    private MutableLiveData<Boolean> loadDataError;
    private MutableLiveData<Boolean> isLoggedOut;
    private Context context;

    private List<Chatlist> list;

    public void init(Context context) {
        this.context = context;
        appDb = AppDb.getInstance(context);
        loadDataStateDao = appDb.loadDataStateDao();
        levelDao = appDb.levelDao();
        masterDao = appDb.masterDao();
        userDao = appDb.userDao();
        contactDao = appDb.contactDao();
        trainingDao = appDb.trainingDao();
        groupChatDao = appDb.groupChatDao();
        chatingDao = appDb.chatingDao();

        service = ApiClient.getInstance().getApi();
        loadDataError = new MutableLiveData<>();
        loadDataError.setValue(false);
        isLoggedOut = new MutableLiveData<>();
        isLoggedOut.setValue(false);

        list = new ArrayList<>();

        token = Constant.getToken();
    }

    public LiveData<Boolean> getIsLoaded() {
        return loadDataStateDao.getIsLoadedLiveData();
    }

    public LiveData<Boolean> getIsError() {
        return loadDataError;
    }

    public MutableLiveData<Boolean> getIsLoggedOut() {
        return isLoggedOut;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void getData () {
        loadDataError.setValue(false);

        Call<MasterResponse> call = service.getMaster(token, "0");
        call.enqueue(new Callback<MasterResponse>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<MasterResponse> call, Response<MasterResponse> response) {
                if (response.isSuccessful()){
                    assert response.body() != null;
                    if (response.body().getStatus().equalsIgnoreCase("ok")){
                        User user = userDao.getUser();
                        String alumni_cabang = user.getDomisili_cabang() != null ? user.getDomisili_cabang() : "";
                        masterDao.insertMaster(response.body().getData());
                        getListGroupChat(user.getCabang(), user.getKomisariat(), alumni_cabang);
                        new Handler().postDelayed(() -> getLevel(), 1000);
                    }
                }
                else {
                    errorMessage = context.getString(R.string.gagal_memuat_data);
                    loadDataError.setValue(true);
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<MasterResponse> call, Throwable t) {
                errorMessage = context.getString(R.string.gagal_memuat_data);
                loadDataError.setValue(true);
            }
        });
    }

    private void getLevel(){
        Call<LevelResponse> call = service.getLevelLK();
        call.enqueue(new Callback<LevelResponse>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<LevelResponse> call, Response<LevelResponse> response) {
                if (response.isSuccessful()){
                    assert response.body() != null;
                    if (response.body().getStatus().equalsIgnoreCase("success")){
                        if (response.body().getData().size() > 0){
                            for (int i = 0; i < response.body().getData().size(); i++) {
                                Level level = response.body().getData().get(i);
                                if (level.getId() == 1){
                                    level.setNewName("Non Kader");
                                } else if (level.getId() == 2){
                                    level.setNewName("Kader");
                                } else if (level.getId() == 5){
                                    level.setNewName("Admin Komisariat");
                                } else if (level.getId() == 8){
                                    level.setNewName("Admin BPL");
                                } else if (level.getId() == 11){
                                    level.setNewName("Admin Alumni");
                                } else if (level.getId() == 13){
                                    level.setNewName("Admin Cabang");
                                } else if (level.getId() == 16){
                                    level.setNewName("Admin PBHMI");
                                } else if (level.getId() == 19){
                                    level.setNewName("Admin Nasional");
                                } else if (level.getId() == 20){
                                    level.setNewName("Super Admin");
                                }
                                levelDao.insertLevel(response.body().getData().get(i));
                            }
                        }
                        new Handler().postDelayed(() -> {
                            getContact();
                            saveProfiletoContact();
                        }, 1000);

                    } else {
                        // error
                        errorMessage = response.body().getMessage();
                        loadDataError.setValue(true);
                    }
                } else {
                    // error
                    errorMessage = context.getString(R.string.gagal_memuat_data);
                    loadDataError.setValue(true);
                }

            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<LevelResponse> call, Throwable t) {
                errorMessage = context.getString(R.string.gagal_memuat_data);
                loadDataError.setValue(true);
            }
        });
    }

    private void getContact(){
        Call<ContactResponse> call = service.getContact(token);
        call.enqueue(new Callback<ContactResponse>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<ContactResponse> call, Response<ContactResponse> response) {
                if (response.isSuccessful()){
                    assert response.body() != null;
                    if (response.body().getStatus().equalsIgnoreCase("success")){
                        List<Contact> contacts = response.body().getData();
                        for (int i = 0; i < contacts.size() ; i++) {
                            Contact c = contacts.get(i);
                            c.setId_level(levelDao.getPengajuanLevel(c.getId_roles()));
                            c.setTahun_daftar(Tools.getYearFromMillis(Long.parseLong(c.getTanggal_daftar())));

                            // TODO: Fix this training data with DataLainActivity and Via API Database
                            if (c.getTanggal_lk1() != null && !c.getTanggal_lk1().trim().isEmpty()){
                                String[] lk1 = c.getTanggal_lk1().split("-");
                                c.setTahun_lk1(lk1[2]);
                                c.setLk1("LK1 (Basic Training)");
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
                        new Handler().postDelayed(() -> getTraining(), 1000);
                    } else {
                        // error
                        errorMessage = response.body().getMessage();
                        loadDataError.setValue(true);
                    }
                } else {
                    // error
                    errorMessage = context.getString(R.string.gagal_memuat_data);
                    loadDataError.setValue(true);
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<ContactResponse> call, Throwable t) {
                // error
                errorMessage = context.getString(R.string.gagal_memuat_data);
                loadDataError.setValue(true);
            }
        });
    }

    private void saveProfiletoContact(){
        User user = userDao.getUser();

        int id_level = levelDao.getPengajuanLevel(user.getId_roles());
        Contact contact = new Contact(user.get_id(), user.getBadko(), user.getCabang(), user.getKorkom(), user.getKomisariat(),
                user.getId_roles(), user.getImage(), user.getNama_depan(), user.getNama_belakang(), user.getNama_panggilan(),
                user.getJenis_kelamin(), user.getNomor_hp(), user.getAlamat(), user.getUsername(), user.getTanggal_daftar(),
                user.getTempat_lahir(), user.getTanggal_lahir(), user.getStatus_perkawinan(), user.getKeterangan(), user.getDevice_name(),
                user.getLast_login(), user.getEmail(), user.getStatus_online(), id_level);
        contact.setDomisili_cabang(user.getDomisili_cabang());
        contact.setTahun_daftar(Tools.getYearFromMillis(Long.parseLong(contact.getTanggal_daftar())));

        if (contact.getTanggal_lk1() != null && !contact.getTanggal_lk1().trim().isEmpty()){
            String[] lk1 = contact.getTanggal_lk1().split("-");
            contact.setTahun_lk1(lk1[2]);
            contact.setLk1("LK1 (Basic Training)");

            Training training = new Training();
            training.setId(contact.get_id()+"-LK1 (Basic Training)");
            training.setId_user(contact.get_id());
            training.setId_level(contact.getId_level());
            training.setTipe("LK1 (Basic Training)");
            training.setTahun(lk1[2]);
            training.setCabang(contact.getCabang());
            training.setKomisariat(contact.getKomisariat());
            training.setDomisili_cabang(contact.getDomisili_cabang());
            training.setJenis_kelamin(contact.getJenis_kelamin());
            if (trainingDao.checkTrainingAvailable(contact.get_id(), "LK1 (Basic Training)", lk1[2]) == null){
                trainingDao.insertTraining(training);
            }
        }
        contactDao.insertContact(contact);
    }

    private void getListGroupChat(String namaKota, String namaUniv, String alumniCabang){
        // TODO: Fix group chat violation
        if (namaKota != null && !namaKota.trim().isEmpty()){
            if (groupChatDao.getGroupChatByName(namaKota) == null){
                addGroupChat(namaKota);
            }
        }

        if (namaUniv != null && !namaUniv.trim().isEmpty()){
            if (groupChatDao.getGroupChatByName(namaUniv) == null){
                addGroupChat(namaUniv);
            }
        }

        if (alumniCabang != null && !alumniCabang.trim().isEmpty()){
            if (groupChatDao.getGroupChatByName("Alumni "+alumniCabang) == null){
                addGroupChat("Alumni "+alumniCabang);
            }
        }

        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatGroup_v2");

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()){
                    GroupChatFirebase groupChatFirebase = s.getValue(GroupChatFirebase.class);
                    assert groupChatFirebase != null;
                    groupChatDao.insertGroupChat(new GroupChat(groupChatFirebase.getNama(), groupChatFirebase.getImage()));
                    getGroupChat(groupChatFirebase);

//                    if (Tools.isSecondAdmin() || Tools.isSuperAdmin()){
//                        CoreApplication.get().getAppDb().interfaceDao().insertGroupChat(new GroupChat(groupChatFirebase.getNama(), groupChatFirebase.getImage()));
//                        getGroupChat(groupChatFirebase);
//                    } else {
//                        if ((groupChatFirebase.getNama().equalsIgnoreCase(namaKota)) || (groupChatFirebase.getNama().equalsIgnoreCase(namaUniv) || groupChatFirebase.getNama().equalsIgnoreCase("nasional"))
//                                || (groupChatFirebase.getNama().equalsIgnoreCase("alumni "+alumniCabang))){
//                            CoreApplication.get().getAppDb().interfaceDao().insertGroupChat(new GroupChat(groupChatFirebase.getNama(), groupChatFirebase.getImage()));
//                            getGroupChat(groupChatFirebase);
//                        }
//                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getGroupChat(GroupChatFirebase groupChatFirebase){
        DatabaseReference referenceChats = FirebaseDatabase.getInstance().getReference("Chats_v2");
        referenceChats.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("roma", "onDataChange: loginProcess 361");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    GroupChat groupChat = new GroupChat(groupChatFirebase.getNama(), groupChatFirebase.getImage());
                    if (chat != null && chat.getReceiver().equals(groupChatFirebase.getNama())) {
                        groupChat.setLast_msg(chat.getType()+"split100x"+chat.getMessage());
                        groupChat.setTime(chat.getTime());
                    }
                    groupChatDao.insertGroupChat(groupChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addGroupChat(String name){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("nama", name);
        hashMap.put("image", "");

        databaseReference.child("ChatGroup_v2").child(name).setValue(hashMap);
    }

    private void getTraining(){
        Call<TrainingResponse> call = service.getTraining(token, "0");
        call.enqueue(new Callback<TrainingResponse>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<TrainingResponse> call, Response<TrainingResponse> response) {
                if (response.isSuccessful()){
                    assert response.body() != null;
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        int size = response.body().getData().size();
                        if (size > 0) {
                            for (int i = 0; i < size ; i++) {
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

                                training.setId(training.getId_user()+"-"+training.getTipe());
                                training.setId_user(training.getId_user());
                                training.setId_level(contact.getId_level());
                                training.setCabang(contact.getCabang());
                                training.setKomisariat(contact.getKomisariat());
                                training.setDomisili_cabang(contact.getDomisili_cabang());
                                training.setJenis_kelamin(contact.getJenis_kelamin());

                                if (trainingDao.checkTrainingAvailable(training.getId_user(), training.getTipe(), training.getTahun()) == null){
                                    trainingDao.insertTraining(training);
                                }
                            }
                        }

                        getPersonalChat();
                    }
                    else {
                        // error
                        errorMessage = context.getString(R.string.gagal_memuat_data);
                        loadDataError.setValue(true);
                    }
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<TrainingResponse> call, Throwable t) {
                // error
                errorMessage = context.getString(R.string.gagal_memuat_data);
                loadDataError.setValue(true);
            }
        });
    }

    public void logout() {
        ApiClient.getInstance().getApi().logout(Constant.getToken())
                .enqueue(new Callback<GeneralResponse>() {
                    @Override
                    @EverythingIsNonNull
                    public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                        Constant.logout();
                        clearData();
                    }

                    @Override
                    @EverythingIsNonNull
                    public void onFailure(Call<GeneralResponse> call, Throwable t) {
                        Constant.logout();
                        clearData();
                    }
                });
    }

    private void clearData() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        Constant.logout();
        appDb.clearAllTables();


        AgendaWorkManager.cancelAllReminder();
        isLoggedOut.setValue(true);
    }

    private void getPersonalChat() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(userDao.getUser().get_id());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                new ListChatAsync().execute(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("StaticFieldLeak")
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

    private void chatList(){
        List<Contact> contactList = new ArrayList<>();
        List<Contact> contactList1 = this.contactDao.getAllListContact("%"+userDao.getUser().get_id()+"%");

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
        DatabaseReference referenceChats = FirebaseDatabase.getInstance().getReference("Chats_v2");
        referenceChats.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                new getListChatingAsync(contactList).execute(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class getListChatingAsync extends AsyncTask<DataSnapshot, Void, Boolean> {
        List<Contact> contactList;

        getListChatingAsync(List<Contact> contactList) {
            this.contactList = contactList;
        }

        @Override
        protected Boolean doInBackground(DataSnapshot... dataSnapshots) {
            List<Chating> thisChating;
            try {
                for (int i = 0; i < contactList.size(); i++) {
                    String myuserid = userDao.getUser().get_id();
                    int unReadCount = 0;
                    for (DataSnapshot snapshot : dataSnapshots[0].getChildren()) {
                        Chat chat = snapshot.getValue(Chat.class);
                        String userid = list.get(i).get_id();
                        thisChating = chatingDao.getChatingById(userid);
                        if (chat != null && (chat.getReceiver().equals(myuserid) && chat.getSender().equals(userid) ||
                                chat.getReceiver().equals(userid) && chat.getSender().equals(myuserid))) {
                            if ((thisChating.size() > 0 && chat.getTime() >= thisChating.get(0).getTime_message()) || thisChating.size() == 0) {
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

                }
            } catch (Exception e){
                e.printStackTrace();
                return true;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            getGroupChatUnRead();
        }
    }

    private void getGroupChatUnRead() {
        DatabaseReference referenceUsers = FirebaseDatabase.getInstance().getReference("Users");
        referenceUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                new getGroupChatUnReadAsync().execute(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class getGroupChatUnReadAsync extends AsyncTask <DataSnapshot, Void, Boolean> {

        @Override
        protected Boolean doInBackground(DataSnapshot... dataSnapshots) {
            for (DataSnapshot snapshot : dataSnapshots[0].getChildren()) {
                GroupChatSeen groupChatSeen = snapshot.getValue(GroupChatSeen.class);
                assert groupChatSeen != null;
                if (groupChatSeen.getId_user() != null && groupChatSeen.getId_user().equals(userDao.getUser().get_id())) {
                    groupChatDao.updateLastSeen(groupChatSeen.getNama(), groupChatSeen.getLast_seen());
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            getGroupChat();
        }
    }

    private void getGroupChat(){
        DatabaseReference referenceChats = FirebaseDatabase.getInstance().getReference("Chats_v2");
        referenceChats.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                new getGroupChatAsync().execute(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class getGroupChatAsync extends AsyncTask<DataSnapshot, Void, Boolean> {

        @Override
        protected Boolean doInBackground(DataSnapshot... dataSnapshots) {
            try {
                for (DataSnapshot snapshot : dataSnapshots[0].getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    assert chat != null;
                    GroupChat groupChat = groupChatDao.getGroupChatByName(chat.getReceiver());
                    if (groupChat != null) {
                        if (chat.getTime() >= groupChat.getTime()) {
                            groupChat.setLast_msg(chat.getType() + "split100x" + chat.getMessage());
                            groupChat.setTime(chat.getTime());
                            groupChat.setLast_seen(groupChat.getLast_seen());
                            int unread;
                            if (chat.getTime() > groupChat.getLast_seen() && !chat.getSender().equals(userDao.getUser().get_id())) {
                                unread = groupChat.getUnread() + 1;
                            } else {
                                unread = 0;
                            }
                            groupChat.setUnread(unread);
                            groupChatDao.insertGroupChat(groupChat);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            loadDataStateDao.insertLoadDataState(new LoadDataState(true, System.currentTimeMillis()));
        }
    }
}

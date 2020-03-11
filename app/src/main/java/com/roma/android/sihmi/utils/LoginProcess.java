package com.roma.android.sihmi.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Account;
import com.roma.android.sihmi.model.database.entity.Chat;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.entity.GroupChat;
import com.roma.android.sihmi.model.database.entity.GroupChatFirebase;
import com.roma.android.sihmi.model.database.entity.Level;
import com.roma.android.sihmi.model.database.entity.Training;
import com.roma.android.sihmi.model.database.entity.User;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.GroupChatDao;
import com.roma.android.sihmi.model.database.interfaceDao.LevelDao;
import com.roma.android.sihmi.model.database.interfaceDao.MasterDao;
import com.roma.android.sihmi.model.database.interfaceDao.PengajuanDao;
import com.roma.android.sihmi.model.database.interfaceDao.TrainingDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.ContactResponse;
import com.roma.android.sihmi.model.response.LevelResponse;
import com.roma.android.sihmi.model.response.LoginResponse;
import com.roma.android.sihmi.model.response.MasterResponse;
import com.roma.android.sihmi.model.response.ProfileResponse;
import com.roma.android.sihmi.model.response.TrainingResponse;
import com.roma.android.sihmi.view.activity.MainActivity;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginProcess {
    Activity activity;
    String username, password, image;
    String token;
    ProgressDialog dialog;
    MasterService service;
    AppDb appDb;

    MasterDao masterDao;
    UserDao userDao;
    LevelDao levelDao;
//    PengajuanDao pengajuanDao;
    TrainingDao trainingDao;
    ContactDao contactDao;
    GroupChatDao groupChatDao;

    DatabaseReference referenceChats, referenceUsers;
    ValueEventListener eventListenerChats, eventListenerUsers;

    public LoginProcess(Activity activity, MasterService service) {
        this.activity = activity;
        this.service = service;
        appDb = AppDb.getInstance(activity);
        masterDao = appDb.masterDao();
        userDao = appDb.userDao();
        levelDao = appDb.levelDao();
//        pengajuanDao = appDb.pengajuanDao();
        trainingDao = appDb.trainingDao();
        groupChatDao = appDb.groupChatDao();
        contactDao = appDb.contactDao();
    }

    public void login (String username, String password){
        this.username = username;
        this.password = password;

        dialog = new ProgressDialog(activity);
        dialog.setMessage(activity.getString(R.string.loginn));
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        HashMap<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);

        Call<LoginResponse> call = service.login(username, password, Build.MODEL);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("ok")) {
                        Constant.login();
                        token = response.body().getToken();

                        new Handler().postDelayed(() -> getData(), 1000);
                    } else {
                        dialog.dismiss();
                        Tools.showDialogAlert(activity, response.body().getMessage());
                    }
                } else {
                    dialog.dismiss();
                    Tools.showToast(activity, activity.getString(R.string.gagal_login));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                dialog.dismiss();
                Tools.showToast(activity, activity.getString(R.string.gagal_login));
            }
        });
    }

    private void getData (){
        Call<MasterResponse> call = service.getMaster(token, "0");
        call.enqueue(new Callback<MasterResponse>() {
            @Override
            public void onResponse(Call<MasterResponse> call, Response<MasterResponse> response) {
                if (response.isSuccessful()){
                    if (response.body().getStatus().equalsIgnoreCase("ok")){
                        masterDao.insertMaster(response.body().getData());
                        new Handler().postDelayed(() -> getProfile(), 1000);
                    }
                }
            }

            @Override
            public void onFailure(Call<MasterResponse> call, Throwable t) {

            }
        });
    }


    private void getProfile(){
        Call<ProfileResponse> call = service.getProfile(token);
        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful()){
                    if (response.body().getStatus().equalsIgnoreCase("ok")){
                        userDao.insertUser(response.body().getData());
                        User user = response.body().getData();
                        Constant.setIdRoles(user.getId_roles());
                        if (user.getImage() != null && !user.getImage().isEmpty()){
                            image = user.getImage();
                        }
                        String alumni_cabang = user.getDomisili_cabang() != null ? user.getDomisili_cabang() : "";

                        getListGroupChat(user.getCabang(), user.getKomisariat(), alumni_cabang);
                        new Handler().postDelayed(() -> getLevel(), 1000);
                    } else {
                        Tools.showDialogAlert(activity, response.body().getMessage());
                    }
                } else {
                    Tools.showToast(activity, activity.getString(R.string.gagal_login));
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                dialog.dismiss();
                Tools.showToast(activity, activity.getString(R.string.gagal_login));
            }
        });
    }

    private void getLevel(){
        Call<LevelResponse> call = service.getLevelLK();
        call.enqueue(new Callback<LevelResponse>() {
            @Override
            public void onResponse(Call<LevelResponse> call, Response<LevelResponse> response) {
                if (response.isSuccessful()){
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
                        Tools.showDialogAlert(activity, response.body().getMessage());
                    }
                } else {
                    Tools.showToast(activity, activity.getString(R.string.gagal_login));
                }

            }

            @Override
            public void onFailure(Call<LevelResponse> call, Throwable t) {
                dialog.dismiss();
                Tools.showToast(activity, activity.getString(R.string.gagal_login));
            }
        });
    }

    private void getContact(){
        Call<ContactResponse> call = service.getContact(token);
        call.enqueue(new Callback<ContactResponse>() {
            @Override
            public void onResponse(Call<ContactResponse> call, Response<ContactResponse> response) {
                if (response.isSuccessful()){
                    if (response.body().getStatus().equalsIgnoreCase("success")){
                        List<Contact> contacts = response.body().getData();
                        for (int i = 0; i < contacts.size() ; i++) {
                            Contact c = contacts.get(i);
                            c.setId_level(levelDao.getPengajuanLevel(c.getId_roles()));
                            c.setTahun_daftar(Tools.getYearFromMillis(Long.parseLong(c.getTanggal_daftar())));
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
                        new Handler().postDelayed(() -> {
                            getTraining();
                        }, 1000);
                    } else {
                        dialog.dismiss();
                        Tools.showDialogAlert(activity, response.body().getMessage());
                    }
                } else {
                    dialog.dismiss();
                    Tools.showToast(activity, activity.getString(R.string.gagal_login));
                }
            }

            @Override
            public void onFailure(Call<ContactResponse> call, Throwable t) {
                dialog.dismiss();
                Tools.showToast(activity, activity.getString(R.string.gagal_login));
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
                Log.e("roma", "onDataChange: loginprocess 315");
                for (DataSnapshot s : dataSnapshot.getChildren()){
                    GroupChatFirebase groupChatFirebase = s.getValue(GroupChatFirebase.class);
                    Log.d("halloboyyy", "onDataChange: "+groupChatFirebase.getNama());
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
        referenceChats = FirebaseDatabase.getInstance().getReference("Chats_v2");
        referenceChats.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("roma", "onDataChange: loginProcess 361");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    GroupChat groupChat = new GroupChat(groupChatFirebase.getNama(), groupChatFirebase.getImage());
                    if (chat.getReceiver().equals(groupChatFirebase.getNama())) {
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

    public void addGroupChat(String name){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("nama", name);
        hashMap.put("image", "");

        databaseReference.child("ChatGroup_v2").child(name).setValue(hashMap);
    }

    private boolean akunAvailable(List<Account> list){
        boolean available = false;
        int size = list.size();
        if (size > 0){
            for (int i = 0; i < size; i++) {
                if (list.get(i).getUsername().equalsIgnoreCase(username)) {
                    available = true;
                }
            }
        } else {
            available = false;
        }
        return available;
    }

    private void removeEventListener(){
        Log.e("roma", "onDataChange removeEventListener: ");
        referenceChats.removeEventListener(eventListenerChats);
    }

    private void getTraining(){
        Call<TrainingResponse> call = service.getTraining(token, "0");
        call.enqueue(new Callback<TrainingResponse>() {
            @Override
            public void onResponse(Call<TrainingResponse> call, Response<TrainingResponse> response) {
                if (response.isSuccessful()){
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
                            new Handler().postDelayed(() -> {
                                dialog.dismiss();
                                Tools.showToast(activity, activity.getString(R.string.berhasil_login));
                                Constant.setToken(token);

//                            removeEventListener();

                                activity.startActivity(new Intent(activity, MainActivity.class));
                                List<Account> list = Constant.getLisAccount();

                                if (akunAvailable(list)){
                                    for (int i = 0; i < list.size(); i++) {
                                        if (list.get(i).getUsername().equalsIgnoreCase(username)) {
                                            list.get(i).setPassword(password);
                                        }
                                    }
                                } else {
                                    list.add(new Account(1, username, password, image));
                                }

                                Constant.setListAccount(list);

                                activity.finish();
                            }, 1000);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<TrainingResponse> call, Throwable t) {

            }
        });
    }
}

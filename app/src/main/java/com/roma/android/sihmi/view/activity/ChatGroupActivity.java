package com.roma.android.sihmi.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.messaging.RemoteMessage;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.core.CoreApplication;
import com.roma.android.sihmi.helper.NotificationHelper;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Chat;
import com.roma.android.sihmi.model.database.entity.GroupChat;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.response.UploadFileResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.utils.UploadFile;
import com.roma.android.sihmi.view.adapter.RoomChatAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roma.android.sihmi.view.adapter.StickerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatGroupActivity extends BaseActivity {
    public static String NAMA_GROUP = "nama_group";
    @BindView(R.id.toolbar)
    Toolbar toolbar;@BindView(R.id.img_profile)
    ImageView imgProfile;
    @BindView(R.id.img_initial)
    ImageView ivInitial;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_message)
    EditText etMessage;
    @BindView(R.id.btn_send)
    ImageView btnSend;
    @BindView(R.id.rv_chat)
    RecyclerView rvChat;
    @BindView(R.id.fab_add)
    FloatingActionButton fabAdd;
    @BindView(R.id.fab_file)
    FloatingActionButton fabFile;
    @BindView(R.id.fab_image)
    FloatingActionButton fabImage;
    @BindView(R.id.ll_add)
    LinearLayout llAdd;
    @BindView(R.id.rlToolbar)
    RelativeLayout rlToolbar;
    @BindView(R.id.rv_stiker)
    RecyclerView rvStiker;
    @BindView(R.id.activity_chat)
    RelativeLayout rlChatActivity;
    @BindView(R.id.chat_input_container)
    RelativeLayout chatInputContainer;

    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    String myuser, message;
    String namaGroup;

    List<Chat> list;

    boolean isAdd = false;

    AppDb appDb;
    UserDao userDao;
    ContactDao contactDao;
    private RoomChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        initAll();
        initToolbar();

        checkAdd(isAdd);

        String[] strings = {"Yakusa", "Hijau Hitam", "Kawan", "Siap","Presidium", "Mainkan", "Kanda", "Ayunda", "Ada Arahan", "Adinda" ,"Ampun Senior", "Green black",
                "Intrupsi", "Justifikasi", "Kahmi", "Ketum", "Nyimak", "Quorum", "Senior"};
        List<String> listString = Arrays.asList(strings);

        StickerAdapter stickerAdapter = new StickerAdapter(this, listString, (StickerAdapter.itemClickListener) sticker -> {
            sendMessage(Constant.STICKER, sticker);
        });

        rvStiker.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        rvStiker.setHasFixedSize(true);
        rvStiker.setAdapter(stickerAdapter);

        etMessage.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (isKeyboardShown(etMessage.getRootView())){
                rvStiker.setVisibility(View.VISIBLE);
            } else {
                rvStiker.setVisibility(View.GONE);
            }

        });

        if (Constant.loadNightModeState()) {
            rlChatActivity.setBackgroundColor(getResources().getColor(R.color.colorDarkAccent));
            chatInputContainer.setBackgroundColor(getResources().getColor(R.color.colorDark));
        }
    }

    private boolean isKeyboardShown(View rootView) {
        /* 128dp = 32dp * 4, minimum button height 32dp and generic 4 rows soft keyboard */
        final int SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD = 128;

        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        /* heightDiff = rootView height - status bar height (r.top) - visible frame height (r.bottom - r.top) */
        int heightDiff = rootView.getBottom() - r.bottom;
        /* Threshold size: dp to pixels, multiply with display density */
        boolean isKeyboardShown = heightDiff > SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD * dm.density;

        Log.d("hallo", "onFocusChange isKeyboardShown ? " + isKeyboardShown + ", heightDiff:" + heightDiff + ", density:" + dm.density
                + "root view height:" + rootView.getHeight() + ", rect:" + r);

        return isKeyboardShown;
    }

    private void initAll(){
        namaGroup = getIntent().getStringExtra(NAMA_GROUP);
        Log.d("hallloooo", "initAll: "+namaGroup);
        appDb = AppDb.getInstance(this);
        userDao = appDb.userDao();
        myuser = userDao.getUser().get_id();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        list = new ArrayList<>();

        setAdapter(list);

        readMessage();

        GroupChat groupChat = appDb.groupChatDao().getGroupChatByName(namaGroup);
        if (groupChat != null) {
            groupChat.setUnread(0);
        }

        if (namaGroup != null) {
            String key = namaGroup+"_"+ NotificationHelper.ID_CHAT;
            NotificationHelper.removeHistoryMessage(key);
        }
    }

    private void initToolbar(){
        tvTitle.setText(getIntent().getStringExtra(NAMA_GROUP));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void checkAdd(Boolean add){
        if (add){
            llAdd.setVisibility(View.VISIBLE);
        } else {
            llAdd.setVisibility(View.GONE);
        }
    }

    private void readMessage(){
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats_v2");
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("roma", "onDataChange: chatactivity 150");
                new readMessageAsync().execute(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private class readMessageAsync extends AsyncTask<DataSnapshot, Void, List<Chat>> {
        List<Chat> chatList;

        public readMessageAsync() {
            this.chatList = new ArrayList<>();
        }

        @Override
        protected List<Chat> doInBackground(DataSnapshot... dataSnapshots) {
            for (DataSnapshot snapshot : dataSnapshots[0].getChildren()){
                Chat chat = snapshot.getValue(Chat.class);
                if (chat != null && chat.getReceiver().equalsIgnoreCase(namaGroup)) {
                    chatList.add(chat);
                }
            }

            return chatList;
        }

        @Override
        protected void onPostExecute(List<Chat> chats) {
            super.onPostExecute(chats);
            list.clear();
            list.addAll(chats);
            adapter.notifyDataSetChanged();
            rvChat.scrollToPosition(list.size()-1);
        }
    }

    private void setAdapter(List<Chat> list){
        adapter = new RoomChatAdapter(this, list, 2, chat -> {
            startActivity(new Intent(ChatGroupActivity.this, ChatFileDetailActivity.class).putExtra(ChatFileDetailActivity.NAMA_FILE, chat.getMessage()).putExtra(ChatFileDetailActivity.TYPE_FILE, chat.getType())
                    .putExtra(ChatFileDetailActivity.TIME_FILE, String.valueOf(chat.getTime())));
        });

        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        llm.setStackFromEnd(true);
        rvChat.setLayoutManager(llm);
        rvChat.setAdapter(adapter);

    }

    @OnClick({R.id.fab_add, R.id.fab_file, R.id.fab_image})
    public void go(FloatingActionButton fab){
        switch (fab.getId()){
            case R.id.fab_add:
                isAdd = !isAdd;
                checkAdd(isAdd);
                break;
            case R.id.fab_file:
                UploadFile.selectFile(ChatGroupActivity.this);
                break;
            case R.id.fab_image:
                UploadFile.selectImage(ChatGroupActivity.this);
                break;
        }
    }

    @OnClick(R.id.btn_send)
    public void goSend(){
        if (!etMessage.getText().toString().isEmpty() && etMessage.getText().toString().trim().length() != 0) {
            sendMessage("text", etMessage.getText().toString());
        } else {
            Toast.makeText(this, "Belum ada pesan", Toast.LENGTH_SHORT).show();
        }


    }

    private void sendMessage(String type, String message){
        databaseReference = FirebaseDatabase.getInstance().getReference();
        Log.e("roma", "onDataChange: SENDMESSAGE BEFORE GROUP");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myuser);
        hashMap.put("receiver", namaGroup);
        hashMap.put("message", message.trim());
        hashMap.put("time", System.currentTimeMillis());
        hashMap.put("isseen", false);
        hashMap.put("type", type);

        databaseReference.child("Chats_v2").push().setValue(hashMap);
        etMessage.setText("");
        Log.e("roma", "onDataChange: SENDMESSAGE AFTER GROUP");
    }

    private void sendFile(String type, String filePath){
        if (Tools.isOnline(this)) {
            Tools.showProgressDialog(ChatGroupActivity.this, "Mengungah "+type+" ...");
            UploadFile.uploadFileToServer(type, filePath, new Callback<UploadFileResponse>() {
                @Override
                public void onResponse(Call<UploadFileResponse> call, Response<UploadFileResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equalsIgnoreCase("ok")) {
                            if (response.body().getData().size() > 0) {
                                String url = response.body().getData().get(0).getUrl();
                                sendMessage(type, url);
                            }
                        }
                    }
                    Tools.dissmissProgressDialog();
                }

                @Override
                public void onFailure(Call<UploadFileResponse> call, Throwable t) {
                    Tools.dissmissProgressDialog();
                }
            });
        } else {
            Tools.showToast(ChatGroupActivity.this, getString(R.string.tidak_ada_internet));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_DOC_CODE && resultCode == Activity.RESULT_OK){
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            sendFile(Constant.DOCUMENT, filePath);
        } else if (requestCode == Constant.REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK){
            Uri uri = data.getData();
            String filePath = UploadFile.getRealPathFromURIPath(uri, ChatGroupActivity.this);
            sendFile(Constant.IMAGE, filePath);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_1:
                break;
            case R.id.action_2:
                break;
            case R.id.action_3:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        databaseReference.removeEventListener(eventListener);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(eventListener);
    }
}

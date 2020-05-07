package com.roma.android.sihmi.model.database.interfaceDao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.roma.android.sihmi.model.database.entity.GroupChat;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface GroupChatDao {
    //Group Chat
    @Insert(onConflict = REPLACE)
    void insertGroupChat(List<GroupChat> groupChat);

    @Insert(onConflict = REPLACE)
    void insertGroupChat(GroupChat groupChat);

    @Query("SELECT * FROM GroupChat ORDER BY time DESC")
    LiveData<List<GroupChat>> getAllGroupLiveData();

    @Query("SELECT * FROM GroupChat WHERE nama LIKE 'nasional' OR nama LIKE :cabang OR nama LIKE :komisariat OR nama LIKE :alumni ORDER BY time DESC")
    LiveData<List<GroupChat>> getAllGroupLiveDataNotSuperAdmin(String cabang, String komisariat, String alumni);

    @Query("SELECT * FROM GroupChat ORDER BY time DESC")
    List<GroupChat> getAllGroupList();

    @Query("SELECT * FROM GroupChat WHERE nama LIKE 'nasional' OR nama LIKE :cabang OR nama LIKE :komisariat OR nama LIKE :alumni ORDER BY time DESC")
    List<GroupChat> getAllGroupListNotSuperAdmin(String cabang, String komisariat, String alumni);

    @Query("SELECT * FROM GroupChat WHERE nama = :name")
    GroupChat getGroupChatByName(String name);

    @Query("SELECT * FROM GroupChat WHERE nama LIKE :name ORDER BY time DESC")
    List<GroupChat> getSearchGroupChatByName(String name);

    @Query("UPDATE GroupChat SET last_seen=:lastSeen WHERE nama = :name")
    void updateLastSeen(String name, long lastSeen);

    @Query("UPDATE GroupChat SET unread=:unread, last_msg=:msg WHERE nama = :name")
    void updateGroupChat(String name, int unread, String msg);

    @Query("DELETE FROM GroupChat")
    void deleteAllGroup();
}

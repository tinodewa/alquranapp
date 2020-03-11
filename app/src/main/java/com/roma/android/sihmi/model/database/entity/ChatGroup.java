package com.roma.android.sihmi.model.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ChatGroup {
    @PrimaryKey
    @NonNull
    int id;
    String nameGroup, lastMsg;
    boolean isBisukan;

    public ChatGroup(int id, String nameGroup, String lastMsg, boolean isBisukan) {
        this.id = id;
        this.nameGroup = nameGroup;
        this.lastMsg = lastMsg;
        this.isBisukan = isBisukan;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameGroup() {
        return nameGroup;
    }

    public void setNameGroup(String nameGroup) {
        this.nameGroup = nameGroup;
    }

    public boolean isBisukan() {
        return isBisukan;
    }

    public void setBisukan(boolean bisukan) {
        isBisukan = bisukan;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }
}

package com.roma.android.sihmi.model.database.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;

import com.roma.android.sihmi.model.database.entity.AboutUs;
import com.roma.android.sihmi.model.database.entity.Agenda;
import com.roma.android.sihmi.model.database.entity.Alamat;
import com.roma.android.sihmi.model.database.entity.Chating;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.entity.File;
import com.roma.android.sihmi.model.database.entity.GroupChat;
import com.roma.android.sihmi.model.database.entity.Job;
import com.roma.android.sihmi.model.database.entity.Konstituisi;
import com.roma.android.sihmi.model.database.entity.Leader;
import com.roma.android.sihmi.model.database.entity.Level;
import com.roma.android.sihmi.model.database.entity.Master;
import com.roma.android.sihmi.model.database.entity.Medsos;
import com.roma.android.sihmi.model.database.entity.Pendidikan;
import com.roma.android.sihmi.model.database.entity.Pengajuan;
import com.roma.android.sihmi.model.database.entity.PengajuanHistory;
import com.roma.android.sihmi.model.database.entity.Sejarah;
import com.roma.android.sihmi.model.database.entity.Training;
import com.roma.android.sihmi.model.database.entity.User;
import com.roma.android.sihmi.model.database.interfaceDao.AboutUsDao;
import com.roma.android.sihmi.model.database.interfaceDao.AgendaDao;
import com.roma.android.sihmi.model.database.interfaceDao.AlamatDao;
import com.roma.android.sihmi.model.database.interfaceDao.ChatingDao;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.FileDao;
import com.roma.android.sihmi.model.database.interfaceDao.GroupChatDao;
import com.roma.android.sihmi.model.database.interfaceDao.HistoryPengajuanDao;
import com.roma.android.sihmi.model.database.interfaceDao.InterfaceDao;
import com.roma.android.sihmi.model.database.interfaceDao.KonstitusiDao;
import com.roma.android.sihmi.model.database.interfaceDao.LeaderDao;
import com.roma.android.sihmi.model.database.interfaceDao.LevelDao;
import com.roma.android.sihmi.model.database.interfaceDao.MasterDao;
import com.roma.android.sihmi.model.database.interfaceDao.PengajuanDao;
import com.roma.android.sihmi.model.database.interfaceDao.SejarahDao;
import com.roma.android.sihmi.model.database.interfaceDao.TrainingDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.utils.Constant;

@Database(
        entities = {
                AboutUs.class,
                Agenda.class,
                Alamat.class,
                Chating.class,
                Contact.class,
                File.class,
                GroupChat.class,
                Job.class,
                Konstituisi.class,
                Leader.class,
                Level.class,
                Master.class,
                Medsos.class,
                Pendidikan.class,
                Pengajuan.class,
                PengajuanHistory.class,
                Sejarah.class,
                Training.class,
                User.class },
        version = 23,
        exportSchema = false)
public abstract class AppDb extends RoomDatabase {
    private static volatile AppDb instance = null;

    public abstract AboutUsDao aboutUsDao();
    public abstract AgendaDao agendaDao();
    public abstract AlamatDao alamatDao();
    public abstract ChatingDao chatingDao();
    public abstract ContactDao contactDao();
    public abstract FileDao fileDao();
    public abstract GroupChatDao groupChatDao();
    public abstract HistoryPengajuanDao historyPengajuanDao();
    public abstract InterfaceDao interfaceDao();
    public abstract KonstitusiDao konstitusiDao();
    public abstract LeaderDao leaderDao();
    public abstract LevelDao levelDao();
    public abstract MasterDao masterDao();
    public abstract PengajuanDao pengajuanDao();
    public abstract SejarahDao sejarahDao();
    public abstract TrainingDao trainingDao();
    public abstract UserDao userDao();

    public static AppDb getInstance(Context context){
        if (instance == null){
            synchronized (AppDb.class){
                instance = Room.databaseBuilder(context.getApplicationContext(), AppDb.class,"sihmi_database")
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        return instance;
    }

}

package com.roma.android.sihmi.model.database.interfaceDao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.roma.android.sihmi.model.database.entity.Contact;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ContactDao { @RawQuery
    List<Contact> rawQueryContact(SupportSQLiteQuery query);

    @RawQuery
    int countRawQueryContact(SupportSQLiteQuery query);

    @Insert(onConflict = REPLACE)
    void insertContact (List<Contact> contacts);

    @Insert (onConflict = REPLACE)
    void insertContact (Contact contacts);

    @Query("SELECT * FROM Contact WHERE id_level == 1 OR id_level == 2 ORDER BY lower(nama_depan) ASC")
    LiveData<List<Contact>> getAllContactMember();

    @Query("SELECT * FROM Contact ORDER BY lower(nama_depan) ASC")
    List<Contact> getAllListContact();

    @Query("SELECT * FROM Contact WHERE id_level == 1 OR id_level == 2 ORDER BY lower(nama_depan) ASC")
    List<Contact> getAllListNonAdminUser();

    @Query("SELECT * FROM Contact WHERE _id NOT LIKE :id AND id_level != 20 ORDER BY lower(nama_depan) ASC")
    List<Contact> getAllListContact(String id);

    @Query("SELECT * FROM Contact WHERE _id NOT LIKE :id AND id_level != 20 AND nama_depan LIKE :nama ORDER BY lower(nama_depan) ASC")
    List<Contact> getSearchListContact(String id, String nama);

    @Query("SELECT * FROM Contact WHERE _id LIKE :id")
    Contact getContactById(String id);

    @Query("SELECT _id FROM Contact WHERE nama_depan LIKE :nama")
    String getContactIdByName(String nama);

    @Query("SELECT * FROM Contact WHERE id_level == 5 OR id_level == 8 OR id_level == 11 OR id_level == 13 OR id_level == 16 ORDER BY lower(nama_depan) ASC")
    List<Contact> getListAdmin();

    @Query("SELECT * FROM Contact WHERE id_level == 5 OR id_level == 8 OR id_level == 11 OR id_level == 13 OR id_level == 16 ORDER BY lower(nama_depan) ASC")
    LiveData<List<Contact>> getLiveDataListAdmin();

    @Query("SELECT * FROM Contact WHERE id_level == 2 ORDER BY lower(nama_depan) ASC")
    LiveData<List<Contact>> getLiveDataListAnggota();

    @Query("SELECT * FROM Contact WHERE id_level == 2 AND komisariat = :koms ORDER BY lower(nama_depan) ASC")
    LiveData<List<Contact>> getLiveDataListAnggotaByKomisariat(String koms);

    @Query("SELECT * FROM Contact WHERE (id_level == 5 OR id_level == 8 OR id_level == 11 OR id_level == 13 OR id_level == 16 OR id_level == 19 OR id_level == 20) AND (nama_depan LIKE :name OR nama_belakang LIKE :name)  ORDER BY lower(nama_depan) ASC")
    List<Contact> getSearchListAdmin(String name);

    @Query("SELECT * FROM Contact WHERE id_level == 2 AND (nama_depan LIKE :name OR nama_belakang LIKE :name)  ORDER BY lower(nama_depan) ASC")
    List<Contact> getSearchListAnggota(String name);

    @Query("SELECT * FROM Contact WHERE id_level == 2 AND komisariat = :koms AND (nama_depan LIKE :name OR nama_belakang LIKE :name)  ORDER BY lower(nama_depan) ASC")
    List<Contact> getSearchListAnggotaByKomisariat(String koms, String name);

    @Query("SELECT * FROM Contact WHERE id_level == 6 OR id_level == 7 ORDER BY nama_depan ASC")
    List<Contact> getListKomisariat();

    @Query("SELECT * FROM Contact WHERE id_level == 14 OR id_level == 15 ORDER BY nama_depan ASC")
    List<Contact> getListCabang();

    @Query("SELECT * FROM Contact WHERE id_level == 17 OR id_level == 18 ORDER BY nama_depan ASC")
    List<Contact> getListPBHMI();

    @Query("SELECT * FROM Contact WHERE id_level == 2 ORDER BY lower(nama_depan) ASC")
    List<Contact> getListAnggota();

    @Query("SELECT * FROM Contact WHERE id_level == 2 AND komisariat = :koms ORDER BY lower(nama_depan) ASC")
    List<Contact> getListAnggotaByKomisariat(String koms);

    @Query("UPDATE Contact SET bisukan=:bisukan WHERE _id = :id")
    void updateBisukan(String id, boolean bisukan);

    @Query("UPDATE Contact SET id_roles= :idRoles, id_level = 2 WHERE _id = :id")
    void updateRolesUser(String id, String idRoles);
}

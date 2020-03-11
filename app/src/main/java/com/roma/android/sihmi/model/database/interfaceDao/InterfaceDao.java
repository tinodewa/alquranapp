package com.roma.android.sihmi.model.database.interfaceDao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.roma.android.sihmi.model.database.entity.AboutUs;
import com.roma.android.sihmi.model.database.entity.Agenda;
import com.roma.android.sihmi.model.database.entity.Alamat;
import com.roma.android.sihmi.model.database.entity.Chating;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.entity.File;
import com.roma.android.sihmi.model.database.entity.GroupChat;
import com.roma.android.sihmi.model.database.entity.Konstituisi;
import com.roma.android.sihmi.model.database.entity.Leader;
import com.roma.android.sihmi.model.database.entity.Level;
import com.roma.android.sihmi.model.database.entity.Master;
import com.roma.android.sihmi.model.database.entity.Pengajuan;
import com.roma.android.sihmi.model.database.entity.PengajuanHistory;
import com.roma.android.sihmi.model.database.entity.Sejarah;
import com.roma.android.sihmi.model.database.entity.Training;
import com.roma.android.sihmi.model.database.entity.User;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public interface InterfaceDao {

    //Nasional
    @Query("SELECT COUNT(_id) FROM Contact")
    int getCountAllUser();

    @Query("SELECT COUNT(_id) FROM Contact WHERE tahun_daftar LIKE :year")
    int getCountAllUserByYear(String year);

    @Query("SELECT COUNT(_id) FROM Contact WHERE jenis_kelamin = :gender AND tahun_daftar = :year")
    int getCountAllUserByGender(String gender, String year);

    // Badko
    @Query("SELECT COUNT(_id) FROM Contact WHERE badko IS NOT NULL AND badko NOT LIKE '' AND badko NOT LIKE ' '")
    int getCountAllBadko();

    @Query("SELECT COUNT(_id) FROM Contact WHERE badko = :badko")
    int getCountBadko(String badko);

    @Query("SELECT COUNT(_id) FROM Contact WHERE badko IS NOT NULL AND badko NOT LIKE '' AND badko NOT LIKE ' ' AND tahun_daftar = :year")
    int getCountAllBadkoByYear (String year);

    @Query("SELECT COUNT(_id) FROM Contact WHERE badko = :badko AND tahun_daftar = :year")
    int getCountBadkoByYear(String badko, String year);

    @Query("SELECT COUNT(_id) FROM Contact WHERE badko IS NOT NULL AND badko NOT LIKE '' AND badko NOT LIKE ' ' AND jenis_kelamin = :gender AND tahun_daftar = :year")
    int getCountAllBadkoByGender(String gender, String year);

    @Query("SELECT COUNT(_id) FROM Contact WHERE badko = :badko AND jenis_kelamin = :gender AND tahun_daftar = :year")
    int getCountBadkoByGender(String badko, String gender, String year);

    //Cabang
    @Query("SELECT COUNT(_id) FROM Contact WHERE cabang IS NOT NULL AND cabang NOT LIKE '' AND cabang NOT LIKE ' ' ")
    int getCountAllCabang();

    @Query("SELECT COUNT(_id) FROM Contact WHERE cabang = :cabang")
    int getCountCabang(String cabang);

    @Query("SELECT COUNT(_id) FROM Contact WHERE korkom IS NOT NULL AND korkom NOT LIKE '' AND korkom NOT LIKE ' ' ")
    int getCountAllKorkom();

    @Query("SELECT COUNT(_id) FROM Contact WHERE korkom = :korkom")
    int getCountKorkom(String korkom);

    @Query("SELECT COUNT(_id) FROM Contact WHERE komisariat IS NOT NULL AND komisariat NOT LIKE '' AND komisariat NOT LIKE ' ' ")
    int getCountAllKomisariat();

    @Query("SELECT COUNT(_id) FROM Contact WHERE komisariat = :komisariat")
    int getCountKomisariat(String komisariat);

    @Query("SELECT COUNT(_id) FROM Contact WHERE domisili_cabang IS NOT NULL AND domisili_cabang NOT LIKE '' AND domisili_cabang NOT LIKE ' ' ")
    int getCountAllAlumni();

    @Query("SELECT COUNT(_id) FROM Contact WHERE domisili_cabang = :cabang")
    int getCountAlumni(String cabang);

    @Query("SELECT COUNT(_id) FROM Contact WHERE id_level = 1")
    int getCountNonLK();

    @Query("SELECT COUNT(_id) FROM Contact WHERE id_level = 2")
    int getCountLK();


    // Member
//    @Insert (onConflict = REPLACE)
//    void insertMember(Member member);
//
//    @Query("SELECT * FROM Member WHERE id_user = :idUser")
//    Member getMemberById(String idUser);

}

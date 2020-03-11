package com.roma.android.sihmi.model.database.interfaceDao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.roma.android.sihmi.model.database.entity.Pengajuan;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface PengajuanDao {
    //Pengajuan
    @Insert(onConflict = REPLACE)
    void insertPengajuan(List<Pengajuan> pengajuan);

    @Query("SELECT * FROM Pengajuan")
    List<Pengajuan> getAllPengajuan();

    @Query("SELECT * FROM Pengajuan WHERE status = :status")
    List<Pengajuan> getPengajuanByStatus(int status);

    @Query("SELECT * FROM Pengajuan WHERE status = 1")
    List<Pengajuan> getPengajuanByStatusNew();

    @Query("SELECT * FROM Pengajuan WHERE id_user = :idUser")
    List<Pengajuan> getPengajuanByUser(String idUser);

    @Query("SELECT * FROM Pengajuan WHERE bulan_lk LIKE :bulan_lk AND tahun_lk1 LIKE :tahun_lk")
    List<Pengajuan> getPengajuanAdmin1(String bulan_lk, String tahun_lk);

    @Query("SELECT tujuan_pengajuan FROM Pengajuan WHERE id_user LIKE :id_user")
    int getTujuanPengajuan(String id_user);

    @Query("DELETE FROM Pengajuan WHERE id_user LIKE :id_user")
    void deletePengajuanById(String id_user);

    @Query("DELETE FROM Pengajuan")
    public abstract void deleteAllPengajuan();
}

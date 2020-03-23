package com.roma.android.sihmi.model.database.interfaceDao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.roma.android.sihmi.model.database.entity.PengajuanHistory;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface HistoryPengajuanDao {
    //HistoryPengajuan
    @Insert(onConflict = REPLACE)
    void insertPengajuanHistory(PengajuanHistory pengajuanHistory);

//    @Query("SELECT * FROM PengajuanHistory")
//    List<PengajuanHistory> getAllPengajuanHistory();

    @Query("SELECT file FROM PengajuanHistory WHERE _id = :idPengajuan AND (status == 0 OR status == -1) AND level != 2 LIMIT 1")
    String getFilePengajuanHistory(String idPengajuan);

    @Query("UPDATE PengajuanHistory SET id_roles= :idRoles AND level = 1 WHERE _id = :id")
    void updatePengajuanUser(String id, String idRoles);

    @Query("SELECT * FROM PengajuanHistory WHERE created_by = :id_user AND (status = 0 OR status = -1) LIMIT 1")
    PengajuanHistory getPengajuanHistory(String id_user);

    @Query("SELECT * FROM PengajuanHistory WHERE _id = :id_pengajuan LIMIT 1")
    PengajuanHistory getPengajuanHistoryById(String id_pengajuan);

    @Query("SELECT * FROM PengajuanHistory WHERE (status == 0) ORDER BY date_created DESC")
    LiveData<List<PengajuanHistory>> getAllPengajuanHistory();

    @Query("SELECT * FROM PengajuanHistory WHERE (status == 0) AND (level != 2 AND level <=:level) ORDER BY date_created DESC")
    LiveData<List<PengajuanHistory>> getAllPengajuanHistory(int level);

    @Query("SELECT * FROM PengajuanHistory WHERE (status == 0) AND (level = 3 OR level = 4) ORDER BY date_created DESC")
    LiveData<List<PengajuanHistory>> getAllPengajuanHistoryLA1();

    @Query("SELECT * FROM PengajuanHistory WHERE (status == 0) AND level = 6 ORDER BY date_created DESC")
    LiveData<List<PengajuanHistory>> getAllPengajuanHistoryLA2();

    @Query("SELECT * FROM PengajuanHistory WHERE (status == 0) AND level = 2 ORDER BY date_created DESC")
    LiveData<List<PengajuanHistory>> getAllPengajuanHistoryAdmin1();

    @Query("SELECT * FROM PengajuanHistory WHERE (status == 0) AND level = 2 AND tanggal_lk1 LIKE :dateNow ORDER BY date_created DESC")
    LiveData<List<PengajuanHistory>> getAllPengajuanHistoryAdmin2(String dateNow);

    @Query("SELECT * FROM PengajuanHistory WHERE (status == 0) AND level = 2 ORDER BY date_created DESC")
    LiveData<List<PengajuanHistory>> getAllPengajuanAnggota();

    @Query("SELECT * FROM PengajuanHistory WHERE (status == 0) ORDER BY date_created DESC")
    List<PengajuanHistory> getAllListPengajuanHistory();

    @Query("SELECT * FROM PengajuanHistory WHERE (status == 0) AND level <= :level ORDER BY date_created DESC")
    List<PengajuanHistory> getAllListPengajuanHistory(int level);

    @Query("SELECT * FROM PengajuanHistory WHERE (status == 0) AND (level = 3 OR level = 4) ORDER BY date_created DESC")
    List<PengajuanHistory> getAllListPengajuanHistoryLA1();

    @Query("SELECT * FROM PengajuanHistory WHERE (status == 0) AND level = 6 ORDER BY date_created DESC")
    List<PengajuanHistory> getAllListPengajuanHistoryLA2();

    @Query("SELECT * FROM PengajuanHistory WHERE (status == 0) AND level == 2 AND tanggal_lk1 LIKE :dateNow ORDER BY date_created DESC")
    List<PengajuanHistory> getAllListPengajuanHistoryAdmin1(String dateNow);

    @Query("SELECT * FROM PengajuanHistory WHERE (status == 0) AND level == 2 AND tanggal_lk1 NOT LIKE :dateNow ORDER BY date_created DESC")
    List<PengajuanHistory> getAllListPengajuanHistoryAdmin2(String dateNow);

    @Query("SELECT * FROM PengajuanHistory WHERE (status == 0) AND level = 2 ORDER BY date_created DESC")
    List<PengajuanHistory> getAllListPengajuanAnggota();

    @Query("SELECT * FROM PengajuanHistory WHERE (status == 0) AND nama LIKE :nama ORDER BY date_created DESC")
    List<PengajuanHistory> getSearchAllPengajuanHistory(String nama);

    @Query("SELECT * FROM PengajuanHistory WHERE (status == 0) AND (level != 2 AND level <= :level) AND nama LIKE :nama ORDER BY date_created DESC")
    List<PengajuanHistory> getSearchAllPengajuanHistory(int level, String nama);

    @Query("SELECT * FROM PengajuanHistory WHERE (status == 0) AND (level = 3 OR level = 4) AND nama LIKE :nama ORDER BY date_created DESC")
    List<PengajuanHistory> getSearchAllPengajuanHistoryLA1(String nama);

    @Query("SELECT * FROM PengajuanHistory WHERE (status == 0) AND (level != 2 AND level = 6) AND nama LIKE :nama ORDER BY date_created DESC")
    List<PengajuanHistory> getSearchAllPengajuanHistoryLA2(String nama);

    @Query("SELECT * FROM PengajuanHistory WHERE (status == 0) AND level == 2 AND tanggal_lk1 LIKE :dateNow AND nama LIKE :nama ORDER BY date_created DESC")
    List<PengajuanHistory> getSearchAllPengajuanHistoryAdmin1(String dateNow, String nama);

    @Query("SELECT * FROM PengajuanHistory WHERE (status == 0) AND level == 2 AND tanggal_lk1 NOT LIKE :dateNow AND  nama LIKE :nama ORDER BY date_created DESC")
    List<PengajuanHistory> getSearchAllPengajuanHistoryAdmin2(String dateNow, String nama);

    @Query("SELECT * FROM PengajuanHistory WHERE (status == 0) AND level = 2 AND  nama LIKE :nama ORDER BY date_created DESC")
    List<PengajuanHistory> getSearchAllListPengajuanAnggota(String nama);

    @Query("SELECT level FROM PengajuanHistory WHERE _id = :id LIMIT 1")
    int getLevelByIdPengajuanHistory(String id);

    @Query("UPDATE PengajuanHistory SET status=1 WHERE _id = :id")
    void updateApprovePengajuan(String id);
//
//    @Query("SELECT * FROM PengajuanHistory WHERE status == 0 AND id_roles LIKE :idRoles")
//    List<PengajuanHistory> getPengajuanHistoryNewByIdRoles(String idRoles);
//
//    @Query("SELECT * FROM PengajuanHistory WHERE status == 1 AND id_roles LIKE :idRoles")
//    List<PengajuanHistory> getPengajuanHistoryApproveByIdRoles(String idRoles);
//
//    @Query("DELETE FROM PengajuanHistory")
//    void deleteAllPengajuanHistory();

}

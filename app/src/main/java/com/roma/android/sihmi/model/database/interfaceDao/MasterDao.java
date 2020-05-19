package com.roma.android.sihmi.model.database.interfaceDao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.roma.android.sihmi.model.database.entity.Master;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface MasterDao {
    //Master
    @Insert(onConflict =  REPLACE)
    void insertMaster(List<Master> list);

    @Query("SELECT * FROM Master where type = 1 OR type = 2 OR type = 3 OR type = 4 ORDER BY type ASC ")
    List<Master> getAllMaster();

    @Query("SELECT value FROM Master where type = :type ORDER BY type ASC LIMIT 1")
    String getFirstDataMasterByType(int type);

    @Query("SELECT * FROM Master WHERE type = :type")
    LiveData<List<Master>> getMasterByType(String type);

    @Query("SELECT * FROM Master WHERE type = :type")
    List<Master> getListMasterByType(String type);

    @Query("SELECT value FROM Master WHERE type = :type")
    List<String> getMasterNameByType(String type);

    @Query("SELECT value FROM Master WHERE type = 2")
    List<String> getMasterCabang();

    @Query("SELECT value FROM Master WHERE type = 4")
    List<String> getMasterKomisariat();

    @Query("SELECT value FROM Master WHERE type = 5")
    List<String> getMasterTraining();

    @Query("SELECT * FROM Master WHERE _id = :id LIMIT 1")
    Master getMasterById(String id);

    @Query("SELECT type FROM Master WHERE value = :value LIMIT 1")
    String getTypeMasterByValue(String value);

    @Query("UPDATE Master SET availableAddres=:availableAddres WHERE _id = :id")
    void updateMasterAddres(String id, boolean availableAddres);

    @Query("DELETE FROM Master WHERE _id LIKE :id")
    void deleteMasterById(String id);

    @Query("SELECT value FROM Master WHERE type = :type AND (parentId = :parentId OR value LIKE 'Non%')")
    List<String> getMasterValueByParentId(String parentId, String type);

    @Query("SELECT * FROM Master WHERE type = :type AND (parentId = :parentId OR value LIKE 'Non%')")
    List<Master> getMasterByParentId(String parentId, String type);
}

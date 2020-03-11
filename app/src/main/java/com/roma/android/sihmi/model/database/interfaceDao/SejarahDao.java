package com.roma.android.sihmi.model.database.interfaceDao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.roma.android.sihmi.model.database.entity.Sejarah;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface SejarahDao {
    @Insert(onConflict = REPLACE)
    void insertSejarah(Sejarah sejarah);

    @Insert (onConflict = REPLACE)
    void insertSejarah(List<Sejarah> sejarah);

    @Query("SELECT * FROM Sejarah WHERE type LIKE '0-PB HMI'")
    LiveData<List<Sejarah>> getSejarahNasional();

    @Query("SELECT * FROM Sejarah WHERE type LIKE :type")
    LiveData<List<Sejarah>> getSejarahByType(String type);

    @Query("SELECT * FROM Sejarah")
    List<Sejarah> getAllSejarah();

    @Query("SELECT * FROM Sejarah WHERE type = :type")
    List<Sejarah> getListSejarahByType(String type);

    @Query("SELECT * FROM Sejarah WHERE type LIKE :type")
    List<Sejarah> getSejarahLikeType(String type);

    @Query("SELECT * FROM Sejarah WHERE type LIKE :type AND judul LIKE :name")
    List<Sejarah> getSearchSejarah(String type, String name);

    @Query("SELECT* FROM Sejarah WHERE _id = :idSejarah LIMIT 1")
    Sejarah getSejarahById(String idSejarah);

    @Query("DELETE FROM Sejarah WHERE _id LIKE :id")
    void deleteSejarahById(String id);

    @Query("DELETE FROM Sejarah")
    public abstract void deleteAllSejarah();
}

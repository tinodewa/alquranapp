package com.roma.android.sihmi.model.database.interfaceDao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.roma.android.sihmi.model.database.entity.Chating;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ChatingDao {
    // Chating
    @Insert(onConflict = REPLACE)
    void insertChating(Chating chating);

    @Query("SELECT * FROM Chating ORDER BY time_message DESC")
    LiveData<List<Chating>> getListChating();

    @Query("SELECT * FROM Chating WHERE _id = :id LIMIT 1")
    Chating getChatingById(String id);

    @Query("SELECT * FROM Chating WHERE name LIKE :name ORDER BY time_message DESC")
    List<Chating> getSearchChating(String name);

    @Query("DELETE FROM Chating")
    public void deleteAllChating();
}

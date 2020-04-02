package com.roma.android.sihmi.model.database.interfaceDao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.roma.android.sihmi.model.database.entity.Training;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface TrainingDao {
    //Training
    @RawQuery
    List<Training> rawQueryTraining(SupportSQLiteQuery query);

    @RawQuery
    int countRawQueryTraining(SupportSQLiteQuery query);

    @Insert(onConflict = REPLACE)
    void insertTraining(Training training);

    @Insert (onConflict = REPLACE)
    void insertTraining(List<Training> training);

    @Query("SELECT * FROM Training")
    LiveData<List<Training>> getLiveDataAllTraining();

    @Query("SELECT * FROM Training")
    List<Training> getAllTraining();

    @Query("SELECT * FROM Training WHERE id_user = :idUser")
    List<Training> getListAllTrainingByUser(String idUser);

    @Query("SELECT * FROM Training WHERE id_user = :idUser AND tipe = :type")
    Training getListAllTrainingByUserType(String idUser, String type);

    @Query("SELECT * FROM Training WHERE id_user = :idUser")
    LiveData<List<Training>> getAllTrainingByUser(String idUser);

    @Query("SELECT * FROM Training WHERE id_user = :idUser AND tipe = :tipe AND tahun = :tahun")
    Training checkTrainingAvailable(String idUser, String tipe, String tahun);
}

package com.example.tvoya_zhdulya.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface MoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(MoodEntry mood);

    @Query("SELECT * FROM mood_entries WHERE personId = :personId AND date = :date")
    LiveData<MoodEntry> getMoodByDate(long personId, String date);

    @Query("SELECT * FROM mood_entries WHERE personId = :personId")
    LiveData<List<MoodEntry>> getAllMoodsForPerson(long personId);

    @Query("SELECT * FROM mood_entries WHERE personId = :personId AND date = :date")
    MoodEntry getMoodByDateDirect(long personId, String date);

    @Query("SELECT * FROM mood_entries WHERE personId = :personId AND date = :date")
    MoodEntry getMoodByDateRaw(long personId, String date);

    @Query("SELECT * FROM mood_entries WHERE personId = :personId")
    List<MoodEntry> getAllMoodsForPersonRaw(long personId);
}

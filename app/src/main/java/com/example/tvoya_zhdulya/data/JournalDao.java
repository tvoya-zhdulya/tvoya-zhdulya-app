package com.example.tvoya_zhdulya.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface JournalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(JournalEntry entry);

    @Query("SELECT * FROM journal_entries WHERE personId = :personId AND date = :date LIMIT 1")
    JournalEntry getEntryByDateRaw(long personId, String date);

    @Query("SELECT * FROM journal_entries WHERE personId = :personId ORDER BY date")
    List<JournalEntry> getAllEntriesForPersonRaw(long personId);

    @Query("DELETE FROM journal_entries WHERE id = :id")
    void deleteById(long id);
}
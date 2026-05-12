package com.example.tvoya_zhdulya.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface PersonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Person person);

    @Update
    void update(Person person);

    @Query("SELECT * FROM persons ORDER BY id")
    LiveData<List<Person>> getAllPersons();

    @Query("SELECT * FROM persons WHERE id = :id LIMIT 1")
    Person getPersonByIdRaw(long id);

    @Query("SELECT * FROM persons ORDER BY id")
    List<Person> getAllPersonsRaw();

    @Query("DELETE FROM persons WHERE id = :id")
    void delete(long id);
}
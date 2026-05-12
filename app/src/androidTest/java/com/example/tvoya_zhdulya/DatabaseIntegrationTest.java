package com.example.tvoya_zhdulya;

import android.content.Context;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.tvoya_zhdulya.data.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class DatabaseIntegrationTest {
    private AppDatabase db;
    private PersonDao personDao;
    private MoodDao moodDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        personDao = db.personDao();
        moodDao = db.moodDao();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void insertAndRetrievePerson() {
        Person person = new Person("Test User", "2026-12-31", 0);
        long id = personDao.insert(person);

        Person retrieved = personDao.getPersonByIdRaw(id);
        assertNotNull(retrieved);
        assertEquals("Test User", retrieved.name);
    }

    @Test
    public void insertAndRetrieveMood() {
        Person person = new Person("Test User", "2026-12-31", 0);
        long personId = personDao.insert(person);

        MoodEntry mood = new MoodEntry(personId, "2026-05-12", 1);
        moodDao.insertOrUpdate(mood);

        MoodEntry retrievedMood = moodDao.getMoodByDateRaw(personId, "2026-05-12");
        assertNotNull(retrievedMood);
        assertEquals(1, retrievedMood.moodType);
    }
}
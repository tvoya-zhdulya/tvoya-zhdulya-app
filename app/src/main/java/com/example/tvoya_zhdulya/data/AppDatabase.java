package com.example.tvoya_zhdulya.data;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {JournalEntry.class, Person.class, MoodEntry.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract JournalDao journalDao();
    public abstract PersonDao personDao();
    public abstract MoodDao moodDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "tvoya_zhdulya_db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
package com.example.tvoya_zhdulya.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "mood_entries")
public class MoodEntry {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public long personId;
    public String date;
    public int moodType;

    public MoodEntry() {
    }

    @Ignore
    public MoodEntry(long personId, String date, int moodType) {
        this.personId = personId;
        this.date = date;
        this.moodType = moodType;
    }
}
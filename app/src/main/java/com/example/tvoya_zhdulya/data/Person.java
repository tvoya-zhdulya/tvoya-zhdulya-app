package com.example.tvoya_zhdulya.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "persons")
public class Person {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String name;
    public String releaseDate;
    public int defaultMoodColor;

    public Person() {
    }

    @Ignore
    public Person(String name, String releaseDate, int defaultMoodColor) {
        this.name = name;
        this.releaseDate = releaseDate;
        this.defaultMoodColor = defaultMoodColor;
    }
}
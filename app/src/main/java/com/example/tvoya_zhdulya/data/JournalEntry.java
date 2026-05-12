package com.example.tvoya_zhdulya.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "journal_entries")
public class JournalEntry {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public long personId;
    public String date;
    public String note;
    public boolean isVisitationDay;
    public boolean isPackageDay;

    public JournalEntry() {
    }

    @Ignore
    public JournalEntry(long personId, String date, String note, boolean isVisitationDay, boolean isPackageDay) {
        this.personId = personId;
        this.date = date;
        this.note = note;
        this.isVisitationDay = isVisitationDay;
        this.isPackageDay = isPackageDay;
    }
}
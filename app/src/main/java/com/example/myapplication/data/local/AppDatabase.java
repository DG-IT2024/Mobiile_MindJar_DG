package com.example.myapplication.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.myapplication.data.local.dao.*;
import com.example.myapplication.data.local.entity.*;

@Database(
        entities = {JournalEntryEntity.class, VideoEntity.class, HotlineEntity.class},
        version = 10,
        exportSchema = true
)



public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract JournalEntryDao journalEntryDao();
    public abstract VideoDao videoDao();
    public abstract HotlineDao hotlineDao();

    // Singleton DB instance
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "mindjar.db"
                            )
                            .fallbackToDestructiveMigration(true)  // to be removed later.
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}

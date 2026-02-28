package com.example.myapplication.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.myapplication.data.local.dao.*;
import com.example.myapplication.data.local.entity.*;

@Database(
        entities = {UserEntity.class, JournalEntryEntity.class,VideoEntity.class},
        version = 2,
        exportSchema = true
)



public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    // DAO accessors (Room generates implementations)
    public abstract UserDao userDao();
    public abstract JournalEntryDao journalEntryDao();
    public abstract VideoDao videoDao();

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

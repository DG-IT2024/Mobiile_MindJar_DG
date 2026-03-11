package com.example.myapplication.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.myapplication.data.local.entity.HotlineEntity;

import java.util.List;

@Dao
public interface HotlineDao {

    /** Insert or update a hotline from Firebase. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(HotlineEntity hotline);

    /** Return all cached hotlines sorted by display order. */
    @Query("SELECT * FROM hotlines ORDER BY `order` ASC")
    List<HotlineEntity> getAll();

    /** Check if any hotlines are cached. */
    @Query("SELECT COUNT(*) FROM hotlines")
    int getCount();

    /** Clear all cached hotlines (used before a full refresh). */
    @Query("DELETE FROM hotlines")
    void deleteAll();
}

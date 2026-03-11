package com.example.myapplication.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.myapplication.data.local.entity.VideoEntity;

import java.util.List;

/**
 * DAO for video cache operations.
 */
@Dao
public interface VideoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(VideoEntity video);

    @Query("SELECT * FROM videos ORDER BY `order` ASC")
    List<VideoEntity> getAllVideos();

    @Query("SELECT COUNT(*) FROM videos")
    int getCount();

    @Query("DELETE FROM videos")   // NEW
    void deleteAll();
}

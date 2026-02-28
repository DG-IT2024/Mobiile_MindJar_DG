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

    /** Insert or update a video from Firebase. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(VideoEntity video);

    /** Get all cached videos ordered by their display order. */
    @Query("SELECT * FROM videos ORDER BY `order` ASC")
    List<VideoEntity> getAllVideos();

    /** Check how many videos are cached (used to detect empty cache). */
    @Query("SELECT COUNT(*) FROM videos")
    int getCount();
}
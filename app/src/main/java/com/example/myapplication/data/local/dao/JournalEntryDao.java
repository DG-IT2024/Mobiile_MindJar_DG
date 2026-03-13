package com.example.myapplication.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.data.local.entity.JournalEntryEntity;

import java.util.List;

@Dao
public interface JournalEntryDao {

    // Returns the auto-generated entryId — important for dual-write
    @Insert
    long insert(JournalEntryEntity entry);

    // Primary read query for Realization screen.
    // ORDER BY createdAtEpochMs DESC = newest entry appears at the top of the list.
    @Query("SELECT * FROM journal_entries WHERE userId = :userId" +
            " ORDER BY createdAtEpochMs DESC")
    List<JournalEntryEntity> getEntriesByUser(String userId);

    // Fetch a single entry by Room primary key.
    @Query("SELECT * FROM journal_entries WHERE entryId = :entryId LIMIT 1")
    JournalEntryEntity findById(long entryId);

    // Used after Firestore confirms success — flips syncedToFirebase to true.
    @Update
    int update(JournalEntryEntity entry);

    // Used by SyncJournalWorker.
    // IMPORTANT: scoped by userId so only the currently logged-in user's
    // entries are retried. Without this filter, the worker would attempt
    // to push other users' entries and Firestore would reject them.
    @Query("SELECT * FROM journal_entries" +
            " WHERE syncedToFirebase = 0 AND userId = :userId")
    List<JournalEntryEntity> findUnsyncedEntries(String userId);

    // Used during Firestore restore to prevent duplicate entries.
    // Before inserting a document from Firestore, check if its firestoreId
    // already exists in Room. If it does, skip the insert.
    @Query("SELECT * FROM journal_entries WHERE firestoreId = :firestoreId LIMIT 1")
    JournalEntryEntity findByFirestoreId(String firestoreId);

    // Deletes a single entry by its Room primary key.
    // Called from JournalRepository.deleteEntry() on a background thread.
    @Query("DELETE FROM journal_entries WHERE entryId = :entryId")
    void deleteByEntryId(long entryId);



}

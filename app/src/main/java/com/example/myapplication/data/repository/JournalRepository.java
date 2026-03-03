package com.example.myapplication.data.repository;

import android.content.Context;

import com.example.myapplication.data.local.AppDatabase;
import com.example.myapplication.data.local.dao.JournalEntryDao;
import com.example.myapplication.data.local.entity.JournalEntryEntity;

import java.util.List;

public class JournalRepository {

    private final JournalEntryDao dao;

    public JournalRepository(Context context) {
        dao = AppDatabase.getInstance(context).journalEntryDao();
    }

    public void addEntry(String userId, String emotion, String description) {
        JournalEntryEntity entry = new JournalEntryEntity(
                userId, emotion, description, System.currentTimeMillis());
        dao.insert(entry);          // ✅ was JournalEntryDao.insert(entry)
    }

    public List<JournalEntryEntity> listEntries(String userId) {
        return dao.getEntriesByUser(userId);  // ✅ was journalEntryDao.getEntriesByUser()
    }

    public JournalEntryEntity getEntry(long entryId) {
        return dao.findById(entryId);
    }
}

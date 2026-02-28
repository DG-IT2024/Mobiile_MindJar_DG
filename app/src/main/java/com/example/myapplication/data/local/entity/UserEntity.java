package com.example.myapplication.data.local.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "users",
        indices = {
                @Index(value = {"email"}, unique = true) // enforce unique email
        }
)
public class UserEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String fullName;

    public String email;

    // Store BCrypt hash string (includes salt internally)
    public String passwordHash;

    public long createdAtEpochMs;

    public UserEntity(String fullName, String email, String passwordHash, long createdAtEpochMs) {
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAtEpochMs = createdAtEpochMs;
    }
}


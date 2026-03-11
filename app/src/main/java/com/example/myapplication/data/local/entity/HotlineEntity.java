package com.example.myapplication.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "hotlines")
public class HotlineEntity {

    @PrimaryKey
    @NonNull
    public String phone;

    public String name;
    public String email;          // NEW
    public String facebookUrl;    // NEW
    public int    order;

    public HotlineEntity(@NonNull String phone, String name,
                         String email, String facebookUrl, int order) {
        this.phone       = phone;
        this.name        = name;
        this.email       = email;
        this.facebookUrl = facebookUrl;
        this.order       = order;
    }
}

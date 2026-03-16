package com.example.myapplication.data.local.entity;

/** Plain object Firebase deserializes hotline JSON into. */
public class HotlineEntry {
    public String name;
    public String phone;
    public String email;          // NEW — empty string if not available
    public String facebookUrl;    // NEW — empty string if not available
    public int    order;

    public HotlineEntry() { /* Required no-arg constructor for Firebase */ }

    public HotlineEntry(String name, String phone,
                        String email, String facebookUrl, int order) {
        this.name        = name;
        this.phone       = phone;
        this.email       = email;
        this.facebookUrl = facebookUrl;
        this.order       = order;
    }
}

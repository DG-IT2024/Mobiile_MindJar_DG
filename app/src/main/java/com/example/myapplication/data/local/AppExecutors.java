package com.example.myapplication.data.local;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppExecutors {
    private static final ExecutorService DB = Executors.newSingleThreadExecutor();

    public static ExecutorService db() {
        return DB;
    }
}

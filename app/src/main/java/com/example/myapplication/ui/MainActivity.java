package com.example.myapplication.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

/*
 * MainActivity
 * - Entry activity for the authentication flow.
 * - Hosts the NavHostFragment defined in activity_auth.xml
 *
*/

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);   // Hosts the NavHostFragment configured with auth_nav_graph.xml

    }
}

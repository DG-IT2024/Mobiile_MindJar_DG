package com.example.myapplication.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


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
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Already logged in — skip auth screens
            startActivity(new Intent(this, Dashboard.class));
            finish();
            return;
        }

        // No active session — show login/sign-up
        setContentView(R.layout.activity_auth);   // Hosts the NavHostFragment configured with auth_nav_graph.xml

    }
}

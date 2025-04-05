package com.example.barcodebuddy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Close SplashActivity
                }
            }, 4000);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Profile profile = new Profile(FirebaseAuth.getInstance().getCurrentUser());
                    if(profile.type =="Admin"){
                        Intent intent = new Intent(SplashActivity.this,AdminMainActivity.class);
                        startActivity(intent);
                    }
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish(); // Close SplashActivity
                }
            }, 4000);
        }
        finish();
    }
}

package com.example.barcodebuddy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // No user logged in, navigate to MainActivity
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Close SplashActivity
                }
            }, 4000);
        } else {
            // User is logged in, check for type
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    FirebaseDatabase.getInstance().getReference("Users").child(uid)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                @Override
                                public void onSuccess(DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        String type = snapshot.child("type").getValue(String.class);

                                        // Navigate based on user type
                                        if ("admin".equals(type)) {
                                            Intent intent = new Intent(SplashActivity.this, AdminMainActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                        }
                                        finish(); // Close SplashActivity
                                    } else {
                                        // In case user data not found in DB
                                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish(); // Close SplashActivity
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    // Optional: handle DB fetch failure
                                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish(); // Close SplashActivity
                                }
                            });
                }
            }, 4000);
        }
    }
}

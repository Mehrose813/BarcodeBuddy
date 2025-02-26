package com.example.barcodebuddy;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.btn);


        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // User not logged in, show login button
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            });
        } else {
            // User logged in, hide login button
            btn.setVisibility(View.GONE);

            // Check if internet is available
            if (isInternetAvailable()) {
                // Fetch user type from the database if internet is available
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String userType = snapshot.child("type").getValue(String.class);
                            if ("admin".equals(userType)) {
                                startActivity(new Intent(MainActivity.this, AdminMainActivity.class));
                            } else {
                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                            }
                        } else {
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(MainActivity.this, SignInActivity.class));
                        }
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(MainActivity.this, SignInActivity.class));
                        finish();
                    }
                });
            } else {
                // No internet, go to HomeActivity directly
                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                finish();
            }
        }
    }

    // Function to check if internet is available
    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }



    }
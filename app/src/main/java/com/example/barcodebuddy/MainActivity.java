package com.example.barcodebuddy;

import android.content.Intent;
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


        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this,SignInActivity.class);
                    startActivity(intent);
                   finish();
                }
            });

        }

        else {
            btn.setVisibility(View.GONE);

            // Fetch user type from the database
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String userType = snapshot.child("type").getValue(String.class);

                        if ("admin".equals(userType)) {
                            Intent intent = new Intent(MainActivity.this, AdminMainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                        startActivity(intent);
                        //finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                    startActivity(intent);
                    //finish();
                }
            });

        }


//        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
//
//                btn.setVisibility(View.GONE);
//                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
//                startActivity(intent);
//                finish();


        }







    }
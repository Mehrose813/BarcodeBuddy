package com.example.barcodebuddy.authdao;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.barcodebuddy.AdminMainActivity;
import com.example.barcodebuddy.HomeActivity;
import com.example.barcodebuddy.Profile;
import com.example.barcodebuddy.ResponseFetch;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AuthDAO {

    public void signin(Activity context, String mail, String pass, ResponseCallBack callBack) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(mail, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String type = snapshot.child("type").getValue(String.class);
                                    if ("admin".equals(type)) {
                                        // Navigate to Admin screen
                                        Intent intent = new Intent(context, AdminMainActivity.class);
                                        context.startActivity(intent);
                                        context.finish();
                                        //callBack.onSuccess(); // Call success after navigation
                                    } else {
                                        // Navigate to User screen
//                                        Intent intent = new Intent(context, HomeActivity.class);
//                                        context.startActivity(intent);
//                                        context.finish();
                                        callBack.onSuccess(); // Call success after navigation
                                    }
                                } else {
                                    FirebaseAuth.getInstance().signOut(); // Log out user
                                    callBack.onError("User data not found in the database.");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                callBack.onError("Database error: " + error.getMessage());
                            }
                        });
                    } else {
                        callBack.onError("Authentication failed: " + task.getException().getMessage());
                    }
                });
    }




    public void signup(Activity context , String name ,String email, String password,String type , ResponseCallBack callbac)
    {
        FirebaseAuth auth =FirebaseAuth.getInstance();
                auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                           String userId =auth.getCurrentUser().getUid();
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                            Profile profile = new Profile(name,email,type);
                            userRef.setValue(profile)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                callbac.onSuccess();
                                            }
                                            else{
                                                callbac.onError(task.getException().getMessage());
                                            }
                                        }
                                    });
                        }
                        else{
                            callbac.onError(task.getException().getMessage());
                        }
                    }
                });


    }


    public void fetchDetail(String userId, ResponseFetch callback) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()) {
                    DataSnapshot snapshot = task.getResult();
                    String email = snapshot.child("email").getValue(String.class);
                    String name = snapshot.child("name").getValue(String.class);
                    String type=snapshot.child("type").getValue(String.class);

                    // Create a Profile object with the fetched data
                    Profile profile = new Profile(name, email,type);

                    // Pass the profile object to the callback
                    callback.onSuccess(profile); // Pass the profile object here
                } else {
                    callback.onError(task.getException().getMessage());
                }
            }


        });
    }




}

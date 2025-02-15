package com.example.barcodebuddy.authdao;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserAllergyDAO {

    public void getUserAllergies(String userId, DataCallBack<List<String>> callBack){
        DatabaseReference allergyRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("allergies_status");

        allergyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> allergies = new ArrayList<>();
                for (DataSnapshot allergySnapshot : snapshot.getChildren()){
                    if(Boolean.TRUE.equals(allergySnapshot.getValue(Boolean.class))){
                        allergies.add(allergySnapshot.getKey());
                    }
                }
                callBack.onSuccess(allergies);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(error.getMessage());
            }
        });

    }
}

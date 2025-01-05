package com.example.barcodebuddy;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserFragment extends Fragment {
    private TextView tvName, tvEmail, tvPassword;
    private DatabaseReference databaseReference;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        // Initialize TextViews
        tvName = view.findViewById(R.id.tv_name);
        tvEmail = view.findViewById(R.id.tv_email);
        tvPassword = view.findViewById(R.id.tv_password);

        // Get current user info
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();  // Get the current user's UID
            databaseReference = FirebaseDatabase.getInstance().getReference("profile").child(userId);

            // Retrieve user data from Firebase Realtime Database
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Get ProfileClass data from Firebase
                        ProfileClass profileClass = snapshot.getValue(ProfileClass.class);
                        if (profileClass != null) {
                            // Display user data on the UI
                            tvName.setText(profileClass.getName());
                            tvEmail.setText(profileClass.getEmail());
                            tvPassword.setText(profileClass.getPassword());  // Avoid displaying password in UI if unnecessary
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("Error", "Database error: ");
                }
            });
        }

        return view;
    }
}

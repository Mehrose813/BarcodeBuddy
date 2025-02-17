package com.example.barcodebuddy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserNotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private ReplyNotificationAdapter adapter;
    private List<Reply> replyList;
    private DatabaseReference userRepliesRef;
    private String currentUserEmail;

    public UserNotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_notification, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_replies);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        replyList = new ArrayList<>();
        adapter = new ReplyNotificationAdapter(replyList);
        recyclerView.setAdapter(adapter);

        // Get current user's email
        currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // Initialize Firebase Database reference
        userRepliesRef = FirebaseDatabase.getInstance().getReference("UserNotifications");

        // Load replies from Firebase
        loadReplies();

        return view;
    }

    private void loadReplies() {
        userRepliesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                replyList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Reply reply = dataSnapshot.getValue(Reply.class);
                    if (reply != null && reply.getUserEmail().equals(currentUserEmail)) {
                        replyList.add(reply);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }
}

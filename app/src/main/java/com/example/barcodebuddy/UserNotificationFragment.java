package com.example.barcodebuddy;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserNotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserNotificationFragment extends Fragment {
    private RecyclerView recyclerView;
    private ReplyNotificationAdapter adapter;
    private List<Reply> replyList;
    private DatabaseReference userRepliesRef;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserNotificationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserNotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserNotificationFragment newInstance(String param1, String param2) {
        UserNotificationFragment fragment = new UserNotificationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_notification, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_replies);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        replyList = new ArrayList<>();
        adapter = new ReplyNotificationAdapter(replyList);
        recyclerView.setAdapter(adapter);

        // Get the current user's ID (or use the appropriate ID)
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRepliesRef = FirebaseDatabase.getInstance().getReference("UserReplies").child(currentUserId);
        userRepliesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                Reply reply = snapshot.getValue(Reply.class);
                if (reply != null) {
                    replyList.add(reply);
                    adapter.notifyItemInserted(replyList.size() - 1);
                }
            }
            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) { }
            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) { }
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) { }
            @Override public void onCancelled(@NonNull DatabaseError error) { }
        });
        return view;

    }
}
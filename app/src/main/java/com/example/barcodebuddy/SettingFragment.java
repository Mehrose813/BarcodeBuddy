package com.example.barcodebuddy;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barcodebuddy.recyclerview.BlogAdapter;
import com.example.barcodebuddy.recyclerview.BlogClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SettingFragment extends Fragment {

    RecyclerView rv_blog;
    BlogAdapter adapter;
    List<BlogClass> blogList;
    DatabaseReference ref;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        // Initialize RecyclerView
        rv_blog = view.findViewById(R.id.recyclerViewBlogs);
        rv_blog.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize Firebase Reference
        ref = FirebaseDatabase.getInstance().getReference("Blogs");

        // Initialize List and Adapter
        blogList = new ArrayList<>();
//        adapter = new BlogAdapter(blogList);
        rv_blog.setAdapter(adapter);

        // Fetch Blogs from Firebase
        fetchBlogsFromFirebase();

        return view;
    }

    private void fetchBlogsFromFirebase() {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                blogList.clear(); // Clear old data
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    BlogClass blog = dataSnapshot.getValue(BlogClass.class);
                    blogList.add(blog);
                }
                adapter.notifyDataSetChanged(); // Update RecyclerView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }
}

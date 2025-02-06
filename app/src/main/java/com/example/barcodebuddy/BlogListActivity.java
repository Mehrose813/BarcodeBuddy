package com.example.barcodebuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barcodebuddy.recyclerview.BlogClass;
import com.example.barcodebuddy.recyclerview.BlogListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BlogListActivity extends AppCompatActivity {

    private DatabaseReference blogRef;
    private RecyclerView recyclerView;
    private BlogListAdapter blogListAdapter;
    private Button btnaddblog;
    private List<BlogClass> blogList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_list);

        // Initialize Firebase
        blogRef = FirebaseDatabase.getInstance().getReference("Blogs");

        // Initialize Views
        recyclerView = findViewById(R.id.rv_blog_list);
        btnaddblog = findViewById(R.id.btn_add_blog);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize List and Adapter
        blogList = new ArrayList<>();
        blogListAdapter = new BlogListAdapter(this, blogList); // Context passed here
        recyclerView.setAdapter(blogListAdapter);

        // Fetch Blogs from Firebase
        loadBlogs();

        // Add Blog Button Click Listener
        btnaddblog.setOnClickListener(view -> {
            startActivity(new Intent(BlogListActivity.this, AddBlogsActivity.class));
        });
    }

    private void loadBlogs() {
        blogRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                blogList.clear(); // Clear old data

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    BlogClass blog = dataSnapshot.getValue(BlogClass.class);

                    if (blog != null) { // Prevent NullPointerException
                        blog.setBlogId(dataSnapshot.getKey());
                        blogList.add(blog);
                    }
                }

                // Notify Adapter for UI update
                blogListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BlogListActivity.this, "Failed to load blogs!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

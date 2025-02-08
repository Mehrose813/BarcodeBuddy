package com.example.barcodebuddy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barcodebuddy.recyclerview.BlogClass;
import com.example.barcodebuddy.recyclerview.BlogListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BlogListActivity extends AppCompatActivity {

    RecyclerView recyclerBlog;
    Button btnAddBlog;
    BlogListAdapter adapter; // Adapter ko manage karne ke liye reference
    List<BlogClass> blogList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_list);

        btnAddBlog = findViewById(R.id.btn_add_blog_list);
        recyclerBlog = findViewById(R.id.rv_blog_list_add);

        // RecyclerView Layout Set
        recyclerBlog.setLayoutManager(new LinearLayoutManager(this));

        // Adapter Initialize
        adapter = new BlogListAdapter(blogList);
        recyclerBlog.setAdapter(adapter);

        btnAddBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BlogListActivity.this, AddBlogsActivity.class);
                startActivity(intent);
            }
        });

        // Firebase Data Fetching
        fetchBlogsFromFirebase();
    }

    private void fetchBlogsFromFirebase() {
        FirebaseDatabase.getInstance().getReference("Blogs")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        blogList.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot blogsnap : snapshot.getChildren()) {
                                try {
                                    BlogClass blogClass = blogsnap.getValue(BlogClass.class);
                                    if (blogClass != null) {
                                        blogClass.setBlogId(blogsnap.getKey());


                                        String imageId = blogClass.getBlogImage();
                                        FirebaseDatabase.getInstance().getReference("imagesString")
                                                .child(imageId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot imgSnapshot) {
                                                        if (imgSnapshot.exists()) {
                                                            String base64Image = imgSnapshot.getValue(String.class);
                                                            blogClass.setBlogImage(base64Image);
                                                        } else {
                                                            blogClass.setBlogImage(null);
                                                        }
                                                        blogList.add(blogClass);
                                                        adapter.notifyDataSetChanged();
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        Log.e("FirebaseError", "Image fetch error: " + error.getMessage());
                                                    }
                                                });
                                    }
                                } catch (Exception e) {
                                    Log.e("FirebaseError", "Data format issue: " + e.getMessage());
                                }
                            }
                        } else {
                            Log.e("FirebaseError", "No Blogs found in database.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseError", "Database error: " + error.getMessage());
                    }
                });
    }

}

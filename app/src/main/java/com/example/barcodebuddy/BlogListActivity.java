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
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class BlogListActivity extends ToolBarActivity {

    RecyclerView recyclerBlog;
    Button btnAddBlog;
    BlogListAdapter adapter;
    List<BlogClass> blogList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_blog_list);

        getLayoutInflater().inflate(R.layout.activity_blog_list, findViewById(R.id.container));
        setToolbarTitle("Blogs List");
        showBackButton(true);

        btnAddBlog = findViewById(R.id.btn_add_blog_list);
        recyclerBlog = findViewById(R.id.rv_blog_list_add);

        recyclerBlog.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BlogListAdapter(this, blogList);
        recyclerBlog.setAdapter(adapter);

        btnAddBlog.setOnClickListener(view -> {
            Intent intent = new Intent(BlogListActivity.this, AddBlogsActivity.class);
            startActivity(intent);
        });

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
                                BlogClass blogClass = blogsnap.getValue(BlogClass.class);
                                if (blogClass != null) {
                                    blogClass.setBlogId(blogsnap.getKey());
                                    blogList.add(blogClass);
                                }
                            }
                            adapter.notifyDataSetChanged(); // ✅ Data update fix
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseError", "Database error: " + error.getMessage());
                    }
                });
    }
}

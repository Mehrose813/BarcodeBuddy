package com.example.barcodebuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryListActivity extends AppCompatActivity {

    private RecyclerView rvCat;
    private Button btnAddCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        // Initialize views
        rvCat = findViewById(R.id.rv_categories);
        btnAddCategory = findViewById(R.id.btn_add_category);

        // Set layout manager for RecyclerView
        rvCat.setLayoutManager(new LinearLayoutManager(this));

        // Add category button click listener
        btnAddCategory.setOnClickListener(view -> {
            Intent intent = new Intent(CategoryListActivity.this, AddCategoryActivity.class);
            startActivity(intent);
        });

        // Load categories from Firebase
        loadCategories();
    }

    private void loadCategories() {
        List<CategoryClass> categoryList = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference("Categories")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        categoryList.clear(); // Clear the list before adding new data

                        for (DataSnapshot catSnapshot : snapshot.getChildren()) {
                            // Get data as CategoryClass
                            CategoryClass category = catSnapshot.getValue(CategoryClass.class);
                            if (category != null) {
                                category.setId(catSnapshot.getKey()); // Set the ID
                                categoryList.add(category); // Add to the list
                            }
                        }

                        // Set the adapter with the updated list
                        rvCat.setAdapter(new CategoryAdapter(CategoryListActivity.this, categoryList));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle errors (optional: log the error or show a toast)
                    }
                });
    }
}

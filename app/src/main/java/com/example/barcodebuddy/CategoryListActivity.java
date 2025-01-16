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
    private RecyclerView rvCategories;
    private Button btnAddCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list); // Ensure the layout file matches this name

        rvCategories = findViewById(R.id.rv_categories);
        btnAddCategory = findViewById(R.id.btn_add_category);

        // Set RecyclerView layout manager
        rvCategories.setLayoutManager(new LinearLayoutManager(this));

        // Navigate to AddCategoryActivity on button click
        btnAddCategory.setOnClickListener(view -> {
            Intent intent = new Intent(CategoryListActivity.this, AddCategoryActivity.class);
            startActivity(intent);
        });

        // Fetch and display categories
        loadCategories();
    }

    private void loadCategories() {
        List<CategoryClass> categoryClasses = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference("Ingredients")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        categoryClasses.clear(); // Clear the list before adding new data
                        for (DataSnapshot ingredientSnapshot : snapshot.getChildren()) {
                            CategoryClass categoryClass = ingredientSnapshot.getValue(CategoryClass.class);
                            if (categoryClass != null) {
                                categoryClass.setId(ingredientSnapshot.getKey()); // Set the ID
                                categoryClasses.add(categoryClass); // Add to the list
                            }
                        }

                        // Set the adapter
                        rvCategories.setAdapter(new CategoryAdapter(CategoryListActivity.this, categoryClasses));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle database errors
                        // Optionally show a toast or log the error
                    }
                });
    }
}

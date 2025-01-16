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

    private RecyclerView rvCategory;
    private Button btnAddCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        // Initialize views
        rvCategory = findViewById(R.id.rv_categories);
        btnAddCategory = findViewById(R.id.btn_add_category);

        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryListActivity.this,AddCategoryActivity.class);
                startActivity(intent);
            }
        });

        List<CategoryClass> categoryList = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference("Categories")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        categoryList.clear();
                        for (DataSnapshot ingredientSnapshot : snapshot.getChildren()){
                            CategoryClass categoryClass = ingredientSnapshot.getValue(CategoryClass.class);
                            categoryClass.setId(ingredientSnapshot.getKey());
                            categoryList.add(categoryClass);
                        }
                        rvCategory.setAdapter(new CategoryAdapter(categoryList));


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
            });

    }
}

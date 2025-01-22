package com.example.barcodebuddy;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barcodebuddy.recyclerview.IngridentAdapaterdisplay;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductDisplayActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private IngridentAdapaterdisplay adapter;
    private List<Ingredient> ingredientList;

    private TextView tvProdName, tvProdCat;

    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_display);

        // Initialize views
        tvProdName = findViewById(R.id.tv_proname);
        tvProdCat = findViewById(R.id.tv_proDes);
        recyclerView = findViewById(R.id.recyclerview); // Initialize recyclerView

        // Set RecyclerView layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Initialize ingredient list
        ingredientList = new ArrayList<>();

        // Initialize adapter
        adapter = new IngridentAdapaterdisplay(ingredientList);
        recyclerView.setAdapter(adapter);

        // Get data from Intent
        String productName = getIntent().getStringExtra("name");
        String productCategory = getIntent().getStringExtra("cat");

        tvProdName.setText(productName);
        tvProdCat.setText(productCategory);

        // Initialize Firebase reference
        ref = FirebaseDatabase.getInstance().getReference("ingredients"); // Assuming "ingredients" is the node

        // Fetch ingredients from Firebase
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ingredientList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Ingredient ingredient = snapshot.getValue(Ingredient.class);
                    ingredientList.add(ingredient);
                }
                adapter.notifyDataSetChanged(); // Notify adapter of data changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProductDisplayActivity.this, "Failed to load ingredients", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

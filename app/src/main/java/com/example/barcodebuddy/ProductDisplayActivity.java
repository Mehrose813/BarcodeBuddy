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

    private TextView tvProdName, tvProdCat,tvProDes,tvProHealth;

    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_display);

        // Initialize Views
        tvProdName = findViewById(R.id.tv_proname);
        tvProdCat = findViewById(R.id.tv_proCat);
        tvProDes = findViewById(R.id.tv_proDes);
        tvProHealth = findViewById(R.id.tv_prohealthy);
        recyclerView = findViewById(R.id.recyclerview); // Initialize recyclerView

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Initialize ingredient list
        ingredientList = new ArrayList<>();

        // Set adapter
        adapter = new IngridentAdapaterdisplay(ingredientList);
        recyclerView.setAdapter(adapter);

        // Get product name, category and product key from intent
        String productName = getIntent().getStringExtra("name");
        String productCategory = getIntent().getStringExtra("cat");
        String productDes = getIntent().getStringExtra("desc");
        String productHealthy= getIntent().getStringExtra("healthy");
        String productKey = getIntent().getStringExtra("productKey");

        tvProdName.setText(productName);
        tvProdCat.setText(productCategory);
        tvProDes.setText(productDes);
        tvProHealth.setText(productHealthy);

        ref = FirebaseDatabase.getInstance().getReference("Products").child(productKey).child("ingredients");



        fetchIngredientsForProduct(productKey);
    }

    private void fetchIngredientsForProduct(String productKey) {
        ref.child(productKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ingredientList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ingredientSnapshot : dataSnapshot.getChildren()) {
                        Ingredient ingredient = ingredientSnapshot.getValue(Ingredient.class);
                        if (ingredient != null) {
                            ingredientList.add(ingredient);
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ProductDisplayActivity.this, "No ingredients found for this product", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProductDisplayActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

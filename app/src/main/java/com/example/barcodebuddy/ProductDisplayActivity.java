package com.example.barcodebuddy;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
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

    private TextView tvProdName, tvProdCat, tvProDes, tvProHealth;
    private ImageView ivProductImage;

    private DatabaseReference ref, Picturref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_display);

        // Initialize Views
        tvProdName = findViewById(R.id.tv_proname);
        tvProdCat = findViewById(R.id.tv_proCat);
        tvProDes = findViewById(R.id.tv_proDes);
        tvProHealth = findViewById(R.id.tv_prohealthy);
        recyclerView = findViewById(R.id.recyclerview);
        ivProductImage = findViewById(R.id.img_product);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        ingredientList = new ArrayList<>();

        // Set adapter for ingredients RecyclerView
        adapter = new IngridentAdapaterdisplay(ingredientList);
        recyclerView.setAdapter(adapter);

        // Get product details passed from the previous activity
        String productName = getIntent().getStringExtra("name");
        String productCategory = getIntent().getStringExtra("cat");
        String productDesc = getIntent().getStringExtra("desc");
        String productHealthy = getIntent().getStringExtra("healthy");
        String productKey = getIntent().getStringExtra("productKey");

        // Fetch imageId from the "Products" node
        Picturref = FirebaseDatabase.getInstance().getReference("Products").child(productKey);
        Picturref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String ImageId = snapshot.child("img").getValue(String.class);
                    if (ImageId != null) {
                        fetchProfileImage(ImageId); // Fetch image using the imageId
                    } else {
                        Log.e("Firebase", "Image ID not found in database.");
                    }
                } else {
                    Log.e("Firebase", "Product not found in database.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error: " + error.getMessage());
            }
        });

        // Set product details to TextViews
        tvProdName.setText(productName);
        tvProdCat.setText(productCategory);
        tvProDes.setText(productDesc);
        tvProHealth.setText(productHealthy);

        // Fetch ingredients for this product from Firebase
        ref = FirebaseDatabase.getInstance().getReference("Products").child(productKey).child("ingredients");
        fetchIngredientsForProduct(productKey);
    }

    private void fetchIngredientsForProduct(String productKey) {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ingredientList.clear(); // Clear the list before adding new data
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ingredientSnapshot : dataSnapshot.getChildren()) {
                        Ingredient ingredient = ingredientSnapshot.getValue(Ingredient.class);
                        if (ingredient != null) {
                            ingredientList.add(ingredient);
                        }
                    }
                    adapter.notifyDataSetChanged(); // Notify the adapter that data has changed
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

    private void fetchProfileImage(String imageId) {
        FirebaseDatabase.getInstance().getReference("Product Images")
                .child(imageId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String imageString = snapshot.getValue(String.class);
                            if (imageString != null && !imageString.isEmpty()) {
                                ivProductImage.setImageBitmap(MyUtilClass.base64ToBitmap(imageString)); // Convert base64 string to Bitmap
                            } else {
                                Log.e("Firebase", "Image string is empty.");
                            }
                        } else {
                            Log.e("Firebase", "Image not found in Product Images.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Error fetching image: " + error.getMessage());
                    }
                });
    }
}

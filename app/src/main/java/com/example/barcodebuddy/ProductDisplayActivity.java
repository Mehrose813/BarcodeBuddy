package com.example.barcodebuddy;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barcodebuddy.recyclerview.IngridentAdapaterdisplay;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductDisplayActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private IngridentAdapaterdisplay adapter;
    private List<Ingredient> ingredientList;
    private TextView tvProdName, tvProdCat, tvProDes, tvProHealth;
    private ImageView ivProductImage, ivNutri;
    private DatabaseReference ref, pictureref;
    private Set<String> userAllergies = new HashSet<>(); // Store user's allergies
    ScrollView scrollView;

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
        ivNutri = findViewById(R.id.iv_nutri);
        scrollView = findViewById(R.id.layout);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        ingredientList = new ArrayList<>();
        adapter = new IngridentAdapaterdisplay(ingredientList);
        recyclerView.setAdapter(adapter);

        // Get product details from intent
        String productName = getIntent().getStringExtra("name");
        String productCategory = getIntent().getStringExtra("cat");
        String productDesc = getIntent().getStringExtra("desc");
        String productHealthy = getIntent().getStringExtra("healthy");
        String productKey = getIntent().getStringExtra("productKey");

        // Set product details in UI
        tvProdName.setText(productName);
        tvProdCat.setText(productCategory);
        tvProDes.setText(productDesc);
        tvProHealth.setText(productHealthy);

        // Fetch product image
        pictureref = FirebaseDatabase.getInstance().getReference("Products").child(productKey);
        fetchProductImage(productKey);

        // Fetch user allergies first
        fetchUserAllergies(() -> {
            // Once allergies are loaded, fetch ingredients
            fetchIngredientsForProduct(productKey);
        });

        ref = FirebaseDatabase.getInstance().getReference("Products").child(productKey).child("healthy");
        fetchHealthinessForProduct();
    }

    private void fetchHealthinessForProduct() {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String value = snapshot.getValue(String.class);
                    if (value != null) {
                        value = value.split(":")[0].trim(); // Extract before ":"

                        switch (value) {
                            case "A":
                            case "1":
                                ivNutri.setImageResource(R.drawable.a);
                                break;
                            case "B":
                            case "2":
                                ivNutri.setImageResource(R.drawable.b);
                                break;
                            case "C":
                            case "3":
                                ivNutri.setImageResource(R.drawable.c);
                                break;
                            case "D":
                            case "4":
                                ivNutri.setImageResource(R.drawable.d);
                                break;
                            case "E":
                            case "5":
                                ivNutri.setImageResource(R.drawable.e);
                                break;
                            default:
                                ivNutri.setImageResource(R.drawable.logo);
                                break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductDisplayActivity.this, "Failed to load healthiness data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Fetch user's allergies from Firebase
    private void fetchUserAllergies(Runnable callback) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference allergyRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("allergies");

        allergyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userAllergies.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot allergySnapshot : snapshot.getChildren()) {
                        String allergy = allergySnapshot.getValue(String.class);
                        if (allergy != null) {
                            userAllergies.add(allergy.toLowerCase().trim()); // Store in lowercase for case-insensitive comparison
                        }
                    }
                }
                callback.run(); // Call the next step after fetching allergies
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductDisplayActivity.this, "Error loading allergies", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void fetchIngredientsForProduct(String productKey) {
        ref = FirebaseDatabase.getInstance().getReference("Products").child(productKey).child("ingredients");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ingredientList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ingredientSnapshot : dataSnapshot.getChildren()) {
                        Ingredient ingredient = ingredientSnapshot.getValue(Ingredient.class);
                        if (ingredient != null) {
                            Log.d("IngredientData", "Ingredient: " + ingredient.getName());
                            // Check if the ingredient is in the user's allergy list
                            if (userAllergies.contains(ingredient.getName().toLowerCase().trim())) {
                                Log.d("AllergyMatch", "Allergy matched: " + ingredient.getName());
                                // Highlight the ingredient in red by changing scrollView background
                                scrollView.setBackground(ContextCompat.getDrawable(ProductDisplayActivity.this, R.color.red));
                            }
                            ingredientList.add(ingredient);
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ProductDisplayActivity.this, "No ingredients found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProductDisplayActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchProductImage(String productKey) {
        DatabaseReference imageRef = FirebaseDatabase.getInstance().getReference("Products").child(productKey);

        imageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Product product = snapshot.getValue(Product.class);
                if (product != null && product.getImg() != null) {
                    String imageKey = product.getImg();
                    Log.d("ProductImage", "Image Key: " + imageKey);

                    FirebaseDatabase.getInstance().getReference("Product Images")
                            .child(imageKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String ImageString = snapshot.getValue(String.class);
                                    if (ImageString != null) {
                                        ivProductImage.setImageBitmap(MyUtilClass.base64ToBitmap(ImageString));
                                    } else {
                                        Log.e("FirebaseError", "Image data is null");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("Firebase", "Error fetching image: " + error.getMessage());
                                }
                            });
                } else {
                    Log.e("FirebaseError", "Product or Image Key is null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error fetching product: " + error.getMessage());
            }
        });
    }
}


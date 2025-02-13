package com.example.barcodebuddy;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.barcodebuddy.recyclerview.IngridentAdapaterdisplay;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import android.util.Base64;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductDisplayActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private IngridentAdapaterdisplay adapter;
    private List<Ingredient> ingredientList;
    private TextView tvProdName, tvProdCat, tvProDes, tvProHealth;
    private ImageView ivProductImage,ivNutri;
    private DatabaseReference ref, pictureref;

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
        ivNutri=findViewById(R.id.iv_nutri);


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

        // Fetch ingredients
        ref = FirebaseDatabase.getInstance().getReference("Products").child(productKey).child("ingredients");
        fetchIngredientsForProduct();

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
                        // Extract first character (Number or Letter)
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
    private void fetchIngredientsForProduct() {
        ref.addValueEventListener(new ValueEventListener() {
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

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
    private BarChart barChart;
    private TextView tvProdName, tvProdCat, tvProDes, tvProHealth;
    private ImageView ivProductImage;
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
        barChart = findViewById(R.id.chart);

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

        // Fetch and update healthy value in bar chart
        fetchHealthyValue(productKey);
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
        FirebaseDatabase.getInstance().getReference("Products").child(productKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String imageUrl = snapshot.child("img").getValue(String.class);
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Glide.with(ProductDisplayActivity.this).load(imageUrl).into(ivProductImage);
                            } else {
                                Log.e("Firebase", "Image URL is empty.");
                            }
                        } else {
                            Log.e("Firebase", "Image node not found.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Error fetching image: " + error.getMessage());
                    }
                });
    }

    private void fetchHealthyValue(String productKey) {
        DatabaseReference healthyRef = FirebaseDatabase.getInstance().getReference("Products").child(productKey).child("healthy");
        healthyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String healthyStr = snapshot.getValue(String.class);
                    if (healthyStr != null) {
                        try {
                            String[] parts = healthyStr.split(":");
                            if (parts.length == 2) {
                                int healthyValue = Integer.parseInt(parts[0].trim());
                                String healthyLabel = parts[1].trim();
                                setupBarChart(healthyValue, healthyLabel);
                            } else {
                                Log.e("FirebaseError", "Invalid healthy value format: " + healthyStr);
                            }
                        } catch (NumberFormatException e) {
                            Log.e("FirebaseError", "Error parsing healthy value: " + healthyStr);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error fetching healthy value: " + error.getMessage());
            }
        });
    }

    private void setupBarChart(int healthyValue, String healthyLabel) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        List<String> labels = Arrays.asList("Unhealthy", "Less Healthy", "Moderate", "Healthy", "Very Healthy");

        int barColor;

        switch (healthyValue) {
            case 1:
                barColor = Color.RED; // Unhealthy
                break;
            case 2:
                barColor = ContextCompat.getColor(this, R.color.light_red); // Using color from colors.xml
                break;

            case 3:
                barColor = ContextCompat.getColor(this,R.color.orange); // Orange for Moderate
                break;
            case 4:
                barColor = ContextCompat.getColor(this,R.color.light_green); // Healthy
                break;
            case 5:
                barColor = ContextCompat.getColor(this,R.color.dark_green); // Dark Green for Very Healthy
                break;
            default:
                barColor = Color.GRAY;
                break;
        }

        entries.add(new BarEntry(healthyValue - 1, healthyValue));

        BarDataSet dataSet = new BarDataSet(entries, "NutriValue");
        dataSet.setColor(barColor);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);
        barChart.setData(barData);

        barChart.getDescription().setEnabled(false);
        barChart.invalidate();

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(5);

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(1f);
        yAxis.setAxisMaximum(5f);
        yAxis.setGranularity(1f);
        yAxis.setLabelCount(5);

        barChart.getAxisRight().setEnabled(false);
    }
}

package com.example.barcodebuddy;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class IngredientQuantityActivity extends ToolBarActivity {

    Spinner spIn;
    EditText edQOI;
    Button btnAdd;
    TextView tvProductName, tvCatName, tvH, tvBarcode;
    ArrayList<String> array;
    ArrayAdapter<String> adapter;
    LinearLayout selected;
    ImageView ivImg;
    String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_ingredient_quantity, findViewById(R.id.container));
        setToolbarTitle("Product Detail");
        showBackButton(true);

        // Initialize views
        tvProductName = findViewById(R.id.tv_product_name);
        tvCatName = findViewById(R.id.tv_cat_name);
        tvH = findViewById(R.id.tv_health);
        selected = findViewById(R.id.selected_ingredient_layout);
        ivImg = findViewById(R.id.iv_show_img);
        tvBarcode = findViewById(R.id.tv_barcode);

        // Get product details from intent
        productId = getIntent().getStringExtra("id");
        String productName = getIntent().getStringExtra("name");
        String productcat = getIntent().getStringExtra("category");
        String productH = getIntent().getStringExtra("healthiness");
        String keyOfImg = getIntent().getStringExtra("img");
        String barcode = getIntent().getStringExtra("barcode");

        // Set product name and category
        tvProductName.setText(productName);
        tvCatName.setText(productcat);
        tvBarcode.setText(barcode);
        setHealthTextColor(productH);

        // Initialize spinner and button
        spIn = findViewById(R.id.spinner_ingredient);
        edQOI = findViewById(R.id.ed_quantity_ingredient);
        btnAdd = findViewById(R.id.btn_add);
        array = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, array);
        spIn.setAdapter(adapter);

        // Fetch ingredients from Firebase
        fetchIngredients();

        // Load existing ingredients
        loadExistingIngredients();

        // Add button click listener
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIngredient();
            }
        });

        // Fetch product image
        fetchProductImage(keyOfImg);
    }

    private void setHealthTextColor(String productH) {
        if (productH != null) {
            tvH.setText(productH);
            int color = Color.BLACK;
            if (productH.equals("Un-healthy")) {
                color = getResources().getColor(R.color.red);
            } else if (productH.equals("Moderate")) {
                color = getResources().getColor(R.color.orange);
            } else if (productH.equals("Healthy") || productH.equals("Very healthy")) {
                color = getResources().getColor(R.color.dark_green);
            }
            tvH.setTextColor(color);
        }
    }

    private void fetchProductImage(String keyOfImg) {
        if (keyOfImg != null) {
            FirebaseDatabase.getInstance().getReference("Product Images").child(keyOfImg)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String imageString = snapshot.getValue(String.class);
                                try {
                                    Bitmap bitmap = MyUtilClass.base64ToBitmap(imageString);
                                    if (bitmap != null) {
                                        ivImg.setImageBitmap(bitmap);
                                    } else {
                                        Toast.makeText(IngredientQuantityActivity.this, "Failed to decode image!", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    Log.e("Error", "Error decoding image: " + e.getMessage());
                                    Toast.makeText(IngredientQuantityActivity.this, "Error decoding image", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(IngredientQuantityActivity.this, "Snapshot does not exist!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Debug", "Error: " + error.getMessage());
                            Toast.makeText(IngredientQuantityActivity.this, "Failed to load image: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void fetchIngredients() {
        FirebaseDatabase.getInstance().getReference("Ingredients").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                array.clear();
                array.add("Select an ingredient"); // First item remains fixed

                HashSet<String> uniqueIngredients = new HashSet<>(); // Case-insensitive set
                ArrayList<String> sortedList = new ArrayList<>(); // List for sorting

                for (DataSnapshot myData : snapshot.getChildren()) {
                    String ingredientName = myData.child("name").getValue(String.class);
                    if (ingredientName != null) {
                        String cleanedName = normalizeIngredient(ingredientName.trim().toLowerCase()); // Normalize name

                        // Ensure uniqueness based on base form
                        if (!uniqueIngredients.contains(cleanedName)) {
                            uniqueIngredients.add(cleanedName); // Add normalized name to set
                            sortedList.add(ingredientName.trim()); // Add original name to list
                        }
                    }
                }

                // Sort list alphabetically (A → Z)
                Collections.sort(sortedList);

                // Add sorted unique items to the main list
                array.addAll(sortedList);

                adapter.notifyDataSetChanged(); // Update adapter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(IngredientQuantityActivity.this, "Failed to load ingredients", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadExistingIngredients() {
        DatabaseReference ingredientsRef = FirebaseDatabase.getInstance().getReference("Products").child(productId).child("ingredients");
        ingredientsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    String ingredientName = child.child("name").getValue(String.class);
                    String ingredientQty = child.child("qty").getValue(String.class);
                    String ingredientId = child.getKey();

                    if (ingredientName != null && ingredientQty != null) {
                        addIngredientToLayout(ingredientName, ingredientQty, ingredientId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(IngredientQuantityActivity.this, "Failed to load existing ingredients", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addIngredientToLayout(String ingredientName, String quantity, String ingredientId) {
        LinearLayout ingredientLayout = new LinearLayout(this);
        ingredientLayout.setOrientation(LinearLayout.HORIZONTAL);
        ingredientLayout.setTag(ingredientId);

        TextView textView = new TextView(this);
        textView.setText(ingredientName + " " + quantity);
        textView.setTextSize(15);

        ImageView deleteIcon = new ImageView(this);
        deleteIcon.setImageResource(android.R.drawable.ic_delete);
        deleteIcon.setPadding(300, 0, 0, 0);

        ingredientLayout.addView(textView);
        ingredientLayout.addView(deleteIcon);

        // Set delete functionality
        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDeleteIngredient(ingredientId, ingredientLayout);
            }
        });

        // Add the ingredient layout to the selected ingredients layout
        selected.addView(ingredientLayout);
    }

    private void confirmDeleteIngredient(String ingredientId, LinearLayout ingredientLayout) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Ingredient")
                .setMessage("Are you sure you want to delete this ingredient?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    DatabaseReference ingredientsRef = FirebaseDatabase.getInstance().getReference("Products").child(productId).child("ingredients");
                    ingredientsRef.child(ingredientId).removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(IngredientQuantityActivity.this, "Ingredient deleted", Toast.LENGTH_SHORT).show();
                            selected.removeView(ingredientLayout);
                        } else {
                            Toast.makeText(IngredientQuantityActivity.this, "Failed to delete ingredient", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void addIngredient() {
        String selectedIng = spIn.getSelectedItem() != null ? spIn.getSelectedItem().toString().trim() : "";
        String qOI = edQOI.getText().toString().trim();

        // Validate ingredient selection
        if (selectedIng.isEmpty() || selectedIng.equals("Select an ingredient")) {
            TextView errorText = (TextView) spIn.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText("Please select ingredient");
            Toast.makeText(IngredientQuantityActivity.this, "Select an ingredient", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate quantity input
        if (qOI.isEmpty()) {
            Toast.makeText(IngredientQuantityActivity.this, "Enter quantity of ingredient", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase reference to ingredients
        DatabaseReference ingredientsRef = FirebaseDatabase.getInstance().getReference("Products").child(productId).child("ingredients");

        // Check if ingredient exists
        ingredientsRef.orderByChild("name").equalTo(selectedIng).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Ingredient exists, update its quantity
                    for (DataSnapshot child : snapshot.getChildren()) {
                        child.getRef().child("qty").setValue(qOI).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(IngredientQuantityActivity.this, "Ingredient updated", Toast.LENGTH_SHORT).show();
                                    // Update UI list dynamically
                                    for (int i = 0; i < selected.getChildCount(); i++) {
                                        LinearLayout childLayout = (LinearLayout) selected.getChildAt(i);
                                        String tag = (String) childLayout.getTag();
                                        if (tag != null && tag.equals(child.getKey())) {
                                            TextView textView = (TextView) childLayout.getChildAt(0);
                                            textView.setText(selectedIng + " " + qOI); // Update displayed quantity
                                            break;
                                        }
                                    }
                                } else {
                                    Toast.makeText(IngredientQuantityActivity.this, "Failed to update ingredient", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    // Ingredient does not exist, create a new one
                    String ingredientId = ingredientsRef.push().getKey();
                    if (ingredientId == null) {
                        Toast.makeText(IngredientQuantityActivity.this, "Failed to generate ingredient ID", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Ingredient newIngredient = new Ingredient();
                    newIngredient.setName(selectedIng);
                    newIngredient.setQty(qOI);

                    ingredientsRef.child(ingredientId).setValue(newIngredient).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(IngredientQuantityActivity.this, "Ingredient successfully saved", Toast.LENGTH_SHORT).show();
                                // Add ingredient to UI
                                addIngredientToLayout(selectedIng, qOI, ingredientId);
                                // Reset input fields
                                edQOI.setText("");
                                spIn.setSelection(0);
                            } else {
                                Toast.makeText(IngredientQuantityActivity.this, "Failed to save ingredient", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(IngredientQuantityActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String normalizeIngredient(String ingredient) {
        // Remove trailing 's' if word is plural (e.g., "sugars" → "sugar")
        if (ingredient.endsWith("s") && ingredient.length() > 1) {
            return ingredient.substring(0, ingredient.length() - 1); // Remove last 's'
        }
        return ingredient;
    }
}
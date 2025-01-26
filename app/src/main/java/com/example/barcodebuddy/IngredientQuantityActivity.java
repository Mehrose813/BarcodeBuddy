package com.example.barcodebuddy;

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
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class IngredientQuantityActivity extends AppCompatActivity {

    Spinner spIn;
    EditText edQOI;
    Button btnAdd;
    TextView tvProductName, tvCatName, tvH;
    ArrayList<String> array;
    ArrayAdapter<String> adapter;
    LinearLayout selected;
    ArrayList<Ingredient> ingredientsList;
    ImageView ivImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_quantity);

        tvProductName = findViewById(R.id.tv_product_name);
        tvCatName = findViewById(R.id.tv_cat_name);
        tvH = findViewById(R.id.tv_health);
        selected = findViewById(R.id.selected_ingredient_layout);
        ivImg = findViewById(R.id.iv_show_img);

        String productId = getIntent().getStringExtra("id");
        String productName = getIntent().getStringExtra("name");
        String productcat = getIntent().getStringExtra("category");
        String productH = getIntent().getStringExtra("healthiness");
        String keyOfImg = getIntent().getStringExtra("img");
        ingredientsList = new ArrayList<>();
        Log.e("imgKey: ", keyOfImg + "");

        // Fetch image from Firebase
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

        if (productId == null || productName == null || productcat == null || productH == null) {
            Toast.makeText(this, "Product details are missing!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set product name and category
        tvProductName.setText("Product Name : " + productName);
        tvCatName.setText("Category Name : " + productcat);

        if (productH != null) {
            tvH.setText("Product Nutri value:" + productH);
            // Set color based on healthiness value
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

        spIn = findViewById(R.id.spinner_ingredient);
        edQOI = findViewById(R.id.ed_quantity_ingredient);
        btnAdd = findViewById(R.id.btn_add);
        array = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, array);
        spIn.setAdapter(adapter);

        // Fetch ingredients from Firebase
        FirebaseDatabase.getInstance().getReference("Ingredients").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                array.clear();
                array.add("Select an ingredient");

                for (DataSnapshot myData : snapshot.getChildren()) {
                    String ingredientName = myData.child("name").getValue(String.class);
                    if (ingredientName != null) {
                        array.add(ingredientName.trim());
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(IngredientQuantityActivity.this, "Failed to load ingredients", Toast.LENGTH_SHORT).show();
            }
        });

        // Add button click listener
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                        LinearLayout ingredientLayout = new LinearLayout(IngredientQuantityActivity.this);
                                        ingredientLayout.setOrientation(LinearLayout.HORIZONTAL);
                                        ingredientLayout.setTag(ingredientId);

                                        TextView textView = new TextView(IngredientQuantityActivity.this);
                                        textView.setText(selectedIng + " " + qOI);
                                        textView.setTextSize(15);

                                        ImageView deleteIcon = new ImageView(IngredientQuantityActivity.this);
                                        deleteIcon.setImageResource(android.R.drawable.ic_delete);
                                        deleteIcon.setPadding(300, 0, 0, 0);

                                        ingredientLayout.addView(textView);
                                        ingredientLayout.addView(deleteIcon);

                                        // Set delete functionality
                                        deleteIcon.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                String ingredientIdToDelete = (String) ingredientLayout.getTag();

                                                // Remove ingredient from Firebase
                                                ingredientsRef.child(ingredientIdToDelete).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(IngredientQuantityActivity.this, "Ingredient deleted", Toast.LENGTH_SHORT).show();
                                                            selected.removeView(ingredientLayout);
                                                        } else {
                                                            Toast.makeText(IngredientQuantityActivity.this, "Failed to delete ingredient", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        });

                                        // Add the ingredient layout to the selected ingredients layout
                                        selected.addView(ingredientLayout);

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
        });
    }
}

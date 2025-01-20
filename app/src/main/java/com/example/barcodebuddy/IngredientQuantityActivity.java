package com.example.barcodebuddy;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class IngredientQuantityActivity extends AppCompatActivity {

    Spinner spIn, spH;
    EditText edQOI;
    Button btnAdd;
    TextView tvProductName, tvCatName;
    ArrayList<String> array, arrayH;
    ArrayAdapter<String> adapter, adapterH;
    LinearLayout selected;
    ArrayList<Ingredient> ingredientsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ingredient_quantity);

        tvProductName = findViewById(R.id.tv_product_name);
        tvCatName = findViewById(R.id.tv_cat_name);
        selected = findViewById(R.id.selected_ingredient_layout);
        spH = findViewById(R.id.spinner_healthy);

        String productId = getIntent().getStringExtra("id");
        String productName = getIntent().getStringExtra("name");
        String productcat = getIntent().getStringExtra("category");
        ingredientsList = new ArrayList<>();

        if (productId == null) {
            Toast.makeText(this, "Product ID is missing!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set product name and category to the TextViews
        if (productName != null) {
            tvProductName.setText("Product Name : " + productName);
        }
        if (productcat != null) {
            tvCatName.setText("Category Name : " + productcat);
        }

        spIn = findViewById(R.id.spinner_ingredient);
        edQOI = findViewById(R.id.ed_quantity_ingredient);
        btnAdd = findViewById(R.id.btn_add);
        array = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, array);
        spIn.setAdapter(adapter);
        arrayH = new ArrayList<>();
        adapterH = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arrayH);
        spH.setAdapter(adapterH);

        // Fetch ingredients from Firebase and populate the spinner
        FirebaseDatabase.getInstance().getReference("Ingredients").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                array.clear();
                // Clear old data
                array.add("Select an ingredient");

                for (DataSnapshot myData : snapshot.getChildren()) {
                    String ingredientName = myData.child("name").getValue(String.class);
                    if (ingredientName != null) {
                        array.add(ingredientName.trim()); // Add new ingredients
                    }
                }
                adapter.notifyDataSetChanged(); // Notify adapter to update spinner
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(IngredientQuantityActivity.this, "Failed to load ingredients", Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch healthiness from Firebase and populate the spinner
        FirebaseDatabase.getInstance().getReference("Healthiness").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayH.clear(); // Clear old data
                arrayH.add("Select healthiness"); // Add default option

                for (DataSnapshot myData : snapshot.getChildren()) {
                    String key = myData.getKey(); // Retrieve the key (e.g., "1")
                    String value = myData.getValue(String.class); // Retrieve the value (e.g., "Unhealthy")
                    if (key != null && value != null) {
                        arrayH.add(key + ": " + value.trim()); // Format as "1: Unhealthy"
                    }
                }
                adapterH.notifyDataSetChanged(); // Notify adapter to update spinner
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(IngredientQuantityActivity.this, "Failed to load Healthiness", Toast.LENGTH_SHORT).show();
            }
        });


        // Fetch healthiness from Firebase and populate the spinner
//        FirebaseDatabase.getInstance().getReference("Healthiness").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                arrayH.clear();
//                // Clear old data
//                arrayH.add("Select healthiness");
//
//                for (DataSnapshot myData : snapshot.getChildren()) {
//                    String healthiness = myData.getValue(String.class);
//                    if (healthiness != null) {
//                        arrayH.add(healthiness.trim()); // Add new healthiness values
//                    }
//                }
//                adapterH.notifyDataSetChanged(); // Notify adapter to update spinner
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(IngredientQuantityActivity.this, "Failed to load Healthiness", Toast.LENGTH_SHORT).show();
//            }
//        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get the selected ingredient from the spinner
                Object selectedItem = spIn.getSelectedItem();
                String selectedH = (String) spH.getSelectedItem();

                if (selectedItem == null || selectedItem.toString().trim().isEmpty()) {
                    Toast.makeText(IngredientQuantityActivity.this, "Select an ingredient", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (selectedH == null || selectedH.toString().trim().isEmpty()) {
                    Toast.makeText(IngredientQuantityActivity.this, "Select healthiness of ingredient", Toast.LENGTH_SHORT).show();
                    return;
                }

                String selectedIng = selectedItem.toString().trim(); // Get selected ingredient
                String qOI = edQOI.getText().toString().trim(); // Get quantity from EditText

                if (selectedIng.isEmpty()) {
                    Toast.makeText(IngredientQuantityActivity.this, "Enter ingredient", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedIng.equals("Select an ingredient")) {
                    TextView errorText = (TextView) spIn.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);
                    errorText.setText("Please select an ingredient");
                    return;
                }

                if (qOI.isEmpty()) {
                    Toast.makeText(IngredientQuantityActivity.this, "Enter quantity of ingredient", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the ingredient is already in the list
                boolean ingredientExists = false;
                Ingredient existingIngredient = null;

                // Loop through the list of ingredients to check if the ingredient already exists
                for (Ingredient ingredient : ingredientsList) {
                    if (ingredient.getName().equals(selectedIng)) {
                        ingredientExists = true;
                        existingIngredient = ingredient;
                        break;
                    }
                }

                // If ingredient exists, update its quantity
                if (ingredientExists) {
                    existingIngredient.setQty(qOI);  // Update the existing ingredient's quantity
                    Toast.makeText(IngredientQuantityActivity.this, "Ingredient updated", Toast.LENGTH_SHORT).show();
                } else {
                    // If ingredient doesn't exist, add it to the list
                    Ingredient newIngredient = new Ingredient();
                    newIngredient.setName(selectedIng);
                    newIngredient.setQty(qOI);
                    newIngredient.setHealthy(selectedH);
                    ingredientsList.add(newIngredient); // Add new ingredient to the list
                    Toast.makeText(IngredientQuantityActivity.this, "Ingredient added", Toast.LENGTH_SHORT).show();
                }

                // Update the displayed list of selected ingredients
                selected.removeAllViews();  // Clear previous views
                for (Ingredient ingredient : ingredientsList) {
                    TextView textView = new TextView(IngredientQuantityActivity.this);
                    textView.setText(ingredient.getName() + " " + ingredient.getQty());
                    textView.setTextSize(15);
                    selected.addView(textView);
                }

                edQOI.setText(""); // Clear the quantity input
                spIn.setSelection(0); // Reset the ingredient spinner selection
                spH.setSelection(0); // Reset the healthiness spinner selection

                // Generate a unique ID for the ingredient if adding new ingredient
                String ingredientId = FirebaseDatabase.getInstance().getReference("Products").child(productId)
                        .child("ingredients").push().getKey();

                if (ingredientId == null) {
                    Toast.makeText(IngredientQuantityActivity.this, "Failed to generate ingredient ID", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Save Ingredient to Firebase (Update or Add)
                FirebaseDatabase.getInstance().getReference("Products").child(productId)
                        .child("ingredients").child(ingredientId).setValue(existingIngredient != null ? existingIngredient : new Ingredient())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(IngredientQuantityActivity.this, "Ingredient successfully saved", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(IngredientQuantityActivity.this, "Failed to save ingredient", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}

package com.example.barcodebuddy;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class IngredientQuantityActivity extends AppCompatActivity {

    Spinner spIn;
    EditText edQOI;
    Button btnAdd;
    TextView tvProductName, tvCatName,tvH;
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
        tvH = findViewById(R.id.tv_health);
        selected = findViewById(R.id.selected_ingredient_layout);
//        spH = findViewById(R.id.spinner_healthy);

        String productId = getIntent().getStringExtra("id");
        String productName = getIntent().getStringExtra("name");
        String productcat = getIntent().getStringExtra("category");
        String productH = getIntent().getStringExtra("healthiness");
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

        if (productH != null) {
            tvH.setText("Product Nutri value:" + productH);

            // Use .toString() and .equals() for string comparison
            if (tvH.getText().toString().equals("Un-healthy")) {
                int red = getResources().getColor(R.color.red);
                tvH.setTextColor(red);
            } else if (tvH.getText().toString().equals("Moderate")) {
                int orange = getResources().getColor(R.color.orange);
                tvH.setTextColor(orange);
            } else if (tvH.getText().toString().equals("Healthy") || tvH.getText().toString().equals("Very healthy")) {
                int green = getResources().getColor(R.color.dark_green);
                tvH.setTextColor(green);
            }
        }


        spIn = findViewById(R.id.spinner_ingredient);
        edQOI = findViewById(R.id.ed_quantity_ingredient);
        btnAdd = findViewById(R.id.btn_add);
        array = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, array);
        spIn.setAdapter(adapter);
//        arrayH = new ArrayList<>();
//        adapterH = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arrayH);
//        spH.setAdapter(adapterH);

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

//        // Fetch healthiness from Firebase and populate the spinner
//        FirebaseDatabase.getInstance().getReference("Healthiness").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                arrayH.clear(); // Clear old data
//                arrayH.add("Select healthiness"); // Add default option
//
//                for (DataSnapshot myData : snapshot.getChildren()) {
//                    String key = myData.getKey(); // Retrieve the key (e.g., "1")
//                    String value = myData.getValue(String.class); // Retrieve the value (e.g., "Unhealthy")
//                    if (key != null && value != null) {
//                        arrayH.add(key + ": " + value.trim()); // Format as "1: Unhealthy"
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
//

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected ingredient and quantity
                String selectedIng = spIn.getSelectedItem().toString().trim();
                String qOI = edQOI.getText().toString().trim();

                // Validate ingredient selection
                if (selectedIng == null || selectedIng.isEmpty() || selectedIng.equals("Select an ingredient")) {
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
                                                    textView.setText(selectedIng + " " + qOI); // Update the displayed quantity
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

        //
//        btnAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                // Get the selected ingredient from the spinner
//                Object selectedItem = spIn.getSelectedItem();
//                String selectedH = (String) spH.getSelectedItem();
//
//                if (selectedItem == null || selectedItem.toString().trim().isEmpty()) {
//                    Toast.makeText(IngredientQuantityActivity.this, "Select an ingredient", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (selectedH == null || selectedH.toString().trim().isEmpty()) {
//                    Toast.makeText(IngredientQuantityActivity.this, "Select healthiness of ingredient", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (selectedH.equals("Select healthiness")) {
//                    TextView errorText = (TextView) spH.getSelectedView();
//                    errorText.setError("");
//                    errorText.setTextColor(Color.RED);
//                    errorText.setText("Please select healthiness");
//                    return;
//                }
//
//                String selectedIng = selectedItem.toString().trim(); // Get selected ingredient
//                String qOI = edQOI.getText().toString().trim(); // Get quantity from EditText
//
//                if (selectedIng.isEmpty()) {
//                    Toast.makeText(IngredientQuantityActivity.this, "Enter ingredient", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if (selectedIng.equals("Select an ingredient")) {
//                    TextView errorText = (TextView) spIn.getSelectedView();
//                    errorText.setError("");
//                    errorText.setTextColor(Color.RED);
//                    errorText.setText("Please select an ingredient");
//                    return;
//                }
//
//                if (qOI.isEmpty()) {
//                    Toast.makeText(IngredientQuantityActivity.this, "Enter quantity of ingredient", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                // Check if the ingredient is already in the list
//                boolean ingredientExists = false;
//                Ingredient existingIngredient = null;
//
//                // Loop through the list of ingredients to check if the ingredient already exists
//                for (Ingredient ingredient : ingredientsList) {
//                    if (ingredient.getName().equals(selectedIng)) {
//                        ingredientExists = true;
//                        existingIngredient = ingredient;
//                        break;
//                    }
//                }
//
//                // If ingredient exists, update its quantity
//                if (ingredientExists) {
//                    existingIngredient.setQty(qOI);  // Update the existing ingredient's quantity
//                    Toast.makeText(IngredientQuantityActivity.this, "Ingredient updated", Toast.LENGTH_SHORT).show();
//                } else {
//                    // If ingredient doesn't exist, add it to the list
//                    Ingredient newIngredient = new Ingredient();
//                    newIngredient.setName(selectedIng);
//                    newIngredient.setQty(qOI);
//                    newIngredient.setHealthy(selectedH);
//                    ingredientsList.add(newIngredient); // Add new ingredient to the list
//                    Toast.makeText(IngredientQuantityActivity.this, "Ingredient added", Toast.LENGTH_SHORT).show();
//                }
//
//                // Update the displayed list of selected ingredients
//                selected.removeAllViews();  // Clear previous views
//                for (Ingredient ingredient : ingredientsList) {
//                    // Create a LinearLayout for each ingredient and its delete button
//                    LinearLayout ingredientLayout = new LinearLayout(IngredientQuantityActivity.this);
//                    ingredientLayout.setOrientation(LinearLayout.HORIZONTAL);
//
//                    TextView textView = new TextView(IngredientQuantityActivity.this);
//                    textView.setText(ingredient.getName() + " " + ingredient.getQty());
//                    textView.setTextSize(15);
//
//                    // Create the delete icon
//                    ImageView deleteIcon = new ImageView(IngredientQuantityActivity.this);
//                    deleteIcon.setImageResource(android.R.drawable.ic_delete); // Use the default delete icon
//                    deleteIcon.setPadding(10, 0, 0, 0); // Add padding to the right of the text
//
//                    // Add TextView and ImageView to the LinearLayout
//                    ingredientLayout.addView(textView);
//                    ingredientLayout.addView(deleteIcon);
//
//                    // Set OnClickListener on the delete icon to remove the ingredient
//                    deleteIcon.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//
//                            // Remove from the Firebase database using the correct path
//                            String ingredientId = FirebaseDatabase.getInstance().getReference("Products").child(productId)
//                                    .child("ingredients").getKey(); // Use the key of the ingredient from Firebase
//
//                            if (ingredientId != null) {
//                                FirebaseDatabase.getInstance().getReference("Products").child(productId)
//                                        .child("ingredients").child(ingredientId).removeValue()
//                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                if (task.isSuccessful()) {
//                                                    Toast.makeText(IngredientQuantityActivity.this, "Ingredient deleted from Firebase", Toast.LENGTH_SHORT).show();
//                                                    // Remove from the list
//                                                    ingredientsList.remove(ingredient);
//                                                } else {
//                                                    Toast.makeText(IngredientQuantityActivity.this, "Failed to delete ingredient from Firebase", Toast.LENGTH_SHORT).show();
//                                                }
//                                            }
//                                        });
//                            }
//
//                            // Remove the ingredient from the layout
//                            selected.removeView(ingredientLayout);
//                        }
//                    });
//
//
//
//                    // Add the ingredient layout to the selected ingredients layout
//                    selected.addView(ingredientLayout);
//                }
//
//                edQOI.setText(""); // Clear the quantity input
//                spIn.setSelection(0); // Reset the ingredient spinner selection
//                spH.setSelection(0); // Reset the healthiness spinner selection
//
//                // Save the ingredient to Firebase under the correct product ID
//                String ingredientId = FirebaseDatabase.getInstance().getReference("Products").child(productId)
//                        .child("ingredients").push().getKey();
//
//                if (ingredientId == null) {
//                    Toast.makeText(IngredientQuantityActivity.this, "Failed to generate ingredient ID", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                // Prepare the ingredient to be saved in Firebase
//                Ingredient ingredientToSave = existingIngredient != null ? existingIngredient : new Ingredient();
//                ingredientToSave.setName(selectedIng);
//                ingredientToSave.setQty(qOI);
//                ingredientToSave.setHealthy(selectedH);
//
//                // Save the ingredient in Firebase
//                FirebaseDatabase.getInstance().getReference("Products").child(productId)
//                        .child("ingredients").child(ingredientId).setValue(ingredientToSave)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Toast.makeText(IngredientQuantityActivity.this, "Ingredient successfully saved", Toast.LENGTH_SHORT).show();
//                                } else {
//                                    Toast.makeText(IngredientQuantityActivity.this, "Failed to save ingredient", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//            }
//        });
//
//
//
//
////        btnAdd.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////
////                // Get the selected ingredient from the spinner
////                Object selectedItem = spIn.getSelectedItem();
////                String selectedH = (String) spH.getSelectedItem();
////
////                if (selectedItem == null || selectedItem.toString().trim().isEmpty()) {
////                    Toast.makeText(IngredientQuantityActivity.this, "Select an ingredient", Toast.LENGTH_SHORT).show();
////                    return;
////                }
////                if (selectedH == null || selectedH.toString().trim().isEmpty()) {
////                    Toast.makeText(IngredientQuantityActivity.this, "Select healthiness of ingredient", Toast.LENGTH_SHORT).show();
////                    return;
////                }
////
////                String selectedIng = selectedItem.toString().trim(); // Get selected ingredient
////                String qOI = edQOI.getText().toString().trim(); // Get quantity from EditText
////
////                if (selectedIng.isEmpty()) {
////                    Toast.makeText(IngredientQuantityActivity.this, "Enter ingredient", Toast.LENGTH_SHORT).show();
////                    return;
////                }
////
////                if (selectedIng.equals("Select an ingredient")) {
////                    TextView errorText = (TextView) spIn.getSelectedView();
////                    errorText.setError("");
////                    errorText.setTextColor(Color.RED);
////                    errorText.setText("Please select an ingredient");
////                    return;
////                }
////
////                if (qOI.isEmpty()) {
////                    Toast.makeText(IngredientQuantityActivity.this, "Enter quantity of ingredient", Toast.LENGTH_SHORT).show();
////                    return;
////                }
////
////                // Check if the ingredient is already in the list
////                boolean ingredientExists = false;
////                Ingredient existingIngredient = null;
////
////                // Loop through the list of ingredients to check if the ingredient already exists
////                for (Ingredient ingredient : ingredientsList) {
////                    if (ingredient.getName().equals(selectedIng)) {
////                        ingredientExists = true;
////                        existingIngredient = ingredient;
////                        break;
////                    }
////                }
////
////                // If ingredient exists, update its quantity
////                if (ingredientExists) {
////                    existingIngredient.setQty(qOI);  // Update the existing ingredient's quantity
////                    Toast.makeText(IngredientQuantityActivity.this, "Ingredient updated", Toast.LENGTH_SHORT).show();
////                } else {
////                    // If ingredient doesn't exist, add it to the list
////                    Ingredient newIngredient = new Ingredient();
////                    newIngredient.setName(selectedIng);
////                    newIngredient.setQty(qOI);
////                    newIngredient.setHealthy(selectedH);
////                    ingredientsList.add(newIngredient); // Add new ingredient to the list
////                    Toast.makeText(IngredientQuantityActivity.this, "Ingredient added", Toast.LENGTH_SHORT).show();
////                }
////
////                // Update the displayed list of selected ingredients
////                selected.removeAllViews();  // Clear previous views
////                for (Ingredient ingredient : ingredientsList) {
////                    TextView textView = new TextView(IngredientQuantityActivity.this);
////                    textView.setText(ingredient.getName() + " " + ingredient.getQty());
////                    textView.setTextSize(15);
////                    selected.addView(textView);
////                }
////
////                edQOI.setText(""); // Clear the quantity input
////                spIn.setSelection(0); // Reset the ingredient spinner selection
////                spH.setSelection(0); // Reset the healthiness spinner selection
////
////                // Generate a unique ID for the ingredient if adding new ingredient
////                String ingredientId = FirebaseDatabase.getInstance().getReference("Products").child(productId)
////                        .child("ingredients").push().getKey();
////
////                if (ingredientId == null) {
////                    Toast.makeText(IngredientQuantityActivity.this, "Failed to generate ingredient ID", Toast.LENGTH_SHORT).show();
////                    return;
////                }
////
////                // Save Ingredient to Firebase (Update or Add)
////                FirebaseDatabase.getInstance().getReference("Products").child(productId)
////                        .child("ingredients").child(ingredientId).setValue(existingIngredient != null ? existingIngredient : new Ingredient())
////                        .addOnCompleteListener(new OnCompleteListener<Void>() {
////                            @Override
////                            public void onComplete(@NonNull Task<Void> task) {
////                                if (task.isSuccessful()) {
////                                    Toast.makeText(IngredientQuantityActivity.this, "Ingredient successfully saved", Toast.LENGTH_SHORT).show();
////                                } else {
////                                    Toast.makeText(IngredientQuantityActivity.this, "Failed to save ingredient", Toast.LENGTH_SHORT).show();
////                                }
////                            }
////                        });
////            }
////        });
//    }
//}

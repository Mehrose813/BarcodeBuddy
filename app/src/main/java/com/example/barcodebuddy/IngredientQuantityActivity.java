package com.example.barcodebuddy;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

    Spinner spIn;
    EditText edQOI;
    Button btnSave;
    ArrayList<String> array;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ingredient_quantity);

        String productId = getIntent().getStringExtra("id");

        if (productId == null) {
            Toast.makeText(this, "Product ID is missing!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        spIn = findViewById(R.id.spinner_ingredient);
        edQOI = findViewById(R.id.ed_quantity_ingredient);
        btnSave = findViewById(R.id.btn_save_iq);
        array = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, array);
        spIn.setAdapter(adapter);

        // Fetch ingredients from Firebase and populate the spinner
        FirebaseDatabase.getInstance().getReference("Ingredients").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                array.clear(); // Clear old data
                for (DataSnapshot myData : snapshot.getChildren()) {
                    array.add(myData.getValue().toString().trim()); // Add new ingredients
                }
                adapter.notifyDataSetChanged(); // Notify adapter to update spinner
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(IngredientQuantityActivity.this, "Failed to load ingredients", Toast.LENGTH_SHORT).show();
            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get the selected ingredient from the spinner
                Object selectedItem = spIn.getSelectedItem();

                if (selectedItem == null || selectedItem.toString().trim().isEmpty()) {
                    Toast.makeText(IngredientQuantityActivity.this, "Select an ingredient", Toast.LENGTH_SHORT).show();
                    return;
                }

                String selectedIng = selectedItem.toString().trim(); // Get selected ingredient
                String qOI = edQOI.getText().toString().trim(); // Get quantity from EditText

                if (qOI.isEmpty()) {
                    Toast.makeText(IngredientQuantityActivity.this, "Enter quantity of ingredient", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Generate a unique ID for the ingredient
                String ingredientId = FirebaseDatabase.getInstance().getReference("Products").child(productId)
                        .child("ingredients").push().getKey();

                if (ingredientId == null) {
                    Toast.makeText(IngredientQuantityActivity.this, "Failed to generate ingredient ID", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create Ingredient object
                Ingredient ingredient = new Ingredient();
                ingredient.setName(selectedIng);
                ingredient.setQty(qOI);

                // Save Ingredient to Firebase under the product's "ingredients"
                FirebaseDatabase.getInstance().getReference("Products").child(productId)
                        .child("ingredients").child(ingredientId).setValue(ingredient)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(IngredientQuantityActivity.this, "Ingredient successfully added", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(IngredientQuantityActivity.this, "Failed to add ingredient", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

//        btnSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                // Get the selected ingredient from the spinner
//                Object selectedItem = spIn.getSelectedItem();
//
//                if (selectedItem == null || selectedItem.toString().trim().isEmpty()) {
//                    Toast.makeText(IngredientQuantityActivity.this, "Select an ingredient", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                String selectedIng = selectedItem.toString().trim(); // Get selected ingredient
//                String qOI = edQOI.getText().toString().trim(); // Get quantity from EditText
//
//                if (qOI.isEmpty()) {
//                    Toast.makeText(IngredientQuantityActivity.this, "Enter quantity of ingredient", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                // Create Ingredient object and save to Firebase
//                Ingredient ingredient = new Ingredient();
//                ingredient.setName(selectedIng);
//                ingredient.setQty(qOI);
//
//                FirebaseDatabase.getInstance().getReference("Products").child(productId)
//                        .child("ingredient").push().setValue(ingredient)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Toast.makeText(IngredientQuantityActivity.this, "Ingredient successfully added", Toast.LENGTH_SHORT).show();
//                                } else {
//                                    Toast.makeText(IngredientQuantityActivity.this, "Ingredient not added", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//            }
//        });
    }
}

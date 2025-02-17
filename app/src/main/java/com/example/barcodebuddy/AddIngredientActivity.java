package com.example.barcodebuddy;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddIngredientActivity extends AppCompatActivity {
    EditText etIngredientName, etIngredientDes, etIngredientPros, etIngredientCons;
    Button btnSaveIngredients;
    Spinner spinSafe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        String id = getIntent().getStringExtra("id");

        btnSaveIngredients = findViewById(R.id.btn_save_ingredients);
        etIngredientName = findViewById(R.id.et_ingredient_name);
        etIngredientDes = findViewById(R.id.et_ingredient_des);
        etIngredientPros = findViewById(R.id.et_ingredient_pros);
        etIngredientCons = findViewById(R.id.et_ingredient_cons);
        spinSafe = findViewById(R.id.spin_safe);

        if (id != null && !id.isEmpty()) {
            FirebaseDatabase.getInstance().getReference("Ingredients")
                    .child(id)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Ingredient ingredient = snapshot.getValue(Ingredient.class);
                            if (ingredient != null) {
                                etIngredientName.setText(ingredient.getName());
                                etIngredientDes.setText(ingredient.getDes());  // Correct method
                                etIngredientPros.setText(ingredient.getPros());
                                etIngredientCons.setText(ingredient.getCons());

                                // Set Spinner Value
                                if (ingredient.getCategory() != null) {
                                    switch (ingredient.getCategory()) {
                                        case "Safe":
                                            spinSafe.setSelection(0);
                                            break;
                                        case "Unsafe":
                                            spinSafe.setSelection(1);
                                            break;
                                        default:
                                            spinSafe.setSelection(2);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(AddIngredientActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        btnSaveIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etIngredientName.getText().toString().trim();
                String des = etIngredientDes.getText().toString().trim();
                String pros = etIngredientPros.getText().toString().trim();
                String cons = etIngredientCons.getText().toString().trim();
                String safety = spinSafe.getSelectedItem().toString();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(des) || TextUtils.isEmpty(pros) || TextUtils.isEmpty(cons)) {
                    Toast.makeText(AddIngredientActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseReference ingredientsRef = FirebaseDatabase.getInstance().getReference("Ingredients");

                ingredientsRef.orderByChild("name").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && TextUtils.isEmpty(id)) {
                            Toast.makeText(AddIngredientActivity.this, "This ingredient already exists!", Toast.LENGTH_SHORT).show();
                        } else {
                            Ingredient ingredient = new Ingredient(id, name, des, pros, cons,safety);

                            if (!TextUtils.isEmpty(id)) {
                                // Update ingredient
                                ingredientsRef.child(id).setValue(ingredient)
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(AddIngredientActivity.this, "Ingredient updated successfully", Toast.LENGTH_SHORT).show();
                                            finish();
                                        })
                                        .addOnFailureListener(e ->
                                                Toast.makeText(AddIngredientActivity.this, "Failed to update ingredient", Toast.LENGTH_SHORT).show());
                            } else {
                                // Add new ingredient
                                String newId = ingredientsRef.push().getKey();
                                ingredient.setId(newId);  // Assign generated ID
                                ingredientsRef.child(newId).setValue(ingredient)
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(AddIngredientActivity.this, "Ingredient added successfully", Toast.LENGTH_SHORT).show();
                                            etIngredientName.setText(""); // Clear input
                                            finish();
                                        })
                                        .addOnFailureListener(e ->
                                                Toast.makeText(AddIngredientActivity.this, "Failed to add ingredient", Toast.LENGTH_SHORT).show());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AddIngredientActivity.this, "Database error!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}

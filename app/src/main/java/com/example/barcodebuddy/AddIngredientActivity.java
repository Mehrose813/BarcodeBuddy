package com.example.barcodebuddy;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;

import java.util.ArrayList;

public class AddIngredientActivity extends AppCompatActivity {
EditText etIngredientName,etIngredientDes,getEtIngredientPros,getEtIngredientCons;
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
        getEtIngredientCons = findViewById(R.id.et_ingredient_cons);
        getEtIngredientPros = findViewById(R.id.et_ingredient_pros);
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
                                etIngredientDes.setText(ingredient.getDes());
                                getEtIngredientPros.setText(ingredient.getPros());
                                getEtIngredientCons.setText(ingredient.getCons());

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }

        btnSaveIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Ingredient ingredient = new Ingredient();
                String name = etIngredientName.getText().toString().trim();
                String zone = spinSafe.getSelectedItem().toString();
                String des = etIngredientDes.getText().toString().trim();
                String pross = getEtIngredientPros.getText().toString().trim();
                String cons = getEtIngredientCons.getText().toString().trim();

                if (name.isEmpty() || des.isEmpty() || pross.isEmpty() || cons.isEmpty()) {
                    Toast.makeText(AddIngredientActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseReference ingredientsRef = FirebaseDatabase.getInstance().getReference("Ingredients");

                // **Check if ingredient already exists**
                ingredientsRef.orderByChild("name").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && (id == null || id.isEmpty())) {

                            Toast.makeText(AddIngredientActivity.this, "This ingredient already exists!", Toast.LENGTH_SHORT).show();
                        } else {



                            if (id != null && !id.isEmpty()) {
                                // Update existing ingredient
                                ingredientsRef.child(id).setValue(ingredient)
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(AddIngredientActivity.this, "Ingredient updated successfully", Toast.LENGTH_SHORT).show();
                                            finish(); // Close activity
                                        })
                                        .addOnFailureListener(e ->
                                                Toast.makeText(AddIngredientActivity.this, "Failed to update ingredient", Toast.LENGTH_SHORT).show());
                            } else {
                                // Add new ingredient
                                ingredientsRef.push().setValue(ingredient)
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(AddIngredientActivity.this, "Ingredient added successfully", Toast.LENGTH_SHORT).show();
                                            etIngredientName.setText(""); // Clear input
                                            finish(); // Close activity
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
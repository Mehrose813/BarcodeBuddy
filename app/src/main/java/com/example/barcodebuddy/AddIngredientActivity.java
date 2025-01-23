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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;

import java.util.ArrayList;

public class AddIngredientActivity extends AppCompatActivity {
EditText etIngredientName;
Button btnSaveIngredients;
Spinner spinSafe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        String id = getIntent().getStringExtra("id");
        btnSaveIngredients = findViewById(R.id.btn_save_ingredients);
        etIngredientName = findViewById(R.id.et_ingredient_name);
        spinSafe = findViewById(R.id.spin_safe);

        if(id != null && ! id.isEmpty()){
            FirebaseDatabase.getInstance().getReference("Ingredients")
                    .child(id)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Ingredient ingredient = snapshot.getValue(Ingredient.class);
                            if(ingredient!= null){
                                etIngredientName.setText(ingredient.getName());
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
                String name = etIngredientName.getText().toString();
                String zone = spinSafe.getSelectedItem().toString();

                if (name.isEmpty()) {
                    Toast.makeText(AddIngredientActivity.this, "Enter the name", Toast.LENGTH_SHORT).show();
                    return;
                }

                Ingredient ingredient = new Ingredient();
                ingredient.setName(name);
                ingredient.setCategory(zone);

                DatabaseReference ingredientsRef = FirebaseDatabase.getInstance().getReference("Ingredients");

                if (id != null && !id.isEmpty()) {
                    // Update existing ingredient
                    ingredientsRef.child(id).setValue(ingredient)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(AddIngredientActivity.this, "Ingredient updated successfully", Toast.LENGTH_SHORT).show();
                                    finish(); // Close this activity and go back to the previous one
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddIngredientActivity.this, "Failed to update ingredient", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    // Add new ingredient
                    ingredientsRef.push().setValue(ingredient)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(AddIngredientActivity.this, "Ingredient added successfully", Toast.LENGTH_SHORT).show();
                                    etIngredientName.setText(""); // Clear the EditText
                                    finish(); // Close this activity and go back to the previous one
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddIngredientActivity.this, "Failed to add ingredient", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    

    }

}
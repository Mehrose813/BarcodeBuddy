package com.example.barcodebuddy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class SetAllergyActivity extends AppCompatActivity {

    Spinner spAllergy;
    LinearLayout selectedLayout;
    Button btnSave;
    DatabaseReference userRef;
    String userId;
    ArrayList<String> ingredientList;
    ArrayAdapter<String> adapter;
    HashSet<String> existingAllergies; // Set to store unique allergies

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_set_allergy);

        spAllergy = findViewById(R.id.spinner_allergic);
        selectedLayout = findViewById(R.id.selected_allergy_layout);
        btnSave = findViewById(R.id.btn_save_allergic);

        userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (userId == null) {
            Log.e("Firebase", "User ID is null. Cannot fetch allergy data.");
            return;
        }
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("allergic");

        ingredientList = new ArrayList<>();
        existingAllergies = new HashSet<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ingredientList);
        spAllergy.setAdapter(adapter);

        // Load ingredients
        loadIngredients();

        // Load already selected allergies for this user
        loadSelectedAllergies();

        // Save button click
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAllergy();
            }
        });
    }

    private void loadIngredients() {
        FirebaseDatabase.getInstance().getReference("Ingredients").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ingredientList.clear();
                ingredientList.add("Select an ingredient");

                for (DataSnapshot data : snapshot.getChildren()) {
                    String ingredient = data.child("name").getValue(String.class);
                    if (ingredient != null) {
                        ingredientList.add(ingredient.trim());
                    }
                }

                Collections.sort(ingredientList.subList(1, ingredientList.size())); // Sort alphabetically
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SetAllergyActivity.this, "Failed to load ingredients", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSelectedAllergies() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                selectedLayout.removeAllViews();
                existingAllergies.clear(); // Clear the set

                for (DataSnapshot data : snapshot.getChildren()) {
                    String key = data.getKey();
                    String allergy = data.getValue(String.class);

                    if (allergy != null) {
                        existingAllergies.add(allergy.toLowerCase()); // Store in set to prevent duplicates
                        addAllergyToUI(key, allergy);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SetAllergyActivity.this, "Failed to load allergies", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveAllergy() {
        String selectedAllergic = spAllergy.getSelectedItem() != null ? spAllergy.getSelectedItem().toString().trim() : "";

        if (selectedAllergic.isEmpty() || selectedAllergic.equals("Select an ingredient")) {
            Toast.makeText(this, "Select an ingredient", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the allergy already exists in the user's list
        if (existingAllergies.contains(selectedAllergic.toLowerCase())) {
            Toast.makeText(this, "This ingredient is already added!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save allergy to Firebase
        String key = userRef.push().getKey();
        if (key != null) {
            userRef.child(key).setValue(selectedAllergic).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        existingAllergies.add(selectedAllergic.toLowerCase()); // Add to set
                        addAllergyToUI(key, selectedAllergic);
                        // Set isAllergic to true when the allergy is saved
                        updateAllergyStatus(selectedAllergic, true);
                        Toast.makeText(SetAllergyActivity.this, "Allergy saved!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SetAllergyActivity.this, "Failed to save allergy", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void addAllergyToUI(String key, String allergy) {
        LinearLayout ingredientLayout = new LinearLayout(this);
        ingredientLayout.setOrientation(LinearLayout.HORIZONTAL);
        ingredientLayout.setTag(key);

        TextView textView = new TextView(this);
        textView.setText(allergy);
        textView.setTextSize(15);
        textView.setPadding(16, 0, 0, 0);

        ImageView deleteIcon = new ImageView(this);
        deleteIcon.setImageResource(android.R.drawable.ic_delete);
        deleteIcon.setPadding(30, 0, 0, 0);

        ingredientLayout.addView(textView);
        ingredientLayout.addView(deleteIcon);
        selectedLayout.addView(ingredientLayout);

        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAllergyFromDatabase(key, ingredientLayout, allergy);
            }
        });
    }

    private void removeAllergyFromDatabase(String key, LinearLayout layout, String allergy) {
        userRef.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    selectedLayout.removeView(layout);
                    existingAllergies.remove(allergy.toLowerCase()); // Remove from set
                    // Set isAllergic to false when the allergy is removed
                    updateAllergyStatus(allergy, false);
                    Toast.makeText(SetAllergyActivity.this, "Allergy removed!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SetAllergyActivity.this, "Failed to remove allergy", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Update allergy status in Firebase
    private void updateAllergyStatus(String allergy, boolean isAllergic) {
        DatabaseReference allergyRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("allergies_status");
        allergyRef.child(allergy.toLowerCase()).setValue(isAllergic);
    }
}

package com.example.barcodebuddy;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class IngridientInformationActivity extends AppCompatActivity {

    private TextView tvIngName, tvIngDes, tvIngPros, tvIngCons;
    private DatabaseReference ingredientRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingridient_information);

        tvIngName = findViewById(R.id.tv_ingname);
        tvIngDes = findViewById(R.id.tv_ingdes);
        tvIngPros = findViewById(R.id.tv_ingpros);
        tvIngCons = findViewById(R.id.tv_ingcon);

        String name = getIntent().getStringExtra("name");


        if (name != null) {
            tvIngName.setText(name);
            ingredientRef = FirebaseDatabase.getInstance().getReference("Ingredients").child(name);
            fetchIngredientDetails();
        } else {
            Toast.makeText(this, "Ingredient name is missing!", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchIngredientDetails() {
        ingredientRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String description = snapshot.child("description").getValue(String.class);
                    String pros = snapshot.child("pros").getValue(String.class);
                    String cons = snapshot.child("cons").getValue(String.class);

                    tvIngDes.setText(description != null ? description : "No description available");
                    tvIngPros.setText(pros != null ? pros : "No pros available");
                    tvIngCons.setText(cons != null ? cons : "No cons available");
                } else {
                    Toast.makeText(IngridientInformationActivity.this, "No details found for this ingredient!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(IngridientInformationActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

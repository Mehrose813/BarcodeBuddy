package com.example.barcodebuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class IngredientListActivity extends AppCompatActivity {
RecyclerView rvIngredient;
Button btnAddIngredient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ingredient_list);

        rvIngredient = findViewById(R.id.rv_ingredients);
        btnAddIngredient = findViewById(R.id.btn_add_ingredients);

        btnAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IngredientListActivity.this,AddIngredientActivity.class);
                startActivity(intent);
            }
        });


        List<Ingredient> ingredientList = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference("Ingredients")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ingredientList.clear();
                        for (DataSnapshot ingredientSnapshot : snapshot.getChildren()){
                            Ingredient ingredient = ingredientSnapshot.getValue(Ingredient.class);
                            ingredient.setId(ingredientSnapshot.getKey());
                            ingredientList.add(ingredient);
                        }
                        rvIngredient.setAdapter(new IngredientAdapter(ingredientList));


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}
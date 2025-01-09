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

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.collection.LLRBNode;

import java.util.ArrayList;

public class AddIngredientActivity extends AppCompatActivity {
    Spinner spinnerIngredient;
    EditText quantityIngredient;
    Button btnAdd,btnSave;
    LinearLayout selectedIngredientLayout;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    ArrayList <Ingredient> ingredientList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        spinnerIngredient = findViewById(R.id.spinner_ingredient);
        quantityIngredient = findViewById(R.id.quantity_ingredient);
        btnAdd = findViewById(R.id.btn_add);
        btnSave = findViewById(R.id.btn_save);
        selectedIngredientLayout = findViewById(R.id.selected_ingredient_layout);


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Ingredients");

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addIngredient();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    public void addIngredient(){
        String selectedItems = spinnerIngredient.getSelectedItem().toString();
        String quantity = quantityIngredient.getText().toString();

//        if(selectedItems.isEmpty()){
//            Toast.makeText(this, "Select the items", Toast.LENGTH_SHORT).show();
//            quantityIngredient.requestFocus();
//            return;
//        }

        // Validate Spinner
        if (selectedItems.equals("Select the ingredient")) {
            TextView errorText = (TextView) spinnerIngredient.getSelectedView();
            errorText.setError(""); // This will highlight the spinner
            errorText.setTextColor(Color.RED); // Optionally, set the error text color
            errorText.setText("Please select an ingredient"); // Set error message
            return;
        }

        if(quantity.isEmpty()){
            quantityIngredient.setError("Enter the quantity");
            quantityIngredient.requestFocus();
            return;
        }

        //set the ingredient in class
        Ingredient ingredient = new Ingredient(selectedItems,quantity);
        ingredientList.add(ingredient);

        //show in ui
        TextView textView = new TextView(this);
        textView.setText(selectedItems+" - "+quantity);
        selectedIngredientLayout.addView(textView);

        // Clear input fields
        quantityIngredient.setText("");
    }
}
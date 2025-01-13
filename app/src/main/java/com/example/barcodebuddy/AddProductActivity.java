package com.example.barcodebuddy;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddProductActivity extends AppCompatActivity {

    Spinner selectProduct,spCat;
    LinearLayout detailLayout;
    Button btnSave, btnAdd;
    EditText edDes;
    String[] pName = {"select Product Name", "Hico's Ice cream", "National Juice", "Lay's Potato chips", "Coca Cola", "Tea Bag", "Shoop Noddles"};
    String[] categories = {"Select category","Nuts" , "Chocolates" , "Cold drinks","Cookies"};

    // Firebase Database reference
    //firebase database
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Initialize Firebase database
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Products");

        // Initialize Views
        selectProduct = findViewById(R.id.spinner);
        detailLayout = findViewById(R.id.detail);
        btnAdd = findViewById(R.id.btn_add);
        btnSave = findViewById(R.id.btn_save);
        edDes = findViewById(R.id.ed_desc);
        spCat = findViewById(R.id.sp_cat);

        // Set up Adapter for Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, pName);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectProduct.setAdapter(adapter);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,categories);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spCat.setAdapter(arrayAdapter);

        // Spinner item selection listener
        selectProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value = parent.getItemAtPosition(position).toString();
                // You can use this to show selected product name in a TextView or any other logic
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing if no item is selected
            }
        });

        // Add button click listener
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedProduct = selectProduct.getSelectedItem().toString();
                String description = edDes.getText().toString();

                if (selectedProduct.equals("select Product Name")) {
                    Toast.makeText(AddProductActivity.this, "Please select a product", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (description.isEmpty()) {
                    Toast.makeText(AddProductActivity.this, "Please enter a description", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a new TextView to display selected product and description
                TextView newTextView = new TextView(AddProductActivity.this);
                newTextView.setText(selectedProduct + " - " + description);
                detailLayout.addView(newTextView);  // Add new TextView to the layout
            }
        });

        // Save Product button click listener
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedProduct = selectProduct.getSelectedItem().toString();
                String selectedCategory = spCat.getSelectedItem().toString();
                String description = edDes.getText().toString();

                if (selectedProduct.equals("select Product Name")) {
                    Toast.makeText(AddProductActivity.this, "Please select a product", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (description.isEmpty()) {
                    Toast.makeText(AddProductActivity.this, "Please enter a description", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (spCat.equals("Select category")) {
                    Toast.makeText(AddProductActivity.this, "Please select a category", Toast.LENGTH_SHORT).show();
                    return;
                }
                int selectedPosition = spCat.getSelectedItemPosition();
                if(selectedPosition == Spinner.INVALID_POSITION){
                    Toast.makeText(AddProductActivity.this, "select a valid category", Toast.LENGTH_SHORT).show();
                }



                saveProductToFirebase(selectedProduct, description,selectedCategory);
            }
        });
    }

    // Method to save product to Firebase
    public void saveProductToFirebase(String productName, String description,String category) {
        // Create a Product object (You can add more fields if needed)
        Product product = new Product();
        product.setName(productName);
        product.setDesc(description);
        product.setCat(category);

        // Save product to Firebase
        String productId = databaseReference.push().getKey(); // Automatically generate a unique ID
        if (productId != null) {
            databaseReference.child(productId).setValue(product)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddProductActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AddProductActivity.this, "Failed to add product", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}

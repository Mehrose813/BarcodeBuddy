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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.UUID;

public class AddProductActivity extends AppCompatActivity {
    String id;
    Spinner spCat;
    Button btnSave;
    EditText edDes, edPName;
    //String[] categories = {"Select category", "Nuts", "Chocolates", "Cold drinks", "Cookies"};
    ArrayList<String> list;
    ArrayAdapter<String> adapter;


    // Firebase Database reference
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);


        id = getIntent().getStringExtra("id");

        // Initialize Firebase database
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Products");

        // Initialize Views
        edPName = findViewById(R.id.ed_pname);
        btnSave = findViewById(R.id.btn_save);
        edDes = findViewById(R.id.ed_desc);
        spCat = findViewById(R.id.sp_cat);

//        // Set up Adapter for Spinner
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
//        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
//        spCat.setAdapter(arrayAdapter);

        list = new ArrayList<String>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, list);
        spCat.setAdapter(adapter);


        FirebaseDatabase.getInstance().getReference("Categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear(); // Clear the list before adding new items
                list.add("Select category"); // Add a default value at the top
                for (DataSnapshot mydata : snapshot.getChildren()) {
                    //list.add(mydata.getValue().toString().trim());
                    String categoryName = mydata.child("catname").getValue(String.class);
                    if (categoryName != null) {
                        list.add(categoryName.trim());
                    }
                    adapter.notifyDataSetChanged(); // Notify adapter to refresh spinner
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddProductActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();

            }
        });


        // If ID is provided, load the product details
        if (id != null && !id.isEmpty()) {
            databaseReference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        // Populate fields with existing data
                        edPName.setText(product.getName());
                        edDes.setText(product.getDesc());
                        int spinnerPosition = adapter.getPosition(product.getCat());
                        spCat.setSelection(spinnerPosition);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(AddProductActivity.this, "Failed to load product details", Toast.LENGTH_SHORT).show();
                }
            });
        }
        // Save Product button click listener
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edPName.getText().toString();
                String selectedCategory = spCat.getSelectedItem().toString();
                String description = edDes.getText().toString();

                if (name.isEmpty()) {
                    Toast.makeText(AddProductActivity.this, "Add a product name", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (description.isEmpty()) {
                    Toast.makeText(AddProductActivity.this, "Please enter a description", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedCategory.equals("Select category")) {
                    Toast.makeText(AddProductActivity.this, "Please select a category", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Save the product to Firebase
                saveProductToFirebase(name, description, selectedCategory);
            }
        });
    }

    public void saveProductToFirebase(String productName, String description, String category) {
        // Validate the input
        if (productName == null || productName.isEmpty()) {
            Toast.makeText(AddProductActivity.this, "Please enter a product name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (description == null || description.isEmpty()) {
            Toast.makeText(AddProductActivity.this, "Please enter a description", Toast.LENGTH_SHORT).show();
            return;
        }
        if (category == null || category.equals("Select category")) {
            Toast.makeText(AddProductActivity.this, "Please select a valid category", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a Product object
        Product product = new Product();
        product.setName(productName);
        product.setDesc(description);
        product.setCat(category);

        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("Products");

        // Check if ID is provided (editing existing product)
        if (id != null && !id.isEmpty()) {
            // Update existing product
            productsRef.child(id).setValue(product)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AddProductActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                        finish();  // Close activity after update
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddProductActivity.this, "Failed to update product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Add new product
            String newProductId = productsRef.push().getKey();  // Generate a new unique ID
            if (newProductId != null) {
                productsRef.child(newProductId).setValue(product)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(AddProductActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                            finish();  // Close activity after insertion
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(AddProductActivity.this, "Failed to add product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(AddProductActivity.this, "Failed to generate product ID", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


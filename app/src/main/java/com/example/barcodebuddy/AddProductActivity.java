package com.example.barcodebuddy;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.util.UUID;

public class AddProductActivity extends AppCompatActivity {

    private String id;
    private Spinner spCat;
    private Button btnSave;
    private EditText edDes, edPName;
    private String[] categories = {"Select category", "Nuts", "Chocolates", "Cold drinks", "Cookies"};

    // Firebase Database reference
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Initialize views
        edPName = findViewById(R.id.ed_pname);
        edDes = findViewById(R.id.ed_desc);
        spCat = findViewById(R.id.sp_cat);
        btnSave = findViewById(R.id.btn_save);

        // Get product ID and existing product details from intent
        id = getIntent().getStringExtra("id");
        String name = getIntent().getStringExtra("name");
        String desc = getIntent().getStringExtra("desc");

        // Set initial values in the EditText fields if available
        if (name != null) {
            edPName.setText(name);
        }
        if (desc != null) {
            edDes.setText(desc);
        }

        // Set up category spinner
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spCat.setAdapter(arrayAdapter);

        // If id exists, fetch the existing product from Firebase
        if (id != null && !id.isEmpty()) {
            FirebaseDatabase.getInstance().getReference("Products")
                    .child(id)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // Retrieve the existing product
                            Product product = snapshot.getValue(Product.class);
                            if (product != null) {
                                edPName.setText(product.getName());
                                edDes.setText(product.getDesc());

                                // Set spinner selection based on product's category
                                String category = product.getCat();
                                ArrayAdapter<String> adapter = (ArrayAdapter<String>) spCat.getAdapter();
                                int spinnerPosition = adapter.getPosition(category);
                                spCat.setSelection(spinnerPosition);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(AddProductActivity.this, "Failed to load product data.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        // Save or update the product
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get values from EditText fields
                String name = edPName.getText().toString();
                String description = edDes.getText().toString();
                String category = spCat.getSelectedItem().toString();

                if (name.isEmpty() || description.isEmpty() || category.equals("Select category")) {
                    Toast.makeText(AddProductActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a new product object
                Product product = new Product();
                product.setName(name);
                product.setDesc(description);
                product.setCat(category);

                if (id != null && !id.isEmpty()) {
                    // Update existing product in Firebase
                    FirebaseDatabase.getInstance().getReference("Products")
                            .child(id) // Use the existing product id to update
                            .setValue(product)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(AddProductActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                                finish();  // Close the activity
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(AddProductActivity.this, "Failed to update product", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    // Add new product to Firebase
                    String newProductId = UUID.randomUUID().toString();  // Use a unique ID for the new product
                    FirebaseDatabase.getInstance().getReference("Products")
                            .child(newProductId)
                            .setValue(product)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AddProductActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                                        finish();  // Close the activity
                                    } else {
                                        Toast.makeText(AddProductActivity.this, "Failed to add product", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}

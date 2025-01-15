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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class AddProductActivity extends AppCompatActivity {

    String id;
    Spinner spCat;
    Button btnSave, btnAdd;
    EditText edDes, edPName;
    String[] categories = {"Select category", "Nuts", "Chocolates", "Cold drinks", "Cookies"};

    // Firebase Database reference
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        String id = getIntent().getStringExtra("id");
        edPName = findViewById(R.id.ed_pname);
        btnSave = findViewById(R.id.btn_save);
        edDes = findViewById(R.id.ed_desc);
        spCat = findViewById(R.id.sp_cat);


        String name = getIntent().getStringExtra("name");
        String desc = getIntent().getStringExtra("desc");
        edPName.setText(name);
        edDes.setText(desc);


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spCat.setAdapter(arrayAdapter);

        if (id != null && !id.isEmpty()) {
            // Fetch the existing product from Firebase
            FirebaseDatabase.getInstance().getReference("Products")
                    .child(id)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // Retrieve the existing product
                            Product product = snapshot.getValue(Product.class);

                            if (product != null) {
                                // Set the EditText fields with existing product details
                                edPName.setText(product.getName());
                                edDes.setText(product.getDesc());

                                // You can set the category if necessary
                                // Set the spinner selection based on product category
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

            // Save updated product
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Get the updated values from user input
                    String name = edPName.getText().toString();
                    String description = edDes.getText().toString();
                    String category = spCat.getSelectedItem().toString();

                    if (name.isEmpty() || description.isEmpty() || category.equals("Select category")) {
                        Toast.makeText(AddProductActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Create a new product with updated values
                    Product updatedProduct = new Product();
                    updatedProduct.setName(name);
                    updatedProduct.setDesc(description);
                    updatedProduct.setCat(category);

                    // Update the product in Firebase
                    FirebaseDatabase.getInstance().getReference("Products")
                            .child(id)  // Use the same id to update
                            .setValue(updatedProduct)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(AddProductActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                                finish();  // Close the activity
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(AddProductActivity.this, "Failed to update product", Toast.LENGTH_SHORT).show();
                            });
                }
            });
        }
    }
}
//
//
//        if(id != null && ! id.isEmpty()){
//            FirebaseDatabase.getInstance().getReference("Products")
//                    .child(id)
//                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            Product product = snapshot.getValue(Product.class);
//
//                            if(product!= null){
//                                edPName.setText(product.getName());
//                                edDes.setText(product.getDesc());
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//
//        }
//
//        btnSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String name = edPName.getText().toString();
//
//                if (name.isEmpty()) {
//                    Toast.makeText(AddProductActivity.this, "Enter product name", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                Product product = new Product();
//                product.setName(name);
//                product.setDesc(edDes.getText().toString());
//                product.setCat(spCat.getSelectedItem().toString());
//
//                DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Products");
//
//                if (id != null && !id.isEmpty()) {
//                    // Update existing ingredient
//                    productRef.child(id).setValue(product)
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void unused) {
//                                    Toast.makeText(AddProductActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
//                                    finish(); // Close this activity and go back to the previous one
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(AddProductActivity.this, "Failed to update product", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                } else {
//                    // Add new ingredient
//                    productRef.push().setValue(product)
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void unused) {
//                                    Toast.makeText(AddProductActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
//                                    //etIngredientName.setText(""); // Clear the EditText
//                                    finish(); // Close this activity and go back to the previous one
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(AddProductActivity.this, "Failed to add product", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                }
//            }
//        });
//
//
//    }
//
//}
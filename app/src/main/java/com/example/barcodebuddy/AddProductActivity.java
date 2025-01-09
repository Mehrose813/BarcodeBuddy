package com.example.barcodebuddy;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

    Spinner select_product;
    ImageView addProduct,addDesc;
    EditText edDesc;
    LinearLayout detailLayout;
    Button btn;
    String[] pName = {"select Product Name","Hico's Ice cream" , "National Juice" ,"Lay's Potato chips" , "Coca Cola" , "Tea Bag", "Shoop Noddles"};

    // Firebase Database reference
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
        select_product = findViewById(R.id.spinner);
        addProduct = findViewById(R.id.iv_add_p);
        edDesc = findViewById(R.id.ed_desc);
        addDesc = findViewById(R.id.iv_desc);
        detailLayout = findViewById(R.id.detail);
        btn =findViewById(R.id.btn_save_product_and_desc);

        TextView textView = new TextView(this);
        detailLayout.addView(textView);

        // Set up Adapter for Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, pName);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        select_product.setAdapter(adapter);

        // Spinner item selection listener
        select_product.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value = parent.getItemAtPosition(position).toString();
                textView.setText(value);
                //detail.setText(value);
                //Toast.makeText(AddProductActivity.this, value + " is selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing if no item is selected
            }
        });

        // Add Product button click listener
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedItem = select_product.getSelectedItem().toString();
               textView.setText(selectedItem);
                if (!selectedItem.equals("select Product Name")) {
                    // Save the selected product to Firebase
                    //saveProductToFirebase(selectedItem);
                } else {
                    // Show a toast if no product is selected
                    Toast.makeText(AddProductActivity.this, "Please select a product", Toast.LENGTH_SHORT).show();
                }
            }
        });
        addDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText(edDesc.getText().toString());
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProductToFirebase(select_product.getSelectedItem().toString(),edDesc.getText().toString());
            }
        });

    }

    // Method to save product to Firebase
    public void saveProductToFirebase(String productName,String description) {
        // Create a Product object (You can add more fields if needed)
        Product product = new Product();
        product.setName(productName);
        product.setDesc(description);
        

        if(productName .isEmpty()){
            Toast.makeText(this, "select product name", Toast.LENGTH_SHORT).show();
        }
        if(description.isEmpty()){
            Toast.makeText(this, "enter any description", Toast.LENGTH_SHORT).show();
        }

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

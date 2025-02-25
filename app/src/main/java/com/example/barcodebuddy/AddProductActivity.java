package com.example.barcodebuddy;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.UUID;

public class AddProductActivity extends AppCompatActivity {
    String id;
    Spinner spCat, spH;
    Button btnSave;
    EditText edDes, edPName, edBar;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    ArrayList<String> arrayH;
    ArrayAdapter<String> adapterH;
    ImageView ivImg, iv_productImage;
    private Uri imageUri;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    String uuid = UUID.randomUUID().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product); // Ensure you set the correct layout

        id = getIntent().getStringExtra("id");
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Products");

        // Initialize views
        edPName = findViewById(R.id.ed_pname);
        btnSave = findViewById(R.id.btn_save);
        edDes = findViewById(R.id.ed_desc);
        spCat = findViewById(R.id.sp_cat);
        spH = findViewById(R.id.spinner_healthy);
        ivImg = findViewById(R.id.iv_pimg);
        edBar = findViewById(R.id.ed_pbar);
        iv_productImage = findViewById(R.id.product_image);

        // Load categories and healthiness options
        loadCategories();
        loadHealthiness();

        // Set existing product data if editing
        if (id != null && !id.isEmpty()) {
            loadProductData();
        }

        btnSave.setOnClickListener(v -> {
            if (validateFields()) {
                String pname = edPName.getText().toString();
                String selectedCategory = spCat.getSelectedItem().toString();
                String description = edDes.getText().toString();
                String selectedH = spH.getSelectedItem().toString().trim();
                String no = edBar.getText().toString();

                saveProductToFirebase(pname, description, selectedCategory, selectedH, uuid, no);
            }
        });

        ivImg.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddProductActivity.this);
            builder.setTitle("Choose Image Source")
                    .setItems(new CharSequence[]{"Camera", "Gallery"}, (dialog, which) -> {
                        if (which == 0) {
                            imageUri = createImageUri();
                            if (imageUri != null) {
                                captureImageLauncher.launch(imageUri);
                            } else {
                                Toast.makeText(this, "Failed to create image URI", Toast.LENGTH_SHORT).show();
                            }
                        } else if (which == 1) {
                            pickImageLauncher.launch("image/*");
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    private void loadProductData() {
        databaseReference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        edPName.setText(product.getName());
                        edDes.setText(product.getDesc());
                        edBar.setText(product.getBarcode());

                        // Set the spinner selections
                        ArrayAdapter<String> catAdapter = (ArrayAdapter<String>) spCat.getAdapter();
                        if (catAdapter != null) {
                            int catPosition = catAdapter.getPosition(product.getCat());
                            spCat.setSelection(catPosition);
                        }

                        ArrayAdapter<String> healthAdapter = (ArrayAdapter<String>) spH.getAdapter();
                        if (healthAdapter != null) {
                            int healthPosition = healthAdapter.getPosition(product.getHealthy());
                            spH.setSelection(healthPosition);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddProductActivity.this, "Failed to load product data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateFields() {
        String pname = edPName.getText().toString();
        String description = edDes.getText().toString();
        String selectedCategory = spCat.getSelectedItem().toString();
        String selectedH = spH.getSelectedItem().toString().trim();
        String barcode = edBar.getText().toString();

        if (pname.isEmpty()) {
            Toast.makeText(this, "Please enter name of product", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (description.isEmpty()) {
            Toast.makeText(this, "Please enter description", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedCategory.equals("Select category") || selectedCategory == null) {
            Toast.makeText(this, "Select a valid category", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedH.equals("Select healthiness")) {
            Toast.makeText(this, "Select a valid healthiness value", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (barcode.isEmpty()) {
            Toast.makeText(this, "Please enter barcode for this product", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void loadCategories() {
        FirebaseDatabase.getInstance().getReference("Categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list = new ArrayList<>();
                list.add("Select category");
                for (DataSnapshot mydata : snapshot.getChildren()) {
                    String categoryName = mydata.child("catname").getValue(String.class);
                    if (categoryName != null) {
                        list.add(categoryName.trim());
                    }
                }
                adapter = new ArrayAdapter<>(AddProductActivity.this, android.R.layout.simple_spinner_dropdown_item, list);
                spCat.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddProductActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadHealthiness() {
        FirebaseDatabase.getInstance().getReference("Healthiness").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayH = new ArrayList<>();
                arrayH.add("Select healthiness");
                for (DataSnapshot myData : snapshot.getChildren()) {
                    String key = myData.getKey();
                    String value = myData.getValue(String.class);
                    if (key != null && value != null) {
                        arrayH.add(key + ": " + value.trim());
                    }
                }
                adapterH = new ArrayAdapter<>(AddProductActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayH);
                spH.setAdapter(adapterH);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddProductActivity.this, "Failed to load Healthiness", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final ActivityResultLauncher<Uri> captureImageLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
                if (result) {
                    if (imageUri != null) {
                        iv_productImage.setImageURI(imageUri);
                        saveImage(imageUri);
                    }
                }
            });

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
                if (result != null) {
                    iv_productImage.setImageURI(result);
                    saveImage(result);
                }
            });

    private Uri createImageUri() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "new_image_" + System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        ContentResolver resolver = getContentResolver();
        return resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    private void saveProductToFirebase(String productName, String description, String category, String selectedH, String img, String barcode) {
        Product product = new Product();
        product.setName(productName);
        product.setDesc(description);
        product.setCat(category);
        product.setHealthy(selectedH);
        product.setImg(uuid);
        product.setBarcode(barcode);

        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("Products");

        if (id != null && !id.isEmpty()) {
            // Update an existing product
            productsRef.child(id).setValue(product).addOnSuccessListener(aVoid -> {
                Toast.makeText(AddProductActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            }).addOnFailureListener(e -> Toast.makeText(AddProductActivity.this, "Failed to update product", Toast.LENGTH_SHORT).show());
        } else {
            // Add a new product
            String newProductId = productsRef.push().getKey();
            if (newProductId != null) {
                productsRef.child(newProductId).setValue(product).addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddProductActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }).addOnFailureListener(e -> Toast.makeText(AddProductActivity.this, "Failed to add product", Toast.LENGTH_SHORT).show());
            }
        }
    }

    private void saveImage(Uri imageUri) {
        String imageString = MyUtilClass.imageUriToBase64(imageUri, getContentResolver());

        FirebaseDatabase.getInstance().getReference("Product Images")
                .child(uuid)
                .setValue(imageString)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddProductActivity.this, "Image saved", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
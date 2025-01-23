package com.example.barcodebuddy;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

// Inside AddProductActivity.java

public class AddProductActivity extends AppCompatActivity {
    String id;
    Spinner spCat, spH;
    Button btnSave;
    EditText edDes, edPName;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    ArrayList<String> arrayH;
    ArrayAdapter<String> adapterH;
    ImageView ivImg;
    private Uri imageUri;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //iss id py image save ha
    String uuid = UUID.randomUUID().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        id = getIntent().getStringExtra("id");
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Products");


        edPName = findViewById(R.id.ed_pname);
        btnSave = findViewById(R.id.btn_save);
        edDes = findViewById(R.id.ed_desc);
        spCat = findViewById(R.id.sp_cat);
        spH = findViewById(R.id.spinner_healthy);
        ivImg = findViewById(R.id.iv_pimg);

        String name = getIntent().getStringExtra("name");
        String desc = getIntent().getStringExtra("desc");
        String cat = getIntent().getStringExtra("cat");
        String health = getIntent().getStringExtra("health");

// Set the EditText values
        edPName.setText(name);
        edDes.setText(desc);

// Find the position of 'cat' in the spinner adapter
        ArrayAdapter<String> catAdapter = (ArrayAdapter<String>) spCat.getAdapter();
        if (catAdapter != null) {
            int catPosition = catAdapter.getPosition(cat);
            spCat.setSelection(catPosition); // Set the spinner selection
        }

// Find the position of 'health' in the spinner adapter
        ArrayAdapter<String> healthAdapter = (ArrayAdapter<String>) spH.getAdapter();
        if (healthAdapter != null) {
            int healthPosition = healthAdapter.getPosition(health);
            spH.setSelection(healthPosition); // Set the spinner selection
        }


        list = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, list);
        spCat.setAdapter(adapter);

        arrayH = new ArrayList<>();
        adapterH = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arrayH);
        spH.setAdapter(adapterH);

        FirebaseDatabase.getInstance().getReference("Categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                list.add("Select category");
                for (DataSnapshot mydata : snapshot.getChildren()) {
                    String categoryName = mydata.child("catname").getValue(String.class);
                    if (categoryName != null) {
                        list.add(categoryName.trim());
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddProductActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });

        FirebaseDatabase.getInstance().getReference("Healthiness").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayH.clear();
                arrayH.add("Select healthiness");

                for (DataSnapshot myData : snapshot.getChildren()) {
                    String key = myData.getKey();
                    String value = myData.getValue(String.class);
                    if (key != null && value != null) {
                        arrayH.add(key + ": " + value.trim());
                    }
                }
                adapterH.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddProductActivity.this, "Failed to load Healthiness", Toast.LENGTH_SHORT).show();
            }
        });

        btnSave.setOnClickListener(v -> {
            String pname = edPName.getText().toString();
            String selectedCategory = spCat.getSelectedItem().toString();
            String description = edDes.getText().toString();
            String selectedH = spH.getSelectedItem().toString().trim();


            if(pname.isEmpty()){
                Toast.makeText(this, "Please enter name of product", Toast.LENGTH_SHORT).show();
                 return;
            }
            if(description.isEmpty()){
                Toast.makeText(this, "Please enter description", Toast.LENGTH_SHORT).show();
                 return;
            }
            if(selectedCategory.equals("Select category")||selectedCategory==null){
                Toast.makeText(this, "Select a valid category", Toast.LENGTH_SHORT).show();
            return;
            }
            if(selectedH.equals("Select healthiness")){
                Toast.makeText(this, "Select a valid healthiness value", Toast.LENGTH_SHORT).show();
           return;
            }


            saveProductToFirebase(pname, description, selectedCategory, selectedH, uuid);
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

    private final ActivityResultLauncher<Uri> captureImageLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
                if (result) {
                    if (imageUri != null) {
                        ivImg.setImageURI(imageUri);
                        saveImage(imageUri);
                    }
                }
            });

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
                if (result != null) {
                    ivImg.setImageURI(result);
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

    private void saveProductToFirebase(String productName, String description, String category, String selectedH, String img) {
        Product product = new Product();
        product.setName(productName);
        product.setDesc(description);
        product.setCat(category);
        product.setHealthy(selectedH);
        product.setImg(uuid);


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
        String imageString = imageUriToBase64(imageUri, getContentResolver());

        FirebaseDatabase.getInstance().getReference("Product Images")
                .child(uuid)
                .setValue(imageString)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddProductActivity.this, "Image saved", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static String imageUriToBase64(Uri imageUri, ContentResolver contentResolver) {
        try (InputStream inputStream = contentResolver.openInputStream(imageUri)) {
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 68, outputStream);
            return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

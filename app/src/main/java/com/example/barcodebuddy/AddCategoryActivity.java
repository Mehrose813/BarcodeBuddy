package com.example.barcodebuddy;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddCategoryActivity extends AppCompatActivity {
Button btnSaveCategoryName;
EditText etCategryName;
DatabaseReference ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_category);

        //Initialize
        etCategryName=findViewById(R.id.et_category_name);
        btnSaveCategoryName=findViewById(R.id.btn_save_category);
        ref= FirebaseDatabase.getInstance().getReference("Categories");
        btnSaveCategoryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveCategoryName();
            }
        });

    }
    private void saveCategoryName() {
        String catName = etCategryName.getText().toString();

        // Validation for empty input
        if (catName.isEmpty()) {
            Toast.makeText(this, "Enter the category name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate unique ID for the category
        String id = ref.push().getKey();

        // Create a category object
        CategoryClass categoryClass = new CategoryClass(catName, id);

        // Save the category in Firebase
        ref.child(id).setValue(categoryClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddCategoryActivity.this, "Saved successfully", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(AddCategoryActivity.this, "Failed to save", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
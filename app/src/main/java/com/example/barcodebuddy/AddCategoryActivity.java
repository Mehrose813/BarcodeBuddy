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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddCategoryActivity extends ToolBarActivity {
Button btnSaveCategoryName;
EditText etCategryName;
DatabaseReference ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        //setContentView(R.layout.activity_add_category);

        getLayoutInflater().inflate(R.layout.activity_add_category, findViewById(R.id.container));
        setToolbarTitle("Add Category");
        showBackButton(true);
        //Initialize
        String id = getIntent().getStringExtra("id");
        etCategryName=findViewById(R.id.et_category_name);
        btnSaveCategoryName=findViewById(R.id.btn_save_category);
        ref= FirebaseDatabase.getInstance().getReference("Categories");


        if(id != null && ! id.isEmpty()){
            FirebaseDatabase.getInstance().getReference("Categories")
                    .child(id)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            CategoryClass categoryClass = snapshot.getValue(CategoryClass.class);
                            if(categoryClass!= null){
                                etCategryName.setText(categoryClass.getCatname());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }

        btnSaveCategoryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etCategryName.getText().toString();

                if (name.isEmpty()) {
                    Toast.makeText(AddCategoryActivity.this, "Enter the name", Toast.LENGTH_SHORT).show();
                    return;
                }

                CategoryClass categoryClass = new CategoryClass();
                categoryClass.setCatname(name);

                DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("Categories");

                if (id != null && !id.isEmpty()) {
                    // Update existing ingredient
                    categoryRef.child(id).setValue(categoryClass)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(AddCategoryActivity.this, "Category updated successfully", Toast.LENGTH_SHORT).show();
                                    finish(); // Close this activity and go back to the previous one
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddCategoryActivity.this, "Failed to update category", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    // Add new ingredient
                    categoryRef.push().setValue(categoryClass)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(AddCategoryActivity.this, "Category added successfully", Toast.LENGTH_SHORT).show();
                                    etCategryName.setText(""); // Clear the EditText
                                    finish(); // Close this activity and go back to the previous one
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddCategoryActivity.this, "Failed to add category", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

    }
}
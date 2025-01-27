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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddBlogsActivity extends AppCompatActivity {
EditText etBlogs;
Button btnSave;
DatabaseReference ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_blogs);
        etBlogs=findViewById(R.id.et_blogs);
        btnSave=findViewById(R.id.btn_save_blog);
        ref= FirebaseDatabase.getInstance().getReference("Blogs");

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String blogs=etBlogs.getText().toString();
                if (blogs.isEmpty()){
                    Toast.makeText(AddBlogsActivity.this, "Field is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    saveBlogs(blogs);
                }
            }
        });



    }
    private void saveBlogs(String blogs) {
        // Generating a unique key for each blog entry
        ref.push().setValue(blogs).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(AddBlogsActivity.this, "Saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AddBlogsActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
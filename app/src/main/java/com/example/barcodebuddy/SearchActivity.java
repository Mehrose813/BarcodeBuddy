package com.example.barcodebuddy;

import android.content.Intent;
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

public class SearchActivity extends AppCompatActivity {
    public EditText edSearch;
    Button btnSearch;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        edSearch = findViewById(R.id.ed_searchname);
        btnSearch = findViewById(R.id.btn_search);
        ref = FirebaseDatabase.getInstance().getReference("Products");

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productName = edSearch.getText().toString().trim();

                if (productName.isEmpty()) {
                    Toast.makeText(SearchActivity.this, "Field is empty", Toast.LENGTH_SHORT).show();
                } else {
                    searchProduct(productName);
                }
            }
        });
    }

    public void searchProduct(String name) {
        ref.orderByChild("name").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                        // Fetch basic details
                        String productName = productSnapshot.child("name").getValue(String.class);
                        String productCategory = productSnapshot.child("cat").getValue(String.class);

                        // Pass basic details to next activity
                        Intent intent = new Intent(SearchActivity.this, ProductDisplayActivity.class);
                        intent.putExtra("name", productName);
                        intent.putExtra("cat", productCategory);
                        intent.putExtra("productKey", productSnapshot.getKey()); // Pass product key for fetching ingredients
                        startActivity(intent);
                        break;
                    }
                } else {
                    Toast.makeText(SearchActivity.this, "Product not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SearchActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}


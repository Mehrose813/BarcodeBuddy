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
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private SearchView searchView;
    private RecyclerView rvProducts;
    private DatabaseReference databaseReference;
    private List<Product> productList;
    private SearchProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize views
        rvProducts = findViewById(R.id.search_product);
        searchView = findViewById(R.id.search_view);

        // Set LayoutManager for RecyclerView
        rvProducts.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the product list and adapter
        productList = new ArrayList<>();
        productAdapter = new SearchProductAdapter(productList); // Pass the list to the adapter
        rvProducts.setAdapter(productAdapter);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Products");

        // Initially hide the RecyclerView
        rvProducts.setVisibility(View.GONE);

        // Set listener for SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchProducts(query); // Call search method on submit
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchProducts(newText); // Call search method on text change
                return true;
            }
        });
    }

    // Method to search products
    private void searchProducts(String query) {
        Query searchQuery = databaseReference.orderByChild("name").startAt(query).endAt(query + "\uf8ff");
        searchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear(); // Clear existing data to avoid duplicates
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    if (product != null) {
                        productList.add(product); // Add product to the list
                    }
                }
                productAdapter.notifyDataSetChanged(); // Notify adapter about data changes

                // Show or hide RecyclerView based on data
                if (productList.isEmpty()) {
                    rvProducts.setVisibility(View.GONE);
                    Toast.makeText(SearchActivity.this, "No products found", Toast.LENGTH_SHORT).show();
                } else {
                    rvProducts.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SearchActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

//        edSearch = findViewById(R.id.ed_searchname);
//        btnSearch = findViewById(R.id.btn_search);
//        ref = FirebaseDatabase.getInstance().getReference("Products");
//
//        btnSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String productName = edSearch.getText().toString().trim();
//
//                if (productName.isEmpty()) {
//                    Toast.makeText(SearchActivity.this, "Field is empty", Toast.LENGTH_SHORT).show();
//                } else {
//                    searchProduct(productName);
//                }
//            }
//        });

//    public void searchProduct(String name) {
//        ref.orderByChild("name").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    for (DataSnapshot productSnapshot : snapshot.getChildren()) {
//                        String productName = productSnapshot.child("name").getValue(String.class);
//                        String productCategory = productSnapshot.child("cat").getValue(String.class);
//                        String producDes = productSnapshot.child("desc").getValue(String.class);
//                        String productHealth = productSnapshot.child("healthy").getValue(String.class);
//
//                        Intent intent = new Intent(SearchActivity.this, ProductDisplayActivity.class);
//                        intent.putExtra("name", productName);
//                        intent.putExtra("cat", productCategory);
//                        intent.putExtra("desc",producDes);
//                        intent.putExtra("healthy",productHealth);
//
//                        intent.putExtra("productKey", productSnapshot.getKey());
//                        startActivity(intent);
//                        break;
//                    }
//                } else {
//                    Toast.makeText(SearchActivity.this, "Product not found", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(SearchActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }




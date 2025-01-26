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
        productAdapter = new SearchProductAdapter(SearchActivity.this,productList); // Pass the list to the adapter
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
        // Only trigger search if the query length is 3 or more characters
        if (query == null || query.trim().isEmpty() || query.length() < 2) {
            productList.clear(); // Clear the list to avoid showing irrelevant results
            productAdapter.notifyDataSetChanged();
            rvProducts.setVisibility(View.GONE);
            return; // Exit if query is too short
        }

        final String lowercaseQuery = query.toLowerCase().trim(); // Normalize query input

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                boolean productFound = false;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String productName = dataSnapshot.child("name").getValue(String.class);

                    if (productName != null && productName.toLowerCase().contains(lowercaseQuery)) {
                        Product product = dataSnapshot.getValue(Product.class);
                        if (product != null) {
                            productList.add(product);
                            productFound = true;
                        }
                    }
                }

                productAdapter.notifyDataSetChanged();

                if (productFound) {
                    rvProducts.setVisibility(View.VISIBLE);
                } else {
                    rvProducts.setVisibility(View.GONE);
                    Toast.makeText(SearchActivity.this, "No products found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SearchActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        rvProducts.setVisibility(View.VISIBLE);
    }

}
//
//    private void searchProducts(String query) {
//        Query searchQuery = databaseReference.orderByChild("name").startAt(query).endAt(query + "\uf8ff");
//        searchQuery.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                productList.clear(); // Clear existing data to avoid duplicates
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    Product product = dataSnapshot.getValue(Product.class);
//                    if (product != null) {
//                        productList.add(product); // Add product to the list
//                    }
//                }
//                productAdapter.notifyDataSetChanged(); // Notify adapter about data changes
//
//                // Show or hide RecyclerView based on data
//                if (productList.isEmpty()) {
//                    rvProducts.setVisibility(View.GONE);
//                    Toast.makeText(SearchActivity.this, "No products found", Toast.LENGTH_SHORT).show();
//                } else {
//                    rvProducts.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(SearchActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//}
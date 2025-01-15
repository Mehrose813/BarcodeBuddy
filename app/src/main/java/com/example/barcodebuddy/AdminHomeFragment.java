package com.example.barcodebuddy;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminHomeFragment extends Fragment {
    Button btnAddProduct, btnAddIngredients;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AdminHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminHomeFragment newInstance(String param1, String param2) {
        AdminHomeFragment fragment = new AdminHomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);
        Button addProduct = view.findViewById(R.id.btn_add_product);

        // Initialize RecyclerView
        RecyclerView rvProduct = view.findViewById(R.id.rv_product);

        // Layout Manager for the RecyclerView
        rvProduct.setLayoutManager(new LinearLayoutManager(getContext()));  // Adding Layout Manager

        // Create the adapter outside the Firebase listener to maintain the same instance
        List<Product> productsList = new ArrayList<>();
        ProductAdapter productAdapter = new ProductAdapter(productsList); // Pass the context and the list
        rvProduct.setAdapter(productAdapter);  // Set the adapter to the RecyclerView

        // Fetch data from Firebase
        FirebaseDatabase.getInstance().getReference("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Clear the list before adding new data
                        productsList.clear();  // Clear old data

                        // Loop through each snapshot and add products to the list
                        for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                            Product product = productSnapshot.getValue(Product.class);
                            product.setId(productSnapshot.getKey());
                            productsList.add(product);
//                            if (product != null) {
//                                productsList.add(product);  // Add the product to the list
//                            }
                        }
                        rvProduct.setAdapter(new ProductAdapter(productsList));


                        // Notify the adapter that the data has changed
                       // productAdapter.notifyDataSetChanged();  // Refresh the RecyclerView
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle database error
                    }
                });

        // Add Ingredients button click listener
        btnAddIngredients = view.findViewById(R.id.btn_add_ingredients);
        btnAddIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), IngredientListActivity.class);
                startActivity(intent);
            }
        });

        // Add Product button click listener
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddProductActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
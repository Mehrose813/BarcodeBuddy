package com.example.barcodebuddy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler; // Correct import for Handler

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView; // Import for Lottie Animation
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    CardView searchCard, scanCard;
    TextView nameSafeIng, categorySafeIng, nameSafe, categorySafe, nameDanger, categoryDanger, nameDangerIng, categoryDangerIng;



    DatabaseReference dbRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        // Initialize UI elements
        searchCard = view.findViewById(R.id.search_card);
        scanCard = view.findViewById(R.id.scan_card);
        nameSafeIng = view.findViewById(R.id.name_safe_ing);
        categorySafeIng = view.findViewById(R.id.category_safe_ing);
        nameSafe = view.findViewById(R.id.name_safe);
        categorySafe = view.findViewById(R.id.category_safe);
        nameDanger = view.findViewById(R.id.name_danger);
        categoryDanger = view.findViewById(R.id.category_danger);
        nameDangerIng = view.findViewById(R.id.name_danger_ing);
        categoryDangerIng = view.findViewById(R.id.category_danger_ing);


        nameSafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),IngDetailActivity.class);
                intent.putExtra("name",nameSafe.getText().toString());
                intent.putExtra("color",R.color.dark_green);
                startActivity(intent);            }
        });
        nameSafeIng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),IngDetailActivity.class);
                intent.putExtra("name",nameSafeIng.getText().toString());
                intent.putExtra("color",R.color.dark_green);
                startActivity(intent);
            }
        });
        nameDanger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),IngDetailActivity.class);
                intent.putExtra("name",nameDanger.getText().toString());
                intent.putExtra("color",R.color.red);
                startActivity(intent);
            }
        });
        nameDangerIng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),IngDetailActivity.class);
                intent.putExtra("name",nameDangerIng.getText().toString());
                intent.putExtra("color",R.color.red);
                startActivity(intent);
            }
        });

        dbRef = FirebaseDatabase.getInstance().getReference("Ingredients");

        searchCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireContext(), SearchActivity.class));
            }
        });
        scanCard.setOnClickListener(v -> startActivity(new Intent(requireContext(), ScanBarcodeActivity.class)));

        fetchIngredients();

        return view;
    }

    private void fetchIngredients() {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String[]> safeIngredients = new ArrayList<>();
                List<String[]> dangerousIngredients = new ArrayList<>();

                // Iterate over all ingredients in the database
                for (DataSnapshot ingredientSnapshot : snapshot.getChildren()) {
                    String name = ingredientSnapshot.child("name").getValue(String.class);
                    String category = ingredientSnapshot.child("category").getValue(String.class);

                    if (category != null && name != null) {
                        if (category.equalsIgnoreCase("safe")) {
                            safeIngredients.add(new String[]{name, category});
                        } else if (category.equalsIgnoreCase("dangerous")) {
                            dangerousIngredients.add(new String[]{name, category});
                        }
                    }
                }

                // Randomize and select ingredients for display
                Collections.shuffle(safeIngredients);
                Collections.shuffle(dangerousIngredients);

                // Display Safe Ingredients
                if (safeIngredients.size() > 0) {
                    nameSafe.setText(safeIngredients.get(0)[0]);
                    categorySafe.setText(safeIngredients.get(0)[1]);
                }
                if (safeIngredients.size() > 1) {
                    nameSafeIng.setText(safeIngredients.get(1)[0]);
                    categorySafeIng.setText(safeIngredients.get(1)[1]);
                }

                // Display Dangerous Ingredients
                if (dangerousIngredients.size() > 0) {
                    nameDanger.setText(dangerousIngredients.get(0)[0]);
                    categoryDanger.setText(dangerousIngredients.get(0)[1]);
                }
                if (dangerousIngredients.size() > 1) {
                    nameDangerIng.setText(dangerousIngredients.get(1)[0]);
                    categoryDangerIng.setText(dangerousIngredients.get(1)[1]);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }
}

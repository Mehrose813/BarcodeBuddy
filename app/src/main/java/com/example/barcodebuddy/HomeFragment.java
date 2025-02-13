package com.example.barcodebuddy;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
CardView searchCard,scanCard;
TextView nameSafeIng,categorySafeIng,nameSafe,categorySafe,nameDanger,categoryDanger,nameDangerIng,categoryDangerIng;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

    DatabaseReference dbRef;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

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


         dbRef = FirebaseDatabase.getInstance().getReference("Ingredients");


        searchCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(),SearchActivity.class);
                startActivity(intent);
            }
        });

        scanCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(),ScanBarcodeActivity.class);
                startActivity(intent);
            }
        });


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
                    nameSafe.setText(safeIngredients.get(0)[0]); // Name
                    categorySafe.setText(safeIngredients.get(0)[1]); // Category
                }
                if (safeIngredients.size() > 1) {
                    nameSafeIng.setText(safeIngredients.get(1)[0]); // Name
                    categorySafeIng.setText(safeIngredients.get(1)[1]); // Category
                }

                // Display Dangerous Ingredients
                if (dangerousIngredients.size() > 0) {
                    nameDanger.setText(dangerousIngredients.get(0)[0]); // Name
                    categoryDanger.setText(dangerousIngredients.get(0)[1]); // Category
                }
                if (dangerousIngredients.size() > 1) {
                    nameDangerIng.setText(dangerousIngredients.get(1)[0]); // Name
                    categoryDangerIng.setText(dangerousIngredients.get(1)[1]); // Category
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }


}
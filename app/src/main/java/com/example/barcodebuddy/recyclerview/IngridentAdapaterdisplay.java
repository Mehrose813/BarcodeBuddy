package com.example.barcodebuddy.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barcodebuddy.Ingredient;  // Assuming Ingredient class is in the same package
import com.example.barcodebuddy.R;
import com.example.barcodebuddy.authdao.DataCallBack;
import com.example.barcodebuddy.authdao.UserAllergyDAO;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class IngridentAdapaterdisplay extends RecyclerView.Adapter<IngridentViewHolderdisplay> {
    private List<Ingredient> ingredientList;
    private List<String> allergies = new ArrayList<>();

    public IngridentAdapaterdisplay(List<Ingredient> ingredientList) {
        this.ingredientList = ingredientList;
        new UserAllergyDAO().getUserAllergies(FirebaseAuth.getInstance().getUid(), new DataCallBack<List<String>>() {
            @Override
            public void onSuccess(List<String> data) {
                allergies = data;
            }

            @Override
            public void onError(String msg) {

            }
        });

    }

    @NonNull
    @Override
    public IngridentViewHolderdisplay onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingrident_display_user, parent, false);
        return new IngridentViewHolderdisplay(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngridentViewHolderdisplay holder, int position) {
        Ingredient ingredient = ingredientList.get(position);
        holder.tvName.setText(ingredient.getName());
        holder.tvQuantity.setText(ingredient.getQty());

        // Get context from the holder's itemView
        Context context = holder.itemView.getContext();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent =new Intent(context, Ingre)
            }
        });


        Log.e("onBindViewHolder: ", ingredient.getName()+" "+allergies.contains(ingredient.getName().toLowerCase()));

        if (allergies.contains(ingredient.getName().toLowerCase())) {
            holder.tvName.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
        } else {
            holder.tvName.setBackgroundColor(ContextCompat.getColor(context, R.color.white)); // Make sure R.color.white exists
        }
    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }
}

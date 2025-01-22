package com.example.barcodebuddy.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barcodebuddy.Ingredient;  // Assuming Ingredient class is in the same package
import com.example.barcodebuddy.R;

import java.util.List;

public class IngridentAdapaterdisplay extends RecyclerView.Adapter<IngridentViewHolderdisplay> {
    private List<Ingredient> ingredientList;


    public IngridentAdapaterdisplay(List<Ingredient> ingredientList) {
        this.ingredientList = ingredientList;
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
    }

    @Override
    public int getItemCount() {
        return ingredientList.size();  // Return the size of ingredientList
    }
}

package com.example.barcodebuddy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {

    private List<CategoryClass> categoryClasses;
    private Context context;
    public CategoryAdapter(Context context, List<CategoryClass> categoryClasses) {
        this.context = context;
        this.categoryClasses = categoryClasses;
    }


    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_category_list, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        // Get the current category
        CategoryClass categoryClass = categoryClasses.get(position);

        // Bind data to the ViewHolder
        holder.tvName.setText(categoryClass.getCatname());

        // Delete button functionality
        holder.ivDelete.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Are you sure you want to delete this ingredient?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        FirebaseDatabase.getInstance().getReference("Ingredients")
                                .child(categoryClass.getId())
                                .removeValue();
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });

            builder.show();
        });

        // Edit button functionality
        holder.ivEdit.setOnClickListener(view -> {
            Intent intent = new Intent(context, AddIngredientActivity.class);
            intent.putExtra("id", categoryClass.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return categoryClasses != null ? categoryClasses.size() : 0;
    }
}

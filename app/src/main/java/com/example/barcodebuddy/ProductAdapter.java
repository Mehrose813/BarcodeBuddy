package com.example.barcodebuddy;

import android.app.ProgressDialog;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {

    private List<Product> products;
    private Context context;

    // Constructor to initialize products and context
    public ProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Get context from parent and inflate the view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {

        Product product = products.get(position);
        holder.tvName.setText(product.getName());
        holder.tvCat.setText(product.getCat());
        holder.ivDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creating AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Are you sure?");
                builder.setMessage("You want to delete the product from the database?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Show progress dialog
                        ProgressDialog progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("Deleting product...");
                        progressDialog.setCancelable(false); // Don't allow the user to dismiss the dialog
                        progressDialog.show();

                        // Remove the product from Firebase
                        FirebaseDatabase.getInstance().getReference("Products")
                                .child(product.getId())  // Use product's unique ID
                                .removeValue()  // Delete the product from Firebase
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();  // Dismiss the progress dialog

                                        if (task.isSuccessful()) {
                                            // Successfully deleted product from Firebase
                                            Toast.makeText(context, "Product deleted successfully", Toast.LENGTH_SHORT).show();

                                            // Remove the product from the list in memory
                                            products.remove(position);

                                            // Notify the adapter that the item has been removed
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position, products.size());
                                        } else {
                                            // Show error Toast
                                            Toast.makeText(context, "Failed to delete product. Please try again.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();  // Dismiss the dialog
                    }
                });

                builder.show();
            }
        });


        holder.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open the AddProductActivity to edit the product
                Intent intent = new Intent(context, AddProductActivity.class);
                intent.putExtra("id", product.getId());
                intent.putExtra("name",product.getName());
                intent.putExtra("desc",product.getDesc());
                intent.putExtra("cat",product.getCat());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}

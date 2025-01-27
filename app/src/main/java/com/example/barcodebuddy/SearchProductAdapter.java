package com.example.barcodebuddy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class SearchProductAdapter extends RecyclerView.Adapter<SearchProductViewHolder> {
    private List<Product> products;
    private Context context;

    public SearchProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public SearchProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_search, parent, false);
        return new SearchProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchProductViewHolder holder, int position) {
        Product product = products.get(position);
        setAnimation(holder.itemView, position);

        Log.e("pro_img: ", product.getImg() + "");

        // Bind product data to views
        holder.tvName.setText(product.getName());
        holder.tvCat.setText(product.getCat());

        // Decode and display image
        String img = product.getImg();
        FirebaseDatabase.getInstance().getReference("Product Images").child(img)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Bitmap bitmap = MyUtilClass.base64ToBitmap(snapshot.getValue().toString());
                            if (bitmap != null) {
                                holder.ivImg.setImageBitmap(bitmap);
                            }
                            else{
                                holder.ivImg.setImageResource(R.drawable.product);
                            }
                        }
                        else{
                            holder.ivImg.setImageResource(R.drawable.product);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Debug", "Error: " + error.getMessage());
//                        Toast.makeText(IngredientQuantityActivity.this, "Failed to load image: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Set OnClickListener for the item
        holder.itemView.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference("Products")
                    .orderByChild("name")
                    .equalTo(product.getName()) // Assuming product names are unique
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                                String productKey = dataSnapshot.getKey(); // Get unique key

                                // Send product details to the ProductDisplayActivity
                                Intent intent = new Intent(context, ProductDisplayActivity.class);
                                intent.putExtra("name", product.getName());
                                intent.putExtra("cat", product.getCat());
                                intent.putExtra("desc", product.getDesc());
                                intent.putExtra("healthy", product.getHealthy());
                                intent.putExtra("productKey", dataSnapshot.getKey());
                                intent.putExtra("barcode",product.getBarcode());
                                context.startActivity(intent);
                                break;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle error
                        }
                    });
        });
    }


    @Override
    public int getItemCount() {
        return products.size();
    }

    // Helper method to convert Base64 string to Bitmap
    public static Bitmap base64ToBitmap(String base64String) {
        try {
            byte[] imageBytes = android.util.Base64.decode(base64String, android.util.Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to set animation for RecyclerView items
    private void setAnimation(View viewToAnimate, int position) {
        Animation slideIn = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        viewToAnimate.startAnimation(slideIn);
    }
}

package com.example.barcodebuddy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

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
    // Updated constructor to include context
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
        // Bind product data to views
        holder.tvName.setText(product.getName());
        holder.tvCat.setText(product.getCat());
        // Decode and display image
        String img = product.getImg();
        if (img != null && !img.isEmpty()) {
            Bitmap bitmap = base64ToBitmap(img);
            if (bitmap != null) {
                holder.ivImg.setImageBitmap(bitmap);
                holder.ivImg.setVisibility(View.VISIBLE);
            } else {
                holder.ivImg.setImageResource(R.drawable.product); // Hide if decoding fails
            }
        } else {
            holder.ivImg.setImageResource(R.drawable.product); // Default image
            holder.ivImg.setVisibility(View.GONE); // Optionally hide if no image is available
        }
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
    // Updated setAnimation method to use the provided context
    private void setAnimation(View viewToAnimate, int position) {
        Animation slideIn = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        viewToAnimate.startAnimation(slideIn);
    }
}

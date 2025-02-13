package com.example.barcodebuddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MissingProductAdapter extends RecyclerView.Adapter<MissingProductViewHolder> {

    private List<MissingProduct> missingProducts;
    private Context context;

    // Constructor to initialize the adapter
    public MissingProductAdapter(List<MissingProduct> missingProducts) {
        this.missingProducts = missingProducts;
    }
    @NonNull
    @Override
    public MissingProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new MissingProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MissingProductViewHolder holder, int position) {

        MissingProduct product = missingProducts.get(position);
        // Set the title with user email and body with barcode information
        holder.tvNotificationTitle.setText("New Message from " + product.getUserEmail());
        holder.tvNotificationBody.setText("Product with barcode " + product.getBarcode() + " is missing.");
    }

    @Override
    public int getItemCount() {
        return missingProducts.size();
    }
}

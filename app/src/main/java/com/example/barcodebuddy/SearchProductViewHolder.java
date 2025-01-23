package com.example.barcodebuddy;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SearchProductViewHolder extends RecyclerView.ViewHolder {

    TextView tvName,tvCat;
    ImageView ivImg;

    public SearchProductViewHolder(@NonNull View itemView) {
        super(itemView);

        ivImg = itemView.findViewById(R.id.iv_pic);
        tvName = itemView.findViewById(R.id.tv_product);
        tvCat = itemView.findViewById(R.id.tv_category);
    }

}

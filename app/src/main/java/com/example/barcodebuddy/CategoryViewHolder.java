package com.example.barcodebuddy;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryViewHolder extends RecyclerView.ViewHolder {
    TextView tvName;
    ImageView ivEdit, ivDelete;
    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        tvName = itemView.findViewById(R.id.tv_name);
        ivEdit = itemView.findViewById(R.id.iv_edit);
        ivDelete = itemView.findViewById(R.id.iv_delete);
    }
}

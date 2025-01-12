package com.example.barcodebuddy;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductViewHolder extends RecyclerView.ViewHolder {
    TextView tvName,tvCat;
    ImageView ivEdit,ivDel;
    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        tvName = itemView.findViewById(R.id.tv_pname);
        tvCat = itemView.findViewById(R.id.tv_pcat);
        ivEdit = itemView.findViewById(R.id.iv_pedit);
        ivDel = itemView.findViewById(R.id.iv_pdelete);

    }
}

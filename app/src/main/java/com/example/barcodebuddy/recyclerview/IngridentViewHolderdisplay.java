package com.example.barcodebuddy.recyclerview;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barcodebuddy.R;

public class IngridentViewHolderdisplay extends RecyclerView.ViewHolder{
    public TextView tvName,tvQuantity;
    public IngridentViewHolderdisplay(@NonNull View itemView) {
        super(itemView);
        tvQuantity=itemView.findViewById(R.id.tv_ingridentquantity);
        tvName=itemView.findViewById(R.id.tv_ingrident_name);
    }
}

package com.example.barcodebuddy;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReplyNotificationViewHolder extends RecyclerView.ViewHolder {
    TextView tvReply;
    public ReplyNotificationViewHolder(@NonNull View itemView) {
        super(itemView);

        tvReply = itemView.findViewById(R.id.tv_reply);

    }
}

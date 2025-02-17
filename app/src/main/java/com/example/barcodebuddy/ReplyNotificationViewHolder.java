package com.example.barcodebuddy;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReplyNotificationViewHolder extends RecyclerView.ViewHolder {
    TextView tvReply,tvBarcode,tvReplyTitle;
    public ReplyNotificationViewHolder(@NonNull View itemView) {
        super(itemView);

        tvReplyTitle = itemView.findViewById(R.id.tv_reply_title);
        tvBarcode = itemView.findViewById(R.id.tv_barcode);
        tvReply = itemView.findViewById(R.id.tv_reply);


    }
}

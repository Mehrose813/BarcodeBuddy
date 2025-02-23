package com.example.barcodebuddy;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MissingProductViewHolder extends RecyclerView.ViewHolder {
    TextView tvNotificationTitle,tvNotificationBody,tvNotificationTime,tvNotificationDate;
    public MissingProductViewHolder(@NonNull View itemView) {
        super(itemView);
        tvNotificationTitle = itemView.findViewById(R.id.tv_notification_title);
        tvNotificationBody = itemView.findViewById(R.id.tv_notification_body);
        tvNotificationDate = itemView.findViewById(R.id.tv_notification_date);
        tvNotificationTime = itemView.findViewById(R.id.tv_notification_time);

    }
}

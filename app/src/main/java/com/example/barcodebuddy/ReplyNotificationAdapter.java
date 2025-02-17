package com.example.barcodebuddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReplyNotificationAdapter extends RecyclerView.Adapter<ReplyNotificationViewHolder> {

    private List<Reply> replyList;
    private Context context;

    public ReplyNotificationAdapter(List<Reply> replyList) {
        this.replyList = replyList;
    }

    @NonNull
    @Override
    public ReplyNotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reply, parent, false);
        return new ReplyNotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyNotificationViewHolder holder, int position) {

        Reply reply = replyList.get(position);
        holder.tvReplyTitle.setText("Reply from Admin");
        holder.tvBarcode.setText("Product Barcode: " + reply.getBarcode());
        holder.tvReply.setText("Reply from Admin: " + reply.getReplyMessage());
    }

    @Override
    public int getItemCount() {
        return replyList.size();
    }
}

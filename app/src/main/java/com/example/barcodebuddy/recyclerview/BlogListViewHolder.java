package com.example.barcodebuddy.recyclerview;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barcodebuddy.R;

public class BlogListViewHolder extends RecyclerView.ViewHolder {
    public TextView title, content, author;
    public ImageView ivDelete, ivedit;

    public BlogListViewHolder(@NonNull View itemView) {
        super(itemView);

        // View Binding
        title = itemView.findViewById(R.id.tv_Title);
        content = itemView.findViewById(R.id.tv_Content);
        author = itemView.findViewById(R.id.tv_Author);
        ivDelete = itemView.findViewById(R.id.iv_delete);
        ivedit = itemView.findViewById(R.id.iv_edit);
    }
}

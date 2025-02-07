package com.example.barcodebuddy.recyclerview;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barcodebuddy.R;

public class BlogListViewHolder extends RecyclerView.ViewHolder {
    TextView tvTitle,tvContent,tvAuthor;
    ImageView ivEdit,ivDelete,iv_BlogImage;
    public BlogListViewHolder(@NonNull View itemView) {
        super(itemView);
       tvTitle= itemView.findViewById(R.id.tv_Title);
        tvAuthor= itemView.findViewById(R.id.tv_Author);
        tvContent= itemView.findViewById(R.id.tv_Content);
        ivEdit=itemView.findViewById(R.id.iv_edit);
        ivDelete=itemView.findViewById(R.id.iv_delete);
        iv_BlogImage=itemView.findViewById(R.id.iv_blog_image);

    }
}

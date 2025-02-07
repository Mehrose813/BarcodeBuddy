package com.example.barcodebuddy.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barcodebuddy.R;

import java.util.List;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder> {

    private List<BlogClass> blogList;
    private Context context;

    public BlogAdapter(Context context, List<BlogClass> blogList) {
        this.context = context;
        this.blogList = blogList;
    }

    @NonNull
    @Override
    public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.blog_item_view, parent, false);
        return new BlogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder holder, int position) {
        BlogClass blog = blogList.get(position);
        holder.tvTitle.setText(blog.getBlogName());
        holder.tvContent.setText(blog.getBlogContent());
        holder.tvAuthor.setText("By: " + blog.getBlogAuthor());
    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvAuthor;
        ImageView ivDelete, ivEdit;

        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_Title);
            tvContent = itemView.findViewById(R.id.tv_Content);
            tvAuthor = itemView.findViewById(R.id.tv_Author);
            ivDelete = itemView.findViewById(R.id.iv_delete);
            ivEdit = itemView.findViewById(R.id.iv_edit);
        }
    }
}

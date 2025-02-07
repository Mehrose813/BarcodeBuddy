package com.example.barcodebuddy.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barcodebuddy.R;

import java.util.List;

public class BlogListAdapter extends RecyclerView.Adapter<BlogListViewHolder> {
    List<BlogClass>bloglist;
    private Context context;

    public BlogListAdapter(List<BlogClass> bloglist) {
        this.bloglist = bloglist;
    }

    @NonNull
    @Override
    public BlogListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_add_list,parent,false);
        return new BlogListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogListViewHolder holder, int position) {
          BlogClass blogClass=bloglist.get(position);
          holder.tvTitle.setText(blogClass.getBlogName());
        holder.tvContent.setText(blogClass.getBlogContent());
        holder.tvAuthor.setText(blogClass.getBlogAuthor());

    }

    @Override
    public int getItemCount() {
        return bloglist.size();
    }
}

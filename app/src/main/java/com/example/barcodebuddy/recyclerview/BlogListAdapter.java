package com.example.barcodebuddy.recyclerview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barcodebuddy.AddBlogsActivity;
import com.example.barcodebuddy.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class BlogListAdapter extends RecyclerView.Adapter<BlogListViewHolder> {

    private final List<BlogClass> blogList;
    private final Context context;

    public BlogListAdapter(Context context, List<BlogClass> blogList) {
        this.context = context;
        this.blogList = blogList;
    }

    @NonNull
    @Override
    public BlogListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_add_list, parent, false);
        return new BlogListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogListViewHolder holder, int position) {
        BlogClass blog = blogList.get(position);

        holder.title.setText(blog.getBlogName());
        holder.content.setText(blog.getBlogContent());
        holder.author.setText(blog.getBlogAuthor());

        // Handle Edit Click
        holder.ivedit.setOnClickListener(view -> {
            Intent intent = new Intent(context, AddBlogsActivity.class);
            intent.putExtra("blogId", blog.getBlogId());
            intent.putExtra("blogTitle", blog.getBlogName());
            intent.putExtra("blogContent", blog.getBlogContent());
            intent.putExtra("blogAuthor", blog.getBlogAuthor());
            context.startActivity(intent);
        });

        // Handle Delete Click with Confirmation
        holder.ivDelete.setOnClickListener(view -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Blog")
                    .setMessage("Are you sure you want to delete this blog?")
                    .setPositiveButton("Delete", (dialog, which) -> deleteBlog(context, position))
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    private void deleteBlog(Context context, int position) {
        BlogClass blog = blogList.get(position);

        FirebaseDatabase.getInstance().getReference("Blogs")
                .child(blog.getBlogId()).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Blog deleted successfully!", Toast.LENGTH_SHORT).show();
                    blogList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, blogList.size());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to delete blog!", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }
}

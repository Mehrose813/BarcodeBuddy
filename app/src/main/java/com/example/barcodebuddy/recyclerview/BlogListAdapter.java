package com.example.barcodebuddy.recyclerview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.barcodebuddy.AddBlogsActivity;
import com.example.barcodebuddy.MyUtilClass;
import com.example.barcodebuddy.R;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class BlogListAdapter extends RecyclerView.Adapter<BlogListAdapter.BlogViewHolder> {
    private List<BlogClass> bloglist;
    private Context context;

    public BlogListAdapter(Context context, List<BlogClass> bloglist) {
        this.context = context;
        this.bloglist = bloglist;
    }

    @NonNull
    @Override
    public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.blog_add_list, parent, false);
        return new BlogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder holder, int position) {
        BlogClass blogClass = bloglist.get(position);
        holder.tvTitle.setText(blogClass.getBlogName());
        holder.tvContent.setText(blogClass.getBlogContent());
        holder.tvAuthor.setText(blogClass.getBlogAuthor());

//        if (blogClass.getBlogImage() != null && !blogClass.getBlogImage().isEmpty()) {
//            Bitmap bitmap = MyUtilClass.base64ToBitmap(blogClass.getBlogImage());
//            holder.iv_BlogImage.setImageBitmap(bitmap);
//        } else {
//            holder.iv_BlogImage.setImageResource(R.drawable.profile);
//        }

        holder.ivDelete.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("Delete Blog");
            builder.setMessage("Are you sure you want to delete this blog?");

            builder.setPositiveButton("Yes", (dialog, which) -> {
                String blogId = blogClass.getBlogId();

                if (blogId != null && !blogId.isEmpty()) {
                    FirebaseDatabase.getInstance().getReference("Blogs")
                            .child(blogId).removeValue()
                            .addOnSuccessListener(aVoid -> {
                                bloglist.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, bloglist.size());
                            })
                            .addOnFailureListener(e -> {
                                // Show error if deletion fails
                                Toast.makeText(context, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(context, "Error: Invalid Blog ID", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            builder.show();
        });

        // ✅ EDIT BUTTON (Fixed)
        holder.ivEdit.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), AddBlogsActivity.class);
            intent.putExtra("id", blogClass.getBlogId());
            intent.putExtra("title", blogClass.getBlogName());
            intent.putExtra("content", blogClass.getBlogContent());
            intent.putExtra("author", blogClass.getBlogAuthor());
            intent.putExtra("image", blogClass.getBlogImage());
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bloglist.size();
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvAuthor;
        ImageView ivDelete, ivEdit, iv_BlogImage;

        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_Title);
            tvContent = itemView.findViewById(R.id.tv_Content);
            tvAuthor = itemView.findViewById(R.id.tv_Author);
            ivDelete = itemView.findViewById(R.id.iv_delete);
            ivEdit = itemView.findViewById(R.id.iv_edit);
           // iv_BlogImage = itemView.findViewById(R.id.iv_BlogImage);
        }
    }
}

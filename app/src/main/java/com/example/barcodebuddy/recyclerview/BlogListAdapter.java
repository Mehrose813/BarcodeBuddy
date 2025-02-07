package com.example.barcodebuddy.recyclerview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barcodebuddy.AddBlogsActivity;
import com.example.barcodebuddy.BlogListActivity;
import com.example.barcodebuddy.MyUtilClass;
import com.example.barcodebuddy.R;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;

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

        if (blogClass.getBlogImage() != null && !blogClass.getBlogImage().isEmpty()) {
            Bitmap bitmap = MyUtilClass.base64ToBitmap(blogClass.getBlogImage());
            holder.iv_BlogImage.setImageBitmap(bitmap);
        }


        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Delete Blog");
                builder.setMessage("Are you sure you want to delete this blog?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference("Blogs")
                                .child(blogClass.getBlogId()).removeValue();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
        holder.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(), AddBlogsActivity.class);
                intent.putExtra("id",blogClass.getBlogId());
                view.getContext().startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return bloglist.size();
    }
}

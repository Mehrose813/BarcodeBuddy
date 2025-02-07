package com.example.barcodebuddy;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.barcodebuddy.recyclerview.BlogClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddBlogsActivity extends AppCompatActivity {

    EditText etBlogsAuthor, etBlogContent, etTitle;
    Button btnSave;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_blogs);

        etTitle = findViewById(R.id.etTitle);
        etBlogsAuthor = findViewById(R.id.etAuthor);
        etBlogContent = findViewById(R.id.etContent);
        btnSave = findViewById(R.id.btnSaveBlog);

        ref = FirebaseDatabase.getInstance().getReference("Blogs");

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveBlog();
            }
        });
    }

    private void saveBlog() {
        String id = getIntent().getStringExtra("id");


        String content = etBlogContent.getText().toString().trim();
        String title = etTitle.getText().toString().trim();
        String author = etBlogsAuthor.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content) || TextUtils.isEmpty(author)) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(id!=null&& !id.isEmpty()){
            FirebaseDatabase.getInstance().getReference("Blogs").child(id)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            BlogClass blog=snapshot.getValue(BlogClass.class);
                            if(blog!=null){
                                etBlogContent.setText(blog.getBlogContent());
                                etTitle.setText(blog.getBlogName());
                                etBlogsAuthor.setText(blog.getBlogAuthor());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
          BlogClass blog=new BlogClass();
        blog.setBlogContent(content);
        blog.setBlogAuthor(author);
        blog.setBlogName(title);
     FirebaseDatabase.getInstance().getReference("Blogs").push()
             .setValue(blog).addOnCompleteListener(new OnCompleteListener<Void>() {
         @Override
         public void onComplete(@NonNull Task<Void> task) {
             if(task.isSuccessful()){
                 Toast.makeText(AddBlogsActivity.this, "Save Successfully", Toast.LENGTH_SHORT).show();
             }
             else{
                 Toast.makeText(AddBlogsActivity.this, "Failed to Save", Toast.LENGTH_SHORT).show();
             }
         }
     });
////        BlogClass blog = new BlogClass( content, title, author);
//
//        ref.child(id).setValue(blog).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    Toast.makeText(AddBlogsActivity.this, "Blog Saved!", Toast.LENGTH_SHORT).show();
//                    finish(); // Close activity after saving
//                } else {
//                    Toast.makeText(AddBlogsActivity.this, "Failed to save blog!", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }
}

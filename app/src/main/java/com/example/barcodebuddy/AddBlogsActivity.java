package com.example.barcodebuddy;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.barcodebuddy.recyclerview.BlogClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class AddBlogsActivity extends AppCompatActivity {

    EditText etBlogsAuthor, etBlogContent, etTitle, etLink;
    ImageView ivBlogCamera, ivBlogImage;
    Button btnSave;
    DatabaseReference ref;
    private Uri imageUri;
    private String blogId, oldImageId;

    // Activity result launchers for capturing and selecting images
    private final ActivityResultLauncher<Uri> captureImage =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
                if (result != null && result) {
                    if (imageUri != null) {
                        ivBlogImage.setImageURI(imageUri); // 📌 Display the captured image
                    }
                }
            });

    private final ActivityResultLauncher<String> pickImage =
            registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
                if (result != null) {
                    imageUri = result;
                    ivBlogImage.setImageURI(result); // 📌 Display the selected image
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_blogs);

        etTitle = findViewById(R.id.etTitle);
        etBlogsAuthor = findViewById(R.id.etAuthor);
        etBlogContent = findViewById(R.id.etContent);
        btnSave = findViewById(R.id.btnSaveBlog);
//        ivBlogCamera = findViewById(R.id.iv_blog_imageicon);
//        ivBlogImage = findViewById(R.id.iv_blog_image);
        etLink = findViewById(R.id.etLink);

        ref = FirebaseDatabase.getInstance().getReference("Blogs");

        blogId = getIntent().getStringExtra("id");
        if (blogId != null && !blogId.isEmpty()) {
            loadBlogData();
        }

        btnSave.setOnClickListener(view -> {
            if (blogId != null && !blogId.isEmpty()) {
                updateBlog();
            } else {
                saveBlog();
            }
        });

        ivBlogCamera.setOnClickListener(view -> setupEditIcon());
    }

    private void loadBlogData() {
        ref.child(blogId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    BlogClass blog = snapshot.getValue(BlogClass.class);
                    if (blog != null) {
                        etTitle.setText(blog.getBlogName());
                        etBlogsAuthor.setText(blog.getBlogAuthor());
                        etBlogContent.setText(blog.getBlogContent());
                        oldImageId = blog.getBlogImage(); // 📌 Store old image ID for updates

                        /*
                        // Load old image from Firebase if exists
                        if (oldImageId != null && !oldImageId.isEmpty()) {
                            FirebaseDatabase.getInstance().getReference("imagesString")
                                    .child(oldImageId)
                                    .get().addOnCompleteListener(task -> {
                                        if (task.isSuccessful() && task.getResult().exists()) {
                                            String base64Image = task.getResult().getValue(String.class);
                                            ivBlogImage.setImageBitmap(MyUtilClass.base64ToBitmap(base64Image));
                                        }
                                    });
                        }
                        */
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("LoadBlogData", "Failed to load blog data", error.toException());
            }
        });
    }

    private void saveBlog() {
        String content = etBlogContent.getText().toString().trim();
        String title = etTitle.getText().toString().trim();
        String author = etBlogsAuthor.getText().toString().trim();
        String link = etLink.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content) || TextUtils.isEmpty(author) || TextUtils.isEmpty(link)) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        BlogClass blog = new BlogClass(title, author, content, null, null, link);

        /*
        // If an image is selected, convert it to Base64 and save in Firebase
        if (imageUri != null) {
            String imageString = MyUtilClass.imageUriToBase64(imageUri, getContentResolver());
            String imageId = UUID.randomUUID().toString();

            FirebaseDatabase.getInstance().getReference("imagesString")
                    .child(imageId)
                    .setValue(imageString)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            blog.setBlogImage(imageId);
                            ref.push().setValue(blog).addOnCompleteListener(blogTask -> {
                                if (blogTask.isSuccessful()) {
                                    Toast.makeText(this, "Blog Saved Successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
        } else {
            */
        ref.push().setValue(blog).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Blog Saved Successfully", Toast.LENGTH_SHORT).show();
            }
        });
        // }
    }

    private void updateBlog() {
        String content = etBlogContent.getText().toString().trim();
        String title = etTitle.getText().toString().trim();
        String author = etBlogsAuthor.getText().toString().trim();
        String link = etLink.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content) || TextUtils.isEmpty(author)) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        BlogClass blog = new BlogClass(title, author, content, blogId, oldImageId, link);

        /*
        // If a new image is selected, replace the old one in Firebase
        if (imageUri != null) {
            String imageString = MyUtilClass.imageUriToBase64(imageUri, getContentResolver());
            String imageId = UUID.randomUUID().toString();

            FirebaseDatabase.getInstance().getReference("imagesString")
                    .child(imageId)
                    .setValue(imageString)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (oldImageId != null) {
                                FirebaseDatabase.getInstance().getReference("imagesString").child(oldImageId).removeValue();
                            }

                            blog.setBlogImage(imageId);
                            ref.child(blogId).setValue(blog).addOnCompleteListener(blogTask -> {
                                if (blogTask.isSuccessful()) {
                                    Toast.makeText(this, "Blog Updated Successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
        } else {
        */
        ref.child(blogId).setValue(blog).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Blog Updated Successfully", Toast.LENGTH_SHORT).show();
            }
        });
        // }
    }

    private void setupEditIcon() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Image")
                .setPositiveButton("Gallery", (dialogInterface, i) -> pickImage.launch("image/*"))
                .setNegativeButton("Camera", (dialogInterface, i) -> {
                    imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
                    if (imageUri != null) {
                        captureImage.launch(imageUri);
                    }
                }).show();
    }
}

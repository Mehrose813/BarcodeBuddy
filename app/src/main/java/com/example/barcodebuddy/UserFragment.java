package com.example.barcodebuddy;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.barcodebuddy.authdao.AuthDAO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class UserFragment extends Fragment {
    TextView tvName, tvEmail, tvPassword;
    Button btnLogout;

    ImageView ivEditicon;

    private Uri imageui;
    private ImageView ivProfile;

    private final ActivityResultLauncher<Uri> captureImage =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), new ActivityResultCallback<Boolean>() {
                        @Override
                        public void onActivityResult(Boolean result) {
                            if (result) {
                                //Hence capture image and display image
                            }
                            if (imageui != null) {
                                ivProfile.setImageURI(imageui);
                                //Do somethging with the capture image
                                saveImage(imageui);
                            }
                        }
                    }
            );
    private final ActivityResultLauncher<String> pickImage = registerForActivityResult(new ActivityResultContracts
            .GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            if (result != null) {
                ivProfile.setImageURI(result);
                saveImage(imageui);
            }
        }
    });

    private void captureImage() {
        imageui = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new ContentValues());

        captureImage.launch(imageui);

    }

    private void pickerImage() {
        imageui = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new ContentValues());
        pickImage.launch("Image/*");
    }


    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        // Initialize TextViews
        tvName = view.findViewById(R.id.tv_name);
        tvEmail = view.findViewById(R.id.tv_email);
        //changes
        ivEditicon = view.findViewById(R.id.editicon);
        ivProfile = view.findViewById(R.id.profile);

        //ab get krni ha
      

        //icon listner
        ivEditicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage();

            }
        });


        btnLogout = view.findViewById(R.id.btn_logout);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        AuthDAO authDAO = new AuthDAO();
        authDAO.fetchDetail(userId, new ResponseFetch() {

            @Override
            public void onSuccess(Profile profile) {

                tvName.setText(profile.getName());
                tvEmail.setText(profile.getEmail());
                //   tvPassword.setText(profile.getPassword());
            }

            @Override
            public void onError(String msg) {
                Toast.makeText(getContext(), "Error: " + msg, Toast.LENGTH_SHORT).show();
            }
        });


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
                builder.setTitle("Are you Sure you want to logout?");
                builder.setCancelable(true);

                AlertDialog alert = builder.create();

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(getContext(), SignInActivity.class);
                        FirebaseAuth.getInstance().signOut();
                        startActivity(intent);

                        requireActivity().finish();

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alert.dismiss();
                    }
                });
                builder.show();
            }
        });

        //gat krna
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Profile profile = snapshot.getValue(Profile.class);
                        if (profile != null) {
                            FirebaseDatabase.getInstance().getReference("Images")
                                    .child(profile.getProfileimageid())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                String imageString = snapshot.getValue().toString();
                                                ivProfile.setImageBitmap(MyUtilClass.base64ToBitmap(imageString));
                                            } else {
                                                // Handle the case where image doesn't exist
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Log.e("Firebase", "Image fetch failed: " + error.getMessage());
                                        }
                                    });
                        } else {
                            Log.e("Firebase", "Profile not found");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "User fetch failed: " + error.getMessage());
                    }
                });


        return view;
    }

    private void saveImage(Uri imageuri) {
        String imageString = MyUtilClass.imageUriToBase64(imageuri, requireContext().getContentResolver());

        // Get the existing profile image ID from Firebase
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getUid())
                .child("profileimageid")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String existingImageId = snapshot.getValue(String.class);

                        if (existingImageId != null) {
                            // If the image ID already exists, use it to update the image
                            updateImage(existingImageId, imageString);
                        } else {
                            // If no image ID exists, generate a new one
                            String uuid = UUID.randomUUID().toString();
                            saveNewImage(uuid, imageString);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Error fetching profile image ID: " + error.getMessage());
                    }
                });
    }

    private void updateImage(String imageId, String imageString) {
        // Update the existing image in Firebase
        FirebaseDatabase.getInstance().getReference("Images")
                .child(imageId)
                .setValue(imageString)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Successfully updated the image
                            Log.d("Firebase", "Image updated successfully.");
                        } else {
                            Log.e("Firebase", "Failed to update image.");
                        }
                    }
                });
    }

    private void saveNewImage(String uuid, String imageString) {
        // Save the new image with a new UUID in Firebase
        FirebaseDatabase.getInstance().getReference("Images")
                .child(uuid)
                .setValue(imageString)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Successfully saved the new image
                            updateProfileImageId(uuid);
                        } else {
                            Log.e("Firebase", "Failed to save new image.");
                        }
                    }
                });
    }

    private void updateProfileImageId(String uuid) {
        // Update the profile's image ID in the Users node
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getUid())
                .child("profileimageid")
                .setValue(uuid)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Firebase", "Profile image ID updated successfully.");
                        } else {
                            Log.e("Firebase", "Failed to update profile image ID.");
                        }
                    }
                });
    }


}
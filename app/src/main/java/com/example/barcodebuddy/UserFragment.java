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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class UserFragment extends Fragment {

    private TextView tvName, tvEmail;
    private Button btnLogout;
    private ImageView ivEditIcon, ivProfile;
    private Uri imageUri;

    private final ActivityResultLauncher<Uri> captureImage =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result != null && result) {
                        if (imageUri != null) {
                            ivProfile.setImageURI(imageUri);
                            saveImage(imageUri);
                        } else {
                            Log.e("CaptureImage", "Image URI is null.");
                        }
                    } else {
                        Log.e("CaptureImage", "Image capture failed.");
                    }
                }
            });

    private final ActivityResultLauncher<String> pickImage =
            registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        ivProfile.setImageURI(result);
                        saveImage(result);
                    } else {
                        Log.e("PickImage", "Image selection failed.");
                    }
                }
            });

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        // Initialize Views
        tvName = view.findViewById(R.id.tv_name);
        tvEmail = view.findViewById(R.id.tv_email);
        ivEditIcon = view.findViewById(R.id.editicon);
        ivProfile = view.findViewById(R.id.profile);
        btnLogout = view.findViewById(R.id.btn_logout);

        setupEditIcon();
        setupLogoutButton();
        fetchUserProfile();

        return view;
    }

    private void setupEditIcon() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Update Profile");
               builder .setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pickImage.launch("image/*");
                    }
                });
               builder .setNegativeButton("Camera", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       imageUri = requireContext().getContentResolver()
                               .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
                       if (imageUri != null) {
                           captureImage.launch(imageUri);
                       } else {
                           Log.e("CaptureImage", "Failed to create Image URI.");
                       }
                   }
               });

        ivEditIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.create();
                builder.show();
            }
        });
    }

    private void setupLogoutButton() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Are you sure you want to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Logout the user
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(getContext(), SignInActivity.class));
                                requireActivity().finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Dismiss the dialog
                                dialogInterface.dismiss();
                            }
                        });

                // Show the dialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }


    private void fetchUserProfile() {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            Log.e("Firebase", "User ID is null.");
            return;
        }

        FirebaseDatabase.getInstance().getReference("Users")
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Profile profile = snapshot.getValue(Profile.class);
                        if (profile != null) {
                            tvName.setText(profile.getName());
                            tvEmail.setText(profile.getEmail());
                            fetchProfileImage(profile.getProfileimageid());
                        } else {
                            Log.e("Firebase", "Profile data is null.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Error fetching user data: " + error.getMessage());
                    }
                });
    }

    private void fetchProfileImage(String imageId) {
        if (imageId == null) {
            Log.e("Firebase", "Image ID is null.");
            return;
        }

        FirebaseDatabase.getInstance().getReference("Images")
                .child(imageId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String imageString = snapshot.getValue(String.class);
                            ivProfile.setImageBitmap(MyUtilClass.base64ToBitmap(imageString));
                        } else {
                            Log.e("Firebase", "Image not found in database.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Error fetching image: " + error.getMessage());
                    }
                });
    }

    private void saveImage(Uri uri) {
        if (uri == null) {
            Log.e("SaveImage", "Image URI is null.");
            return;
        }

        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            Log.e("Firebase", "User ID is null.");
            return;
        }

        String imageString = MyUtilClass.imageUriToBase64(uri, requireContext().getContentResolver());
        FirebaseDatabase.getInstance().getReference("Users")
                .child(userId)
                .child("profileimageid")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String existingImageId = snapshot.getValue(String.class);
                        if (existingImageId != null) {
                            updateImage(existingImageId, imageString);
                        } else {
                            String newImageId = UUID.randomUUID().toString();
                            saveNewImage(newImageId, imageString);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Error fetching image ID: " + error.getMessage());
                    }
                });
    }

    private void updateImage(String imageId, String imageString) {
        FirebaseDatabase.getInstance().getReference("Images")
                .child(imageId)
                .setValue(imageString)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firebase", "Image updated successfully.");
                    } else {
                        Log.e("Firebase", "Failed to update image.");
                    }
                });
    }

    private void saveNewImage(String imageId, String imageString) {
        FirebaseDatabase.getInstance().getReference("Images")
                .child(imageId)
                .setValue(imageString)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        updateProfileImageId(imageId);
                    } else {
                        Log.e("Firebase", "Failed to save new image.");
                    }
                });
    }

    private void updateProfileImageId(String imageId) {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            Log.e("Firebase", "User ID is null.");
            return;
        }

        FirebaseDatabase.getInstance().getReference("Users")
                .child(userId)
                .child("profileimageid")
                .setValue(imageId)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firebase", "Profile image ID updated successfully.");
                    } else {
                        Log.e("Firebase", "Failed to update profile image ID.");
                    }
                });
    }
}

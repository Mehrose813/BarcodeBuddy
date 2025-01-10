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

    // For capturing an image
    private final ActivityResultLauncher<Uri> captureImage =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
                if (result != null && result) {
                    if (imageUri != null) {
                        ivProfile.setImageURI(imageUri);
                      updateUserProfileAndImage(imageUri); // Save image and update profile
                    } else {
                        Log.e("CaptureImage", "Image URI is null.");
                    }
                } else {
                    Log.e("CaptureImage", "Image capture failed.");
                }
            });

    // For picking an image
    private final ActivityResultLauncher<String> pickImage =
            registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
                if (result != null) {
                    ivProfile.setImageURI(result);
                   updateUserProfileAndImage(result); // Save image and update profile
                } else {
                    Log.e("PickImage", "Image selection failed.");
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
        builder.setTitle("Update Profile")
                .setPositiveButton("Gallery", (dialogInterface, i) -> pickImage.launch("image/*"))
                .setNegativeButton("Camera", (dialogInterface, i) -> {
                    imageUri = requireContext().getContentResolver()
                            .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
                    if (imageUri != null) {
                        captureImage.launch(imageUri);
                    } else {
                        Log.e("CaptureImage", "Failed to create Image URI.");
                    }
                });

        ivEditIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.create().show();
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
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(getContext(), SignInActivity.class));
                                requireActivity().finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                builder.create().show();
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

                            // Fetch profile image if it exists
                            String existingImageId = profile.getProfileimageid();
                            if (existingImageId != null) {
                                fetchProfileImage(existingImageId);
                            }
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

    private void updateUserProfileAndImage(Uri imageUri) {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            Log.e("Firebase", "User ID is null.");
            return;
        }

        String imageString = MyUtilClass.imageUriToBase64(imageUri, requireContext().getContentResolver());
        FirebaseDatabase.getInstance().getReference("Users")
                .child(userId)
                .child("profileimageid")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String existingImageId = snapshot.getValue(String.class);
                        if (existingImageId != null) {
                            FirebaseDatabase.getInstance().getReference("Images")
                                    .child(existingImageId)
                                    .setValue(imageString)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "Image updated successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.e("Firebase", "Failed to update image.");
                                        }
                                    });
                        } else {
                            String newImageId = UUID.randomUUID().toString();
                            FirebaseDatabase.getInstance().getReference("Images")
                                    .child(newImageId)
                                    .setValue(imageString)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            FirebaseDatabase.getInstance().getReference("Users")
                                                    .child(userId)
                                                    .child("profileimageid")
                                                    .setValue(newImageId)
                                                    .addOnCompleteListener(updateTask -> {
                                                        if (updateTask.isSuccessful()) {
                                                            Toast.makeText(getContext(), "Image saved successfully", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Log.e("Firebase", "Failed to update profile image ID.");
                                                        }
                                                    });
                                        } else {
                                            Log.e("Firebase", "Failed to save new image.");
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Error fetching image ID: " + error.getMessage());
                    }
                });
    }
}

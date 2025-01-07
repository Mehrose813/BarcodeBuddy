package com.example.barcodebuddy;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.barcodebuddy.authdao.AuthDAO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserFragment extends Fragment {
    TextView tvName, tvEmail, tvPassword;
    Button btnLogout;

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
        tvPassword = view.findViewById(R.id.tv_password);
        btnLogout = view.findViewById(R.id.btn_logout);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        AuthDAO authDAO = new AuthDAO();
        authDAO.fetchDetail(userId, new ResponseFetch() {

            @Override
            public void onSuccess(Profile profile) {

                tvName.setText(profile.getName());
                tvEmail.setText(profile.getEmail());
                tvPassword.setText(profile.getPassword());
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
                builder.setTitle("Logout Account");
                builder.setMessage("Are you Sure");
                builder.setCancelable(true);

                AlertDialog alert = builder.create();

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(getContext(),SignInActivity.class);
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

                return view;
    }
}

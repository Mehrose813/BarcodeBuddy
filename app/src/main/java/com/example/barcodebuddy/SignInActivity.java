package com.example.barcodebuddy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.barcodebuddy.authdao.AuthDAO;
import com.example.barcodebuddy.authdao.ResponseCallBack;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {
    AppCompatButton btnLogin;
    EditText email,passw;
    TextView txtSigup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);

        btnLogin = findViewById(R.id.btn_login);
        email = findViewById(R.id.mail);
        passw = findViewById(R.id.pass);
        txtSigup = findViewById(R.id.txt_sigup);


        ProgressDialog progressDialog = new ProgressDialog(SignInActivity.this);
        progressDialog.setTitle("SignIn in process");
        progressDialog.setMessage("Please wait.....");
        progressDialog.setCancelable(false);


        txtSigup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this,SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mail = email.getText().toString();
                String pass = passw.getText().toString();
                String passwordVal = "^(?=.*[!@#$%^&*(),.?\":{}|<>])[^\\s]+$";
                //validation
                if (mail.isEmpty()) {
                    email.setError("Email is required");
                    email.requestFocus();
                    return;
                }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                    email.setError("Enter a valid email");
                    email.requestFocus();
                    return;
                }

                if (pass.isEmpty()) {
                    passw.setError("Password is required");
                    passw.requestFocus();
                    return;
                }

                if(pass.length() <8){
                    passw.setError("Password should be atleast 8 characters");
                    passw.requestFocus();
                    return;
                }
                if(!pass.matches(passwordVal)){
                    if (pass.contains(" ")) {
                        passw.setError("Spaces are not allowed in the password");
                    } else {
                        passw.setError("Password must contain at least one special character");
                    }
                    return;
                }

                progressDialog.show();


                // Check if admin credentials are entered



                // Sign-in Logic

                AuthDAO auth = new AuthDAO();
                auth.signin(SignInActivity.this, mail, pass, new ResponseCallBack() {
                    @Override
                    public void onSuccess() {
                        progressDialog.dismiss();
                        // Navigation handled in signin() method
//                        Intent intent = new Intent(SignInActivity.this,HomeActivity.class);
//                        startActivity(intent);
//                        Toast.makeText(SignInActivity.this, "Sign-in successful", Toast.LENGTH_SHORT).show();
//                        finish();

                        if (mail.equals("superadmin@gmail.com")) {
                            progressDialog.dismiss();
                            // Admin credentials matched, no need to proceed with regular sign-in
                            Intent intent = new Intent(SignInActivity.this, AdminMainActivity.class);
                            startActivity(intent);
                            finish();

                        }
                        else{
                            Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }



                    }

                    @Override
                    public void onError(String msg) {
                        progressDialog.dismiss();
                        Toast.makeText(SignInActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }
}
package com.example.barcodebuddy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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

public class SignUpActivity extends AppCompatActivity {
AppCompatButton btnSignup;
EditText mail,pass,conPass,nameSign;
TextView txtLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        btnSignup = findViewById(R.id.btn_signup);
        mail = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        conPass = findViewById(R.id.con_pass);
        txtLogin = findViewById(R.id.txt_login);
        nameSign = findViewById(R.id.name);

        ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("SignUp in process");
        progressDialog.setMessage("Please wait.....");
        progressDialog.setCancelable(false);


        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this,SignInActivity.class);

                startActivity(intent);
                finish();
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mail.getText().toString();
                String password = pass.getText().toString();
                String confirm = conPass.getText().toString();
                String name = nameSign.getText().toString();
                String type="user";

                String nameRegex = "^[^\\s]+$";
                String passwordVal = "^(?=.*[!@#$%^&*(),.?\":{}|<>])[^\\s]+$";

                if(name.isEmpty()){
                    nameSign.setError("Name is required");
                    nameSign.requestFocus();
                    return;
                }
                if(!name.matches(nameRegex)){
                    nameSign.setError("Spaces are not allowed in naming");
                    nameSign.requestFocus();
                    return;
                }

                if (email.isEmpty()) {
                    mail.setError("Email is required");
                    mail.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mail.setError("Enter a valid email");
                    mail.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    pass.setError("Password is required");
                    pass.requestFocus();
                    return;
                }
                if(password.length() <8){
                    pass.setError("Password should be atleast 8 characters");
                    pass.requestFocus();
                    return;
                }
                if(!password.matches(passwordVal)){
                    if (password.contains(" ")) {
                        pass.setError("Spaces are not allowed in the password");
                    } else {
                        pass.setError("Password must contain at least one special character");
                    }
                    return;
                }
                if(confirm.isEmpty()){
                    conPass.setError("Password confirmation is required");
                    conPass.requestFocus();
                    return;
                }
                if(!confirm.equals(password)){
                    conPass.setError("Password is not correct");
                    conPass.requestFocus();
                    return;

                }




                else{
                progressDialog.show();
                AuthDAO auth = new AuthDAO();
                auth.signup(SignUpActivity.this,name, email, password, type,new ResponseCallBack() {
                    @Override
                    public void onSuccess() {

                        progressDialog.dismiss();
                        Intent intent = new Intent(SignUpActivity.this,HomeActivity.class);

                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String msg) {
                        progressDialog.dismiss();
                        Toast.makeText(SignUpActivity.this, msg, Toast.LENGTH_SHORT).show();

                    }
                });


            }
                }
        });

    }
}
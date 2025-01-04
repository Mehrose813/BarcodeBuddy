package com.example.barcodebuddy;

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

public class SignUpActivity extends AppCompatActivity {
AppCompatButton btnSignup;
EditText mail,pass,conPass;
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

        Intent intent = new Intent(SignUpActivity.this,SignInActivity.class);

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mail.getText().toString();
                String password = pass.getText().toString();
                String confirm = conPass.getText().toString();

                if (email.isEmpty()) {
                    mail.setError("Email is required");
                    mail.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    pass.setError("Password is required");
                    pass.requestFocus();
                    return;
                }

                if(password.length() <6){
                    pass.setError("Password should be atleast 6 characters");
                    pass.requestFocus();
                    return;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mail.setError("Enter a valid email");
                    mail.requestFocus();
                    return;
                }

                AuthDAO auth = new AuthDAO();
                auth.signin(SignUpActivity.this, email, password, new ResponseCallBack() {
                    @Override
                    public void onSuccess() {
                        startActivity(intent);
                    }

                    @Override
                    public void onError(String msg) {
                        Toast.makeText(SignUpActivity.this, msg, Toast.LENGTH_SHORT).show();

                    }
                });


            }
        });

    }
}
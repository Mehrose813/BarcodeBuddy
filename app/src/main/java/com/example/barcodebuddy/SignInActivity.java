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

        txtSigup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mail = email.getText().toString();
                String pass = passw.getText().toString();

                //validation
                if (mail.isEmpty()) {
                    email.setError("Email is required");
                    email.requestFocus();
                    return;
                }

                if (pass.isEmpty()) {
                    passw.setError("Password is required");
                    passw.requestFocus();
                    return;
                }

                if(pass.length() <6){
                    passw.setError("Password should be atleast 6 characters");
                    passw.requestFocus();
                    return;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                    email.setError("Enter a valid email");
                    email.requestFocus();
                    return;
                }

                AuthDAO auth = new AuthDAO();
                auth.signin(SignInActivity.this, mail, pass, new ResponseCallBack() {
                    @Override
                    public void onSuccess() {
                        Intent intent = new Intent(SignInActivity.this,SignUpActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(String msg) {
                        Toast.makeText(SignInActivity.this, msg, Toast.LENGTH_SHORT).show();

                    }
                });


            }
        });

    }
}
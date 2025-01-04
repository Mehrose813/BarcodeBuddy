package com.example.barcodebuddy.authdao;


import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AuthDAO {

    public void signin(Context context ,String mail , String pass ,ResponseCallBack callBack){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(mail,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    callBack.onSuccess();
                }
                else {
                    callBack.onError(task.getException().getMessage());

                }

            }
        });

    }

    public void signup(Context context , String email, String password , ResponseCallBack callback){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            callback.onSuccess();
                        }
                        else{
                            callback.onError(task.getException().getMessage());
                        }
                    }
                });

    }
}

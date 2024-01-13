package com.eneskilic.aboneliklerim.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.eneskilic.aboneliklerim.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private FirebaseAuth auth;
    private String Email;
    private String Password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if( user != null){
            Intent intent = new Intent(getApplicationContext(),DashBoardActivity.class);
            finish();
            startActivity(intent);
        }
    }

    public void RedirectToRegister(View view){
        Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
        startActivity(intent);
    }
    public void LoginButton(View view){
        Email = binding.EmailText.getText().toString();
        Password = binding.PasswordText.getText().toString();
        if(!Email.equals("") && !Password.equals("")){
            auth.signInWithEmailAndPassword(Email,Password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Intent intent = new Intent(MainActivity.this,DashBoardActivity.class);
                            finish();
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(binding.getRoot(), Objects.requireNonNull(e.getLocalizedMessage()),Snackbar.LENGTH_LONG).show();
                        }
                    });

        }
        else {
            Snackbar.make(binding.getRoot(), "E-posta Veya Şifre Bilgisi Eksik.",Snackbar.LENGTH_LONG).show();
        }
    }

    public void ResetPassword(View view){
        Intent intent = new Intent(getApplicationContext(),ForgotPasswordActivity.class);
        startActivity(intent);
        finish();
    }
}
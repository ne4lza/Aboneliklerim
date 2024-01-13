package com.eneskilic.aboneliklerim.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.eneskilic.aboneliklerim.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;

    private FirebaseAuth auth;
    private String Email;
    private String Password;
    private String PasswordConfirm;
    Uri imagePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        auth = FirebaseAuth.getInstance();

    }

    public void ComplateRegistration(View view){
        Email = binding.EmailText.getText().toString();
        Password = binding.PasswordText.getText().toString();
        PasswordConfirm = binding.PasswordConfirmText.getText().toString();
        if (Password.equals(PasswordConfirm)){
            auth.createUserWithEmailAndPassword(Email,Password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            auth.signInWithEmailAndPassword(Email,Password)
                                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {
                                            Intent intent = new Intent(RegisterActivity.this,RegisterFeaturesActivity.class);
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
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(binding.getRoot(), Objects.requireNonNull(e.getLocalizedMessage()),Snackbar.LENGTH_LONG).show();
                        }
                    });
        }
        else {
            Snackbar.make(binding.getRoot(),"Şifrelerinizin Aynı Olması Gerekmektedir.",Snackbar.LENGTH_LONG)
                    .setAction("Tamam", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    }).show();
        }
    }
    public void RedirectToLogin(View view){
        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(intent);
    }
}
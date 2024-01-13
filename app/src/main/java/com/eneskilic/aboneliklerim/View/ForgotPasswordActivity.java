package com.eneskilic.aboneliklerim.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;

import com.eneskilic.aboneliklerim.R;
import com.eneskilic.aboneliklerim.databinding.ActivityForgotPasswordBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {
    FirebaseAuth auth;
    ActivityForgotPasswordBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        auth = FirebaseAuth.getInstance();
    }
    public void GoHome(View view){
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }
    public void ForgotPassword(View view){
        String Email = binding.EmailText.getText().toString();
        if(!Email.equals("")){
            auth.sendPasswordResetEmail(Email)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(),Email+" adresine bir şifre sıfırlama bağlantısı gönderdik. Lütfen gelen kutunuzu kontrol edin.",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), Objects.requireNonNull(e.getLocalizedMessage()),Toast.LENGTH_LONG).show();
                        }
                    });
        }else {
            Snackbar.make(view,"Lütfen bir E-posta adresi sağlayın.",Snackbar.LENGTH_LONG).show();
        }
    }
}
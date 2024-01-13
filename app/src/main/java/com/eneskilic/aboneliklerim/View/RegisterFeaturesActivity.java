package com.eneskilic.aboneliklerim.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.eneskilic.aboneliklerim.R;
import com.eneskilic.aboneliklerim.databinding.ActivityRegisterFeaturesBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class RegisterFeaturesActivity extends AppCompatActivity {
    ActivityRegisterFeaturesBinding binding;
    private FirebaseAuth auth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Uri imagePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterFeaturesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        auth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();
        RegisterPermission();
    }
    public void ComplateRegistration(View view){
        UUID uuid = UUID.randomUUID();
        String imageName = "images/"+uuid+".jpg";
        if(!binding.NameText.getText().equals("") || !binding.LastNameText.getText().equals("") || !binding.DateText.getText().equals("")){
            if(imagePath != null){
                storageReference.child(imageName).putFile(imagePath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                StorageReference newStorageReference =firebaseStorage.getReference(imageName);
                                newStorageReference.getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String downloadUrl = uri.toString();
                                                String userName = binding.NameText.getText().toString();
                                                String userLastName = binding.LastNameText.getText().toString();
                                                String userBirthDate = binding.DateText.getText().toString();
                                                FirebaseUser user = auth.getCurrentUser();
                                                String documentId = uuid.toString();
                                                assert user !=null;
                                                String userId = user.getUid();
                                                String userEmail = user.getEmail();
                                                String userGender = "";
                                                if(binding.CheckboxFemale.isChecked()){
                                                    userGender = "Kadın";
                                                } else if (binding.CheckboxMale.isChecked()) {
                                                    userGender = "Erkek";
                                                }
                                                HashMap<String, Object> userData = new HashMap<>();
                                                userData.put("UserId",userId);
                                                userData.put("DocumentId",documentId);
                                                userData.put("UserName",userName);
                                                userData.put("UserLastName",userLastName);
                                                userData.put("UserEmail",userEmail);
                                                userData.put("UserBirthDate",userBirthDate);
                                                userData.put("UserGender",userGender);
                                                userData.put("CreatedDate", FieldValue.serverTimestamp());
                                                userData.put("UserPhoto",downloadUrl);
                                                firebaseFirestore.collection("Users")
                                                        .document(documentId)
                                                        .set(userData)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Intent intent = new Intent(RegisterFeaturesActivity.this,MainActivity.class);;
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                auth.signOut();
                                                                startActivity(intent);
                                                                Toast.makeText(getApplicationContext(),"Başarı İle Kayıt Oldunuz",Toast.LENGTH_LONG).show();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Snackbar.make(binding.getRoot(),Objects.requireNonNull(e.getLocalizedMessage()),Snackbar.LENGTH_LONG).show();
                                                            }
                                                        });

                                            }
                                        });
                            }
                        });

            }
        }
        else {
            Snackbar.make(binding.getRoot(),"Lütfen Boş Alan Bırakmayınız",Snackbar.LENGTH_LONG).show();
        }
    }
    public void SetProfileImage(View view){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(RegisterFeaturesActivity.this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(RegisterFeaturesActivity.this,Manifest.permission.READ_MEDIA_IMAGES)){
                    Snackbar.make(binding.getRoot(),"Profil Fotoğrafı Eklemek İçin İzin Gerekmektedir.",Snackbar.LENGTH_INDEFINITE).setAction("İzin Ver", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    }).show();
                }
                else {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                }
            }
            else {
                Intent intentToGallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }
        }
        else {
            if(ContextCompat.checkSelfPermission(RegisterFeaturesActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(RegisterFeaturesActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(binding.getRoot(),"Profil Fotoğrafı Eklemek İçin İzin Gerekmektedir.",Snackbar.LENGTH_INDEFINITE).setAction("İzin Ver", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    }).show();
                }
                else {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            }
            else {
                Intent intentToGallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }
        }
    }

    private void RegisterPermission(){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                if (o.getResultCode() == RESULT_OK){
                    Intent intentFromResult = o.getData();
                    if (intentFromResult != null){
                        imagePath = intentFromResult.getData();
                        binding.ProfileImage.setImageURI(imagePath);
                    }
                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean o) {
                if (o){
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                }
            }
        });
    }

}
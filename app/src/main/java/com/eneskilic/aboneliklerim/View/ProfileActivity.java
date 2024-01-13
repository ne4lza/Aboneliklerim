package com.eneskilic.aboneliklerim.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.eneskilic.aboneliklerim.R;
import com.eneskilic.aboneliklerim.databinding.ActivityProfileBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    ActivityProfileBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    String userName;
    String userLastName;
    String userCreatedDate;
    private AdView adView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        adView = binding.adView;
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        auth =  FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = auth.getCurrentUser();
        GetProfileData();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationBar);
        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.bottom_home){
                    Intent intent = new Intent(getApplicationContext(),DashBoardActivity.class);
                    finish();
                    startActivity(intent);
                    return true;
                }
                else if(item.getItemId() == R.id.bottom_add){
                    Intent intent = new Intent(getApplicationContext(),AddSubscriptionActivity.class);
                    finish();
                    startActivity(intent);
                    return true;
                }
                else if(item.getItemId() == R.id.bottom_profile){
                    return true;
                }
                else if(item.getItemId() == R.id.log_out){
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    auth.signOut();
                    finish();
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }
    private void GetProfileData(){
        firebaseFirestore.collection("Users")
                .whereEqualTo("UserId",firebaseUser.getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        for (DocumentSnapshot documentSnapshot : value.getDocuments()){
                                    Map<String,Object> data = documentSnapshot.getData();
                                    String userName = (String) data.get("UserName");
                                    String userLastName = (String) data.get("UserLastName");
                                    String userEmail = (String) data.get("UserEmail");
                                    String userPhoto = (String) data.get("UserPhoto");
                                    String userBirthDate = (String) data.get("UserBirthDate");
                                    String userGender = (String) data.get("UserGender");
                                    binding.userNameLastNameText.setText(userName+" "+userLastName);
                                    binding.textView.setText(userEmail);
                                    Picasso.get().load(userPhoto).into(binding.ProfileImage);
                                }
                    }
                });
    }
}
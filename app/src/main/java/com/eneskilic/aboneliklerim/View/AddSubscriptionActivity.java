package com.eneskilic.aboneliklerim.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.eneskilic.aboneliklerim.Adapter.ApiAdapter;
import com.eneskilic.aboneliklerim.Adapter.ProductAdapter;
import com.eneskilic.aboneliklerim.Model.ApiModel;
import com.eneskilic.aboneliklerim.Model.Product;
import com.eneskilic.aboneliklerim.R;
import com.eneskilic.aboneliklerim.Service.SubscriptionAPI;
import com.eneskilic.aboneliklerim.databinding.ActivityAddSubscriptionBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class AddSubscriptionActivity extends AppCompatActivity {
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference  storageReference;
    private ActivityAddSubscriptionBinding binding;
    String BASE_URL = "https://raw.githubusercontent.com/ne4lza/";
    Retrofit retrofit;
    ArrayList<ApiModel> apiModelArrayList;
    ApiAdapter apiAdapter;
    private AdView adView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddSubscriptionBinding.inflate(getLayoutInflater());
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


        binding.SearchView.clearFocus();
        Search();
        firebaseStorage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();;
        Gson gson = new GsonBuilder().setLenient().create();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        loadData();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationBar);
        bottomNavigationView.setSelectedItemId(R.id.bottom_add);
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
                    return true;
                }
                else if(item.getItemId() == R.id.bottom_profile){
                    Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
                    finish();
                    startActivity(intent);
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
    private void loadData(){
        SubscriptionAPI subscriptionAPI = retrofit.create(SubscriptionAPI.class);
        Call<List<ApiModel>> call = subscriptionAPI.getData();
        call.enqueue(new Callback<List<ApiModel>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<List<ApiModel>> call, Response<List<ApiModel>> response) {
                if(response.isSuccessful()){
                    List<ApiModel> responseList = response.body();
                    assert responseList != null;
                    apiModelArrayList = new ArrayList<>(responseList);
                    binding.recyclerViewProducts.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    apiAdapter = new ApiAdapter(apiModelArrayList,getApplicationContext());
                    binding.recyclerViewProducts.setAdapter(apiAdapter);
                    apiAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<ApiModel>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
    private void Search(){
        binding.SearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
               filterList(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void filterList(String text) {
        ArrayList<ApiModel> filterList = new ArrayList<>();
        for (ApiModel apiModel : apiModelArrayList){
            if(apiModel.productName.toLowerCase().contains(text.toLowerCase())){
                filterList.add(apiModel);
            }else {
                apiAdapter.setFilteredList(apiModelArrayList);
            }

        }
        if (filterList.isEmpty()){
            Toast.makeText(getApplicationContext(),"Hiçbir Sonuç Bulunamadı...",Toast.LENGTH_LONG).show();

        }
        else {
            apiAdapter.setFilteredList(filterList);
        }
    }
}
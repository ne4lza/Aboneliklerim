package com.eneskilic.aboneliklerim.View;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.eneskilic.aboneliklerim.Adapter.ProductAdapter;
import com.eneskilic.aboneliklerim.Model.Product;
import com.eneskilic.aboneliklerim.R;
import com.eneskilic.aboneliklerim.databinding.ActivityDashBoardBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class DashBoardActivity extends AppCompatActivity {
    ActivityDashBoardBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    ArrayList<Product> productArrayList;
    ProductAdapter productAdapter;
    Integer productCount = 0;
    Double productPriceD = 0.0;
    Double totalSum = 0.0;
    Double totalSumYear = 0.0;
    private AdView adView;
    private InterstitialAd mInterstitialAd;
    boolean IsLoaded = false;
    DocumentReference documentReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashBoardBinding.inflate(getLayoutInflater());
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

        InterstitialAd.load(this, "ca-app-pub-5307960049426470/2254229876", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        if (mInterstitialAd != null) {
                            mInterstitialAd.show(DashBoardActivity.this);
                        } else {
                            Log.d("TAG", "The interstitial ad wasn't ready yet.");
                        }
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = auth.getCurrentUser();
        GetData();
        productArrayList = new ArrayList<>();
        binding.prdocutRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
         productAdapter = new ProductAdapter(productArrayList,DashBoardActivity.this);
        binding.prdocutRecyclerView.setAdapter(productAdapter);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationBar);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.bottom_home){
                    return true;
                }
                else if(item.getItemId() == R.id.bottom_add){
                    Intent intent = new Intent(getApplicationContext(),AddSubscriptionActivity.class);
                    finish();
                    startActivity(intent);
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
    private void GetData(){
        firebaseFirestore.collection("Products")
                .whereEqualTo("UserId",firebaseUser.getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            Snackbar.make(binding.getRoot(), Objects.requireNonNull(error.getLocalizedMessage()),Snackbar.LENGTH_LONG).show();
                        }
                        if (value != null){
                            productArrayList.clear();
                            for (DocumentSnapshot snapshot : value.getDocuments()){
                                Map<String,Object> data = snapshot.getData();
                                String productName =(String) data.get("ProductName");
                                String productPrice = (String) data.get("ProductPrice");
                                String productId = (String) data.get("ProductId");
                                String productPlan = (String) data.get("ProductPlan");
                                assert productPrice != null;
                                productPriceD = Double.parseDouble(productPrice);
                                Product product = new Product(productId,productName,productPrice,productPlan);
                                productArrayList.add(product);
                                productCount++;
                                totalSum += productPriceD;

                            }
                            totalSumYear = totalSum * 12;
                            binding.productCountText.setText(productCount.toString());

                            DecimalFormat decimalFormat = new DecimalFormat("#.##");
                            String newPriceYear= decimalFormat.format(totalSumYear);
                            String newPrice = decimalFormat.format(totalSum);
                            binding.priceSumTextView.setText(newPrice + "₺");
                            binding.priceSumYearTextView.setText(newPriceYear + "₺");
                            productAdapter.notifyDataSetChanged();

                        }
                    }
                });

    }
}
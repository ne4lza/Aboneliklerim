package com.eneskilic.aboneliklerim.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eneskilic.aboneliklerim.Model.ApiModel;
import com.eneskilic.aboneliklerim.databinding.RecyclerApiRowBinding;
import com.eneskilic.aboneliklerim.databinding.RecyclerRowBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.zip.Inflater;

public class ApiAdapter extends RecyclerView.Adapter<ApiAdapter.ApiViewHolder> {
    ArrayList<ApiModel> apiModelArrayList;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    Context context;
    public ApiAdapter(ArrayList<ApiModel> apiModelArrayList, Context context){
        this.apiModelArrayList = apiModelArrayList;
        this.context = context;
    }
    public void setFilteredList(ArrayList<ApiModel> filteredList){
        this.apiModelArrayList = filteredList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ApiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerApiRowBinding recyclerApiRowBinding = RecyclerApiRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ApiViewHolder(recyclerApiRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ApiViewHolder holder, int position) {
        holder.recyclerApiRowBinding.textViewProductName.setText(apiModelArrayList.get(holder.getAdapterPosition()).productName);
        holder.recyclerApiRowBinding.textViewsProductPlan.setText("Plan: "+apiModelArrayList.get(holder.getAdapterPosition()).plan);
        holder.recyclerApiRowBinding.productPrice.setText(apiModelArrayList.get(holder.getAdapterPosition()).price);
        holder.recyclerApiRowBinding.AddProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UUID uuid = UUID.randomUUID();
                    String productName = (String) holder.recyclerApiRowBinding.textViewProductName.getText();
                    String productPlan = (String) holder.recyclerApiRowBinding.textViewsProductPlan.getText();
                    String productPrice = (String) holder.recyclerApiRowBinding.productPrice.getText();
                    FirebaseUser user = auth.getCurrentUser();
                    String documentId = uuid.toString();
                    assert user !=null;
                    String userId = user.getUid();
                    HashMap<String, Object> productData = new HashMap<>();
                    productData.put("UserId",userId);
                    productData.put("ProductName",productName);
                    productData.put("ProductPrice",productPrice);
                    productData.put("ProductPlan",productPlan);
                    productData.put("CreatedDate", FieldValue.serverTimestamp());
                    productData.put("ProductId",documentId);
                    firebaseFirestore.collection("Products")
                            .document(documentId)
                            .set(productData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context, "Başarı İle Eklendi", Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
        });
    }

    @Override
    public int getItemCount() {
        return apiModelArrayList.size();
    }

    public static class ApiViewHolder extends RecyclerView.ViewHolder{
        RecyclerApiRowBinding recyclerApiRowBinding;
        public ApiViewHolder(RecyclerApiRowBinding recyclerApiRowBinding) {
            super(recyclerApiRowBinding.getRoot());
            this.recyclerApiRowBinding = recyclerApiRowBinding;
        }
    }
}

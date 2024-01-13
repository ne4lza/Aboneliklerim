package com.eneskilic.aboneliklerim.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.eneskilic.aboneliklerim.Model.Product;
import com.eneskilic.aboneliklerim.R;
import com.eneskilic.aboneliklerim.View.DashBoardActivity;
import com.eneskilic.aboneliklerim.databinding.RecyclerRowBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import javax.xml.transform.dom.DOMLocator;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    ArrayList<Product> productArrayList;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    Context context;
    public ProductAdapter(ArrayList<Product> productArrayList,Context context)
    {
        this.productArrayList = productArrayList;
        this.context = context;
    }
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ProductViewHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.recyclerRowBinding.textViewProductName.setText(productArrayList.get(holder.getAdapterPosition()).ProductName);
        holder.recyclerRowBinding.textViewsProductPlan.setText(productArrayList.get(holder.getAdapterPosition()).ProductPlan);
        holder.recyclerRowBinding.productPrice.setText(productArrayList.get(holder.getAdapterPosition()).ProductPrice);
        String docId = productArrayList.get(holder.getAdapterPosition()).ProductId;
        holder.recyclerRowBinding.RemoveProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Delete(docId);
                RefreshPage();
            }
        });
    }
    public int getItemCount() {
        return productArrayList.size();
    }
    public static class ProductViewHolder extends RecyclerView.ViewHolder{
        RecyclerRowBinding recyclerRowBinding;
        public ProductViewHolder(RecyclerRowBinding recyclerRowBinding) {
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding = recyclerRowBinding;
        }
    }
    public void Delete(String id){
           if(!((Activity) context).isFinishing()){
               AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.Theme_Aboneliklerim));
               alert.setTitle("Aboneliğinizi Silmek İstediğinizden Emin Misiniz?");
               alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       firebaseFirestore.collection("Products")
                               .whereEqualTo("ProductId",id)
                               .get()
                               .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                   @Override
                                   public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                       firebaseFirestore.collection("Products")
                                               .document(id)
                                               .delete()
                                               .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                   @SuppressLint("NotifyDataSetChanged")
                                                   @Override
                                                   public void onSuccess(Void unused) {
                                                       if (context instanceof DashBoardActivity) {
                                                           // Aktivite metoduna erişim sağlayabilirsiniz
                                                           ((DashBoardActivity) context).recreate();
                                                       }
                                                       Toast.makeText(context, "Silme işlemi başarıyla gerçekleşti.", Toast.LENGTH_SHORT).show();
                                                   }
                                               })
                                               .addOnFailureListener(new OnFailureListener() {
                                                   @Override
                                                   public void onFailure(@NonNull Exception e) {
                                                       Toast.makeText(context, "Silme işlemi yapılırken bir hata oluştu lütfen tekrar deneyin..", Toast.LENGTH_SHORT).show();
                                                   }
                                               });
                                   }
                               });


                   }
               });
               alert.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       Toast.makeText(context, "Silme işlemi iptal edildi.", Toast.LENGTH_SHORT).show();
                   }
               });
               alert.show();
           }

    }
    public void RefreshPage() {

    }
}

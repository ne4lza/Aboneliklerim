package com.eneskilic.aboneliklerim.Service;

import com.eneskilic.aboneliklerim.Model.ApiModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;

public interface SubscriptionAPI {
    @GET("AboneliklerimApi/main/API.json")
    Call<List<ApiModel>> getData();
}

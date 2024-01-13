package com.eneskilic.aboneliklerim.Model;

import com.google.gson.annotations.SerializedName;

public class ApiModel {
    @SerializedName("ProductName")
    public String productName;
    @SerializedName("Plan")
    public String plan;
    @SerializedName("Price")
    public String price;
}

package com.eneskilic.aboneliklerim.Model;

public class Product {
    public String ProductId;
    public String ProductName;
    public String ProductPrice;
    public String ProductPlan;

    public Product(String ProductId,String ProductName,String ProductPrice,String ProductPlan){
        this.ProductId = ProductId;
        this.ProductName = ProductName;
        this.ProductPrice = ProductPrice;
        this.ProductPlan = ProductPlan;

    }
}
